package sight.models

enum Error(val msg: String)
    case InvalidAPIKeyFormat(message: String) extends Error(message)
    case InvalidExtension(message: String) extends Error(message)
    case FileDoesNotExist(message: String) extends Error(message)
    case NoExtensionGiven(message: String) extends Error(message)
    case DecodingFailure(message: String) extends Error(message)
    case ErrorResponse(message: String) extends Error(message)