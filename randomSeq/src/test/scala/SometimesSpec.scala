import org.scalatest._
import scala.util.Random

class SometimesSpec extends FunSpec with ShouldMatchers {

  def evenDistribution(n: Int) = Random.shuffle(Range(0,n).map(1.0/n*_))

  describe("doing things sometimes") {
    it("returns true 40% of the time") {
      val notSoRandom = evenDistribution(100).iterator
      val sometimes = new Sometimes(0.4, notSoRandom.next())

      def results = Stream.continually(sometimes.shouldI).take(100)

      results.count(t => t) should equal(40)
    }
  }

}
