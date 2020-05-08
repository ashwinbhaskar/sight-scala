package sight.client

import sight.types.APIKey
import sight.models.{Pages, MimeType, RecognizedTexts, PollingUrl, Page}
import sight.models.Error
import sight.models.Error.{DecodingFailure, ErrorResponse}
import sttp.client._
import sttp.client.circe._
import io.circe.{Json, Encoder, Decoder}
import io.circe.syntax._
import io.circe.parser.decode
import sight.decoders.{given Decoder[Pages], given Decoder[RecognizedTexts], given Decoder[PollingUrl]}
import cats.implicits.{given _}
import scala.language.implicitConversions
import sttp.client.{SttpBackend, Identity, NothingT}
import scala.util.chaining._

class SightClientImpl(private val apiKey: APIKey, private val fileContentReader: FileContentReader)(using SttpBackend[Identity, Nothing, NothingT]) extends SightClient(apiKey, fileContentReader):
    type DecodedPostResponse = Either[Error, PollingUrl | RecognizedTexts]
    private case class FileContent(mimeType: MimeType, base64FileContent: String)
    private case class Payload(shouldMakeSentences: Boolean, files: Seq[FileContent])
    private given Encoder[FileContent]:
        def apply(fileContent: FileContent): Json = Json.obj(
            ("mimeType", Json.fromString(fileContent.mimeType.strRep)),
            ("base64File", Json.fromString(fileContent.base64FileContent)))
    private given Encoder[Payload]:
        def apply(payload: Payload): Json = Json.obj(
            ("makeSentences", Json.fromBoolean(payload.shouldMakeSentences)),
            ("files", payload.files.asJson))

    private def handlePollingUrl(url: String, numberOfFiles: Int): Either[Error, Pages] = 
        var pages = List[Page]()
        val pageSeenTracker: Array[Array[Boolean]] = Array.fill(numberOfFiles)(Array(false))
        var error: Option[Error] = None
        while(error.isEmpty && !isSeenAllPages(pageSeenTracker))
            sightGet(url) match
                case Left(err) => error = Some(err)
                case Right(p) => pages = pages ++ p.pages
                    markSeen(pageSeenTracker, p.pages)
        error.fold(Pages(pages).asRight[Error])(_.asLeft[Pages])

    private def handlePollingUrlStream(url: String, numberOfFiles: Int): StreamResponse = 
        val pageSeenTracker: Array[Array[Boolean]] = Array.fill(numberOfFiles)(Array(false))
        var errors: List[Error] = List()
        def fetch: Either[Error, Seq[Page]] = sightGet(url).map(_.pages)
        LazyList.continually(fetch).takeWhile{
            case Left(e) => errors = e :: errors; !(errors.size > 1)
            case Right(p) => 
                if(!isSeenAllPages(pageSeenTracker) && errors.isEmpty) true.tap(_ => markSeen(pageSeenTracker, p))
                else false             
        }.filter(_.fold(_ => true, fb =  _.nonEmpty))
    
    private def decodePostResponse(response: String): DecodedPostResponse = 
        def decodePollingUrl(r: String): Either[Error, PollingUrl] = 
            decode[PollingUrl](r).left.map(e => DecodingFailure(e.toString))
        decode[RecognizedTexts](response) match 
            case Left(e) => decodePollingUrl(response)
            case Right(rt) => rt.asRight[Error]

    private def sightGet(url: String, retryCount: Int = 3): Either[Error, Pages] = 
        def decodePages(p: String): Either[Error, Pages] = decode[Pages](p).left.map(e => ErrorResponse(e.toString))
        val request = basicRequest.header("Authorization", s"Basic $apiKey").get(uri"$url")
        val response = request.send()
        if(response.isServerError && retryCount > 0) sightGet(url, retryCount - 1)
        else response.body.left.map(ErrorResponse(_)).flatMap(decodePages)
    
    private def sightPost(payload: Payload, retryCount: Int = 3): DecodedPostResponse = 
        val request = basicRequest.body(payload).header("Authorization", s"Basic $apiKey")
                    .post(uri"https://siftrics.com/api/sight/")
        val response = request.send()
        if(response.isServerError && retryCount > 0) sightPost(payload, retryCount - 1)
        else response.body.fold[DecodedPostResponse](ErrorResponse(_).asLeft[PollingUrl | RecognizedTexts], decodePostResponse)
    
    //side effecty function
    private def markSeen(pageSeenTracker: Array[Array[Boolean]], pages: Seq[Page]): Unit = 
        pages.filter(_.pageNumber >= 0).foreach{ page => 
            if(pageSeenTracker(page.fileIndex).size != page.numberOfPagesInFile)
                pageSeenTracker(page.fileIndex) = Array.fill(page.numberOfPagesInFile)(false)
            pageSeenTracker(page.fileIndex)(page.pageNumber - 1) = true
        }
    
    private def isSeenAllPages(pageSeenTracker: Array[Array[Boolean]]): Boolean = 
        pageSeenTracker.forall(_.foldLeft(true)(_ && _))

    private def getPayload(filePaths: Seq[String], shouldWordLevelBoundBoxes: Boolean): Either[Error, Payload] = 
        for 
            filePaths1 <- filePaths.asRight[Nothing]
            mimeTypes <- fileContentReader.fileMimeTypes(filePaths1)
            base64 <- fileContentReader.filesToBase64(filePaths1)
        yield 
            val fileContents = mimeTypes.zip(base64).map((m, b) => FileContent(m,b))
            Payload(shouldWordLevelBoundBoxes, fileContents)
    
    override def recognize(filePaths: Seq[String], shouldWordLevelBoundBoxes: Boolean): Either[Error, Pages] = 
        getPayload(filePaths, shouldWordLevelBoundBoxes) match 
            case Left(error) => Left(error)
            case Right(payload) => 
                sightPost(payload).flatMap{
                    case rt: RecognizedTexts => 
                        val page = Page(error = None, fileIndex = 0, pageNumber = 1, numberOfPagesInFile = 1, recognizedText = rt.recognizedTexts)
                        val pages = Pages(Seq(page))
                        Right(pages)
                    case pu: PollingUrl => 
                        handlePollingUrl(pu.pollingUrl, filePaths.size)
                 }

    override def recognizeStream(filePaths: Seq[String], shouldWordLevelBoundBoxes: Boolean): StreamResponse = 
        def onRecognizedTexts(rt: RecognizedTexts): StreamResponse = 
            val page = Page(error = None, fileIndex = 0, pageNumber = 1, numberOfPagesInFile = 1, recognizedText = rt.recognizedTexts)
            LazyList((Seq(page).asRight[Error]))
        getPayload(filePaths, shouldWordLevelBoundBoxes) match
            case Left(error) => LazyList(Left(error))
            case Right(payload) => 
                sightPost(payload).fold[StreamResponse](e => LazyList(e.asLeft[Seq[Page]]), {
                    case pu: PollingUrl => handlePollingUrlStream(pu.pollingUrl, filePaths.size)
                    case rt: RecognizedTexts => onRecognizedTexts(rt)
                })