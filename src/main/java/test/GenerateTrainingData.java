package test;

import call.Call;
import cfg.CFGGraph;
import config.CmdConfig;
import config.PathConfig;
import embedding.Graph2Vec;
import embedding.Word2Vec;
import feature.Feature;
import joern.CPG;
import method.Method;
import method.MethodCall;
import method.MethodInfo;
import org.apache.commons.io.FileUtils;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author cary.shi on 2019/12/26
 */
public class GenerateTrainingData {
    public static void main(String[] args) {
        CPG cpg = new CPG();
        Method methodClass = new Method();
        Call callClass = new Call();

//        List<File> fileList = Tool.getSourceFilesFromPath("G:\\share\\CloneData\\data\\src");
        List<File> fileList = Tool.getSourceFilesFromPath("/mnt/share/CloneData/data/src");

        // for save graph json file
        int index = 0;
//        Map<String, String> cfgDotFilePath2graphJsonFilePath = new HashMap<>();
        // should not map file path to syntax and semantic feature
        List<String> dot2cfgPath = new ArrayList<>();
        List<String> dot2astPath = new ArrayList<>();

        for (File sourceFile : fileList) {

            System.out.println(sourceFile.getAbsolutePath());

//            if (!sourceFile.getAbsolutePath().contains("/mnt/share/CloneData/data/src/0")) {
//                continue;
//            }

            String subPath = Tool.getFolderAndFilePath(sourceFile);

//            String s = PathConfig.FEATURE_CFG_FOLDER_PATH + File.separator + subPath;
//            File dotFolder = new File(s);
//            if (dotFolder.exists()) {
//                continue;
//            }

            // get cpg
            String cpgPath = PathConfig.CPG_FOLDER_PATH + File.separator + subPath + File.separator + "cpg.bin.zip";
//            File cpgFile = cpg.getCPGFileBySourceFolder(sourceFile, cpgPath);
            File cpgFile = new File(cpgPath);

            // get ast
            String astPath = PathConfig.AST_FOLDER_PATH + File.separator + subPath + File.separator + "ast.dot";
//            File astFile = cpg.getASTFileByCPGFile(cpgFile, astPath);
            File astFile = new File(astPath);

            // get cfg
            String cfgPath = PathConfig.CFG_FOLDER_PATH + File.separator + subPath + File.separator + "cfg.dot";
//            File cfgFile = cpg.getCFGFileByCPGFile(cpgFile, cfgPath);
            File cfgFile = new File(cfgPath);

            // get method info
            String methodInfoPath = PathConfig.METHOD_INFO_FOLDER_PATH + File.separator + subPath + File.separator + "methodInfo.txt";
//            File methodInfoFile = cpg.getMethodInfoFileByCpgFile(cpgFile, methodInfoPath);
            File methodInfoFile = new File(methodInfoPath);

            // get call
            String callPath = PathConfig.CALL_FOLDER_PATH + File.separator + subPath + File.separator + "call.txt";
//            File callFile = cpg.getCallFileByCPGFile(cpgFile, callPath);
            File callFile = new File(callPath);

            // get method info list, contain location, start line and end line, etc
            List<MethodInfo> methodInfoList = MethodInfo.getMethodInfoListByMethodInfoFile(methodInfoFile);

            // get method, contain ast and cfg
            List<Method> methodList = methodClass.getMethodList(astFile, cfgFile);

            // method to simplify cfg graph (no literal)
            Map<Method, CFGGraph> methodCFGGraphMap = new HashMap<>();
            for (Method method : methodList) {
                if (method.getCfg() == null || method.getCfg().getCfgNodeList().size() == 1 ||
                        method.getAst() == null || method.getAst().getAstNodeList().size() == 1) {
//                    System.out.println(method.getName());
//                    System.out.println(method.getCfg().getCfgNodeList());
                    continue;
                }
                CFGGraph cfgGraph = Tool.getCFGGraphOfSelectedMethod(methodList, method);
                methodCFGGraphMap.put(method, cfgGraph);
            }

            // get method call relationship
            List<Call> callList = callClass.resolveFuncCallFile(callFile, methodInfoFile);

            List<MethodCall> methodCallList = MethodCall.getMethodCallListFromMethodAndCallInfo(methodList, methodInfoList, callList);

            if (methodCallList.isEmpty()) {
                // do not have call relationship, use it directly
                String featureCfgDotPath = PathConfig.FEATURE_CFG_FOLDER_PATH + File.separator + subPath + File.separator + "0.dot";
                Method selectedMethod = null;
                // some method contains operator.<>
                for (Method method : methodList) {
                    if (method.getName().equals(methodInfoList.get(0).getMethodName())) {
                        selectedMethod = method;
                        break;
                    }
                }

                assert selectedMethod != null;
                String astString = Tool.traverseAST(selectedMethod.getAst());
                File astContentFile = new File(PathConfig.AST_CONTENT_FOLDER_PATH + File.separator + index + ".txt");
                if (astContentFile.exists()) {
                    astContentFile.delete();
                }

                // the relationship between methods and function
                File methodInFuncFolder = new File(PathConfig.METHOD_IN_FUNC_FOLDER_PATH + File.separator + subPath);
                if (!methodInFuncFolder.exists()) {
                    methodInFuncFolder.mkdirs();
                }
                File methodInFuncFile = new File(methodInFuncFolder.getAbsolutePath() + File.separator + "methodInFunc.txt");
                String methodInFunc = "0:" + selectedMethod.getName();
                try {
                    FileUtils.write(astContentFile, astString, StandardCharsets.UTF_8, true);
                    FileUtils.write(methodInFuncFile, methodInFunc, StandardCharsets.UTF_8, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // remove some literal
                CFGGraph cfgGraph = Tool.getCFGGraphOfSelectedMethod(methodList, selectedMethod);

                // remove redundant node
                CFGGraph simplifyCfgGraph = Tool.simplifyCFGGraphOfAddedCallRelationship(cfgGraph);

                File graphJsonFile = Tool.generateGraphFile(simplifyCfgGraph, PathConfig.CFG_CONTENT_FOLDER_PATH + File.separator + index + ".json");
                index++;

                File cfgDotFile = Tool.constructCFGDotFileOfCFGGraph(simplifyCfgGraph, featureCfgDotPath);

                dot2cfgPath.add(cfgDotFile.getAbsolutePath() + " " + graphJsonFile.getAbsolutePath());
                dot2astPath.add(cfgDotFile.getAbsolutePath() + " " + astContentFile.getAbsolutePath());
//                cfgDotFilePath2graphJsonFilePath.put(cfgDotFile.getAbsolutePath(), graphJsonFile.getAbsolutePath());

            } else {
                // the relationship between methods and function
                File methodInFuncFolder = new File(PathConfig.METHOD_IN_FUNC_FOLDER_PATH + File.separator + subPath);
                if (!methodInFuncFolder.exists()) {
                    methodInFuncFolder.mkdirs();
                }
                File methodInFuncFile = new File(methodInFuncFolder.getAbsolutePath() + File.separator + "methodInFunc.txt");

                // construct by call relationship
                List<Feature> featureList = Feature.getFeatureFromMethodCallList(methodCallList);
                for (int i = 0; i < featureList.size(); i++) {
                    Feature feature = featureList.get(i);

                    String astString = Tool.traverseAST(feature);
                    File astContentFile = new File(PathConfig.AST_CONTENT_FOLDER_PATH + File.separator + index + ".txt");
                    if (astContentFile.exists()) {
                        astContentFile.delete();
                    }

                    StringBuilder methodInFunc = new StringBuilder();
                    methodInFunc.append(i).append(":");
                    Set<String> methodNameSet = new HashSet<>();
                    for (MethodCall methodCall : feature.getMethodCallSet()) {
                        if (!methodCall.getCallerMethod().getName().contains("<operator>")) {
                            methodNameSet.add(methodCall.getCallerMethod().getName());
                        }
                        if (!methodCall.getCalleeMethod().getName().contains("<operator>")) {
                            methodNameSet.add(methodCall.getCalleeMethod().getName());
                        }
                    }
                    for (String name : methodNameSet) {
                        methodInFunc.append(name).append(",");
                    }

                    try {
                        FileUtils.write(astContentFile, astString, StandardCharsets.UTF_8, true);
                        FileUtils.write(methodInFuncFile, methodInFunc.toString() + PathConfig.LINE_SEP, StandardCharsets.UTF_8, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    CFGGraph cfgGraph = Feature.generateCFGByFeature(feature, methodCFGGraphMap);

                    CFGGraph simplifyCfgGraph = Tool.simplifyCFGGraphOfAddedCallRelationship(cfgGraph);

                    File graphJsonFile = Tool.generateGraphFile(simplifyCfgGraph, PathConfig.CFG_CONTENT_FOLDER_PATH + File.separator + index + ".json");
                    index++;

                    String featureCfgDotPath = PathConfig.FEATURE_CFG_FOLDER_PATH + File.separator + subPath + File.separator + i + ".dot";
                    File cfgDotFile = Tool.constructCFGDotFileOfCFGGraph(simplifyCfgGraph, featureCfgDotPath);

                    dot2cfgPath.add(cfgDotFile.getAbsolutePath() + " " + graphJsonFile.getAbsolutePath());
                    dot2astPath.add(cfgDotFile.getAbsolutePath() + " " + astContentFile.getAbsolutePath());
//                    cfgDotFilePath2graphJsonFilePath.put(cfgDotFile.getAbsolutePath(), graphJsonFile.getAbsolutePath());
                }
            }
        }

        // save dot2cfg map
        File cfgDotFilePath2graphJsonFile = new File(PathConfig.DOT2CFG_PATH);
        if (cfgDotFilePath2graphJsonFile.exists()) {
            cfgDotFilePath2graphJsonFile.delete();
        }
        try {
            FileUtils.writeLines(cfgDotFilePath2graphJsonFile, dot2cfgPath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // save dot2ast map
        File dotFile2astContentFile = new File(PathConfig.DOT2AST_PATH);
        if (dotFile2astContentFile.exists()) {
            dotFile2astContentFile.delete();
        }
        try {
            FileUtils.writeLines(dotFile2astContentFile, dot2astPath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // generate ast corpus
        File astCorpusFile = Word2Vec.generateAstCorpus(PathConfig.AST_CONTENT_FOLDER_PATH, PathConfig.AST_WORD2VEC_CORPUS_FILE_PATH);
        // generate word2vec vectors for corpus
        File word2vecOutFile = Word2Vec.generateWord2vecFile(astCorpusFile, PathConfig.AST_WORD2VEC_OUT_FILE_PATH, CmdConfig.WORD2VEC_CMD_PATH, 16);
        // generate syntax feature, according to the folder structure
        Word2Vec.generateSyntaxFeatureFiles(word2vecOutFile, dotFile2astContentFile);

        // generate graph2vec vectors for cfg
        File graph2vecOutFile = Graph2Vec.generateGraph2VecFeatureFile(PathConfig.CFG_CONTENT_FOLDER_PATH, PathConfig.CFG_GRAPH2VEC_OUT_PATH, 16);
        // generate semantic feature, according to the folder structure
        Graph2Vec.generateSemanticFeatureFiles(graph2vecOutFile, cfgDotFilePath2graphJsonFile);

        File trainingDataFile = Tool.generateTrainingData(PathConfig.TRAINING_MERGE_DATA_FILE_PATH, PathConfig.TRAINING_SYNTAX_DATA_FILE_PATH,
                PathConfig.TRAINING_SEMANTIC_DATA_FILE_PATH, PathConfig.SYNTAX_FEATURE_FOLDER_PATH, PathConfig.SEMANTIC_FEATURE_FOLDER_PATH);
    }
}
