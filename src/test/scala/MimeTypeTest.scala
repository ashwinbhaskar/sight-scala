import sight.adt.MimeType._
import sight.adt.MimeType
import sight.adt.Error.InvalidExtension


class MimeTypeTest extends munit.FunSuite:

    test("fromExtension should return the correct MimeType") {
        def forceRight(str: String): MimeType = MimeType.fromExtension(str) match
            case Right(m) => m
            case Left(e) => throw new Exception(e.toString)
        assertEquals(forceRight("Bmp"), BMP)
        assertEquals(forceRight("gif"), GIF)
        assertEquals(forceRight("jpeg"), JPEG)
        assertEquals(forceRight("JPG"), JPG)
        assertEquals(forceRight("pdf"), PDF)
        assertEquals(forceRight("Png"), PNG)
    }

    test("fromExtension should return an error when an invalid extension is given") {
        MimeType.fromExtension("foo") match 
            case Left(e) => assertEquals(e, InvalidExtension("foo is not a valid file extension. Only bmp, pdf, gif, jpg, jpeg and png are allowed"))
            case Right(_) => fail("Should not happen as the extension is invalid")
    }