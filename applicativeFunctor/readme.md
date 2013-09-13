Applicative functor: you wrap some food in a tortilla, you wrap the
microwave in a tortilla, and you get hot food in a tortilla!
— Jessica Kerr (@jessitron) September 10, 2013

But why would you do that??

Say you have a collection of document IDs and you are going to retrieve
the documents from the database. Not right away, but someday. You are
writing a function that defines the operation of retrieving a sequence
of documents.

So there's such a thing as an DbTask, which says "If you give me a
database, I can do something with it." In our case we wind up with an
DbTask[Seq[Document]], which says, "If you give me a database, I can
give you this collection of documents you want."

If you're wondering why we would abstract the decision of "what to do
with a database" from actually doing stuff with a database, check out
Runar's presentation about purely functional I/O.

Our goal is:

    def retrieveAll: Seq[DocId] => DbTask[Seq[Document]] = ???

How do we implement it? Say we have a primitive operation for retrieving
one document.

    def retrieveOne: DocId => DbTask[Document] = // already implemented by someone else

If we can retrieve one, we can retrieve them all! Let's do that:

    (docIds : Seq[DocId]) => docIds map retrieveOne

That doesn't give us quite what we want. We turned each DocId into a
retrieval DbTask[Document], so we have a sequence of single
operations.

    Seq[DocId] => Seq[DbTask[Document]]

How do we turn that inside out, to get to an DbTask[Seq[Document]]?

To construct a sequence inside an DbTask out of a bunch of
DbTasks, we'll use these pieces:
* each DbTask[Document]
* an empty sequence, Seq()
* a way to add a document into a sequence, which is the +: (prepend)
method on Seq.
* a way to put each of these last two into an DbTask. Call this a
factory method.[1]

    DbTask(x: X) => DbTask[X]

* a way to plug things together within DbTasks. Call this apply. It's a method on DbTask[X] that takes a wrapped function DbTask[X => Y] and spits out an DbTask[Y].

    DbTask[X] { ...
       def apply(f: DbTask[X => Y]) : DbTask[Y]
    }

We have the first three easily enough. The last two together form the
qualifying characteristics for an applicative functor. Applicative
functors let us plug the things inside them together, like building a
ship in a bottle.

Let's use these five pieces to turn our DbTask[Seq[_]] into a
Seq[DbTask[_]]. Fold up the sequence of DbTasks, starting
with an DbTask containing an empty sequence, and then prepending each
item into the sequence.

    def turnInsideOut[X]: Seq[DbTask[X]] => DbTask[Seq[X]] = { seqOfOps =>
        val wrappedEmpty: DbTask[Seq[X]] = DbTask of Seq.empty[X]
        def wrappedPrepend = DbTask of ((b: Seq[X]) => (a: X) => a +: b)

        seqOfOps.foldRight(wrappedEmpty){(e, soFar) =>
           e apply (soFar apply wrappedPrepend)}
    }

Note that wrappedPrepend needs two arguments, and they're applied one at
a time. All of the operations take place within the bottle.

    def retrieveAll = { ids =>
       turnInsideOut( ids map DbTask.retrieveOne)
    }

The point is: applicative functors let you build up what you
want to do, within a context. Think of the context as a bottle, or a
tortilla, whatever you like. In this example, the purpose of that
context is to hold operations on a database for later execution.
The example assembles singles into a sequence. The same ideas can
assemble them into tuples, arrays, etc. Libraries like scalaz abstract
the turnInsideOut functionality so you don't have to write it yourself.

------------
[1] FP people call this X => O[X] "pure" or "return".

