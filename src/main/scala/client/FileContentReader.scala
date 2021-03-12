package sight.client

import sight.adt.Error
import sight.adt.MimeType

trait FileContentReader:
    def filesToBase64(filePaths: Seq[String]): Either[Error, Seq[String]]
    def fileMimeTypes(filePaths: Seq[String]): Either[Error, Seq[MimeType]]
