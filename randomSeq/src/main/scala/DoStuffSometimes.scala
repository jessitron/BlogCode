import scala.util.Random

class Sometimes(howOften: Double, rand: => Double = Random.nextDouble) {

   def shouldI : Boolean = rand < howOften

}
