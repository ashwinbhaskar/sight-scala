package givens

import io.circe.Decoder
import io.circe.Decoder.Result
import models.RecognizedText
import io.circe.HCursor
import io.circe.Encoder
import io.circe.Decoder
import io.circe.Json

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


    