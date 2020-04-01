import munit.FunSuite
import sight.models.{MimeType,RecognizedText, RecognizedTexts, Page, Pages}
import sight.models.MimeType._
import sight.client.FileContentReader
import sight.models.Error._
import sight.models.Error
import sight.types.APIKey
import sight.client.SightClientImpl
import sttp.client.{SttpBackend, Identity, NothingT, Request, Response, ResponseAs, StringBody, HttpURLConnectionBackend}
import sttp.model.Method.{GET, POST}
import sttp.model.{StatusCode, Header}
import sttp.client.ws.WebSocketResponse
import sttp.client.monad.{IdMonad, MonadError}
import io.circe.{Encoder, Decoder}
import io.circe.syntax.{given _}
import scala.collection.mutable.ListBuffer

class SightClientImplTest extends FunSuite:

    private val postArgs: ListBuffer[(String, String, String)] = ListBuffer() //Url, payload, Auth
    private val getArgs: ListBuffer[(String, String)] = ListBuffer() //url, Auth
    
    override def beforeEach(context: BeforeEach): Unit = 
        postArgs.clear
        getArgs.clear

    given SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
    def withSttpBackend[M](postResponse: Either[String, String], getResponse: Either[String, String]) = new SttpBackend[Identity, Nothing, NothingT]:
        def send[T](request: Request[T, Nothing]): Response[T] = 
            if(request.method == POST)
                val uri = request.productElement(1)
                val body: StringBody = request.productElement(2).asInstanceOf[StringBody]
                val payload = body.productElement(0)
                val headers: Vector[Header] = request.productElement(3).asInstanceOf[Vector[Header]]
                val authorization = headers(2)
                postArgs.append((uri.toString, payload.toString, authorization.toString))
                Response(body = postResponse.asInstanceOf[T],code = StatusCode.Ok)
            else if(request.method == GET)
                val uri = request.productElement(1)
                val headers: Vector[Header] = request.productElement(3).asInstanceOf[Vector[Header]]
                val authorization = headers(1)
                getArgs.append((uri.toString, authorization.toString))
                getResponse match
                    case Left(_) => Response(body = getResponse.asInstanceOf[T], code = StatusCode.Unauthorized)
                    case Right(_) => Response(body = getResponse.asInstanceOf[T], code = StatusCode.Ok)
            else throw Exception(s"Unexpected post method ${request.method}")
        def openWebsocket[T, WS_RESULT](request: Request[T, Nothing], handler: NothingT[WS_RESULT]): Identity[WebSocketResponse[WS_RESULT]] = ???
        def close(): Identity[Unit] = ()
        def responseMonad: MonadError[Identity] = IdMonad

    
    private val apiKey: APIKey = APIKey("12345678-1234-1234-1234-123456781234") match
        case Right(apiKey) => apiKey
        case Left(err) => throw new Exception(s"Unexpected error $err")

    def fileContentReaderWith(fileToBase64: Either[Error, Seq[String]], fileMimeType: Either[Error, Seq[MimeType]]) = 
        new FileContentReader:
            def filesToBase64(filePaths: Seq[String]): Either[Error, Seq[String]] = fileToBase64
            def fileMimeTypes(filePaths: Seq[String]): Either[Error, Seq[MimeType]] = fileMimeType

    test("SightClientImpl should return the same error as FileContenReader - With wordLevelBoundBoxes = false") {
        val fileContentReader1 = fileContentReaderWith(Right(Seq("foo==")), Left(InvalidExtension("invalid extension")))
        val sightClientImpl1 = new SightClientImpl(apiKey, fileContentReader1)
        val filePaths1 = Seq("foo/goo.qwe")
        val expected1 = InvalidExtension("invalid extension")
        sightClientImpl1.recognize(filePaths1) match
            case Left(error) => assertEquals(error, expected1)
            case Right(_) => assertFail("Should not happen as the extension given is invalid")

        val fileContentReader2 = fileContentReaderWith(Left(FileDoesNotExist("file does not exist")), Right(Seq(BMP)))
        val sightClientImpl2 = new SightClientImpl(apiKey, fileContentReader2)
        val filePaths2 = Seq("foo/goo.bmp")
        val expected2 = FileDoesNotExist("file does not exist")
        sightClientImpl2.recognize(filePaths2) match
            case Left(error) => assertEquals(error, expected2)
            case Right(_) => assertFail("Should not happen as the file does not exist")
    }

    test("SightClientImpl should return the same error as FileContenReader - With wordLevelBoundBoxes = true") {
        val fileContentReader1 = fileContentReaderWith(Right(Seq("foo==")), Left(InvalidExtension("invalid extension")))
        val sightClientImpl1 = new SightClientImpl(apiKey, fileContentReader1)
        val filePaths1 = Seq("foo/goo.qwe")
        val expected1 = InvalidExtension("invalid extension")
        sightClientImpl1.recognize(filePaths1) match
            case Left(error) => assertEquals(error, expected1)
            case Right(_) => assertFail("Should not happen as the extension given is invalid")

        val fileContentReader2 = fileContentReaderWith(Left(FileDoesNotExist("file does not exist")), Right(Seq(BMP)))
        val sightClientImpl2 = new SightClientImpl(apiKey, fileContentReader2)
        val filePaths2 = Seq("foo/goo.bmp")
        val expected2 = FileDoesNotExist("file does not exist")
        sightClientImpl2.recognize(filePaths2, true) match
            case Left(error) => assertEquals(error, expected2)
            case Right(_) => assertFail("Should not happen as the file does not exist")
    }

    test("SightClientImpl should make the http call and return expected result when the content is valid - Single Page, One Shot") {
        val fileContentReader = fileContentReaderWith(Right(Seq("foo==")), Right(Seq(BMP)))
        val response: String = """
        {
            "RecognizedText": [
                {
                    "Text": "Invoice",
                    "Confidence": 0.22863210084975458,
                    "TopLeftX": 395,
                    "TopLeftY": 35,
                    "TopRightX": 449,
                    "TopRightY": 35,
                    "BottomLeftX": 395,
                    "BottomLeftY": 47,
                    "BottomRightX": 449,
                    "BottomRightY": 47
                }
            ]
        }
        """
        val expectedResponse = 
            val rt = RecognizedText("Invoice", 0.22863210084975458, 395, 35, 449, 35, 395, 47, 449, 47)
            Pages(Seq(Page(error = None, fileIndex = 0, pageNumber = 1, numberOfPagesInFile = 1, recognizedText = Seq(rt))))
        given  SttpBackend[Identity, Nothing, NothingT] = withSttpBackend(postResponse = Right(response), getResponse = Right("foo"))
        val sightClient = new SightClientImpl(apiKey,fileContentReader)
        val filePaths = Seq("foo/goo.bmp")
        sightClient.recognize(filePaths) match 
            case Right(pages) => assertEquals(pages, expectedResponse)
            case Left(error) => assertFail(s"Unexpected error $error")
        assertEquals(postArgs.size, 1)
        val expectedPayload = """{"makeSentences":false,"files":[{"mimeType":"image/bmp","base64File":"foo=="}]}"""
        val expectedAuth = "Authorization: Basic 12345678-1234-1234-1234-123456781234"
        val expectedUrl = "https://siftrics.com/api/sight/"
        assertEquals(postArgs.head, (expectedUrl, expectedPayload, expectedAuth))
    }

    test("SightClientImpl should make the http call and return expected result when the content is valid - Single Page, Stream") {
        val fileContentReader = fileContentReaderWith(Right(Seq("foo==")), Right(Seq(BMP)))
        val response: String = """
        {
            "RecognizedText": [
                {
                    "Text": "Invoice",
                    "Confidence": 0.22863210084975458,
                    "TopLeftX": 395,
                    "TopLeftY": 35,
                    "TopRightX": 449,
                    "TopRightY": 35,
                    "BottomLeftX": 395,
                    "BottomLeftY": 47,
                    "BottomRightX": 449,
                    "BottomRightY": 47
                }
            ]
        }
        """
        val expectedResponse = 
            val rt = RecognizedText("Invoice", 0.22863210084975458, 395, 35, 449, 35, 395, 47, 449, 47)
            Seq(Page(error = None, fileIndex = 0, pageNumber = 1, numberOfPagesInFile = 1, recognizedText = Seq(rt)))
        given  SttpBackend[Identity, Nothing, NothingT] = withSttpBackend(postResponse = Right(response), getResponse = Right("foo"))
        val sightClient = new SightClientImpl(apiKey,fileContentReader)
        val filePaths = Seq("foo/goo.bmp")
        val actualResponse: LazyList[Either[Error, Seq[Page]]] = sightClient.recognizeStream(filePaths)
        actualResponse.head match 
            case Right(pages) => assertEquals(pages, expectedResponse)
            case Left(error) => assertFail(s"Unexpected error $error")
        assertEquals(actualResponse.force.size, 1)
        assertEquals(postArgs.size, 1)
        val expectedPayload = """{"makeSentences":false,"files":[{"mimeType":"image/bmp","base64File":"foo=="}]}"""
        val expectedAuth = "Authorization: Basic 12345678-1234-1234-1234-123456781234"
        val expectedUrl = "https://siftrics.com/api/sight/"
        assertEquals(postArgs.head, (expectedUrl, expectedPayload, expectedAuth))
    }
    

    test("SightClientImpl should make the http call and return expected result when the content is valid - PollingUrl, One Shot") {
        val fileContentReader = fileContentReaderWith(Right(Seq("foo==")), Right(Seq(BMP)))
        val response: String = """{"PollingURL":"http://foo-polling-url.com"}"""
        val pollingUrlResponse: String = """
        {
        "Pages":[
        {   "Error":"",
            "FileIndex":0,
            "PageNumber":1,
            "NumberOfPagesInFile":2,
            "RecognizedText": [
                {
                    "Text": "Invoice",
                    "Confidence": 0.22863210084975458,
                    "TopLeftX": 395,
                    "TopLeftY": 35,
                    "TopRightX": 449,
                    "TopRightY": 35,
                    "BottomLeftX": 395,
                    "BottomLeftY": 47,
                    "BottomRightX": 449,
                    "BottomRightY": 47
                }
            ]   
        },
        {   "Error":"",
            "FileIndex":0,
            "PageNumber":2,
            "NumberOfPagesInFile":2,
            "RecognizedText": [
                {
                    "Text": "Invoice",
                    "Confidence": 0.22863210084975458,
                    "TopLeftX": 395,
                    "TopLeftY": 35,
                    "TopRightX": 449,
                    "TopRightY": 35,
                    "BottomLeftX": 395,
                    "BottomLeftY": 47,
                    "BottomRightX": 449,
                    "BottomRightY": 47
                }
            ]
        }]}
        """
        val expectedResponse = 
            val rt = RecognizedText("Invoice", 0.22863210084975458, 395, 35, 449, 35, 395, 47, 449, 47)
            Pages(Seq(Page(error = None, fileIndex = 0, pageNumber = 1, numberOfPagesInFile = 2, recognizedText = Seq(rt)),Page(error = None, fileIndex = 0, pageNumber = 2, numberOfPagesInFile = 2, recognizedText = Seq(rt))))
        given  SttpBackend[Identity, Nothing, NothingT] = withSttpBackend(postResponse = Right(response), getResponse = Right(pollingUrlResponse))
        val sightClient = new SightClientImpl(apiKey,fileContentReader)
        val filePaths = Seq("foo/goo.bmp")
        sightClient.recognize(filePaths) match 
            case Right(pages) => assertEquals(pages, expectedResponse)
            case Left(error) => assertFail(s"Unexpected error $error")
        assertEquals(postArgs.size, 1)
        val expectedPayload = """{"makeSentences":false,"files":[{"mimeType":"image/bmp","base64File":"foo=="}]}"""
        val expectedAuth = "Authorization: Basic 12345678-1234-1234-1234-123456781234"
        assertEquals(postArgs.head, ("https://siftrics.com/api/sight/", expectedPayload, expectedAuth))
        assertEquals(getArgs.head, ("http://foo-polling-url.com", expectedAuth))
    }

    test("SightClientImpl should make the http call and return expected result when the content is valid - PollingUrl, Stream") {
        val fileContentReader = fileContentReaderWith(Right(Seq("foo==")), Right(Seq(BMP)))
        val response: String = """{"PollingURL":"http://foo-polling-url.com"}"""
        val pollingUrlResponse: String = """
        {
        "Pages":[
        {   "Error":"",
            "FileIndex":0,
            "PageNumber":1,
            "NumberOfPagesInFile":2,
            "RecognizedText": [
                {
                    "Text": "Invoice",
                    "Confidence": 0.22863210084975458,
                    "TopLeftX": 395,
                    "TopLeftY": 35,
                    "TopRightX": 449,
                    "TopRightY": 35,
                    "BottomLeftX": 395,
                    "BottomLeftY": 47,
                    "BottomRightX": 449,
                    "BottomRightY": 47
                }
            ]   
        },
        {   "Error":"",
            "FileIndex":0,
            "PageNumber":2,
            "NumberOfPagesInFile":2,
            "RecognizedText": [
                {
                    "Text": "Invoice",
                    "Confidence": 0.22863210084975458,
                    "TopLeftX": 395,
                    "TopLeftY": 35,
                    "TopRightX": 449,
                    "TopRightY": 35,
                    "BottomLeftX": 395,
                    "BottomLeftY": 47,
                    "BottomRightX": 449,
                    "BottomRightY": 47
                }
            ]
        }]}
        """
        val expectedResponse = 
            val rt = RecognizedText("Invoice", 0.22863210084975458, 395, 35, 449, 35, 395, 47, 449, 47)
            Seq(Page(error = None, fileIndex = 0, pageNumber = 1, numberOfPagesInFile = 2, recognizedText = Seq(rt)),Page(error = None, fileIndex = 0, pageNumber = 2, numberOfPagesInFile = 2, recognizedText = Seq(rt)))
        given  SttpBackend[Identity, Nothing, NothingT] = withSttpBackend(postResponse = Right(response), getResponse = Right(pollingUrlResponse))
        val sightClient = new SightClientImpl(apiKey,fileContentReader)
        val filePaths = Seq("foo/goo.bmp")
        val actualResponse: LazyList[Either[Error, Seq[Page]]] = sightClient.recognizeStream(filePaths)
        actualResponse.head match 
            case Right(pages) => assertEquals(pages, expectedResponse)
            case Left(error) => assertFail(s"Unexpected error $error")
        assertEquals(actualResponse.force.size, 1)
        assertEquals(postArgs.size, 1)
        val expectedPayload = """{"makeSentences":false,"files":[{"mimeType":"image/bmp","base64File":"foo=="}]}"""
        val expectedAuth = "Authorization: Basic 12345678-1234-1234-1234-123456781234"
        assertEquals(postArgs.head, ("https://siftrics.com/api/sight/", expectedPayload, expectedAuth))
        assertEquals(getArgs.head, ("http://foo-polling-url.com", expectedAuth))
    }

    test("SightClientImpl should return error when call to PollingURL returns error -  Stream") {
        val fileContentReader = fileContentReaderWith(Right(Seq("foo==")), Right(Seq(BMP)))
        val response: String = """{"PollingURL":"http://foo-polling-url.com"}"""
        val pollingUrlResponse: String = """{"message":"Unauthorized"}"""
        val expectedResponse = ErrorResponse("""{"message":"Unauthorized"}""")
        given  SttpBackend[Identity, Nothing, NothingT] = withSttpBackend(postResponse = Right(response), getResponse = Left(pollingUrlResponse))
        val sightClient = new SightClientImpl(apiKey,fileContentReader)
        val filePaths = Seq("foo/goo.bmp")
        val actualResponse: LazyList[Either[Error, Seq[Page]]] = sightClient.recognizeStream(filePaths)
        actualResponse.head match 
            case Right(pages) => assertFail(s"Cannot succeed!")
            case Left(error) => assertEquals(error, expectedResponse)
        assertEquals(actualResponse.force.size, 1)
        assertEquals(postArgs.size, 1)
        val expectedPayload = """{"makeSentences":false,"files":[{"mimeType":"image/bmp","base64File":"foo=="}]}"""
        val expectedAuth = "Authorization: Basic 12345678-1234-1234-1234-123456781234"
        assertEquals(postArgs.head, ("https://siftrics.com/api/sight/", expectedPayload, expectedAuth))
        assertEquals(getArgs.head, ("http://foo-polling-url.com", expectedAuth))
    }
