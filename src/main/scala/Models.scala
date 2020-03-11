package models

// import io.circe.generic.extras._
import io.circe.Codec
// @ConfiguredJsonCodec
// case class RecognizedText(text: String, @JsonKey("Confidence") confidence: String, @JsonKey("TopLeftX") topLeftX: Int
// , @JsonKey("TopLeftY") topLeftY: Int, @JsonKey("TopRightX") topRightX: Int, @JsonKey("TopRightY") topRightY: Int
// , @JsonKey("BottomLeftX") bottomLeftX: Int, @JsonKey("BottomLeftY") bottomLeftY: Int
// , @JsonKey("BottomRightX") bottomRightX: Int,@JsonKey("BottomRightY") bottomRightY: Int) derives Codec.AsObject

// case class Page(@JsonKey("Error") error: Option[String],@JsonKey("FileIndex") fileIndex: Int
// , @JsonKey("PageNumber") pageNumber: Int, @JsonKey("NumberOfPagesInFile") numberOfPagesInFile: Int
// , @JsonKey("RecognizedText") recognizedText: Seq[RecognizedText])

// case class Pages(@JsonKey("Pages") pages: Seq[Page])


case class RecognizedText(text: String,  confidence: String,  topLeftX: Int
, topLeftY: Int, topRightX: Int,topRightY: Int
,  bottomLeftX: Int,  bottomLeftY: Int
,  bottomRightX: Int, bottomRightY: Int)

case class Page(error: Option[String], fileIndex: Int
, pageNumber: Int,  numberOfPagesInFile: Int
, recognizedText: Seq[RecognizedText])

case class Pages(pages: Seq[Page])