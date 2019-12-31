package cfg;

import java.util.Set;

/**
 * @author cary.shi on 2019/12/25
 */
public class CFGGraph {
    // Func对应的CFG graph（简化版）
    private Set<CFGNode> cfgNodeSet;
    private Set<CFGEdge> pureCFGEdgeSet;

    public CFGGraph(Set<CFGNode> cfgNodeSet, Set<CFGEdge> pureCFGEdgeSet) {
        this.cfgNodeSet = cfgNodeSet;
        this.pureCFGEdgeSet = pureCFGEdgeSet;
    }

    public CFGGraph() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CFGNode cfgNode : cfgNodeSet) {
            sb.append(cfgNode).append(",");
        }
        for (CFGEdge cfgEdge : pureCFGEdgeSet) {
            sb.append(cfgEdge).append(",");
        }
        return sb.toString();
    }

    public Set<CFGNode> getCfgNodeSet() {
        return cfgNodeSet;
    }

    public void setCfgNodeSet(Set<CFGNode> cfgSet) {
        this.cfgNodeSet = cfgSet;
    }

    public Set<CFGEdge> getPureCFGEdgeSet() {
        return pureCFGEdgeSet;
    }

    public void setPureCFGEdgeSet(Set<CFGEdge> cfgEdgeSet) {
        this.pureCFGEdgeSet = cfgEdgeSet;
    }
}
