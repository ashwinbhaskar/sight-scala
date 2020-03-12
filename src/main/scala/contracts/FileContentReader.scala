package contracts

import models.Error
import models.MimeType

trait FileContentReader
    def filesToBase64(filePaths: Seq[String]): Either[Error, Seq[String]]
    def fileMimeTypes(filePaths: Seq[String]): Either[Error, Seq[MimeType]]