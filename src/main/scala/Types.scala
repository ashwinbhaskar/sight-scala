package types

import errors.Error
import errors.Error._

opaque type APIKey = String
object APIKey
    def apply(apiKey: String): Either[Error,APIKey] = apiKey.split("-") match
        case Array(f, s, t, fo) if (f.length == 8 && s.length == 4 && t.length == 4 && fo.length == 12) => Right(apiKey)
        case _ => Left(InvalidAPIKeyFormat("Invalid key. Key should be of the form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"))