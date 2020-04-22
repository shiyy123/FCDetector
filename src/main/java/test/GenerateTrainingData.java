package test;

import call.Call;
import cfg.CFGGraph;
import config.CmdConfig;
import config.Config;
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
import java.nio.file.Path;
import java.util.*;

/**
 * @author cary.shi on 2019/12/26
 */
public class GenerateTrainingData {
    public static void main(String[] args) {

        String basePath = args[0];
        String rootPath = args[1];
        String sourcePath = args[2];

        PathConfig.getInstance().setBase(basePath);
        PathConfig.getInstance().setROOT_PATH(rootPath);

        PathConfig.getInstance().init();

        CPG cpg = new CPG();
        Method methodClass = new Method();
        Call callClass = new Call();

//        List<File> fileList = Tool.getSourceFilesFromPath("G:\\share\\CloneData\\data\\src");
        List<File> fileList = Tool.getSourceFilesFromPath(sourcePath);

        // for save graph json file
        int index = 0;
//        Map<String, String> cfgDotFilePath2graphJsonFilePath = new HashMap<>();
        // should not map file path to syntax and semantic feature
        List<String> dot2cfgPath = new ArrayList<>();
        List<String> dot2astPath = new ArrayList<>();
        List<String> dot2textPath = new ArrayList<>();

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
            String cpgPath = PathConfig.getInstance().getCPG_FOLDER_PATH() + File.separator + subPath + File.separator + "cpg.bin.zip";
            File cpgFile = cpg.getCPGFileBySourceFolder(sourceFile, cpgPath);
//            File cpgFile = new File(cpgPath);

            // get ast
            String astPath = PathConfig.getInstance().getAST_FOLDER_PATH() + File.separator + subPath + File.separator + "ast.dot";
            File astFile = cpg.getASTFileByCPGFile(cpgFile, astPath);
//            File astFile = new File(astPath);

            // get cfg
            String cfgPath = PathConfig.getInstance().getCFG_FOLDER_PATH() + File.separator + subPath + File.separator + "cfg.dot";
            File cfgFile = cpg.getCFGFileByCPGFile(cpgFile, cfgPath);
//            File cfgFile = new File(cfgPath);

            // get method info
            String methodInfoPath = PathConfig.getInstance().getMETHOD_INFO_FOLDER_PATH() + File.separator + subPath + File.separator + "methodInfo.txt";
            File methodInfoFile = cpg.getMethodInfoFileByCpgFile(cpgFile, methodInfoPath);
//            File methodInfoFile = new File(methodInfoPath);

            // get call
            String callPath = PathConfig.getInstance().getCALL_FOLDER_PATH() + File.separator + subPath + File.separator + "call.txt";
            File callFile = cpg.getCallFileByCPGFile(cpgFile, callPath);
//            File callFile = new File(callPath);

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
                String featureCfgDotPath = PathConfig.getInstance().getFEATURE_CFG_FOLDER_PATH() + File.separator + subPath + File.separator + "0.dot";
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
                File astContentFile = new File(PathConfig.getInstance().getAST_CONTENT_FOLDER_PATH() + File.separator + index + ".txt");
                if (astContentFile.exists()) {
                    astContentFile.delete();
                }

                File textContentFile = new File(PathConfig.getInstance().getTEXT_CONTENT_FOLDER_PATH() + File.separator + index + ".txt");
                if (textContentFile.exists()) {
                    textContentFile.delete();
                }

                // the relationship between methods and function
                File methodInFuncFolder = new File(PathConfig.getInstance().getMETHOD_IN_FUNC_FOLDER_PATH() + File.separator + subPath);
                if (!methodInFuncFolder.exists()) {
                    methodInFuncFolder.mkdirs();
                }
                File methodInFuncFile = new File(methodInFuncFolder.getAbsolutePath() + File.separator + "methodInFunc.txt");
                String methodInFunc = "0:" + selectedMethod.getName();
                try {
                    // text code representation
                    String srcContent = FileUtils.readFileToString(sourceFile, StandardCharsets.UTF_8);
                    String textString = Tool.removeComments(srcContent);
                    FileUtils.write(textContentFile, astString, StandardCharsets.UTF_8, true);
                    // ast code representation
                    FileUtils.write(astContentFile, astString, StandardCharsets.UTF_8, true);
                    // cfg code representation
                    FileUtils.write(methodInFuncFile, methodInFunc, StandardCharsets.UTF_8, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // remove some literal
                CFGGraph cfgGraph = Tool.getCFGGraphOfSelectedMethod(methodList, selectedMethod);

                // remove redundant node
                CFGGraph simplifyCfgGraph = Tool.simplifyCFGGraphOfAddedCallRelationship(cfgGraph);

                File graphJsonFile = Tool.generateGraphFile(simplifyCfgGraph, PathConfig.getInstance().getCFG_CONTENT_FOLDER_PATH() + File.separator + index + ".json");
                index++;

                File cfgDotFile = Tool.constructCFGDotFileOfCFGGraph(simplifyCfgGraph, featureCfgDotPath);

                dot2cfgPath.add(cfgDotFile.getAbsolutePath() + " " + graphJsonFile.getAbsolutePath());
                dot2astPath.add(cfgDotFile.getAbsolutePath() + " " + astContentFile.getAbsolutePath());
                dot2textPath.add(cfgDotFile.getAbsolutePath() + " " + textContentFile.getAbsolutePath());
//                cfgDotFilePath2graphJsonFilePath.put(cfgDotFile.getAbsolutePath(), graphJsonFile.getAbsolutePath());

            } else {
                // the relationship between methods and function
                File methodInFuncFolder = new File(PathConfig.getInstance().getMETHOD_IN_FUNC_FOLDER_PATH() + File.separator + subPath);
                if (!methodInFuncFolder.exists()) {
                    methodInFuncFolder.mkdirs();
                }
                File methodInFuncFile = new File(methodInFuncFolder.getAbsolutePath() + File.separator + "methodInFunc.txt");

                // construct by call relationship
                List<Feature> featureList = Feature.getFeatureFromMethodCallList(methodCallList);
                for (int i = 0; i < featureList.size(); i++) {
                    Feature feature = featureList.get(i);

                    String astString = Tool.traverseAST(feature);
                    File astContentFile = new File(PathConfig.getInstance().getAST_CONTENT_FOLDER_PATH() + File.separator + index + ".txt");
                    if (astContentFile.exists()) {
                        astContentFile.delete();
                    }

                    File textContentFile = new File(PathConfig.getInstance().getTEXT_CONTENT_FOLDER_PATH() + File.separator + index + ".txt");
                    if (textContentFile.exists()) {
                        textContentFile.delete();
                    }

                    // write function and method relationship
                    StringBuilder methodInFunc = new StringBuilder();
                    methodInFunc.append(i).append(":");
                    Set<String> methodNameSet = new HashSet<>();
                    Set<MethodInfo> methodInfoSet = new HashSet<>();

                    for (MethodCall methodCall : feature.getMethodCallSet()) {
                        if (!methodCall.getCallerMethod().getName().contains("<operator>")) {
                            methodNameSet.add(methodCall.getCallerMethod().getName());
                        }
                        if (!methodCall.getCalleeMethod().getName().contains("<operator>")) {
                            methodNameSet.add(methodCall.getCalleeMethod().getName());
                        }
                        if (methodCall.getCalleeMethodInfo() != null) {
                            methodInfoSet.add(methodCall.getCalleeMethodInfo());
                        }
                        if (methodCall.getCallerMethodInfo() != null) {
                            methodInfoSet.add(methodCall.getCallerMethodInfo());
                        }
                    }
                    for (String name : methodNameSet) {
                        methodInFunc.append(name).append(",");
                    }

                    StringBuilder textContent = new StringBuilder();
                    // get content of feature
                    for (MethodInfo methodInfo : methodInfoSet) {
                        textContent.append(Tool.getMethodInfoTextContent(methodInfo, sourceFile)).append(" ");
                    }

                    try {
                        FileUtils.write(astContentFile, astString, StandardCharsets.UTF_8, true);
                        FileUtils.write(methodInFuncFile, methodInFunc.toString() + Config.LINE_SEP, StandardCharsets.UTF_8, true);
                        FileUtils.write(textContentFile, textContent.toString().trim() + Config.LINE_SEP, StandardCharsets.UTF_8, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    CFGGraph cfgGraph = Feature.generateCFGByFeature(feature, methodCFGGraphMap);

                    CFGGraph simplifyCfgGraph = Tool.simplifyCFGGraphOfAddedCallRelationship(cfgGraph);

                    File graphJsonFile = Tool.generateGraphFile(simplifyCfgGraph, PathConfig.getInstance().getCFG_CONTENT_FOLDER_PATH() + File.separator + index + ".json");
                    index++;

                    String featureCfgDotPath = PathConfig.getInstance().getFEATURE_CFG_FOLDER_PATH() + File.separator + subPath + File.separator + i + ".dot";
                    File cfgDotFile = Tool.constructCFGDotFileOfCFGGraph(simplifyCfgGraph, featureCfgDotPath);

                    dot2cfgPath.add(cfgDotFile.getAbsolutePath() + " " + graphJsonFile.getAbsolutePath());
                    dot2astPath.add(cfgDotFile.getAbsolutePath() + " " + astContentFile.getAbsolutePath());
                    dot2textPath.add(cfgDotFile.getAbsolutePath() + " " + textContentFile.getAbsolutePath());
//                    cfgDotFilePath2graphJsonFilePath.put(cfgDotFile.getAbsolutePath(), graphJsonFile.getAbsolutePath());
                }
            }
        }

        // save dot2cfg map
        File cfgDotFilePath2graphJsonFile = new File(PathConfig.getInstance().getDOT2CFG_PATH());
        if (cfgDotFilePath2graphJsonFile.exists()) {
            cfgDotFilePath2graphJsonFile.delete();
        }
        try {
            FileUtils.writeLines(cfgDotFilePath2graphJsonFile, dot2cfgPath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // save dot2ast map
        File dotFile2astContentFile = new File(PathConfig.getInstance().getDOT2AST_PATH());
        if (dotFile2astContentFile.exists()) {
            dotFile2astContentFile.delete();
        }
        try {
            FileUtils.writeLines(dotFile2astContentFile, dot2astPath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // save dot2text map
        File dotFile2textContentFile = new File(PathConfig.getInstance().getDOT2TEXT_PATH());
        if (dotFile2textContentFile.exists()) {
            dotFile2textContentFile.delete();
        }
        try {
            FileUtils.writeLines(dotFile2textContentFile, dot2textPath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // generate ast corpus
        File astCorpusFile = Word2Vec.generateCorpusFromFolder(PathConfig.getInstance().getAST_CONTENT_FOLDER_PATH(), PathConfig.getInstance().getAST_WORD2VEC_CORPUS_FILE_PATH());
        // generate word2vec vectors for corpus
        File word2vecOutFile = Word2Vec.generateWord2vecFile(astCorpusFile, PathConfig.getInstance().getAST_WORD2VEC_OUT_FILE_PATH(), PathConfig.getInstance().getWORD2VEC_CMD_PATH(), 16);
        // generate syntax feature, according to the folder structure
        Word2Vec.generateSyntaxFeatureFiles(word2vecOutFile, dotFile2astContentFile, PathConfig.getInstance().getSYNTAX_FEATURE_FOLDER_PATH());

        // generate graph2vec vectors for cfg
        File graph2vecOutFile = Graph2Vec.generateGraph2VecFeatureFile(PathConfig.getInstance().getCFG_CONTENT_FOLDER_PATH(), PathConfig.getInstance().getCFG_GRAPH2VEC_OUT_PATH(), 16);
        // generate semantic feature, according to the folder structure
        Graph2Vec.generateSemanticFeatureFiles(graph2vecOutFile, cfgDotFilePath2graphJsonFile);

        // generate text vectors
        File textCorpusFile = Word2Vec.generateCorpusFromFolder(PathConfig.getInstance().getTEXT_CONTENT_FOLDER_PATH(), PathConfig.getInstance().getTEXT_WORD2VEC_CORPUS_FILE_PATH());
        // generate word2vec vectors for corpus
        File textWord2vecOutFile = Word2Vec.generateWord2vecFile(textCorpusFile, PathConfig.getInstance().getTEXT_WORD2VEC_OUT_FILE_PATH(), PathConfig.getInstance().getWORD2VEC_CMD_PATH(), 16);
        // generate syntax feature
        Word2Vec.generateSyntaxFeatureFiles(textWord2vecOutFile, dotFile2textContentFile, PathConfig.getInstance().getTEXT_FEATURE_FOLDER_PATH());

        Tool.generateTrainingData(PathConfig.getInstance().getTRAINING_MERGE_DATA_FILE_PATH(),
                PathConfig.getInstance().getTRAINING_TEXT_DATA_FILE_PATH(), PathConfig.getInstance().getTRAINING_SYNTAX_DATA_FILE_PATH(),
                PathConfig.getInstance().getTRAINING_SEMANTIC_DATA_FILE_PATH(), PathConfig.getInstance().getTRAINING_TEXT_SYNTAX_DATA_FILE_PATH(),
                PathConfig.getInstance().getTRAINING_TEXT_SEMANTIC_DATA_FILE_PATH(), PathConfig.getInstance().getTRAINING_SYNTAX_SEMANTIC_DATA_FILE_PATH(),
                PathConfig.getInstance().getTEXT_FEATURE_FOLDER_PATH(), PathConfig.getInstance().getSYNTAX_FEATURE_FOLDER_PATH(),
                PathConfig.getInstance().getSEMANTIC_FEATURE_FOLDER_PATH());
    }
}
