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

import java.io.{PrintWriter, File => JFile}
import scala.collection.JavaConverters._

import gremlin.scala._
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.VertexProperty

@main def exec(cpgFile: String, outFile: String) = {
  loadCpg(cpgFile)
  val writer = new PrintWriter(new JFile(outFile))

  cpg.method.internal.l.map { caller =>
    val callerLocation = caller.location.filename
    val callerName = caller.fullName
    val callerLineNumStart = caller.lineNumber
    val callerLineNumEnd = caller.lineNumberEnd
    val callees = caller.out(EdgeTypes.CONTAINS).hasLabel(NodeTypes.CALL).cast[nodes.Call].l
    
    callees.foreach { callee =>
      val calleeLocation = callee.location.filename
      val calleeName = callee.name
      val calleeLineNum = callee.lineNumber
      val calleeColumnNumber = callee.columnNumber
      val calleeArgumentIndex = callee.argumentIndex
      val calleeCode = callee.code

      writer.write(callerLocation.toString + "\n")
      writer.write(callerName.toString + "\n")
      writer.write(callerLineNumStart.toString + "\n")
      writer.write(callerLineNumEnd.toString + "\n")

      writer.write(calleeLocation.toString + "\n")
      writer.write(calleeName.toString + "\n")
      writer.write(calleeArgumentIndex.toString + "\n")
      writer.write(calleeLineNum.toString + "\n")
      writer.write(calleeColumnNumber.toString + "\n")
      writer.write(calleeCode.toString + "\n")
      writer.write("------\n")
    }
  }
  writer.close()
}
