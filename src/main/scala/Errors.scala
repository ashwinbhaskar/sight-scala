package errors

enum Error(val msg: String)
    case InvalidAPIKeyFormat(message: String) extends Error(message)