import org.scalatest._

class SometimesSpec extends FunSpec with ShouldMatchers {


  describe("doing things sometimes") {
    it("returns true 40% of the time") {
      val sometimes = new Sometimes(0.4)

      def results = Stream.continually(sometimes.shouldI).take(100)

      results.count(t => t) should equal(40)
    }
  }

}
