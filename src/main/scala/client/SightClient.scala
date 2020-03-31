package sight.client

import sight.types.APIKey
import sight.models.{Pages, Error, Page}
import sttp.client.{SttpBackend, HttpURLConnectionBackend, Identity, NothingT}
import scala.collection.immutable.LazyList

trait SightClient(private val apiKey: APIKey, private val fileContentReader: FileContentReader):
    type LazyResponse = LazyList[Either[Error, Seq[Page]]] | Error
    def recognize(filePaths: Seq[String]): Either[Error,Pages] = recognize(filePaths, false)
    def recognize(filePaths: Seq[String], shouldWordLevelBoundBoxes: Boolean): Either[Error, Pages]
    def recognizeStream(filePaths: Seq[String]): LazyResponse = recognizeStream(filePaths, false)
    def recognizeStream(filePaths: Seq[String], shouldWorlLevelBoundBoxes: Boolean): LazyResponse

object SightClient:
    def apply(apiKey: APIKey): SightClient = 
        given SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
        val fileContentReader = new FileContentReaderImpl()
        new SightClientImpl(apiKey, fileContentReader)
