package tool;

import cfg.CFG;
import cfg.CFGEdge;
import cfg.CFGGraph;
import cfg.CFGNode;
import config.CFGConfig;
import config.PathConfig;
import method.Method;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import process.ProcessExecutorList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

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
                    append(formatNodeId).append(PathConfig.LINE_SEP).
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
                    append(formatNodeId).append(PathConfig.LINE_SEP);

            if (code.length() > 0) {
                nodeLine.append("code:").append(cfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "").replace("\"", "\\\"")).append(PathConfig.LINE_SEP);
            }
            if (lineNum.length() > 0) {
                nodeLine.append("lineNum:").append(cfgNode.getProperties().getOrDefault(CFGConfig.LINE_NUMBER_PROPERTY, "")).append(PathConfig.LINE_SEP);
            }
            if (columnNum.length() > 0) {
                nodeLine.append("columnNum:").append(cfgNode.getProperties().getOrDefault(CFGConfig.COLUMN_NUMBER_PROPERTY, "")).append(PathConfig.LINE_SEP);
            }
            nodeLine.append("\"];");

            try {
                FileUtils.write(dotFile, nodeLine.toString() + PathConfig.LINE_SEP, StandardCharsets.UTF_8, true);
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


                    if (cnt1 == 1 && cnt2 == 1) {
                        CFGNode outOutCfgNode = id2CFGNode.get(toOutEdge.getOut());
                        String outOutCode = outOutCfgNode.getProperties().getOrDefault(CFGConfig.CODE_PROPERTY, "");
                        int outOutLineNum = Integer.parseInt(outOutCfgNode.getProperties().getOrDefault(CFGConfig.LINE_NUMBER_PROPERTY, "-1"));

                        if (inCode.contains(outOutCode) && inLineNum == outOutLineNum) {

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
                    if (cnt1 == 1) {
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
}
