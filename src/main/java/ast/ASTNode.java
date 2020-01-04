package ast;

import config.CFGConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cary.shi on 2019/12/24
 */
public class ASTNode {
    private String id;
    private List<ASTEdge> edges;
    private Map<String, String> propertyMap;

    public ASTNode(String id, List<ASTEdge> edges, Map<String, String> propertyMap) {
        this.id = id;
        this.edges = edges;
        this.propertyMap = propertyMap;
    }

    @Override
    public String toString() {
        return "ASTNode[id=" + this.id +
                "\n,edges=" + this.edges +
                "\n,propertyMap" + this.propertyMap +
                "]\n";
    }

    public boolean isMethodNode() {
        return !this.id.contains(CFGConfig.METHOD_RETURN) && this.id.contains(CFGConfig.METHOD);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ASTEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<ASTEdge> edges) {
        this.edges = edges;
    }

    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }
}
