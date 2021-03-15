import sight.Types.APIKey
import sight.adt.Error.InvalidAPIKeyFormat
import sight.adt.Error
import cats.implicits._
import scala.language.implicitConversions

class APIKeyTest extends munit.FunSuite:

    test("Should return error when the format of the key is wrong") {
        val expected = InvalidAPIKeyFormat("Invalid key. Key should be of the form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx").asLeft[APIKey]
        assertEquals(APIKey("foo"), expected)
        assertEquals(APIKey("1234-4568"), expected)
        assertEquals(APIKey("12345678-4568-1234-123456781234"), expected)
    }
    test("Should return APIKey when the format is correct") {
        APIKey("12345678-1234-1234-1234-123456781234") match
            case Right(_) => assert(true)
            case Left(e) => fail(s"failed with $e")
    }