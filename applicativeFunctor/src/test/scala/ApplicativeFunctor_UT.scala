package jessitron.af

import collection.immutable.Stack
import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class StackSpec extends FlatSpec with ShouldMatchers {

  // The Goal
  "retrieveAll" should "return an operation that retrieves multiple" in {
     val multiRetrievalOperation = RetrieveAll.retrieveAll(Seq("chunky", "bacon"))
     info("whatcha got? " + multiRetrievalOperation)
     PretendDbTasker(multiRetrievalOperation) should be (Seq("Pretend I retrieved chunky","Pretend I retrieved bacon"))
  }

  // tests I wrote as I'm going along
  "A dbtask" should "pretend to retrieve rows from the database" in {

    val singleRetrieval = DbTask.retrieveOne("somedoc")

    PretendDbTasker(singleRetrieval) should be("Pretend I retrieved somedoc")
  }

  "the apply method" should "do that wrapping thing" in {
    val microwave: String => String = (s:String) => s"HOT $s"

    val microwavedFood: DbTask[String] = DbTask.of("food").apply(DbTask.of(microwave))

    PretendDbTasker(microwavedFood) should be("HOT food")
  }

  "the apply method" should "do that wrapping thing in the right order" in {
    val microwave: String => String = (s:String) => s"HOT $s"
    val oven: String => String = (s:String) => s"very $s"

    val microwavedFood: DbTask[String] = DbTask.of("food").apply(DbTask.of(microwave))
    val ovenned: DbTask[String] = microwavedFood.apply(DbTask.of(oven))

    PretendDbTasker(ovenned) should be("very HOT food")
  }


}

