package sight.client

import sight.models.Error
import sight.models.MimeType

trait FileContentReader
    def filesToBase64(filePaths: Seq[String]): Either[Error, Seq[String]]
    def fileMimeTypes(filePaths: Seq[String]): Either[Error, Seq[MimeType]]