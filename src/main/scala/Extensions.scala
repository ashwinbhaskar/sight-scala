package sight.extensions

import sight.models.{Page, Pages}


extension (ps: Pages)
    def allText: Seq[String] = ps.pages.map(_.recognizedText.map(_.text)).flatten
    def allTextWithConfidenceGreaterThan(confidence: Double) = 
        ps.pages.map(_.recognizedText.filter(_.confidence > confidence).map(_.text)).flatten

extension (bs: Array[Boolean])
    def allTrue: Boolean = bs.foldLeft(true)(_ && _)