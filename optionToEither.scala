

// This doesn't work because Either doesn't implement flatMap. LAME


case class Result(value : String)
case class Failure(message : String)

def test(f : String => Either[Failure, Result]) {
   assert(f("nonexistentFile").left == Failure("File does not exist: nonexistentFile"), "nonexistent")

   assert(f("emptyFile").left == Failure("File is empty"), "empty")

   assert(f("fileWithInvalidFormat").left == Failure("Invalid format"), "invalid format") 

   assert(f("goodFile").right == Result("fart"), "good file not parsed")
   println("Four tests passed")
}

object transformers {

  def openFile(n: String) : Either[Failure, java.io.File] = {
    val f = new java.io.File(n) // catch IOException
    if (f.exists)
      Right(f)
    else
      Left(Failure("File does not exist: " + n))    
  }

  def readFirstLine (f : java.io.File) : Either[Failure, String] = {
    val source = io.Source.fromFile(f)
    try {
      if (!source.hasNext) 
        Left(Failure("File is empty"))
      else 
        Right(source.getLines.next)
    } finally {
      source.close
    }
  }

  def parseLine(line : String) : Either[Failure, Result] = {
    val ExpectedFormat = "The secret message is '(.*)'".r
    line match {
      case ExpectedFormat(secretMessage) => Right(Result(secretMessage))
      case _ => Left(Failure("Invalid format"))
    }
  }
}

def forComprehension(filename:String) : Either[Failure,Result] = {
  import transformers._
  for( file <- openFile(filename);
       line <- readFirstLine(file);
       result <- parseLine(line))
  yield { result }  
}

test(forComprehension)