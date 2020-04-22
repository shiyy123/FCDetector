package ast;

import config.CFGConfig;
import config.PathConfig;
import method.Method;
import method.MethodInfo;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author cary.shi on 2019/11/28
 */
public class AST {

    private List<ASTNode> astNodeList;

    public AST(List<ASTNode> astNodeList) {
        this.astNodeList = astNodeList;
    }

    public AST() {
    }

    // 从ast json文件中获取方法对下的ast
    public List<Method> getMethodASTListFromASTFile(File astJsonFile) {
        List<Method> methodList = new ArrayList<>();
        String fileContent = null;
        try {
            fileContent = FileUtils.readFileToString(astJsonFile, StandardCharsets.UTF_8);
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
            JSONArray astJsonArray = funcJson.getJSONArray("AST");

            // method node's edge list
            List<ASTEdge> methodNodeEdgeList = new ArrayList<>();

            List<ASTNode> astNodeList = new ArrayList<>();
            for (int j = 0; j < astJsonArray.length(); j++) {

                JSONObject astJson = astJsonArray.getJSONObject(j);
                String astId = astJson.getString("id");
                JSONArray astEdgeJsonArray = astJson.getJSONArray("edges");
                List<ASTEdge> edgeList = new ArrayList<>();
                for (int k = 0; k < astEdgeJsonArray.length(); k++) {
                    JSONObject astEdgeJsonObject = astEdgeJsonArray.getJSONObject(k);
                    String edgeId = astEdgeJsonObject.getString("id");
                    String edgeIn = astEdgeJsonObject.getString("in");
                    String edgeOut = astEdgeJsonObject.getString("out");
                    ASTEdge edge = new ASTEdge(edgeId, edgeIn, edgeOut);
                    edgeList.add(edge);

                    // add method node's edge
                    if (edgeIn.equals(funcId) || edgeOut.equals(funcId)) {
                        methodNodeEdgeList.add(edge);
                    }
                }

                Map<String, String> propertyMap = new HashMap<>();
                JSONArray astPropertyJsonArray = astJson.getJSONArray("properties");
                for (int k = 0; k < astPropertyJsonArray.length(); k++) {
                    JSONObject astPropertyJson = astPropertyJsonArray.getJSONObject(k);
                    String propertyKey = astPropertyJson.getString("key");
                    String propertyValue = astPropertyJson.getString("value");
                    propertyMap.put(propertyKey, propertyValue);
                }

                ASTNode astNode = new ASTNode(astId, edgeList, propertyMap);
                astNodeList.add(astNode);
            }

            // add method node
            Map<String, String> propertyMap = new HashMap<>();
            propertyMap.put(CFGConfig.CODE_PROPERTY, funcName);
            astNodeList.add(new ASTNode(funcId, methodNodeEdgeList, propertyMap));

            AST ast = new AST(astNodeList);
            Method method = new Method(funcId, funcName, ast);
            methodList.add(method);
        }
        return methodList;
    }

    public List<ASTNode> getAstNodeList() {
        return astNodeList;
    }

    public void setAstNodeList(List<ASTNode> astNodeList) {
        this.astNodeList = astNodeList;
    }

    public List<File> sourceFile2ASTFileList(File sourceFile) {
        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);

        File[] files = new File(PathConfig.getInstance().getAST_FOLDER_PATH() + File.separator + folderAndFilePath).listFiles();
        List<File> fileList = new ArrayList<>();
        assert files != null;
        Collections.addAll(fileList, files);
        return fileList;
    }

    // 为每个method生成相应的ast dot文件，存放路径 base/ast/{classFolder/sourceFile/methodName_signature/ast.dot}
    public void generateASTForEachMethod(File methodInfoFile) {
        Map<String, List<MethodInfo>> methodPath2MethodInfoList = MethodInfo.getMethodPath2MethodInfoByMethodInfoFile(methodInfoFile);
    }
}
