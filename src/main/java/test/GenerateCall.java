package test;

import call.Call;
import cfg.CFGGraph;
import config.PathConfig;
import feature.CallGraph;
import feature.Feature;
import joern.CPG;
import method.Method;
import method.MethodCall;
import method.MethodInfo;
import tool.Tool;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cary.shi on 2019/12/26
 */
public class GenerateCall {
    public static void main(String[] args) {
        CPG cpg = new CPG();
        Method methodClass = new Method();
        Call callClass = new Call();

//        List<File> fileList = Tool.getSourceFilesFromPath("G:\\share\\CloneData\\data\\src");
        List<File> fileList = Tool.getSourceFilesFromPath("/mnt/share/CloneData/data/src");

        for (File sourceFile : fileList) {
//            if (!sourceFile.getAbsolutePath().equals("/mnt/share/CloneData/data/src/1/150.cpp")) {
//                continue;
//            }


            System.out.println(sourceFile.getAbsolutePath());
            String subPath = Tool.getFolderAndFilePath(sourceFile);

            String s = PathConfig.FEATURE_CFG_FOLDER_PATH + File.separator + subPath;
            File dotFolder = new File(s);
            if (dotFolder.exists()) {
                continue;
            }

            // get cpg
            String cpgPath = PathConfig.CPG_FOLDER_PATH + File.separator + subPath + File.separator + "cpg.bin.zip";
            File cpgFile = cpg.getCPGFileBySourceFolder(sourceFile, cpgPath);
//            File cpgFile = new File(cpgPath);

            // get ast
            String astPath = PathConfig.AST_FOLDER_PATH + File.separator + subPath + File.separator + "ast.dot";
            File astFile = cpg.getASTFileByCPGFile(cpgFile, astPath);
//            File astFile = new File(astPath);

            // get cfg
            String cfgPath = PathConfig.CFG_FOLDER_PATH + File.separator + subPath + File.separator + "cfg.dot";
            File cfgFile = cpg.getCFGFileByCPGFile(cpgFile, cfgPath);
//            File cfgFile = new File(cfgPath);

            // get method info
            String methodInfoPath = PathConfig.METHOD_INFO_FOLDER_PATH + File.separator + subPath + File.separator + "methodInfo.txt";
            File methodInfoFile = cpg.getMethodInfoFileByCpgFile(cpgFile, methodInfoPath);
//            File methodInfoFile = new File(methodInfoPath);

            // get call
            String callPath = PathConfig.CALL_FOLDER_PATH + File.separator + subPath + File.separator + "call.txt";
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
                String featureCfgDotPath = PathConfig.FEATURE_CFG_FOLDER_PATH + File.separator + subPath + File.separator + "method.dot";
                Method selectedMethod = null;
                for (Method method : methodList) {
                    if (method.getName().equals(methodInfoList.get(0).getMethodName())) {
                        selectedMethod = method;
                        break;
                    }
                }

                CFGGraph cfgGraph = Tool.getCFGGraphOfSelectedMethod(methodList, selectedMethod);
                File cfgDotFile = Tool.constructCFGDotFileOfCFGGraph(cfgGraph, featureCfgDotPath);
            } else {
                // construct by call relationship
//                System.out.println(methodCallList.size());
//                System.out.println(methodCallList);
                List<Feature> featureList = Feature.getFeatureFromMethodCallList(methodCallList);
                for (int i = 0; i < featureList.size(); i++) {
                    Feature feature = featureList.get(i);
                    CFGGraph cfgGraph = Feature.generateCFGByFeature(feature, methodCFGGraphMap);
                    String featureCfgDotPath = PathConfig.FEATURE_CFG_FOLDER_PATH + File.separator + subPath + File.separator + i + ".dot";
                    File cfgDotFile = Tool.constructCFGDotFileOfCFGGraph(cfgGraph, featureCfgDotPath);
                }
            }

//            System.exit(0);
        }
    }
}
