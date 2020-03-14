package sight.client

import sight.types.APIKey
import sight.models.{Pages, Error}

trait SightClient(private val apiKey: APIKey, private val fileContentReader: FileContentReader)
    def recognize(filePaths: Seq[String]): Either[Error,Pages] = recognize(filePaths, false)
    def recognize(filePaths: Seq[String], shouldWordLevelBoundBoxes: Boolean): Either[Error, Pages]