package cfg;

import config.CFGConfig;

/**
 * @author cary.shi on 2019/12/8
 */
public class CFGEdge {
    private String id;
    private String in;
    private String out;

    // in && out 是节点的id
    public CFGEdge(String id, String in, String out) {
        this.id = id;
        this.in = in;
        this.out = out;
    }

    public CFGEdge(String in, String out) {
        this.in = in;
        this.out = out;
    }

    public boolean isCall() {
        return in.contains(CFGConfig.CALL_NODE) && out.contains(CFGConfig.CALL_NODE);
    }

    @Override
    public String toString() {
        return "Edge id=" + id + System.getProperty("line.separator") +
                "Out=" + out + System.getProperty("line.separator") +
                "In=" + in + System.getProperty("line.separator");
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
