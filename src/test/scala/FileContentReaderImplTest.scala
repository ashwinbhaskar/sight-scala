import sight.client.FileContentReaderImpl
import sight.models.MimeType._
import sight.models.Error.InvalidExtension

class FileContentReaderImplTest extends munit.FunSuite:
    test("Should return correct mime types when valid") {
        val filePaths = Seq("foo/goo.bmp", "baz/de.gif")
        val fileContentReader = new FileContentReaderImpl()
        fileContentReader.fileMimeTypes(filePaths) match 
            case Left(err) => assertFail(s"Failed with $err")
            case Right(mimeTypes) => assertEquals(mimeTypes, Seq(BMP, GIF))
    }

    test("Should return correct error when mime types are not valid") {
        val filePaths = Seq("foo/goo.bmp", "baz/de.gif", "bax/df.nib")
        val fileContentReader = new FileContentReaderImpl()
        fileContentReader.fileMimeTypes(filePaths) match 
            case Left(err) => assertEquals(err, InvalidExtension("nib is not a valid file extension. Only bmp, pdf, gif, jpg, jpeg and png are allowed"))
            case Right(_) => assertFail("Cannot happen")
    }
