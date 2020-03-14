package sight.client

import sight.models.{MimeType, Error}
import sight.models.Error.FileDoesNotExist
import cats.implicits.{given _}
import scala.language.implicitConversions
import org.apache.commons.codec.binary.Base64
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ListBuffer

class FileContentReaderImpl extends FileContentReader
    def filesToBase64(filePaths: Seq[String]): Either[Error, Seq[String]] = 
        val base64s = ListBuffer[String]()
        var error: Option[Error] = None
        filePaths.foreach{ path =>
            try
                val byteArray = Files.readAllBytes(Paths.get(path))
                val b64 = Base64.encodeBase64(byteArray)
                base64s.append(String(b64))
            catch 
                case e: Exception => error = Some(FileDoesNotExist(e.toString))
        }
        error match
            case None => base64s.toList.asRight[Error]
            case Some(e) => e.asLeft[Seq[String]]


    def fileMimeTypes(filePaths: Seq[String]): Either[Error, Seq[MimeType]] = 
        val extensions = filePaths.map(_.split("\\.").last)
        val r: Seq[Either[Error, MimeType]] = extensions.map(MimeType.fromExtension)
        r.find(_.isLeft) match
            case Some(e) => e.map(Seq(_))
            case None => 
                val s = r.foldLeft(Seq[MimeType]())((acc, e) => e.fold[Seq[MimeType]](fa = _ => throw new Exception("not possible"), fb = acc :+ _))
                s.asRight[Error]