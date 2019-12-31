/* list-funcs.scala

   This script simply returns a list of the names as String of all methods contained in the currently loaded CPG.

   Input: A valid CPG
   Output: scala.List[String]

   Running the Script
   ------------------
   see: README.md

   Sample Output
   -------------
   List("<operator>.indirectMemberAccess", "<operator>.assignment", "free_list", "free", "<operator>.notEquals")
 */

import java.io._
import scala.collection.JavaConverters._

import gremlin.scala._
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.VertexProperty

@main def exec(cpgFile: String, outFile: String) = {
    loadCpg(cpgFile)
    val writer = new PrintWriter(new java.io.File(outFile))

    cpg.method.internal.l.groupBy(_.location.filename).foreach { x=>
        // writer.write("file:")
        writer.write(x._1)
        writer.write("\n")
        x._2.foreach { y=>
            val name = y.fullName
            val startLineNumber = y.lineNumber
            val endLineNumber = y.lineNumberEnd
            val signature = y.signature
            // val id = y.id

            // writer.write(id.toString + "\n")
            writer.write(name.toString + "\n")
            writer.write(startLineNumber.toString + "\n")
            writer.write(endLineNumber.toString + "\n")
            writer.write(signature.toString + "\n")
        }
        writer.write("------\n")
    }
    writer.close()
}