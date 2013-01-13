sealed trait Result[A] {
  def flatMap[B](f : A => Result[B]) : Result[B]
  def map[B](f : A => B): Result[B]
}
case class Success[A](value : A) extends Result[A] {
  def flatMap[B](f : A => Result[B]) = f(value)
  def map[B](f : A => B) = Success(f(value))
}
case class Failure[A](message: String) extends Result[A] {
  def flatMap[B](f : A => Result[B] ) = Failure(message)
  def map[B](f : A => B) = Failure(message)
}


def test(f : String => Result[String]) {
   assert(f("nonexistentFile") == Failure("File does not exist: nonexistentFile"), "nonexistent")

   assert(f("emptyFile") == Failure("File is empty"), "empty")

   assert(f("fileWithInvalidFormat") == Failure("Invalid format"), "invalid format") 

   assert(f("goodFile") == Success("fart"), "good file not parsed")
   println("Four tests passed")
}

object transformers {

  def openFile(n: String) : Result[java.io.File] = {
    val f = new java.io.File(n) // catch IOException
    if (f.exists)
      Success(f)
    else
      Failure("File does not exist: " + n)    
  }

  def readFirstLine (f : java.io.File) : Result[String] = {
    val source = io.Source.fromFile(f)
    try {
      if (!source.hasNext) 
        Failure("File is empty") 
      else 
        Success(source.getLines.next)
    } finally {
      source.close
    }
  }

  def parseLine(line : String) : Result[String] = {
    val ExpectedFormat = "The secret message is '(.*)'".r
    line match {
      case ExpectedFormat(secretMessage) => Success(secretMessage)
      case _ => Failure("Invalid format")
    }
  }
}

def forComprehension(filename:String) : Result[String] = {
  import transformers._
  for( file <- openFile(filename);
       line <- readFirstLine(file);
       result <- parseLine(line))
  yield { result }  
}

test(forComprehension)