package models

case class RecognizedText(text: String,  confidence: String,  topLeftX: Int
, topLeftY: Int, topRightX: Int,topRightY: Int
,  bottomLeftX: Int,  bottomLeftY: Int
,  bottomRightX: Int, bottomRightY: Int)

case class RecognizedTexts(recognizedTexts: Seq[RecognizedText])

case class Page(error: Option[String], fileIndex: Int
, pageNumber: Int,  numberOfPagesInFile: Int
, recognizedText: Seq[RecognizedText])

case class Pages(pages: Seq[Page])