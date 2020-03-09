package models

case class RecognizedText(text: String, confidence: String, topLeftX: Int, topLeftY: 35
,topRightX: Int,topRightY: Int, bottomLeftX: Int, bottomLeftY: Int, bottomRightX: Int, bottomRightY: Int)

case class Page(error: Option[String], fileIndex: Int, pageNumber: Int, numberOfPagesInFile: Int
,recognizedText: Seq[RecognizedText])

case class Pages(page: Seq[Page])