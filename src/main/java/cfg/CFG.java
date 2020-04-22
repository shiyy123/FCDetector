package cfg;

import config.CFGConfig;
import config.PathConfig;
import method.Method;
import org.apache.commons.io.FileUtils;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author cary.shi on 2019/11/29
 */
public class CFG {
    // node包含边
    private List<CFGNode> cfgNodeList;

    public CFG(List<CFGNode> cfgNodeList) {
        this.cfgNodeList = cfgNodeList;
    }

    public CFG() {
    }

    /**
     * 获取每个节点对应的前向节点集合
     */
    public Map<CFGNode, Set<CFGNode>> getCfgNode2ForwardCfgNodeList(Set<CFGNode> cfgNodeSet, Set<CFGEdge> cfgEdgeSet, Map<String, CFGNode> cfgNodeId2CfgNode) {
        Map<CFGNode, Set<CFGNode>> node2EdgeSetMap = new HashMap<>();
        for (CFGEdge edge : cfgEdgeSet) {
            CFGNode inCfg = cfgNodeId2CfgNode.get(edge.getIn());
            CFGNode outCfg = cfgNodeId2CfgNode.get(edge.getOut());
            if (inCfg == null || outCfg == null ||
                    !(cfgNodeSet.contains(inCfg) && cfgNodeSet.contains(outCfg))) {
                continue;
            }
            Set<CFGNode> inCfgSet = node2EdgeSetMap.getOrDefault(outCfg, new HashSet<>());
            inCfgSet.add(inCfg);
            node2EdgeSetMap.put(outCfg, inCfgSet);
        }
        return node2EdgeSetMap;
    }

    /**
     * 获取每个节点对应的后向节点集合
     */
    public Map<CFGNode, Set<CFGNode>> getCfgNode2BackwardCfgNodeList(Set<CFGNode> cfgNodeSet, Set<CFGEdge> cfgEdgeSet, Map<String, CFGNode> cfgNodeId2CfgNode) {
        Map<CFGNode, Set<CFGNode>> node2EdgeSetMap = new HashMap<>();
        for (CFGEdge edge : cfgEdgeSet) {
            CFGNode inCfg = cfgNodeId2CfgNode.get(edge.getIn());
            CFGNode outCfg = cfgNodeId2CfgNode.get(edge.getOut());
            if (inCfg == null || outCfg == null ||
                    !(cfgNodeSet.contains(inCfg) && cfgNodeSet.contains(outCfg))) {
                continue;
            }
            Set<CFGNode> outCfgSet = node2EdgeSetMap.getOrDefault(inCfg, new HashSet<>());
            outCfgSet.add(outCfg);
            node2EdgeSetMap.put(inCfg, outCfgSet);
        }
        return node2EdgeSetMap;
    }

    /**
     * CFG中除Method,MethodReturn,Return,Call外的Node，需要映射到一个structure Node
     */
    public Map<CFGNode, CFGNode> getCfgNode2CfgStructureNode(Set<CFGNode> cfgNodeSet, Set<CFGEdge> cfgEdgeSet, Map<String, CFGNode> cfgNodeId2CfgNode) {
        // 存储每个节点指向的节点集合
        Map<CFGNode, Set<CFGNode>> node2NodeSetMap = getCfgNode2ForwardCfgNodeList(cfgNodeSet, cfgEdgeSet, cfgNodeId2CfgNode);

        // 所有的node
        List<CFGNode> cfgNodeList = new ArrayList<>(cfgNodeSet);

        int nodeSize = cfgNodeList.size();
        int needMarkSize = nodeSize;
        // initial
        boolean[] marked = new boolean[nodeSize];
        for (int i = 0; i < nodeSize; i++) {
            marked[i] = false;
        }

        // node对应的structure node
        Map<CFGNode, CFGNode> cfgNode2CfgStructureNode = new HashMap<>();
        for (int i = 0; i < cfgNodeList.size(); i++) {
            CFGNode cfgNode = cfgNodeList.get(i);
            if (cfgNode.isCallNode() || cfgNode.isReturnNode() || cfgNode.isMethodReturnNode() || cfgNode.isMethodNode()) {
                cfgNode2CfgStructureNode.put(cfgNode, cfgNode);
                // structure node 不需要再标记
                marked[i] = true;
                needMarkSize--;
            } else {
                Set<CFGNode> nodeCfgSet = node2NodeSetMap.getOrDefault(cfgNode, null);
                if (nodeCfgSet != null && nodeCfgSet.size() > 1) {
                    cfgNode2CfgStructureNode.put(cfgNode, cfgNode);
                    // structure node 不需要再标记
                    marked[i] = true;
                    needMarkSize--;
                }
            }
        }

        // 获取所有的node对应的structure node
        while (needMarkSize > 0) {
//            printMark(marked);
            for (int i = 0; i < nodeSize; i++) {
                CFGNode curNode = cfgNodeList.get(i);
                if (!marked[i]) {
                    Set<CFGNode> nodeSet = node2NodeSetMap.getOrDefault(curNode, null);
                    if (nodeSet == null || nodeSet.isEmpty()) {
                        continue;
                    }
                    for (CFGNode toCfgNode : nodeSet) {
                        CFGNode structureNode = cfgNode2CfgStructureNode.getOrDefault(toCfgNode, null);
                        if (structureNode != null) {
                            String curNodeCode = curNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "");
                            String structureNodeCode = structureNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "");
                            if (structureNodeCode.contains(curNodeCode)) {
                                // 包含在structure node内
                                cfgNode2CfgStructureNode.put(curNode, structureNode);
                            } else {
                                // 不包含，自身为structure node
                                cfgNode2CfgStructureNode.put(curNode, curNode);
                            }

                            marked[i] = true;
                            needMarkSize--;
                            break;
                        }
                    }
                }
            }
        }
        return cfgNode2CfgStructureNode;
    }

    /**
     * 获取每个structure node包含的node list
     */
    public Map<CFGNode, List<CFGNode>> getStructureNode2NodeListMap(Map<CFGNode, CFGNode> cfgNode2CfgStructureNode) {
        Map<CFGNode, List<CFGNode>> structureNode2NodeListMap = new HashMap<>();
        for (Map.Entry<CFGNode, CFGNode> entry : cfgNode2CfgStructureNode.entrySet()) {
            List<CFGNode> cfgNodeList = structureNode2NodeListMap.getOrDefault(entry.getValue(), new ArrayList<>());
            cfgNodeList.add(entry.getKey());
            structureNode2NodeListMap.put(entry.getValue(), cfgNodeList);
        }
        return structureNode2NodeListMap;
    }

    /**
     * 判断集合中是否已存在该边
     */
    public boolean existedPureEdge(String in, String out, Set<CFGEdge> pureEdgeHashSet) {
        boolean existed = false;
        for (CFGEdge pureEdge : pureEdgeHashSet) {
            if (pureEdge.getIn().equals(in) && pureEdge.getOut().equals(out)) {
                existed = true;
                break;
            }
        }
        return existed;
    }


    public List<CFGNode> getCfgNodeList() {
        return cfgNodeList;
    }

    public void setCfgNodeList(List<CFGNode> cfgNodeList) {
        this.cfgNodeList = cfgNodeList;
    }

    /**
     * 解析CFG的json文件，获取所有的CFG信息，CFG与method一一对应
     */
    public List<Method> getMethodCFGListFromCFGFile(File cfgJsonFile) {
        List<Method> methodList = new ArrayList<>();

        String fileContent = null;
        try {
            fileContent = FileUtils.readFileToString(cfgJsonFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileContent == null) {
            return methodList;
        }
        JSONObject fileJsonObject = new JSONObject(fileContent);
        JSONArray allFuncJsonArray = fileJsonObject.getJSONArray("functions");
        for (int i = 0; i < allFuncJsonArray.length(); i++) {
            JSONObject funcJson = allFuncJsonArray.getJSONObject(i);
            String funcName = funcJson.getString("function");
            String funcId = funcJson.getString("id");
            JSONArray cfgJsonArray = funcJson.getJSONArray("CFG");

            List<CFGNode> cfgNodeList = new ArrayList<>();
            for (int j = 0; j < cfgJsonArray.length(); j++) {

                JSONObject cfgJson = cfgJsonArray.getJSONObject(j);
                String cfgId = cfgJson.getString("id");
                JSONArray cfgEdgeJsonArray = cfgJson.getJSONArray("edges");
                List<CFGEdge> edgeList = new ArrayList<>();
                for (int k = 0; k < cfgEdgeJsonArray.length(); k++) {
                    JSONObject cfgEdgeJsonObject = cfgEdgeJsonArray.getJSONObject(k);
                    String edgeId = cfgEdgeJsonObject.getString("id");
                    String edgeIn = cfgEdgeJsonObject.getString("in");
                    String edgeOut = cfgEdgeJsonObject.getString("out");
                    CFGEdge edge = new CFGEdge(edgeId, edgeIn, edgeOut);
                    edgeList.add(edge);
                }

                Map<String, String> propertyMap = new HashMap<>();
                JSONArray cfgPropertyJsonArray = cfgJson.getJSONArray("properties");
                for (int k = 0; k < cfgPropertyJsonArray.length(); k++) {
                    JSONObject cfgPropertyJson = cfgPropertyJsonArray.getJSONObject(k);
                    String propertyKey = cfgPropertyJson.getString("key");
                    String propertyValue = cfgPropertyJson.getString("value");
                    propertyMap.put(propertyKey, propertyValue);
                }

                CFGNode cfgNode = new CFGNode(cfgId, edgeList, propertyMap);
                cfgNodeList.add(cfgNode);
            }
            CFG cfg = new CFG(cfgNodeList);
            Method method = new Method(funcId, funcName, cfg);
            methodList.add(method);
        }
        return methodList;
    }

    public List<File> sourceFile2CFGFileList(File sourceFile) {
        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);

        File[] files = new File(PathConfig.getInstance().getCFG_FOLDER_PATH() + File.separator + folderAndFilePath).listFiles();
        List<File> fileList = new ArrayList<>();
        assert files != null;
        Collections.addAll(fileList, files);
        return fileList;
    }
}
