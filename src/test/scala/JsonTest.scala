import io.circe.{Encoder, Decoder}
import models.{RecognizedText, Page, Pages}
import io.circe.syntax.{given _}
import io.circe.Json
import munit.Location.generate


class JsonTest extends munit.FunSuite

    import givens.{given Encoder[RecognizedText],given Decoder[RecognizedText]}
    test("Should encode and decode recognized text json correctly") {
        val recognizedText = RecognizedText("foo-text", "foo-confidence", 1, 2, 3, 4, 5, 6, 7, 8)
        val expected: Json = Json.obj(
        ("Text", Json.fromString("foo-text")),
        ("Confidence", Json.fromString("foo-confidence")),
        ("TopLeftX", Json.fromInt(1)),
        ("TopLeftY", Json.fromInt(2)),
        ("TopRightX", Json.fromInt(3)),
        ("TopRightY", Json.fromInt(4)),
        ("BottomLeftX", Json.fromInt(5)),
        ("BottomLeftY", Json.fromInt(6)),
        ("BottomRightX", Json.fromInt(7)),
        ("BottomRightY", Json.fromInt(8)))
        val actual: Json = recognizedText.asJson
        assertEquals(actual, expected)

        expected.as[RecognizedText] match 
            case Left(failure) => assertFail(s"failed with $failure")
            case Right(decoded) => assertEquals(decoded, recognizedText)
    }

    import givens.{given Encoder[Page], given Decoder[Page]}
    test("Should encode and decode page json correctly when Error is None") {
        val recognizedText = RecognizedText("foo-text", "foo-confidence", 1, 2, 3, 4, 5, 6, 7, 8)
        val page: Page = Page(None, 0, 1, 3, Seq(recognizedText))
        val expected: Json = Json.obj(
            ("Error", Json.Null),
            ("FileIndex", Json.fromInt(0)),
            ("PageNumber", Json.fromInt(1)),
            ("NumberOfPagesInFile", Json.fromInt(3)),
            ("RecognizedText", page.recognizedText.asJson)
        )
        val actual: Json = page.asJson
        assertEquals(actual, expected)
        expected.as[Page] match
            case Left(failure) => assertFail(s"failed with $failure")
            case Right(decoded) => assertEquals(decoded, page)
    }
    test("Should encode and decode page json correctly when Error is NOT None but IS empty") {
        val recognizedText = RecognizedText("foo-text", "foo-confidence", 1, 2, 3, 4, 5, 6, 7, 8)
        val page: Page = Page(Some(""), 0, 1, 3, Seq(recognizedText))
        val expected: Json = Json.obj(
            ("Error", Json.Null),
            ("FileIndex", Json.fromInt(0)),
            ("PageNumber", Json.fromInt(1)),
            ("NumberOfPagesInFile", Json.fromInt(3)),
            ("RecognizedText", page.recognizedText.asJson)
        )
        val actual: Json = page.asJson
        assertEquals(actual, expected)
        expected.as[Page] match
            case Left(failure) => assertFail(s"failed with $failure")
            case Right(decoded) => assertEquals(decoded, page.copy(error = None))
    }
    test("Should encode and decode page json correctly when Error is NOT empty") {
        val recognizedText = RecognizedText("foo-text", "foo-confidence", 1, 2, 3, 4, 5, 6, 7, 8)
        val page: Page = Page(Some("There was an error"), 0, 1, 3, Seq(recognizedText))
        val expected: Json = Json.obj(
            ("Error", Json.fromString("There was an error")),
            ("FileIndex", Json.fromInt(0)),
            ("PageNumber", Json.fromInt(1)),
            ("NumberOfPagesInFile", Json.fromInt(3)),
            ("RecognizedText", page.recognizedText.asJson)
        )
        val actual: Json = page.asJson
        assertEquals(actual, expected)
        expected.as[Page] match
            case Left(failure) => assertFail(s"failed with $failure")
            case Right(decoded) => assertEquals(decoded, page)
    }
    
    import givens.{given Encoder[Pages], given Decoder[Pages]}
    test("Should encode and decode pages json correctly") {
        val recognizedText = RecognizedText("foo-text", "foo-confidence", 1, 2, 3, 4, 5, 6, 7, 8)
        val page: Page = Page(None, 0, 1, 3, Seq(recognizedText))
        val pages: Pages = Pages(Seq(page))
        
        val expected: Json = Json.obj(
            ("Pages", Seq(page).asJson)
        )
        val actual: Json = pages.asJson
        assertEquals(actual, expected)
        expected.as[Pages] match
            case Left(failure) => assertFail(s"failed with $failure")
            case Right(decoded) => assertEquals(decoded, pages)
    }

    
        

