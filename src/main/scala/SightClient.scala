package sightapi

import types.APIKey
import models.Pages

class SightClient(private val apiKey: APIKey)
    def recognize(filePaths: Seq[String]): Either[Error,Pages] = recognize(filePaths, false)
    def recognize(filePaths: Seq[String], shouldWordLevelBoundBoxes: Boolean): Either[Error, Pages] = ???
