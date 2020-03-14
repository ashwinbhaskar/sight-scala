import munit.FunSuite
import models.MimeType
import models.MimeType._
import contracts.FileContentReader
import models.Error._
import models.Error
import types.APIKey
import impl.SightClientImpl

class SightClientImplTest extends FunSuite

    private val apiKey: APIKey = APIKey("12345678-1234-1234-123456781234") match
        case Right(apiKey) => apiKey
        case Left(err) => throw new Exception(s"Unexpected error $err")

    def fileContentReaderWith(fileToBase64: Either[Error, Seq[String]], fileMimeType: Either[Error, Seq[MimeType]]) = 
        new FileContentReader with
            def filesToBase64(filePaths: Seq[String]): Either[Error, Seq[String]] = fileToBase64
            def fileMimeTypes(filePaths: Seq[String]): Either[Error, Seq[MimeType]] = fileMimeType

    test("SightClientImpl should return the same error as FileContenReader") {
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
