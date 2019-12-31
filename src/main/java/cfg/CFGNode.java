package cfg;

import config.CFGConfig;

import java.util.List;
import java.util.Map;

/**
 * @author cary.shi on 2019/12/24
 */
public class CFGNode {
    private String id;
    private List<CFGEdge> edges;//与该node有关的边，入和出
    private Map<String, String> propertyMap;

    public CFGNode(String id, List<CFGEdge> edges, Map<String, String> propertyMap) {
        this.id = id;
        this.edges = edges;
        this.propertyMap = propertyMap;
    }

    public boolean isCallNode() {
        return this.id.contains(CFGConfig.CALL_NODE);
    }

    public boolean isBlockNode() {
        return this.id.contains(CFGConfig.BLOCK_NODE);
    }

    public boolean isIdentifierNode() {
        return this.id.contains(CFGConfig.IDENTIFIER_NODE);
    }

    public boolean isLiteralNode() {
        return this.id.contains(CFGConfig.LITERAL_NODE);
    }

    public boolean isReturnNode() {
        return this.id.contains(CFGConfig.RETURN_NODE);
    }

    public boolean isMethodReturnNode() {
        return this.id.contains(CFGConfig.METHOD_RETURN);
    }

    public boolean isMethodNode() {
        return !this.id.contains(CFGConfig.METHOD_RETURN) && this.id.contains(CFGConfig.METHOD);
    }

    public boolean isUnknownNode() {
        return this.id.contains(CFGConfig.UNKNOWN_NODE);
    }

    @Override
    public String toString() {
        return "cfg node id=" + id + System.getProperty("line.separator") +
                "edges=" + edges.toString() + System.getProperty("line.separator") +
                "properties=" + propertyMap.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CFGEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<CFGEdge> edges) {
        this.edges = edges;
    }

    public Map<String, String> getProperties() {
        return propertyMap;
    }

    public void setProperties(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }
}
