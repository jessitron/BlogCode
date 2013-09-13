package jessitron.af

object DbTask {
  def retrieveOne(id: DocID): DbTask[Document] = SingleRetrieval(id)
  def of[A](thing: A): DbTask[A] = ThisThingRightHere(thing)
}

sealed trait DbTask[A] {
  def apply[B](op: DbTask[A => B]): DbTask[B] = AndThen(this, op)
}

private case class SingleRetrieval(id: DocID) extends DbTask[DocID]
private case class ThisThingRightHere[A](it: A) extends DbTask[A]

private trait RecursiveDbTask[A] extends DbTask[A] {
  def evaluate(how: DbTasker): A
}

private case class AndThen[A,B](start: DbTask[A], op: DbTask[A => B]) extends RecursiveDbTask[B] {
  def evaluate(how: DbTasker): B = {
    val microwave = how(op)
    val food = how(start)
    microwave(food)
  }
}

trait DbTasker {
  def apply[A](op: DbTask[A]): A
}

object PretendDbTasker extends DbTasker {

  def apply[A](op: DbTask[A]): A = op match {
    case r: RecursiveDbTask[A] => r.evaluate(this)
    case SingleRetrieval(id : DocID) => s"Pretend I retrieved $id"
    case ThisThingRightHere(it) => it
  }

}

