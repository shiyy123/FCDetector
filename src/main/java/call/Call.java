package call;

import method.MethodInfo;
import org.apache.commons.io.FileUtils;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author cary.shi on 2019/12/25
 */
public class Call {
    private String callerPath;
    private String callerName;
    private int callerStartLine;
    private int callerEndLine;

    private String calleePath;
    private String calleeName;
    private int calleeStartLine;
    private int calleeEndLine;
    private int calleeArgumentNum;

    private int lineNum;
    private int columnNum;

    private String callCode;

    public Call(String callerPath, String callerName, int callerStartLine, int callerEndLine,
                String calleePath, String calleeName, int calleeStartLine, int calleeEndLine, int calleeArgumentNum,
                int lineNum, int columnNum,
                String callCode) {
        this.callerPath = callerPath;
        this.callerName = callerName;
        this.callerStartLine = callerStartLine;
        this.callerEndLine = callerEndLine;

        this.calleePath = calleePath;
        this.calleeName = calleeName;
        this.calleeStartLine = calleeStartLine;
        this.calleeEndLine = calleeEndLine;
        this.calleeArgumentNum = calleeArgumentNum;

        this.lineNum = lineNum;
        this.columnNum = columnNum;

        this.callCode = callCode;
    }

    public Call() {
    }

    @Override
    public String toString() {
        return "Call{" +
                "callerPath='" + callerPath + '\'' +
                ", callerName='" + callerName + '\'' +
                ", callerStartLine=" + callerStartLine +
                ", callerEndLine=" + callerEndLine +
                ", calleePath='" + calleePath + '\'' +
                ", calleeName='" + calleeName + '\'' +
                ", calleeStartLine=" + calleeStartLine +
                ", calleeEndLine=" + calleeEndLine +
                ", calleeArgumentNum=" + calleeArgumentNum +
                ", lineNum=" + lineNum +
                ", columnNum=" + columnNum +
                ", callCode='" + callCode + '\'' +
                '}';
    }

    // some(num)，获取数字num
    static int getNumFromSome(String s) {
        s = s.trim();
        return Integer.parseInt(s.substring(s.indexOf("(") + 1, s.indexOf(")")));
    }

    /**
     * 是否匹配该函数
     * 0:match,1:not_match,2:unknown
     * C++类型：
     */
    public int judgeMatch(MethodInfo methodInfo, String actualMethodName, String callCode) {

        // 函数名
        if (methodInfo.getMethodName() == null || !methodInfo.getMethodName().equals(actualMethodName)) {
            return 1;
        }

        String signature = methodInfo.getSignature();
        if (signature == null) {
            return 1;
        }

        String[] actualArguments = callCode.substring(callCode.indexOf("(") + 1, callCode.indexOf(")")).split(",");

        // 参数数量
        int formalArgumentNum = methodInfo.getArgumentNum(signature);
        if (formalArgumentNum != actualArguments.length) {
            return 1;
        } else {
            return 0;
        }

        // TODO 获取实参类型
        // 参数列表
//        String[] formalArgumentTypes = methodInfo.getArguments(signature);
//        String[] actualArguments = callCode.substring(callCode.indexOf("(") + 1, callCode.indexOf(")")).split(",");
//        if (formalArgumentTypes.length != actualArguments.length) {
//            return 1;
//        }

//        int len = formalArgumentTypes.length;
//        // 列表中的每个参数
//        boolean flag = true;
//        String[] actualArgumentTypes = getTypeOfArgument(actualArguments, new File("cpfFile"));
//        assert actualArgumentTypes != null;
//        for (int i = 0; i < len; i++) {
//            // 0:match,1:not_match,2:unknown
//            int match = judgeArgumentMatch(formalArgumentTypes[i], actualArgumentTypes[i]);
//            if (match == 1) {
//                return 1;
//            }
//        }

        // unknown
//        return 2;
    }


    /**
     * 获取实参类型，
     */
    private static String[] getTypeOfArgument(String[] argument, File cpgFile) {
        return null;
    }

    /**
     * 判断是否是数字
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * 类型是否匹配
     * 0:match,1:not_match,2:unknown
     */
    private static int judgeArgumentMatch(String type, String argument) {

        if (isNumeric(argument) &&
                (type.equals("int") || type.equals("float") || type.equals("double"))) {
            return 0;
        }

        return 2;
    }

    /**
     * 根据函数信息，获取相应的FuncInfo，包含具体位置信息（唯一标识）
     * TODO argumentNum不是理解的参数数量，存在问题
     */
    public MethodInfo getFuncInfoByNameAndSignature(File methodInfoFile, String methodPath, String methodName, int argumentNum, String callCode) {
        // 文件和函数的map
        Map<String, List<MethodInfo>> methodPath2MethodInfo = MethodInfo.getMethodPath2MethodInfoByMethodInfoFile(methodInfoFile);

        List<MethodInfo> methodInfoList = methodPath2MethodInfo.getOrDefault(methodPath.trim(), new ArrayList<>());

        List<MethodInfo> unknownFuncInfoList = new ArrayList<>();
        List<MethodInfo> matchFuncInfoList = new ArrayList<>();

        for (MethodInfo methodInfo : methodInfoList) {
            int matchRes = judgeMatch(methodInfo, methodName, callCode);
            if (matchRes == 0) {
                matchFuncInfoList.add(methodInfo);
            } else if (matchRes == 2) {
                unknownFuncInfoList.add(methodInfo);
            }
        }
        if (!matchFuncInfoList.isEmpty()) {
            return matchFuncInfoList.get(0);
        } else if (!unknownFuncInfoList.isEmpty()) {
            return unknownFuncInfoList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 解析method call文件，获取函数间调用关系
     * <p>
     * /mnt/share/test/a/a/a.c  调用函数路径
     * main                     调用函数名
     * Some(7)                  调用函数起始行
     * Some(19)                 调用函数终止行
     * /mnt/share/test/a/a/a.c  被调函数路径
     * <operator>.assignment    被调函数名
     * 1                        被调函数参数个数
     * Some(8)                  调用发生位置行号
     * Some(5)                  调用发生位置列号
     * i = 0                    被调语句
     */
    public List<Call> resolveFuncCallFile(File methodCallFile, File methodInfoFile) {
        List<Call> callList = new ArrayList<>();
        String[] funcCalls = null;

        try {
            funcCalls = FileUtils.readFileToString(methodCallFile, StandardCharsets.UTF_8).split("------");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (funcCalls == null) {
            return callList;
        }
        List<MethodInfo> allMethodInfoList = MethodInfo.getMethodInfoListByMethodInfoFile(methodInfoFile);
        Set<String> methodNameSet = new HashSet<>();
        allMethodInfoList.forEach(methodInfo -> {
            methodNameSet.add(methodInfo.getMethodName());
        });

        for (String content : funcCalls) {
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

            String callerPath = lines.get(0);
            String callerName = lines.get(1);
            int callerStartLine = getNumFromSome(lines.get(2));
            int callerEndLine = getNumFromSome(lines.get(3));

            String calleePath = lines.get(4);
            String calleeName = lines.get(5);

            if (callerName.contains("<operator>") || calleeName.contains("<operator>")
                    || !methodNameSet.contains(callerName) || !methodNameSet.contains(calleeName)) {
                continue;
            }

            // -1为错误标记
            int calleeStartLine = -1;
            int calleeEndLine = -1;
            int calleeArgumentNum = Integer.parseInt(lines.get(6));

            int lineNum = Tool.getNumFromSome(lines.get(7));
            int columnNum = Tool.getNumFromSome(lines.get(8));

            StringBuilder sb = new StringBuilder();
            for (int i = 9; i < lines.size(); i++) {
                sb.append(lines.get(i)).append("\n");
            }
            String callCode = sb.toString();

            // get callee funcInfo
            MethodInfo calleeFuncInfo = getFuncInfoByNameAndSignature(methodInfoFile, calleePath, calleeName, calleeArgumentNum, callCode);
            if (calleeFuncInfo != null) {
                calleeStartLine = calleeFuncInfo.getStartLine();
                calleeEndLine = calleeFuncInfo.getEndLine();
            }

            Call call = new Call(callerPath, callerName, callerStartLine, callerEndLine,
                    calleePath, calleeName, calleeStartLine, calleeEndLine, calleeArgumentNum,
                    lineNum, columnNum,
                    callCode);
            callList.add(call);
        }
        return callList;
    }

    public static void main(String[] args) {
        Call call = new Call();
        List<Call> callList = call.resolveFuncCallFile(new File("G:\\share\\CloneData\\data\\property\\call.txt"),
                new File("G:\\share\\CloneData\\data\\property\\func.txt"));
        callList.forEach(System.out::println);
//        callList.forEach(call1 -> {
//            System.out.println(call1.getCalleeName());
//            System.out.println(call1.getCalleeStartLine());
//            System.out.println(call1.getCalleeEndLine());
//        });
    }

    public String getCallerPath() {
        return callerPath;
    }

    public void setCallerPath(String callerPath) {
        this.callerPath = callerPath;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public int getCallerStartLine() {
        return callerStartLine;
    }

    public void setCallerStartLine(int callerStartLine) {
        this.callerStartLine = callerStartLine;
    }

    public int getCallerEndLine() {
        return callerEndLine;
    }

    public void setCallerEndLine(int callerEndLine) {
        this.callerEndLine = callerEndLine;
    }

    public String getCalleePath() {
        return calleePath;
    }

    public void setCalleePath(String calleePath) {
        this.calleePath = calleePath;
    }

    public String getCalleeName() {
        return calleeName;
    }

    public void setCalleeName(String calleeName) {
        this.calleeName = calleeName;
    }

    public int getCalleeStartLine() {
        return calleeStartLine;
    }

    public void setCalleeStartLine(int calleeStartLine) {
        this.calleeStartLine = calleeStartLine;
    }

    public int getCalleeEndLine() {
        return calleeEndLine;
    }

    public void setCalleeEndLine(int calleeEndLine) {
        this.calleeEndLine = calleeEndLine;
    }

    public int getCalleeArgumentNum() {
        return calleeArgumentNum;
    }

    public void setCalleeArgumentNum(int calleeArgumentNum) {
        this.calleeArgumentNum = calleeArgumentNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getColumnNum() {
        return columnNum;
    }

    public void setColumnNum(int columnNum) {
        this.columnNum = columnNum;
    }

    public String getCallCode() {
        return callCode;
    }

    public void setCallCode(String callCode) {
        this.callCode = callCode;
    }
}

