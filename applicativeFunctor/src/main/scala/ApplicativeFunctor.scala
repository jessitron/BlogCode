package jessitron.af


sealed trait DbTask[A]
private case class SingleRetrieval(id: DocID) extends DbTask[DocID]


object DbTask {
  def retrieveOne(id: DocID): DbTask[Document] = SingleRetrieval(id)
}

object PretendDbTasker {

  def apply[A](op: DbTask[A]): A = op match {
    case SingleRetrieval(id : DocID) => s"Pretend I retrieved $id"
  }

}

object RetrieveAll {
  def retrieveAll: Seq[DocID] => DbTask[Seq[Document]] = ???
}
