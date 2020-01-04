package ast;

/**
 * @author cary.shi on 2019/12/24
 */
public class ASTEdge {
    private String id;
    private String in;
    private String out;


    public ASTEdge(String id, String in, String out) {
        this.id = id;
        this.in = in;
        this.out = out;
    }

    @Override
    public String toString() {
        return "edges[id=" + this.id +
                "\n,out=" + this.out +
                "\n,in=" + this.in +
                "]\n";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
