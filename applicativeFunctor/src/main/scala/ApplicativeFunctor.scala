package jessitron.af



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


object DbTask {
  def retrieveOne(id: DocID): DbTask[Document] = SingleRetrieval(id)

  def of[A](thing: A): DbTask[A] = ThisThingRightHere(thing)
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

// todo: move this to different file
object RetrieveAll {
  def retrieveAll: Seq[DocID] => DbTask[Seq[Document]] = { ids =>
    val seqOfOps = ids map DbTask.retrieveOne
    val wrappedEmpty: DbTask[Seq[Document]] = DbTask.of(Seq.empty[Document])
    def wrappedPrepend = DbTask.of((b: Seq[Document]) => (a: Document) => a +: b)

    seqOfOps.foldRight[DbTask[Seq[Document]]](wrappedEmpty){(e, soFar) => e.apply(soFar.apply(wrappedPrepend))}
  }
}
