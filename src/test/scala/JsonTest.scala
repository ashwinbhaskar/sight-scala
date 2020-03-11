import io.circe.{Encoder, Decoder}
import models.RecognizedText
import givens.{given Encoder[RecognizedText],given Decoder[RecognizedText]}
import io.circe.syntax.{given _}
import io.circe.Json
import munit.Location.generate


class JsonTest extends munit.FunSuite
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
        
