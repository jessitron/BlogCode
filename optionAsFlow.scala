case class SecretMessage(meaning: String) 


def test(f : String => Option[SecretMessage]) {
   assert(f("nonexistentFile") == None, "nonexistent")

   assert(f("emptyFile") == None, "empty")

   assert(f("fileWithInvalidFormat") == None, "invalid format") 

   assert(f("goodFile") == Some(SecretMessage("fart")), "good file not parsed")
   println("Four tests passed")
}

def imperativeStyle(filename : String) : Option[SecretMessage] = {
  val file = new java.io.File(filename)
  if(!file.exists)
    None
  else {
    val source = io.Source.fromFile(file)
    if (!source.hasNext) {
      source.close
      None
    }
    else {
      val firstLine = source.getLines.next
      source.close
      val ExpectedFormat = "The secret message is '(.*)'".r
      firstLine match {
        case ExpectedFormat(secretMessage) => Some(SecretMessage(secretMessage))
        case _ => None
      }
    }
  }
}

object transformers {

  def openFile(n: String) : Option[java.io.File] = {
    val f = new java.io.File(n) // catch IOException
    if (f.exists)
      Some(f)
    else
      None    
  }

  def readFirstLine (f : java.io.File) : Option[String] = {
    val source = io.Source.fromFile(f)
    try {
      if (!source.hasNext) 
        None 
      else 
        Some(source.getLines.next)
    } finally {
      source.close
    }
  }

  def parseLine(line : String) : Option[SecretMessage] = {
    val ExpectedFormat = "The secret message is '(.*)'".r
    line match {
      case ExpectedFormat(secretMessage) => Some(SecretMessage(secretMessage))
      case _ => None
    }
  }
}

def useTransforms(filename: String) : Option[SecretMessage] = {
  import transformers._
  val fileOption = openFile(filename)
  if (fileOption.isEmpty) 
    None
  else {
    val lineOption = readFirstLine(fileOption.get)
    if (lineOption.isEmpty) 
      None
    else {
      parseLine(lineOption.get)
    }
  }    
}
   
def patternMatching(filename:String) : Option[SecretMessage] = {
  import transformers._
  openFile(filename) match {
    case None => None
    case Some(file) => 
      readFirstLine(file) match {
        case None => None
        case Some(line) =>
          parseLine(line)
     } 
  }
}

def chainOfMaps(filename:String) : Option[SecretMessage] = {
  import transformers._
  openFile(filename).flatMap(readFirstLine).flatMap(parseLine)
}

def forComprehension(filename:String) : Option[SecretMessage] = {
  import transformers._
  for( file <- openFile(filename);
       line <- readFirstLine(file);
       secretMessage <- parseLine(line))
  yield { secretMessage }  
}

test(imperativeStyle)
test(useTransforms)
test(patternMatching)
test(chainOfMaps)
test(forComprehension)