package method;

import config.PathConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Definition and operations about functions
 *
 * @author cary.shi on 2019/12/5
 */
public class MethodInfo {
    private final static Logger logger = LogManager.getLogger(MethodInfo.class);

    private String location;
    private String methodName;
    private int startLine;
    private int endLine;
    private String signature;

    public MethodInfo(String location, String methodName, int startLine, int endLine, String signature) {
        this.location = location;
        this.methodName = methodName;
        this.startLine = startLine;
        this.endLine = endLine;
        this.signature = signature;
    }

    public MethodInfo() {
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "location='" + location + '\'' +
                ", methodName='" + methodName + '\'' +
                ", startLine=" + startLine +
                ", endLine=" + endLine +
                ", signature='" + signature + '\'' +
                '}';
    }

    // int(int, int)

    /**
     * 获取函数的参数数量
     */
    public int getArgumentNum(String signature) {
        if (signature == null || signature.equals("TODO assignment signature")) {
            return -1;
        }
        String[] arguments = signature.substring(signature.indexOf("(") + 1, signature.indexOf(")")).split(",");
        return arguments.length;
    }

    /**
     * 获取函数的参数列表，列表中每个元素为变量类型
     */
    public String[] getArguments(String signature) {
        if (signature == null || signature.equals("TODO assignment signature")) {
            return null;
        }
        return signature.substring(signature.indexOf("(") + 1, signature.indexOf(")")).split(",");
    }

    /**
     * 解析函数列表文件，包含文件夹中所有源码文件的列表，以"------"分隔
     * file:{filepath}
     * filename
     * Some({startLine})
     * Some({endLine})
     */
    public static List<MethodInfo> getMethodInfoListByMethodInfoFile(File methodInfoFile) {
        List<MethodInfo> methodInfoList = new ArrayList<>();

        String[] contents = null;
        try {
            contents = FileUtils.readFileToString(methodInfoFile, StandardCharsets.UTF_8).split("------");
        } catch (IOException e) {
            logger.error("Read file in getFuncInfoListByFuncInfoFile error");
        }
        if (contents == null) {
            return methodInfoList;
        }
        for (String content : contents) {
            String[] tmp = content.split("\\r?\\n");
            List<String> lines = new ArrayList<>();
            for (String s : tmp) {
                if (s.trim().length() > 0) {
                    lines.add(s);
                }
            }
            if (lines.isEmpty()) {
                continue;
            }
            String location = lines.get(0).trim();
            int propertyLen = 4;
            for (int i = 1; i < lines.size(); i += propertyLen) {
                String methodName = lines.get(i);
                int startLine = Tool.getNumFromSome(lines.get(i + 1));
                int endLine = Tool.getNumFromSome(lines.get(i + 2));
                String signature = lines.get(i + 3);
                MethodInfo methodInfo = new MethodInfo(location, methodName, startLine, endLine, signature);
                methodInfoList.add(methodInfo);
            }
        }
        return methodInfoList;
    }

    /**
     * 获取文件路径和函数的map
     */
    public static Map<String, List<MethodInfo>> getMethodPath2MethodInfoByMethodInfoFile(File methodInfoFile) {
        Map<String, List<MethodInfo>> methodPath2FuncInfo = new HashMap<>();

        List<MethodInfo> methodInfoList = getMethodInfoListByMethodInfoFile(methodInfoFile);
        for (MethodInfo methodInfo : methodInfoList) {
            List<MethodInfo> cur = methodPath2FuncInfo.getOrDefault(methodInfo.location, new ArrayList<>());
            cur.add(methodInfo);
            methodPath2FuncInfo.put(methodInfo.location, cur);
        }
        return methodPath2FuncInfo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
