package sight.adt

import Error.InvalidExtension

enum MimeType(val strRep: String):
    case GIF extends MimeType("image/gif")
    case BMP extends MimeType("image/bmp")
    case PDF extends MimeType("application/pdf")
    case JPG extends MimeType("image/jpg")
    case JPEG extends MimeType("image/jpeg")
    case PNG extends MimeType("image/png")

object MimeType:
    def fromExtension(ext: String): Either[Error, MimeType] = ext.toLowerCase match
        case "gif" => Right(GIF)
        case "bmp" => Right(BMP)
        case "pdf" => Right(PDF)
        case "png" => Right(PNG)
        case "jpg" => Right(JPG)
        case "jpeg" => Right(JPEG)
        case other => Left(InvalidExtension(s"$other is not a valid file extension. Only bmp, pdf, gif, jpg, jpeg and png are allowed"))
