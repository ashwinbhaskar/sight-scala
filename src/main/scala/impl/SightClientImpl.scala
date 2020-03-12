package impl

import types.APIKey
import models.{Pages, MimeType}
import contracts.{FileContentReader, SightClient}
import models.Error

class SightClientImpl(private val apiKey: APIKey, private val fileContentReader: FileContentReader) extends SightClient(apiKey, fileContentReader)
    private case class FileContent(mimeType: MimeType, base64FileContent: String)
    private case class Payload(shouldMakeSentences: Boolean, files: Seq[FileContent])

    override def recognize(filePaths: Seq[String], shouldWordLevelBoundBoxes: Boolean): Either[Error, Pages] = 
        val payload: Either[Error, Payload] =  
        for 
            filePaths1 <- Right(filePaths): Either[Nothing, Seq[String]]
            mimeTypes <- fileContentReader.fileMimeTypes(filePaths1)
            base64 <- fileContentReader.filesToBase64(filePaths1)
        yield 
            val fileContents = mimeTypes.zip(base64).map((m, b) => FileContent(m,b))
            Payload(shouldWordLevelBoundBoxes, fileContents)
        val a: Pages = ???
        Right(a)
        
        





