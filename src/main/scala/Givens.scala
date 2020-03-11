package givens

import io.circe.Decoder
import io.circe.Decoder.Result
import models.{RecognizedText, Page, Pages}
import io.circe.HCursor
import io.circe.Encoder
import io.circe.Decoder
import io.circe.Json
import io.circe.syntax._

given Decoder[RecognizedText] 
    def apply(c: HCursor): Result[RecognizedText] = 
        for {
            text <- c.get[String]("Text")
            confidence <- c.get[String]("Confidence")
            topLeftX <- c.get[Int]("TopLeftX")
            topLeftY <- c.get[Int]("TopLeftY")
            topRightX <- c.get[Int]("TopRightX")
            topRightY <- c.get[Int]("TopRightY")
            bottomLeftX <- c.get[Int]("BottomLeftX")
            bottomRightX <- c.get[Int]("BottomRightX")
            bottomLeftY <- c.get[Int]("BottomLeftY")
            bottomRightY <- c.get[Int]("BottomRightY")
        } yield
            RecognizedText(text, confidence, topLeftX, topLeftY, topRightX, topRightY, bottomLeftX
            ,bottomLeftY, bottomRightX, bottomRightY)

given Encoder[RecognizedText]
    def apply(rt: RecognizedText): Json = Json.obj(
        ("Text", Json.fromString(rt.text)),
        ("Confidence", Json.fromString(rt.confidence)),
        ("TopLeftX", Json.fromInt(rt.topLeftX)),
        ("TopRightX", Json.fromInt(rt.topRightX)),
        ("TopRightY", Json.fromInt(rt.topRightY)),
        ("TopLeftY", Json.fromInt(rt.topLeftY)),
        ("BottomLeftX", Json.fromInt(rt.bottomLeftX)),
        ("BottomLeftY", Json.fromInt(rt.bottomLeftY)),
        ("BottomRightX", Json.fromInt(rt.bottomRightX)),
        ("BottomRightY", Json.fromInt(rt.bottomRightY)))

given Decoder[Page]
    def apply(c: HCursor): Result[Page] =
        for {
            error <- c.get[Option[String]]("Error")
            mappedError <- Right(error.flatMap(a => if(a.isEmpty) None else Some(a)))
            fileIndex <- c.get[Int]("FileIndex")
            pageNumber <- c.get[Int]("PageNumber")
            numberOfPagesInFile <- c.get[Int]("NumberOfPagesInFile")
            recognizedText <- c.get[Seq[RecognizedText]]("RecognizedText") 
        } yield 
            Page(error, fileIndex, pageNumber, numberOfPagesInFile, recognizedText)

given Encoder[Page]
    def apply(p: Page): Json = Json.obj(
        ("Error", if(p.error.isEmpty || p.error.get.isEmpty) Json.Null else Json.fromString(p.error.get)),
        ("FileIndex", Json.fromInt(p.fileIndex)),
        ("PageNumber", Json.fromInt(p.pageNumber)),
        ("NumberOfPagesInFile", Json.fromInt(p.numberOfPagesInFile)),
        ("RecognizedText", p.recognizedText.asJson)
    )

given Decoder[Pages]
    def apply(c: HCursor): Result[Pages] = 
        for {
            pages <- c.get[Seq[Page]]("Pages")
        } yield
            Pages(pages)

given Encoder[Pages]
    def apply(ps: Pages): Json = Json.obj(
        ("Pages", ps.pages.asJson)
    )
