package sight.decoders

import io.circe.Decoder
import io.circe.Decoder.Result
import sight.models.{RecognizedText, Page, Pages, RecognizedTexts, PollingUrl}
import io.circe.HCursor
import io.circe.Encoder
import io.circe.Decoder
import io.circe.Json
import io.circe.syntax._

given Decoder[RecognizedText]:
    def apply(c: HCursor): Result[RecognizedText] = 
        for {
            text <- c.get[String]("Text")
            confidence <- c.get[Double]("Confidence")
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

given Encoder[RecognizedText]:
    def apply(rt: RecognizedText): Json = Json.obj(
        ("Text", Json.fromString(rt.text)),
        ("Confidence", Json.fromDouble(rt.confidence).get),
        ("TopLeftX", Json.fromInt(rt.topLeftX)),
        ("TopRightX", Json.fromInt(rt.topRightX)),
        ("TopRightY", Json.fromInt(rt.topRightY)),
        ("TopLeftY", Json.fromInt(rt.topLeftY)),
        ("BottomLeftX", Json.fromInt(rt.bottomLeftX)),
        ("BottomLeftY", Json.fromInt(rt.bottomLeftY)),
        ("BottomRightX", Json.fromInt(rt.bottomRightX)),
        ("BottomRightY", Json.fromInt(rt.bottomRightY)))
    
given Decoder[RecognizedTexts]:
    def apply(c: HCursor): Result[RecognizedTexts] = 
        c.get[Seq[RecognizedText]]("RecognizedText").map(RecognizedTexts)

given Encoder[RecognizedTexts]:
    def apply(rts: RecognizedTexts): Json = Json.obj(
        ("RecognizedText", rts.recognizedTexts.asJson))
given Decoder[Page]:
    def apply(c: HCursor): Result[Page] =
        for {
            error <- c.get[Option[String]]("Error")
            mappedError <- Right(error.flatMap(a => if(a.isEmpty) None else Some(a)))
            fileIndex <- c.get[Int]("FileIndex")
            pageNumber <- c.get[Int]("PageNumber")
            numberOfPagesInFile <- c.get[Int]("NumberOfPagesInFile")
            recognizedText <- c.get[Seq[RecognizedText]]("RecognizedText") 
        } yield
            val sanitizedError = error.flatMap((e: String) => if(e.isEmpty) None else Some(e))
            Page(sanitizedError, fileIndex, pageNumber, numberOfPagesInFile, recognizedText)

given Encoder[Page]:
    def apply(p: Page): Json = Json.obj(
        ("Error", if(p.error.isEmpty || p.error.get.isEmpty) Json.Null else Json.fromString(p.error.get)),
        ("FileIndex", Json.fromInt(p.fileIndex)),
        ("PageNumber", Json.fromInt(p.pageNumber)),
        ("NumberOfPagesInFile", Json.fromInt(p.numberOfPagesInFile)),
        ("RecognizedText", p.recognizedText.asJson))

given Decoder[Pages]:
    def apply(c: HCursor): Result[Pages] = 
        c.get[Seq[Page]]("Pages").map(Pages)

given Encoder[Pages]:
    def apply(ps: Pages): Json = Json.obj(
        ("Pages", ps.pages.asJson))

given Decoder[PollingUrl]:
    def apply(c: HCursor): Result[PollingUrl] = 
        c.get[String]("PollingURL").map(PollingUrl)

given Encoder[PollingUrl]:
    def apply(pu: PollingUrl): Json = Json.obj(
        ("PollingURL", Json.fromString(pu.pollingUrl)))
