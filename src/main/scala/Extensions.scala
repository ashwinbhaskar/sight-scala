package sight.extensions

import sight.models.{Page, Error, Pages}

def (ps: Pages) allText: Seq[String] = ps.pages.map(_.recognizedText.map(_.text)).flatten

def (ps: Pages) allTextWithConfidenceGreaterThan(confidence: Double) = 
    ps.pages.map(_.recognizedText.filter(_.confidence > confidence).map(_.text)).flatten