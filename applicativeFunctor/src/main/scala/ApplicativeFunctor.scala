package jessitron.af

object RetrieveAll {
  def retrieveAll: Seq[DocID] => DbTask[Seq[Document]] = { ids =>
    val seqOfOps = ids map DbTask.retrieveOne
    turnInsideOut(seqOfOps)
  }

  private def turnInsideOut[X]: Seq[DbTask[X]] => DbTask[Seq[X]] = { seqOfOps =>
    val wrappedEmpty: DbTask[Seq[X]] = DbTask of Seq.empty[X]
    def wrappedPrepend = DbTask of ((b: Seq[X]) => (a: X) => a +: b)

    seqOfOps.foldRight(wrappedEmpty){(e, soFar) => e apply (soFar apply wrappedPrepend)}
  }
}
