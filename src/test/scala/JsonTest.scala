import io.circe.{Encoder, Decoder}
import sight.models.{RecognizedText, Page, Pages, RecognizedTexts, PollingUrl}
import io.circe.syntax.{given _}
import io.circe.Json
import munit.Location.generate
import io.circe.parser.decode


class JsonTest extends munit.FunSuite:


    import sight.decoders.{given Encoder[Pages], given Decoder[Pages], given Encoder[Page]}
    test("Should encode and decode json correctly when error is Empty String") {
        val recognizedText = RecognizedText("foo-text", 0.22863210084975458, 1, 2, 3, 4, 5, 6, 7, 8)
        val page: Page = Page(None, 0, 1, 3, Seq(recognizedText))
        
        val pages: Pages = Pages(Seq(page))
        
        val expected: Json = Json.obj(
            ("Pages", Seq(page).asJson)
        )
        val actual: Json = pages.asJson
        assertEquals(actual, expected)
        val pagesString: String = """
            {"Pages": [{"Error": "",
            "FileIndex":0,
            "PageNumber":1,
            "NumberOfPagesInFile":3,
            "RecognizedText":[{"Text":"foo-text",
                               "Confidence": 0.22863210084975458,
                               "TopLeftX":1,
                               "TopLeftY":2,
                               "TopRightX":3,
                               "TopRightY":4,
                               "BottomLeftX":5,
                               "BottomLeftY":6,
                               "BottomRightX":7,
                               "BottomRightY":8}]}]}
        """
        decode[Pages](pagesString) match
            case Left(failure) => assertFail(s"failed with $failure")
            case Right(decoded) => assertEquals(decoded, pages)
    }

    test("Should encode and decode json correctly when error is NOT an Empty String") {
        val recognizedText = RecognizedText("foo-text", 0.22863210084975458, 1, 2, 3, 4, 5, 6, 7, 8)
        val page: Page = Page(Some("there was an error"), 0, 1, 3, Seq(recognizedText))
        
        val pages: Pages = Pages(Seq(page))
        
        val expected: Json = Json.obj(
            ("Pages", Seq(page).asJson)
        )
        val actual: Json = pages.asJson
        assertEquals(actual, expected)
        val pagesString: String = """
            {"Pages": [{"Error": "there was an error",
            "FileIndex":0,
            "PageNumber":1,
            "NumberOfPagesInFile":3,
            "RecognizedText":[{"Text":"foo-text",
                               "Confidence": 0.22863210084975458,
                               "TopLeftX":1,
                               "TopLeftY":2,
                               "TopRightX":3,
                               "TopRightY":4,
                               "BottomLeftX":5,
                               "BottomLeftY":6,
                               "BottomRightX":7,
                               "BottomRightY":8}]}]}
        """
        decode[Pages](pagesString) match
            case Left(failure) => assertFail(s"failed with $failure")
            case Right(decoded) => assertEquals(decoded, pages)
    }

    import sight.decoders.{given Encoder[PollingUrl], given Decoder[PollingUrl]}
    test("Should encode and decode polling url correctly") {
        val pollingUrl = PollingUrl("http://foo.com")
        val expected: Json = Json.obj(
            ("PollingURL", Json.fromString("http://foo.com")))
        val actual: Json = pollingUrl.asJson
        assertEquals(actual, expected)
        val stringResponse = """
            {"PollingURL":"http://foo.com"}
        """
        decode[PollingUrl](stringResponse) match
            case Left(failure) => assertFail(s"failed with $failure")
            case Right(decoded) => assertEquals(decoded, pollingUrl)
    }
    
        

