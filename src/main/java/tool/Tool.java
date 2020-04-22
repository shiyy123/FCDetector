package tool;

import ast.AST;
import ast.ASTEdge;
import ast.ASTNode;
import cfg.CFG;
import cfg.CFGEdge;
import cfg.CFGGraph;
import cfg.CFGNode;
import com.opencsv.CSVWriter;
import config.CFGConfig;
import config.Config;
import feature.Feature;
import method.Method;
import method.MethodCall;
import method.MethodInfo;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import process.ProcessExecutorList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author cary.shi on 2019/11/29
 */
public class Tool {
    public static String getFolderAndFilePath(File sourceFile) {
        String name = sourceFile.getName();
        name = name.substring(0, name.indexOf("."));
        String folderName = sourceFile.getParentFile().getName();
        return folderName + File.separator + name;
    }

    static void deleteUseless() {
        File[] files = new File("G:\\share\\CloneData\\data\\identEmbed\\2").listFiles();
        HashSet<String> set = new HashSet<>();
        for (File file : files) {
            set.add(file.getName());
        }
        File[] sources = new File("G:\\share\\CloneData\\data\\src\\2").listFiles();
        for (File source : sources) {
            String name = source.getName();
            if (!set.contains(name.substring(0, name.indexOf(".")))) {
                source.delete();
            }
        }
    }

    /**
     * 执行cmd对应的指令，并将日志记录下来
     */
    public static void executeCmdAndSaveLog(String cmd, Logger logger) {
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProcessExecutorList processExecutorList = new ProcessExecutorList(process);
        processExecutorList.execute();

        List<String> errorList = processExecutorList.getErrorList();
        List<String> outList = processExecutorList.getOutputList();

        logger.info(outList);
        logger.error(errorList);
    }

    /**
     * io.shiftleft.codepropertygraph.generated.nodes.Method@65 -> Method_65
     * 格式化id，使其可以写入作为dot文件被解析
     */
    public static String formatId(String id) {
        String[] cols = id.split("\\.");
        return cols[cols.length - 1].replace("@", "_");
    }

    public static File writeCFGNodeAndEdge2Dot(Set<CFGNode> cfgNodeSet, Set<CFGEdge> cfgEdgeSet, String dotFilePath) {
        File dotFile = new File(dotFilePath);
        if (dotFile.exists()) {
            dotFile.delete();
        }

        // head
        try {
            FileUtils.write(dotFile, "digraph {" + System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // node
        for (CFGNode cfgNode : cfgNodeSet) {
            StringBuilder nodeLine = new StringBuilder();
            String formatNodeId = formatId(cfgNode.getId());

            nodeLine.append(formatNodeId).append(" ").append(" [label=\"").
                    append(formatNodeId).append(Config.LINE_SEP).
                    append(cfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "").replace("\"", "\\\"")).
                    append("\"];");
            try {
                FileUtils.write(dotFile, nodeLine.toString() + System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // edge
        for (CFGEdge edge : cfgEdgeSet) {
            StringBuilder edgeLine = new StringBuilder();
            edgeLine.append(formatId(edge.getOut())).append(" -> ").append(formatId(edge.getIn())).append(" ;");

            try {
                FileUtils.write(dotFile, edgeLine.toString() + System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // tail
        try {
            FileUtils.write(dotFile, "}" + System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dotFile;
    }

    /**
     * 根据structure node（简化版cfg中的node）和pure edge（structure node之间简单的边）生成CFG dot文件
     */
    public static File generateCfgDotFileByNodeAndEdge(Set<CFGNode> structureCFGSet, Set<CFGEdge> pureEdgeSet, String dotFilePath) {
        File dotFile = new File(dotFilePath);
        if (dotFile.exists()) {
            dotFile.delete();
        }

        // head
        try {
            FileUtils.write(dotFile, "digraph {" + System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // node
        for (CFGNode cfgNode : structureCFGSet) {
            StringBuilder nodeLine = new StringBuilder();
            String formatNodeId = formatId(cfgNode.getId());

            String code = cfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "");
            String lineNum = cfgNode.getProperties().getOrDefault(CFGConfig.LINE_NUMBER_PROPERTY, "");
            String columnNum = cfgNode.getProperties().getOrDefault(CFGConfig.LINE_NUMBER_PROPERTY, "");

            nodeLine.append(formatNodeId).append(" ").append(" [label=\"").
                    append(formatNodeId).append(Config.LINE_SEP);

            if (code.length() > 0) {
                nodeLine.append("code:").append(cfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "").replace("\"", "\\\"")).append(Config.LINE_SEP);
            }
            if (lineNum.length() > 0) {
                nodeLine.append("lineNum:").append(cfgNode.getProperties().getOrDefault(CFGConfig.LINE_NUMBER_PROPERTY, "")).append(Config.LINE_SEP);
            }
            if (columnNum.length() > 0) {
                nodeLine.append("columnNum:").append(cfgNode.getProperties().getOrDefault(CFGConfig.COLUMN_NUMBER_PROPERTY, "")).append(Config.LINE_SEP);
            }
            nodeLine.append("\"];");

            try {
                FileUtils.write(dotFile, nodeLine.toString() + Config.LINE_SEP, StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // edge
        for (CFGEdge pureEdge : pureEdgeSet) {
            StringBuilder edgeLine = new StringBuilder();
            edgeLine.append(formatId(pureEdge.getOut())).append(" -> ").append(formatId(pureEdge.getIn())).append(" ;");

            try {
                FileUtils.write(dotFile, edgeLine.toString() + System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // tail
        try {
            FileUtils.write(dotFile, "}" + System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dotFile;
    }

    /**
     * 为保持一致，将method构造为CFG Node
     */
    public static CFGNode constructCfgNodeByMethod(Method method) {
        HashMap<String, String> propertyMap = new HashMap<>();
        propertyMap.put(CFGConfig.CODE_PROPERTY, method.getName());
        CFGNode methodCFGNode = new CFGNode(method.getId(), new ArrayList<>(), propertyMap);
        return methodCFGNode;
    }

    /**
     * 将method中的MethodReturn构建为cfg node
     */
    public static Set<CFGNode> constructMethodReturnCfgSet(Method method) {
        Set<CFGNode> methodReturnCfgHashSet = new HashSet<>();
        Set<String> methodReturnIdSet = new HashSet<>();
        List<CFGNode> cfgNodeList = method.getCfg().getCfgNodeList();
        for (CFGNode cfgNode : cfgNodeList) {
            if (cfgNode.getId().contains(CFGConfig.METHOD_RETURN)) {
                methodReturnIdSet.add(cfgNode.getId());
            }
            for (CFGEdge cfgEdge : cfgNode.getEdges()) {
                if (cfgEdge.getIn().contains(CFGConfig.METHOD_RETURN)) {
                    methodReturnIdSet.add(cfgEdge.getIn());
                }
                if (cfgEdge.getOut().contains(CFGConfig.METHOD_RETURN)) {
                    methodReturnIdSet.add(cfgEdge.getOut());
                }
            }
        }
        for (String id : methodReturnIdSet) {
            CFGNode methodReturnCfg = new CFGNode(id, new ArrayList<>(), new HashMap<>());
            methodReturnCfgHashSet.add(methodReturnCfg);
        }
        return methodReturnCfgHashSet;
    }

    /**
     * 判断待加入边是否已存在
     */
    public static boolean existedCfgEdge(Set<CFGEdge> relatedCFGEdgeSet, CFGEdge edge) {
        for (CFGEdge e : relatedCFGEdgeSet) {
            if (e.getIn().equals(edge.getIn()) && e.getOut().equals(edge.getOut())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 构建Method的cfg graph，存为dot文件
     */
    public static File constructCFGDotFileOfCFGGraph(CFGGraph cfgGraph, String cfgDotFilePath) {
        File cfgDotFile = new File(cfgDotFilePath);
        if (cfgDotFile.exists()) {
            cfgDotFile.delete();
        }
        Tool.createFolderIfNotExist(cfgDotFilePath);

        cfgDotFile = Tool.generateCfgDotFileByNodeAndEdge(cfgGraph.getCfgNodeSet(), cfgGraph.getPureCFGEdgeSet(), cfgDotFilePath);
        return cfgDotFile;
    }

    /**
     * 获取指定Method的CFGGraph
     */
    public static CFGGraph getCFGGraphOfSelectedMethod(List<Method> methodList, Method selectedMethod) {
        Map<String, CFGNode> cfgNodeId2CfgNode = new HashMap<>();
        for (Method method : methodList) {
            // 加入Entry，即Method节点
            CFGNode methodCfgNode = constructCfgNodeByMethod(method);
            cfgNodeId2CfgNode.put(methodCfgNode.getId(), methodCfgNode);

            // 加入普通node
            List<CFGNode> cfgNodeList = method.getCfg().getCfgNodeList();
            for (CFGNode cfgNode : cfgNodeList) {
                cfgNodeId2CfgNode.put(cfgNode.getId(), cfgNode);
            }

            // 加入Exit，即MethodReturn节点
            Set<CFGNode> methodReturnCfgNodeSet = constructMethodReturnCfgSet(method);
            for (CFGNode cfgNode : methodReturnCfgNodeSet) {
                cfgNodeId2CfgNode.put(cfgNode.getId(), cfgNode);
            }
        }

        Set<CFGNode> relatedCFGNodeSet = new HashSet<>();
        Set<CFGEdge> relatedCFGEdgeSet = new HashSet<>();

        List<CFGNode> cfgNodeList = selectedMethod.getCfg().getCfgNodeList();
        for (CFGNode cfgNode : cfgNodeList) {
            // 去除无用CFG
            if (cfgNode.isBlockNode() ||
                    (cfgNode.isUnknownNode() && !cfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "").equals("break;")
                            && !cfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "").equals("goto"))) {
                continue;
            }
            relatedCFGNodeSet.add(cfgNode);
            for (CFGEdge cfgEdge : cfgNode.getEdges()) {
                CFGNode inCfg = cfgNodeId2CfgNode.get(cfgEdge.getIn());
                CFGNode outCfg = cfgNodeId2CfgNode.get(cfgEdge.getOut());

                // add in node
                if (inCfg != null) {
                    relatedCFGNodeSet.add(inCfg);
                }
                // add out node
                if (outCfg != null) {
                    relatedCFGNodeSet.add(outCfg);
                }
                // add edge not existed
                if (!existedCfgEdge(relatedCFGEdgeSet, cfgEdge)) {
                    relatedCFGEdgeSet.add(cfgEdge);
                }
            }
        }
        return constructCFGGraphByNodeAndEdge(relatedCFGNodeSet, relatedCFGEdgeSet, cfgNodeId2CfgNode);
    }

    /**
     * 通过CFG节点和边构造函数的CFG图，不包含Identifier和Literal等，只使用structure node
     * 其中，structure node包括：Method;Call;Return;MethodReturn
     */
    public static CFGGraph constructCFGGraphByNodeAndEdge(Set<CFGNode> cfgNodeSet, Set<CFGEdge> cfgEdgeSet, Map<String, CFGNode> cfgNodeId2CfgNode) {
        CFG cfg = new CFG();
        // get CFG node -> CFG structure node
        Map<CFGNode, CFGNode> cfgNode2CfgStructureNode = cfg.getCfgNode2CfgStructureNode(cfgNodeSet, cfgEdgeSet, cfgNodeId2CfgNode);

        // structure cfg node set
        Set<CFGNode> structureCFGSet = new HashSet<>();
        for (Map.Entry<CFGNode, CFGNode> entry : cfgNode2CfgStructureNode.entrySet()) {
            structureCFGSet.add(entry.getValue());
        }

        // structure cfg pureEdge set
        Set<CFGEdge> pureEdgeSet = new HashSet<>();
        for (CFGEdge edge : cfgEdgeSet) {
            CFGNode inCfg = cfgNodeId2CfgNode.getOrDefault(edge.getIn(), null);
            CFGNode outCfg = cfgNodeId2CfgNode.getOrDefault(edge.getOut(), null);
            if (inCfg == null || outCfg == null ||
                    !(cfgNodeSet.contains(inCfg) && cfgNodeSet.contains(outCfg))) {
                continue;
            }
            CFGNode structureCFGOfInCfg = cfgNode2CfgStructureNode.getOrDefault(inCfg, null);
            CFGNode structureCFGOfOutCfg = cfgNode2CfgStructureNode.getOrDefault(outCfg, null);
            if (structureCFGOfInCfg == null || structureCFGOfOutCfg == null ||
                    !(structureCFGSet.contains(structureCFGOfInCfg) && structureCFGSet.contains(structureCFGOfOutCfg))) {
                continue;
            }
            // 不属于同一个structure node && 未存在此边
            if (structureCFGOfInCfg != structureCFGOfOutCfg &&
                    !cfg.existedPureEdge(structureCFGOfInCfg.getId(), structureCFGOfOutCfg.getId(), pureEdgeSet)) {
                CFGEdge pureEdge = new CFGEdge(structureCFGOfInCfg.getId(), structureCFGOfOutCfg.getId());
                pureEdgeSet.add(pureEdge);
            }
        }
        return new CFGGraph(structureCFGSet, pureEdgeSet);
    }

    // some(num)，获取数字num
    public static int getNumFromSome(String s) {
        return Integer.parseInt(s.substring(s.indexOf("(") + 1, s.indexOf(")")));
    }

    /**
     * 从sourceFolderPath下获取，所有的源码文件
     */
    public static List<File> getSourceFilesFromPath(String sourceFolderPath) {
        List<File> fileList = new ArrayList<>();

        File[] sourceFolders = new File(sourceFolderPath).listFiles();
        assert sourceFolders != null;
        for (File sourceFolder : sourceFolders) {
            if (sourceFolder.isDirectory()) {
                fileList.addAll(Arrays.asList(Objects.requireNonNull(sourceFolder.listFiles())));
            } else if (sourceFolder.isFile()) {
                fileList.add(sourceFolder);
            }
        }
        return fileList;
    }

    /**
     * 当文件夹不存在时，创建
     */
    public static void createFolderIfNotExist(String path) {
        File folder = new File(path.substring(0, path.lastIndexOf(System.getProperty("file.separator"))));
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Simplify the cfg graph generated by added call relationship cfg graph
     * the edge in cfgGraph do not have id(id is null)
     */
    public static CFGGraph simplifyCFGGraphOfAddedCallRelationship(CFGGraph cfgGraph) {
        Set<CFGNode> cfgNodeSet = cfgGraph.getCfgNodeSet();
        Set<CFGEdge> cfgEdgeSet = cfgGraph.getPureCFGEdgeSet();

        Set<CFGNode> needDeleteCfgNodeSet = new HashSet<>();
        Set<CFGEdge> needDeleteCfgEdgeSet = new HashSet<>();

        Set<CFGEdge> needAddedCfgEdgeSet = new HashSet<>();

        Map<String, CFGNode> id2CFGNode = new HashMap<>();
        for (CFGNode cfgNode : cfgNodeSet) {
            id2CFGNode.put(cfgNode.getId(), cfgNode);
        }

        // find can simplify cfg edge
        for (CFGEdge cfgEdge : cfgEdgeSet) {
            CFGNode outCfgNode = id2CFGNode.get(cfgEdge.getOut());
            CFGNode inCfgNode = id2CFGNode.get(cfgEdge.getIn());

            if (inCfgNode == null || outCfgNode == null) {
                continue;
            }

            // is not method entry and method exit node
            if (!outCfgNode.isMethodNode() && !outCfgNode.isMethodReturnNode() &&
                    !inCfgNode.isMethodNode() && !inCfgNode.isMethodReturnNode()) {

                String inCode = inCfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "");
                String outCode = outCfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "");

                int inLineNum = Integer.parseInt(inCfgNode.getProperties().getOrDefault(CFGConfig.LINE_NUMBER_PROPERTY, "-1"));
                int outLineNum = Integer.parseInt(outCfgNode.getProperties().getOrDefault(CFGConfig.LINE_NUMBER_PROPERTY, "-1"));

                if (inCode.contains(outCode) && inLineNum == outLineNum && inLineNum != -1) {
                    int cnt1 = 0, cnt2 = 0;
                    CFGEdge toOutEdge = null, toInEdge = null;
                    for (CFGEdge edge : cfgEdgeSet) {
                        if (edge.getIn().equals(inCfgNode.getId())) {
                            toInEdge = edge;
                            cnt1++;
                        }
                        if (edge.getIn().equals(outCfgNode.getId())) {
                            toOutEdge = edge;
                            cnt2++;
                        }
                    }

                    boolean flag = false;
                    if (cnt1 == 1 && cnt2 == 1) {
                        CFGNode outOutCfgNode = id2CFGNode.get(toOutEdge.getOut());
                        String outOutCode = outOutCfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "");
                        int outOutLineNum = Integer.parseInt(outOutCfgNode.getProperties().getOrDefault(CFGConfig.LINE_NUMBER_PROPERTY, "-1"));

                        if (inCode.contains(outOutCode) && inLineNum == outOutLineNum) {

                            flag = true;

                            needDeleteCfgEdgeSet.add(toOutEdge);
                            needDeleteCfgEdgeSet.add(toInEdge);

                            needDeleteCfgNodeSet.add(outCfgNode);
                            needDeleteCfgNodeSet.add(outOutCfgNode);

                            List<CFGNode> outCfgNodeList = new ArrayList<>();
                            List<CFGNode> inCfgNodeList = new ArrayList<>();

                            for (CFGEdge edge : cfgEdgeSet) {
                                if (edge.getIn().equals(toOutEdge.getOut())) {
                                    outCfgNodeList.add(id2CFGNode.get(edge.getOut()));
                                    needDeleteCfgEdgeSet.add(edge);
                                }

                                if (edge.getOut().equals(toInEdge.getIn())) {
                                    inCfgNodeList.add(id2CFGNode.get(edge.getIn()));
                                }
                            }
                            for (CFGNode cfgNode : outCfgNodeList) {
                                needAddedCfgEdgeSet.add(new CFGEdge(inCfgNode.getId(), cfgNode.getId()));
                            }
                        }
                    }
                    if (!flag && cnt1 == 1) {
                        needDeleteCfgEdgeSet.add(toInEdge);

                        needDeleteCfgNodeSet.add(outCfgNode);

                        List<CFGNode> outCfgNodeList = new ArrayList<>();

                        for (CFGEdge edge : cfgEdgeSet) {
                            if (edge.getIn().equals(toInEdge.getOut())) {
                                outCfgNodeList.add(id2CFGNode.get(edge.getOut()));
                                needDeleteCfgEdgeSet.add(edge);
                            }
                        }

                        for (CFGNode cfgNode : outCfgNodeList) {
                            needAddedCfgEdgeSet.add(new CFGEdge(inCfgNode.getId(), cfgNode.getId()));
                        }
                    }
                }
            }
        }

        Set<CFGNode> simplifyCfgNodeSet = new HashSet<>();
        Set<CFGEdge> simplifyCfgEdgeSet = new HashSet<>();

        for (CFGNode cfgNode : cfgNodeSet) {
            if (!needDeleteCfgNodeSet.contains(cfgNode)) {
                simplifyCfgNodeSet.add(cfgNode);
            }
        }

        for (CFGEdge cfgEdge : cfgEdgeSet) {
            if (!needDeleteCfgEdgeSet.contains(cfgEdge)) {
                simplifyCfgEdgeSet.add(cfgEdge);
            }
        }

        simplifyCfgEdgeSet.addAll(needAddedCfgEdgeSet);

        return new CFGGraph(simplifyCfgNodeSet, simplifyCfgEdgeSet);
    }

    /**
     * Prepare file data for graph2vec
     */
    public static File generateGraphFile(CFGGraph cfgGraph, String graphFilePath) {
        File graphFile = new File(graphFilePath);
        if (graphFile.exists()) {
            graphFile.delete();
        }

        Set<CFGNode> cfgNodeSet = cfgGraph.getCfgNodeSet();
        Set<CFGEdge> cfgEdgeSet = cfgGraph.getPureCFGEdgeSet();

//        cfgNodeSet.forEach(System.out::println);
        //io.shiftleft.codepropertygraph.generated.nodes.Call@4d

        Map<CFGNode, Integer> node2index = new HashMap<>();
        Map<String, CFGNode> id2node = new HashMap<>();

        int cur = 0;
        for (CFGNode cfgNode : cfgNodeSet) {
            node2index.put(cfgNode, cur);
            cur++;

            id2node.put(cfgNode.getId(), cfgNode);
        }

        JSONObject graphJsonObject = new JSONObject();

        Map<Integer, Integer> index2degree = new HashMap<>();

        Set<Integer> indexSet = new HashSet<>();
        JSONArray edgeJsonArray = new JSONArray();
        for (CFGEdge cfgEdge : cfgEdgeSet) {

            if (id2node.get(cfgEdge.getIn()) == null ||
                    id2node.get(cfgEdge.getOut()) == null) {
                continue;
            }

            int inIndex = node2index.get(id2node.get(cfgEdge.getIn()));
            int outIndex = node2index.get(id2node.get(cfgEdge.getOut()));

            indexSet.add(inIndex);
            indexSet.add(outIndex);

            index2degree.put(inIndex, index2degree.getOrDefault(inIndex, 0) + 1);
            index2degree.put(outIndex, index2degree.getOrDefault(outIndex, 0) + 1);

            JSONArray singleJsonArray = new JSONArray();
            singleJsonArray.put(inIndex);
            singleJsonArray.put(outIndex);
            edgeJsonArray.put(singleJsonArray);
        }
        graphJsonObject.put("edges", edgeJsonArray);

        JSONObject featureJsonObject = new JSONObject();
        for (Integer index : indexSet) {
            featureJsonObject.put(index.toString(), index2degree.getOrDefault(index, 0).toString());
        }

        graphJsonObject.put("features", featureJsonObject);

        try {
            FileUtils.write(graphFile, graphJsonObject.toString(), StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graphFile;
    }

    private static void preOrderAST(ASTNode root, StringBuilder res, Map<ASTNode, List<ASTNode>> node2Children) {
        if (root != null) {
            res.append(root.getPropertyMap().getOrDefault(CFGConfig.CODE_PROPERTY, "")).append(" ");
            List<ASTNode> children = node2Children.get(root);
            if (children != null) {
                for (ASTNode astNode : children) {
                    preOrderAST(astNode, res, node2Children);
                }
            }
        }
    }

    public static String traverseAST(Feature feature) {
        StringBuilder sb = new StringBuilder();

        Set<MethodCall> methodCallSet = feature.getMethodCallSet();
        Set<Method> methodSet = new HashSet<>();
        for (MethodCall methodCall : methodCallSet) {
            methodSet.add(methodCall.getCalleeMethod());
            methodSet.add(methodCall.getCallerMethod());
        }
        for (Method method : methodSet) {
            sb.append(traverseAST(method.getAst())).append(" ");
        }
        return sb.toString().trim().replaceAll(" +", " ");
    }

    // traverse ast and get the data sequence
    public static String traverseAST(AST ast) {
        List<ASTNode> astNodeList = ast.getAstNodeList();

        StringBuilder sb = new StringBuilder();

        Map<String, ASTNode> id2Node = new HashMap<>();
        for (ASTNode astNode : astNodeList) {
            id2Node.put(astNode.getId(), astNode);
        }

        Map<ASTNode, List<ASTNode>> node2Children = new HashMap<>();
        ASTNode root = null;

        for (ASTNode astNode : astNodeList) {
            String curId = astNode.getId();
            if (astNode.isMethodNode()) {
                root = astNode;
            }
            for (ASTEdge astEdge : astNode.getEdges()) {
                if (astEdge.getOut().equals(curId)) {
                    List<ASTNode> childrenASTNodeList = node2Children.getOrDefault(astNode, new ArrayList<>());
                    childrenASTNodeList.add(id2Node.get(astEdge.getIn()));
                    node2Children.put(astNode, childrenASTNodeList);
                }
            }
        }

        if (root == null) {
            return sb.toString();
        }

        preOrderAST(root, sb, node2Children);

        return sb.toString().trim().replaceAll(" +", " ");
    }

    public static Map<String, List<Double>> getIdentifier2Vec(File word2vecFile) {
        Map<String, List<Double>> identifier2Vec = new HashMap<>();
        try {
            List<String> contentList = FileUtils.readLines(word2vecFile, StandardCharsets.UTF_8);
            for (int i = 1; i < contentList.size(); i++) {
                String content = contentList.get(i);
                if (content.trim().isEmpty()) {
                    continue;
                }
                String[] cols = content.split(" ");
                List<Double> vec = new ArrayList<>();
                for (int j = 1; j < cols.length; j++) {
                    if (cols[j].trim().length() > 0) {
                        vec.add(Double.parseDouble(cols[j]));
                    }
                }
                identifier2Vec.put(cols[0], vec);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return identifier2Vec;
    }

    // /mnt/share/CloneData/data/feature_cfg/0/1/method.dot -> 0/1
    public static String getSrcPath(File file) {
        return file.getParentFile().getParentFile().getName() + File.separator + file.getParentFile().getName();
    }

    public static List<Double> getAstVecForFeature(String astIdentifiers, Map<String, List<Double>> identifier2Vec) {
        String[] cols = astIdentifiers.split(" ");
        List<Double> res = identifier2Vec.get(cols[0]);
        for (int i = 1; i < cols.length; i++) {
            List<Double> cur = identifier2Vec.get(cols[i]);
            if (cur == null || cur.isEmpty()) {
                continue;
            }
            for (int j = 0; j < res.size(); j++) {
                res.set(j, res.get(j) + cur.get(j));
            }
        }
        for (int i = 0; i < res.size(); i++) {
            res.set(i, res.get(i) / cols.length);
        }
        return res;
    }

    public static List<Double> getCfgVecForFeature(File featureCsvFile, File cfgFile) {
        List<Double> res = new ArrayList<>();
        String id = cfgFile.getName().substring(0, cfgFile.getName().indexOf("."));

        try {
            List<String> lines = FileUtils.readLines(featureCsvFile, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                String[] cols = lines.get(i).split(",");
                if (id.equals(cols[0])) {
                    for (int j = 1; j < cols.length; j++) {
                        res.add(Double.parseDouble(cols[j]));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    //[-0.24233099818229675, -0.40978747606277466, -0.1596420556306839, 0.147801011800766, 0.10527323186397552, -0.18085640668869019, -0.16109880805015564, 0.4755779504776001, 0.2085733264684677, -0.1569078415632248, -0.06173935905098915, -0.09034819155931473, 0.07120082527399063, -0.2028520554304123, -0.22904281318187714, 0.11911839991807938]
    public static List<Double> getDoubleListFromFile(File file) {
        List<Double> res = new ArrayList<>();
        try {
            String s = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            String[] cols = s.substring(s.indexOf("[") + 1, s.indexOf("]")).split(",");
            for (String col : cols) {
                if (col.trim().length() == 0) {
                    continue;
                }
                Double d = BigDecimal.valueOf(Double.parseDouble(col)).setScale(6, RoundingMode.HALF_UP).doubleValue();
                res.add(d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * generate csv data for DNN model training, syntax and semantic feature
     * 1: similar
     * 0: not similar
     * Because OJClone only has one function in one file, and we need to use this
     * information to generate clone and not clone pair, so we can think one file
     * only has one function
     */

//    PathConfig.TRAINING_MERGE_DATA_FILE_PATH,
//    PathConfig.TRAINING_TEXT_DATA_FILE_PATH, PathConfig.TRAINING_SYNTAX_DATA_FILE_PATH, PathConfig.TRAINING_SEMANTIC_DATA_FILE_PATH,
//    PathConfig.TRAINING_TEXT_SYNTAX_DATA_FILE_PATH, PathConfig.TRAINING_TEXT_SEMANTIC_DATA_FILE_PATH, PathConfig.TRAINING_SYNTAX_SEMANTIC_DATA_FILE_PATH,
//    PathConfig.SYNTAX_FEATURE_FOLDER_PATH, PathConfig.SEMANTIC_FEATURE_FOLDER_PATH, PathConfig.TEXT_FEATURE_FOLDER_PATH
    public static void generateTrainingData(String mergeTrainingCsvPath,
                                            String textTrainingCsvPath, String syntaxTrainingCsvPath, String semanticTrainingCsvPath,
                                            String textSyntaxTrainingCsvPath, String textSemanticTrainingCsvPath, String syntaxSemanticTrainingCsvPath,
                                            String textFeatureFolderPath, String syntaxFeatureFolderPath, String semanticFeatureFolderPath) {
//        String trainingCsvPath = PathConfig.TRAINING_DATA_FOLDER + File.separator + "data.csv";
        File mergeCsvFile = new File(mergeTrainingCsvPath);
        if (mergeCsvFile.exists()) {
            mergeCsvFile.delete();
        }

        File textCsvFile = new File(textTrainingCsvPath);
        if (textCsvFile.exists()) {
            mergeCsvFile.delete();
        }
        File syntaxCsvFile = new File(syntaxTrainingCsvPath);
        if (syntaxCsvFile.exists()) {
            syntaxCsvFile.delete();
        }
        File semanticCsvFile = new File(semanticTrainingCsvPath);
        if (semanticCsvFile.exists()) {
            semanticCsvFile.delete();
        }

        File textSyntaxCsvFile = new File(textSyntaxTrainingCsvPath);
        if (textSyntaxCsvFile.exists()) {
            textSyntaxCsvFile.delete();
        }
        File textSemanticCsvFile = new File(textSemanticTrainingCsvPath);
        if (textSemanticCsvFile.exists()) {
            textSemanticCsvFile.delete();
        }
        File syntaxSemanticCsvFile = new File(syntaxSemanticTrainingCsvPath);
        if (syntaxSemanticCsvFile.exists()) {
            syntaxSemanticCsvFile.delete();
        }

        CSVWriter mergeCsvWriter = null;
        CSVWriter textCsvWriter = null;
        CSVWriter syntaxCsvWriter = null;
        CSVWriter semanticCsvWriter = null;
        CSVWriter textSyntaxWriter = null;
        CSVWriter textSemanticWriter = null;
        CSVWriter syntaxSemanticWriter = null;

        try {
            mergeCsvWriter = new CSVWriter(new FileWriter(mergeCsvFile));

            textCsvWriter = new CSVWriter(new FileWriter(textCsvFile));
            syntaxCsvWriter = new CSVWriter(new FileWriter(syntaxCsvFile));
            semanticCsvWriter = new CSVWriter(new FileWriter(semanticCsvFile));

            textSyntaxWriter = new CSVWriter(new FileWriter(textSyntaxCsvFile));
            textSemanticWriter = new CSVWriter(new FileWriter(textSemanticCsvFile));
            syntaxSemanticWriter = new CSVWriter(new FileWriter(syntaxSemanticCsvFile));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // clone data
        File[] syntaxFolders = new File(syntaxFeatureFolderPath).listFiles();
        assert syntaxFolders != null;

        Map<String, List<File>> class2TextFileList = new HashMap<>();
        Map<String, List<File>> class2SyntaxFileList = new HashMap<>();
        Map<String, List<File>> class2SemanticFileList = new HashMap<>();

        for (File syntaxFolder : syntaxFolders) {
            String className = syntaxFolder.getName();
            for (File syntaxSubFolder : Objects.requireNonNull(syntaxFolder.listFiles())) {
                File syntaxFile = new File(syntaxSubFolder.getAbsolutePath() + File.separator + "0.txt");
                String subPath = Tool.getSrcPath(syntaxFile);

                File semanticFile = new File(semanticFeatureFolderPath + File.separator + subPath + File.separator + "0.txt");
                File textFile = new File(textFeatureFolderPath + File.separator + subPath + File.separator + "0.txt");

                List<File> syntaxFileList = class2SyntaxFileList.getOrDefault(className, new ArrayList<>());
                syntaxFileList.add(syntaxFile);
                class2SyntaxFileList.put(className, syntaxFileList);

                List<File> semanticFileList = class2SemanticFileList.getOrDefault(className, new ArrayList<>());
                semanticFileList.add(semanticFile);
                class2SemanticFileList.put(className, semanticFileList);

                List<File> textFileList = class2TextFileList.getOrDefault(className, new ArrayList<>());
                textFileList.add(textFile);
                class2TextFileList.put(className, textFileList);
            }
        }

        List<String> classNameList = new ArrayList<>();
        // similar
        int similarCnt = 0;
        for (Map.Entry<String, List<File>> entry : class2SyntaxFileList.entrySet()) {
            classNameList.add(entry.getKey());
            List<File> syntaxFileList = entry.getValue();
            List<File> semanticFileList = class2SemanticFileList.get(entry.getKey());
            List<File> textFileList = class2TextFileList.get(entry.getKey());

            int len = syntaxFileList.size();

            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    similarCnt++;
                    List<Double> textVecLeft = Tool.getDoubleListFromFile(textFileList.get(i));
                    List<Double> syntaxVecLeft = Tool.getDoubleListFromFile(syntaxFileList.get(i));
                    List<Double> semanticVecLeft = Tool.getDoubleListFromFile(semanticFileList.get(i));

                    List<Double> textVecRight = Tool.getDoubleListFromFile(textFileList.get(i));
                    List<Double> syntaxVecRight = Tool.getDoubleListFromFile(syntaxFileList.get(j));
                    List<Double> semanticVecRight = Tool.getDoubleListFromFile(semanticFileList.get(j));

                    // write to merge csv
                    writeToCsv(mergeCsvWriter, 1, textVecLeft, syntaxVecLeft, semanticVecLeft, textVecRight, syntaxVecRight, semanticVecRight);

                    writeToCsv(textSyntaxWriter, 1, textVecLeft, syntaxVecLeft, textVecRight, syntaxVecRight);
                    writeToCsv(textSemanticWriter, 1, textVecLeft, semanticVecLeft, textVecRight, semanticVecRight);
                    writeToCsv(syntaxSemanticWriter, 1, syntaxVecLeft, semanticVecLeft, syntaxVecRight, semanticVecRight);

                    // write to text
                    writeToCsv(textCsvWriter, 1, textVecLeft, textVecRight);
                    // write to syntax
                    writeToCsv(syntaxCsvWriter, 1, syntaxVecLeft, syntaxVecRight);
                    // write to semantic
                    writeToCsv(semanticCsvWriter, 1, semanticVecLeft, semanticVecRight);
                }
            }
        }

        int notSimilarCnt = 0;
        boolean flag = true;
        int classCnt = classNameList.size();
        for (int i = 0; i < classCnt; i++) {
            if (!flag) {
                break;
            }
            for (int j = i + 1; j < classCnt; j++) {
                if (!flag) {
                    break;
                }
                List<File> textFileListLeft = class2TextFileList.get(classNameList.get(i));
                List<File> syntaxFileListLeft = class2SyntaxFileList.get(classNameList.get(i));
                List<File> semanticFileListLeft = class2SemanticFileList.get(classNameList.get(i));

                List<File> textFileListRight = class2TextFileList.get(classNameList.get(j));
                List<File> syntaxFileListRight = class2SyntaxFileList.get(classNameList.get(j));
                List<File> semanticFileListRight = class2SemanticFileList.get(classNameList.get(j));

                int len1 = syntaxFileListLeft.size();
                int len2 = syntaxFileListRight.size();

                for (int k = 0; k < len1; k++) {
                    if (!flag) {
                        break;
                    }
                    for (int l = 0; l < len2; l++) {
                        if (notSimilarCnt > similarCnt) {
                            flag = false;
                            break;
                        }
                        if ((k + l) % 10 < 5) {
                            continue;
                        }
                        notSimilarCnt++;

                        List<Double> textVecLeft = Tool.getDoubleListFromFile(textFileListLeft.get(k));
                        List<Double> syntaxVecLeft = Tool.getDoubleListFromFile(syntaxFileListLeft.get(k));
                        List<Double> semanticVecLeft = Tool.getDoubleListFromFile(semanticFileListLeft.get(k));

                        List<Double> textVecRight = Tool.getDoubleListFromFile(textFileListRight.get(l));
                        List<Double> syntaxVecRight = Tool.getDoubleListFromFile(syntaxFileListRight.get(l));
                        List<Double> semanticVecRight = Tool.getDoubleListFromFile(semanticFileListRight.get(l));

                        // write to merge csv
                        writeToCsv(mergeCsvWriter, 0, textVecLeft, syntaxVecLeft, semanticVecLeft, textVecRight, syntaxVecRight, semanticVecRight);

                        writeToCsv(textSyntaxWriter, 0, textVecLeft, syntaxVecLeft, textVecRight, syntaxVecRight);
                        writeToCsv(textSemanticWriter, 0, textVecLeft, semanticVecLeft, textVecRight, semanticVecRight);
                        writeToCsv(syntaxSemanticWriter, 0, syntaxVecLeft, semanticVecLeft, syntaxVecRight, semanticVecRight);

                        writeToCsv(textCsvWriter, 0, textVecLeft, textVecRight);
                        // write to syntax
                        writeToCsv(syntaxCsvWriter, 0, syntaxVecLeft, syntaxVecRight);
                        // write to semantic
                        writeToCsv(semanticCsvWriter, 0, semanticVecLeft, semanticVecRight);
                    }
                }

            }
        }

        try {
            assert mergeCsvWriter != null;
            mergeCsvWriter.close();

            assert textCsvWriter != null;
            textCsvWriter.close();
            assert syntaxCsvWriter != null;
            syntaxCsvWriter.close();
            assert semanticCsvWriter != null;
            semanticCsvWriter.close();

            assert textSyntaxWriter != null;
            textSyntaxWriter.close();
            assert textSemanticWriter != null;
            textSemanticWriter.close();
            assert syntaxSemanticWriter != null;
            syntaxSemanticWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToCsv(CSVWriter csvWriter, int similarOrNot, List<Double> textVecLeft, List<Double> textVecRight,
                                   List<Double> syntaxVecLeft, List<Double> syntaxVecRight, List<Double> semanticVecLeft, List<Double> semanticVecRight) {
        int vecLen = textVecLeft.size() + syntaxVecLeft.size() + semanticVecLeft.size() +
                textVecRight.size() + syntaxVecRight.size() + semanticVecRight.size() + 1;
        String[] line = new String[vecLen];
        int index = 0;
        for (Double d : textVecLeft) {
            line[index++] = d.toString();
        }
        for (Double d : syntaxVecLeft) {
            line[index++] = d.toString();
        }
        for (Double d : semanticVecLeft) {
            line[index++] = d.toString();
        }
        for (Double d : textVecRight) {
            line[index++] = d.toString();
        }
        for (Double d : syntaxVecRight) {
            line[index++] = d.toString();
        }
        for (Double d : semanticVecRight) {
            line[index++] = d.toString();
        }
        line[index] = (similarOrNot + "");
        assert csvWriter != null;
        csvWriter.writeNext(line);
    }

    private static void writeToCsv(CSVWriter csvWriter, int similarOrNot, List<Double> syntaxVecLeft, List<Double> syntaxVecRight, List<Double> semanticVecLeft, List<Double> semanticVecRight) {
        int vecLen = syntaxVecLeft.size() + semanticVecLeft.size() + syntaxVecRight.size() + semanticVecRight.size() + 1;
        String[] line = new String[vecLen];
        int index = 0;
        for (Double d : syntaxVecLeft) {
            line[index++] = d.toString();
        }
        for (Double d : semanticVecLeft) {
            line[index++] = d.toString();
        }
        for (Double d : syntaxVecRight) {
            line[index++] = d.toString();
        }
        for (Double d : semanticVecRight) {
            line[index++] = d.toString();
        }
        line[index] = (similarOrNot + "");
        assert csvWriter != null;
        csvWriter.writeNext(line);
    }

    // write training data and label into the csv file
    private static void writeToCsv(CSVWriter csvWriter, int similarOrNot, List<Double> vecLeft, List<Double> vecRight) {
        int vecLen = vecLeft.size() + vecRight.size() + 1;
        String[] line = new String[vecLen];
        int index = 0;
        for (Double d : vecLeft) {
            line[index++] = d.toString();
        }
        for (Double d : vecRight) {
            line[index++] = d.toString();
        }
        line[index] = (similarOrNot + "");
        assert csvWriter != null;
        csvWriter.writeNext(line);
    }

    /**
     * remove comments in the source code
     */
    public static String removeComments(String prgm) {
        int n = prgm.length();
        StringBuilder res = new StringBuilder();
        boolean s_cmt = false;
        boolean m_cmt = false;

        for (int i = 0; i < n; i++) {
            if (s_cmt && prgm.charAt(i) == '\n') {
                s_cmt = false;
            } else if (m_cmt && prgm.charAt(i) == '*' && prgm.charAt(i + 1) == '/') {
                m_cmt = false;
                i++;
            } else if (s_cmt || m_cmt) {
                continue;
            } else if (prgm.charAt(i) == '/' && prgm.charAt(i + 1) == '/') {
                s_cmt = true;
                i++;
            } else if (prgm.charAt(i) == '/' && prgm.charAt(i + 1) == '*') {
                m_cmt = true;
                i++;
            } else {
                res.append(prgm.charAt(i));
            }
        }
        return res.toString();
    }

    /**
     * get specific text content from the method info
     */
    public static String getMethodInfoTextContent(MethodInfo methodInfo, File sourceFile) {
        int startLine = methodInfo.getStartLine();
        int endLine = methodInfo.getEndLine();

        StringBuilder sb = new StringBuilder();
        try {
            List<String> contents = FileUtils.readLines(sourceFile, StandardCharsets.UTF_8);
            for (int i = startLine - 1; i < endLine; i++) {
                sb.append(contents.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Tool.removeComments(sb.toString()).replace("\n", "").replace("\r", "").
                replaceAll("\t+", " ").replaceAll(" +", " ").trim();
    }
}
