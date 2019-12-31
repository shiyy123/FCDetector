import java.io.{PrintWriter, File => JFile}


loadCpg("a.bin.zip")

val writer = new PrintWriter(new JFile("a.txt"))

val methods = cpg.method.l

methods.foreach { method =>
    System.out.println(method.Edges.In.l)
    method.Edges.In.l
    
}

writer.close()