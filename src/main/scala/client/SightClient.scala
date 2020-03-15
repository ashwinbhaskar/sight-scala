package sight.client

import sight.types.APIKey
import sight.models.{Pages, Error}
import sttp.client.{SttpBackend, HttpURLConnectionBackend, Identity, NothingT}

trait SightClient(private val apiKey: APIKey, private val fileContentReader: FileContentReader)
    def recognize(filePaths: Seq[String]): Either[Error,Pages] = recognize(filePaths, false)
    def recognize(filePaths: Seq[String], shouldWordLevelBoundBoxes: Boolean): Either[Error, Pages]

object SightClient
    def apply(apiKey: APIKey): SightClient = 
        given SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
        val fileContentReader = new FileContentReaderImpl()
        new SightClientImpl(apiKey, fileContentReader)