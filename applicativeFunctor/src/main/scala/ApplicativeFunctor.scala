package jessitron.af

object RetrieveAll {
  def retrieveAll: Seq[DocID] => DbTask[Seq[Document]] = { ids =>
    val seqOfOps = ids map DbTask.retrieveOne
    val wrappedEmpty: DbTask[Seq[Document]] = DbTask of Seq.empty[Document]
    def wrappedPrepend = DbTask of ((b: Seq[Document]) => (a: Document) => a +: b)

    seqOfOps.foldRight(wrappedEmpty){(e, soFar) => e apply (soFar apply wrappedPrepend)}
  }
}
