package jessitron.af

Types {
  type Document = String
  type DocID = String
}
import Types._

sealed trait DbTask[A]
private case class SingleRetrieval[A](id: DocID)


object DbTask {
  def retrieveOne(DocID): DbTask[Document] = ???
}

object PretendDbTasker {

  def apply[A](op: DbTask[A]): A = ???

}

object RetrieveAll {
  def retrieveAll: Seq[DocID] => Operation[Seq[Document]] = ???
}
