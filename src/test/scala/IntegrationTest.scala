import munit.FunSuite
import sight.models.{Page, Pages}
import sight.adt.Error
import sight.types.APIKey
import sight.client.SightClient
import sight.extensions._

class IntegrationTest extends FunSuite:

    test("Integration test - One shot") {
        val res = getClass().getClassLoader().getResource("dummy.pdf")
        import java.nio.file._
        val file = Paths.get(res.toURI()).toFile()
        val files = Seq(file.getAbsolutePath())
        val apiKey: Either[Error, APIKey] = APIKey(System.getenv("API_KEY"))
        val result: Either[Error, Pages] = apiKey.flatMap(k => SightClient(k).recognize(files))
        result match
            case Left(e) => fail(s"Unexpected Error $e")
            case Right(p) => assertEquals(p.allText, Seq("Dummy","PDF","file"))
    }

    test("Integration test - Stream") {
        val res = getClass().getClassLoader().getResource("dummy.pdf")
        import java.nio.file._
        val file = Paths.get(res.toURI()).toFile()
        val files = Seq(file.getAbsolutePath())
        val apiKey: Either[Error, APIKey] = APIKey(System.getenv("API_KEY"))
        val result: LazyList[Either[Error, Seq[Page]]] = apiKey match
            case Left(e) => fail(s"unexpected error $e")
            case Right(k) => SightClient(k).recognizeStream(files)
        result.head match
            case Left(e) => fail(s"Unexpected Error $e")
            case Right(p) => assertEquals(Pages(p).allText, Seq("Dummy","PDF","file"))
        assertEquals(result.force.size, 1)
    }