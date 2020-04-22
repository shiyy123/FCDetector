package feature;

import cfg.CFG;
import cfg.CFGEdge;
import cfg.CFGGraph;
import cfg.CFGNode;
import config.CFGConfig;
import method.Method;
import method.MethodCall;
import tool.Tool;

import java.util.*;

/**
 * @author cary.shi on 2019/11/29
 */
public class Feature {
    private Set<MethodCall> methodCallSet;

    public Feature(Set<MethodCall> methodCallSet) {
        this.methodCallSet = methodCallSet;
    }

    /**
     * judge existed cfg edge
     */
    private static boolean existPureEdge(CFGEdge cfgEdge, Set<CFGEdge> cfgEdgeSet) {
        boolean flag = false;
        for (CFGEdge cur : cfgEdgeSet) {
            if (cfgEdge.getIn().equals(cur.getIn()) &&
                    cfgEdge.getOut().equals(cur.getOut())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * get all pure cfg edge, with no duplicate
     */
    public static Set<CFGEdge> getPureCFGEdgeSetFromCFG(CFG cfg) {
        List<CFGNode> cfgNodeList = cfg.getCfgNodeList();
        Set<CFGEdge> cfgEdgeSet = new HashSet<>();
        for (CFGNode cfgNode : cfgNodeList) {
            List<CFGEdge> cfgEdgeList = cfgNode.getEdges();
            for (CFGEdge cfgEdge : cfgEdgeList) {
                if (existPureEdge(cfgEdge, cfgEdgeSet)) {
                    continue;
                }
                CFGEdge pureCfgEdge = new CFGEdge(cfgEdge.getIn(), cfgEdge.getOut());
                cfgEdgeSet.add(pureCfgEdge);
            }
        }
        return cfgEdgeSet;
    }

    /**
     * generate CFG by single feature, link by call relationship
     */
    public static CFGGraph generateCFGByFeature(Feature feature, Map<Method, CFGGraph> methodCFGGraphMap) {
        Set<MethodCall> methodCalls = feature.getMethodCallSet();

        CFGGraph cfgGraph = new CFGGraph();
        // save all nodes
        Set<CFGNode> cfgNodeSet = new HashSet<>();
        Set<CFGEdge> cfgEdgeSet = new HashSet<>();

        // add all the methods
        for (MethodCall methodCall : methodCalls) {
//            System.out.println(methodCall.getCallerMethod().getName());
            CFGGraph callerCFGGraph = methodCFGGraphMap.get(methodCall.getCallerMethod());
            CFGGraph calleeCFGGraph = methodCFGGraphMap.get(methodCall.getCalleeMethod());

            if (callerCFGGraph == null || calleeCFGGraph == null) {
                continue;
            }

            Set<CFGNode> callerCfgNodeSet = callerCFGGraph.getCfgNodeSet();
            Set<CFGNode> calleeCfgNodeSet = calleeCFGGraph.getCfgNodeSet();
            cfgNodeSet.addAll(callerCfgNodeSet);
            cfgNodeSet.addAll(calleeCfgNodeSet);

            Set<CFGEdge> callerCfgEdgeSet = callerCFGGraph.getPureCFGEdgeSet();
            Set<CFGEdge> calleeCfgEdgeSet = calleeCFGGraph.getPureCFGEdgeSet();
            cfgEdgeSet.addAll(callerCfgEdgeSet);
            cfgEdgeSet.addAll(calleeCfgEdgeSet);
        }
        Map<String, CFGNode> id2CFGNodeMap = new HashMap<>();
        for (CFGNode cfgNode : cfgNodeSet) {
            id2CFGNodeMap.put(cfgNode.getId(), cfgNode);
        }

        Set<CFGNode> needDeleteCFGNodeSet = new HashSet<>();
        Set<CFGEdge> needDeleteCFGEdgeSet = new HashSet<>();

        // add call relationship
        for (MethodCall methodCall : methodCalls) {

            int lineNum = methodCall.getLineNum();
            CFGNode selectedCFGNode = null;
            for (CFGNode cfgNode : cfgNodeSet) {
                int cfgNodeLineNum = Integer.parseInt(cfgNode.getProperties().getOrDefault(CFGConfig.LINE_NUMBER_PROPERTY, "-1"));
                if (lineNum == cfgNodeLineNum) {
                    selectedCFGNode = cfgNode;
                    break;
                }
            }
            if (selectedCFGNode == null) {
                continue;
            }
            needDeleteCFGNodeSet.add(selectedCFGNode);

            // in selected node
            Set<CFGNode> inToCFGNodeSet = new HashSet<>();
            // out from selected node
            Set<CFGNode> outFromCFGNodeSet = new HashSet<>();

            for (CFGEdge cfgEdge : cfgEdgeSet) {
                if (cfgEdge.getIn().equals(selectedCFGNode.getId())) {
                    inToCFGNodeSet.add(id2CFGNodeMap.get(cfgEdge.getOut()));
                    needDeleteCFGEdgeSet.add(cfgEdge);
                }
                if (cfgEdge.getOut().equals(selectedCFGNode.getId())) {
                    outFromCFGNodeSet.add(id2CFGNodeMap.get(cfgEdge.getIn()));
                    needDeleteCFGEdgeSet.add(cfgEdge);
                }
            }
            CFGGraph methodCalleeCFGGraph = methodCFGGraphMap.get(methodCall.getCalleeMethod());

            if (methodCalleeCFGGraph == null) {
                continue;
            }

            CFGNode entryNode = getEntry(methodCalleeCFGGraph);
            CFGNode exitNode = getExit(methodCalleeCFGGraph);

            if (entryNode == null || exitNode == null) {
                continue;
            }

            for (CFGNode cfgNode : inToCFGNodeSet) {
                CFGEdge cfgEdge = new CFGEdge(entryNode.getId(), cfgNode.getId());
                cfgEdgeSet.add(cfgEdge);
            }
            for (CFGNode cfgNode : outFromCFGNodeSet) {
                CFGEdge cfgEdge = new CFGEdge(cfgNode.getId(), exitNode.getId());
                cfgEdgeSet.add(cfgEdge);
            }
        }

        Set<CFGNode> finalCfgNodeSet = new HashSet<>();
        for (CFGNode cfgNode : cfgNodeSet) {
            if (!needDeleteCFGNodeSet.contains(cfgNode)) {
                finalCfgNodeSet.add(cfgNode);
            }
        }

        Set<CFGEdge> finalCfgEdgeSet = new HashSet<>();
        for (CFGEdge cfgEdge : cfgEdgeSet) {
            if (!needDeleteCFGEdgeSet.contains(cfgEdge)) {
                finalCfgEdgeSet.add(cfgEdge);
            }
        }

        cfgGraph.setCfgNodeSet(finalCfgNodeSet);
        cfgGraph.setPureCFGEdgeSet(finalCfgEdgeSet);

        return cfgGraph;
    }

    /**
     * get entry from simplify cfg graph
     */
    private static CFGNode getEntry(CFGGraph cfgGraph) {
        Set<CFGNode> cfgNodeSet = cfgGraph.getCfgNodeSet();
//        System.out.println(cfgNodeSet.size());
//        cfgNodeSet.forEach(System.out::println);

        CFGNode entryNode = null;
        for (CFGNode cfgNode : cfgNodeSet) {
            if (cfgNode.isMethodNode()) {
                entryNode = cfgNode;
            }
        }
        return entryNode;
    }

    /**
     * get exit node from simplify cfg graph
     */
    private static CFGNode getExit(CFGGraph cfgGraph) {
        Set<CFGNode> cfgNodeSet = cfgGraph.getCfgNodeSet();
        CFGNode exitNode = null;
        for (CFGNode cfgNode : cfgNodeSet) {
            if (cfgNode.isMethodReturnNode()) {
                exitNode = cfgNode;
            }
        }
        return exitNode;
    }

    /**
     * split method by call relationship
     */
    public static List<Feature> getFeatureFromMethodCallList(List<MethodCall> localMethodCallList) {
        List<Feature> featureList = new ArrayList<>();

        for (MethodCall methodCall : localMethodCallList) {
            boolean found = false;
            int size = featureList.size();
            for (int i = 0; i < size; i++) {
                Feature feature = featureList.get(i);
                Set<MethodCall> innerMethodCallSet = feature.getMethodCallSet();
                for (MethodCall innerMethodCall : innerMethodCallSet) {
                    if (methodCall.getCalleeMethod().equals(innerMethodCall.getCalleeMethod()) ||
                            methodCall.getCalleeMethod().equals(innerMethodCall.getCallerMethod()) ||
                            methodCall.getCallerMethod().equals(innerMethodCall.getCalleeMethod()) ||
                            methodCall.getCallerMethod().equals(innerMethodCall.getCallerMethod())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    innerMethodCallSet.add(methodCall);
                    feature.setMethodCallSet(innerMethodCallSet);
                }
            }
            if (!found) {
                Set<MethodCall> methodCalls = new HashSet<>();
                methodCalls.add(methodCall);
                featureList.add(new Feature(methodCalls));
            }
        }
        return featureList;
    }

    public Set<MethodCall> getMethodCallSet() {
        return methodCallSet;
    }

    public void setMethodCallSet(Set<MethodCall> methodCallSet) {
        this.methodCallSet = methodCallSet;
    }

//    private String folderAndFilePath;
//    private List<Long> funcIdList;
//
//    Feature(String folderAndFilePath, List<Long> funcIdList) {
//        this.folderAndFilePath = folderAndFilePath;
//        this.funcIdList = funcIdList;
//    }
//    public static List<Feature> getFeatureListBySourceFile(File sourceFile) {
//        List<Feature> featureList = new ArrayList<>();
//
//        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);
//        File featureFile = new File(PathConfig.FEATURE_FOLDER_PATH + File.separator + folderAndFilePath + File.separator + "feature.txt");
//        List<String> lines = null;
//        try {
//            lines = FileUtils.readLines(featureFile, StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assert lines != null;
//        for (String line : lines) {
//            if (line.trim().isEmpty()) {
//                continue;
//            }
//            line = line.substring(1, line.length() - 1);
//            String[] cols = line.split(",");
//            List<Long> funcIds = new ArrayList<>();
//            for (String col : cols) {
//                funcIds.add(Long.parseLong(col.trim()));
//            }
//            Feature feature = new Feature(folderAndFilePath, funcIds);
//            featureList.add(feature);
//        }
//        return featureList;
//    }
//
//    List<File> getFeatureEdgeFileListBySourceFile(File sourceFile) {
//        List<File> resList = new ArrayList<>();
//
//        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);
//
//        File[] files = new File(PathConfig.FEATURE_EDGE_PATH + folderAndFilePath).listFiles();
//
//        assert files != null;
//        Collections.addAll(resList, files);
//
//        return resList;
//    }
//
//
//    public List<Long> getFuncIdList() {
//        return funcIdList;
//    }
//
//    public void setFuncIdList(List<Long> funcIdList) {
//        this.funcIdList = funcIdList;
//    }
}
