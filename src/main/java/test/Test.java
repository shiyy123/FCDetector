package test;

import ast.AST;
import cfg.CFG;
import cfg.CFGGraph;
import config.PathConfig;
import joern.CPG;
import method.Method;
import method.MethodInfo;
import tool.Tool;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author cary.shi on 2019/12/24
 */
public class Test {
    public static void main(String[] args) {
        String srcFolderPath = PathConfig.base + File.separator + "src";
        File srcFolder = new File(srcFolderPath);

        CPG cpg = new CPG();
        cpg.generateCallForSourceFolder("/mnt/share/CloneData/data/src");

//        File cpgFile = cpg.getCPGFileBySourceFolder(srcFolder, PathConfig.CPG_PATH);
//        System.out.println(cpgFile.getAbsolutePath());

//        File methodInfoFile = cpg.getFuncInfoFileByCpgFile(cpgFile, PathConfig.FUNC_FILE_PATH);
//        System.out.println(methodInfoFile.getAbsolutePath());
        File methodInfoFile = new File(PathConfig.FUNC_FILE_PATH);

//        File cfgFile = cpg.getCFGFileByCPGFile(cpgFile, PathConfig.CFG_FILE_PATH);
//        System.out.println(cfgFile.getAbsolutePath());
        File cfgFile = new File(PathConfig.CFG_FILE_PATH);

//        File callFile = cpg.getCallFileByCPGFile(cpgFile, PathConfig.CALL_FILE_PATH);
//        System.out.println(callFile.getAbsolutePath());
        File callFile = new File(PathConfig.CALL_FILE_PATH);

//        File pdgFile = cpg.getPDGFileByCPGFile(cpgFile, PathConfig.PDG_FILE_PATH);
//        System.out.println(pdgFile.getAbsolutePath());

//        File astFile = cpg.getASTFileByCPGFile(cpgFile, PathConfig.AST_FILE_PATH);
//        System.out.println(astFile.getAbsolutePath());
        File astFile = new File(PathConfig.AST_FILE_PATH);

        CFG cfg = new CFG();
        List<Method> cfgMethodList = cfg.getMethodCFGListFromCFGFile(cfgFile);

        AST ast = new AST();
        List<Method> astMethodList = ast.getMethodASTListFromASTFile(astFile);

        Method method = new Method();
        List<Method> methodList = method.mergeASTMethodListAndCFGMethodList(astMethodList, cfgMethodList);

        // simplify cfg graph
        CFGGraph cfgGraph = Tool.getCFGGraphOfSelectedMethod(methodList, methodList.get(2));
//        File cfgDotFile = Tool.constructCFGDotFileOfCFGGraph(cfgGraph, "/mnt/share/CloneData/data/cfg2.dot");

        Map<String, List<MethodInfo>> methodPath2MethodInfoList = MethodInfo.getMethodPath2MethodInfoByMethodInfoFile(methodInfoFile);
        System.out.println(methodPath2MethodInfoList.size());

    }
}
