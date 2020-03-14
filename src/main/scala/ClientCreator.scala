package sight

import sight.types.APIKey
import sight.client.{SightClient, SightClientImpl, FileContentReaderImpl}
import sttp.client.{SttpBackend, HttpURLConnectionBackend, Identity, NothingT}

def newClient(apiKey: APIKey): SightClient = 
    given SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
    val fileContentReader = new FileContentReaderImpl()
    new SightClientImpl(apiKey, fileContentReader)