package models

// import io.circe.generic.extras._
import io.circe.Codec
case class RecognizedText(text: String,  confidence: String,  topLeftX: Int
, topLeftY: Int, topRightX: Int,topRightY: Int
,  bottomLeftX: Int,  bottomLeftY: Int
,  bottomRightX: Int, bottomRightY: Int)

case class Page(error: Option[String], fileIndex: Int
, pageNumber: Int,  numberOfPagesInFile: Int
, recognizedText: Seq[RecognizedText])

case class Pages(pages: Seq[Page])