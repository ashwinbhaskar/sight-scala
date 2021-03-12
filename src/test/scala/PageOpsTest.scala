import sight.extensions._
import sight.models.{RecognizedText, Pages, Page}

class PageOpsTest extends munit.FunSuite:

    test("method allText") {
        val rt1 = RecognizedText("foo", 0.22863210084975458, 1, 2, 3, 4, 5, 6, 7, 8)    
        val rt2 = RecognizedText("baz", 0.22863210084975458, 1, 2, 3, 4, 5, 6, 7, 8)    
        val rt3 = RecognizedText("quxx", 0.22863210084975458, 1, 2, 3, 4, 5, 6, 7, 8)
        val pages = Pages(Seq(Page(error = None, fileIndex = 0, pageNumber = 1, numberOfPagesInFile = 2, recognizedText = Seq(rt1, rt2))
        ,Page(error = None, fileIndex = 0, pageNumber = 2, numberOfPagesInFile = 2, recognizedText = Seq(rt3))))
        assertEquals(pages.allText, Seq("foo", "baz", "quxx"))
    }

    test("method allTextWithConfidenceGreaterThan") {
        val rt1 = RecognizedText("foo", 0.23863210084975458, 1, 2, 3, 4, 5, 6, 7, 8)    
        val rt2 = RecognizedText("baz", 0.20863210084975458, 1, 2, 3, 4, 5, 6, 7, 8)    
        val rt3 = RecognizedText("quxx", 0.19863210084975458, 1, 2, 3, 4, 5, 6, 7, 8)
        val pages = Pages(Seq(Page(error = None, fileIndex = 0, pageNumber = 1, numberOfPagesInFile = 2, recognizedText = Seq(rt1, rt2))
        ,Page(error = None, fileIndex = 0, pageNumber = 2, numberOfPagesInFile = 2, recognizedText = Seq(rt3))))
        assertEquals(pages.allTextWithConfidenceGreaterThan(0.20), Seq("foo", "baz"))
    }