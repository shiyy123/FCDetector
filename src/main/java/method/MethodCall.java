package method;

import call.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cary.shi on 2019/12/26
 */
public class MethodCall {
    private Method callerMethod;
    private Method calleeMethod;
    private MethodInfo callerMethodInfo;
    private MethodInfo calleeMethodInfo;

    private int lineNum;
    private int columnNum;
    private String callCode;

    public MethodCall(Method callerMethod, Method calleeMethod, MethodInfo callerMethodInfo, MethodInfo calleeMethodInfo, int lineNum, int columnNum, String callCode) {
        this.callerMethod = callerMethod;
        this.calleeMethod = calleeMethod;
        this.callerMethodInfo = callerMethodInfo;
        this.calleeMethodInfo = calleeMethodInfo;
        this.lineNum = lineNum;
        this.columnNum = columnNum;
        this.callCode = callCode;
    }

    /**
     * 将已有的Method，MethodInfo，Call信息连接起来
     */
    public static List<MethodCall> getMethodCallListFromMethodAndCallInfo(List<Method> methodList, List<MethodInfo> methodInfoList, List<Call> callList) {
        List<MethodCall> methodCallList = new ArrayList<>();

        Map<MethodInfo, Method> methodInfoMethodMap = new HashMap<>();
        for (MethodInfo methodInfo : methodInfoList) {
            for (Method method : methodList) {
                if (method.getName().equals(methodInfo.getMethodName())) {
                    methodInfoMethodMap.put(methodInfo, method);
                    break;
                }
            }
        }
        for (Call call : callList) {
            MethodInfo callerMethodInfo = null;
            MethodInfo calleeMethodInfo = null;
            Method callerMethod = null;
            Method calleeMethod = null;
            for (MethodInfo methodInfo : methodInfoList) {
                if (call.getCallerStartLine() >= methodInfo.getStartLine() &&
                        call.getCallerEndLine() <= methodInfo.getEndLine()) {
                    callerMethodInfo = methodInfo;
                }
                if (call.getCalleeStartLine() >= methodInfo.getStartLine() &&
                        call.getCalleeEndLine() <= methodInfo.getEndLine()) {
                    calleeMethodInfo = methodInfo;
                }
            }

            if (callerMethodInfo != null) {
                callerMethod = methodInfoMethodMap.getOrDefault(callerMethodInfo, null);
            }

            if (calleeMethodInfo != null) {
                calleeMethod = methodInfoMethodMap.getOrDefault(calleeMethodInfo, null);
            }

            if (callerMethod != null && calleeMethod != null) {
                MethodCall methodCall = new MethodCall(callerMethod, calleeMethod, callerMethodInfo, calleeMethodInfo, call.getLineNum(), call.getColumnNum(), call.getCallCode());
                methodCallList.add(methodCall);
            }
        }
        return methodCallList;
    }

    @Override
    public String toString() {
        return "MethodCall{" +
                "callerMethod=" + callerMethod +
                ", calleeMethod=" + calleeMethod +
                ", callerMethodInfo=" + callerMethodInfo +
                ", calleeMethodInfo=" + calleeMethodInfo +
                ", lineNum=" + lineNum +
                ", columnNum=" + columnNum +
                ", callCode='" + callCode + '\'' +
                '}';
    }

    public Method getCallerMethod() {
        return callerMethod;
    }

    public void setCallerMethod(Method callerMethod) {
        this.callerMethod = callerMethod;
    }

    public Method getCalleeMethod() {
        return calleeMethod;
    }

    public void setCalleeMethod(Method calleeMethod) {
        this.calleeMethod = calleeMethod;
    }

    public MethodInfo getCallerMethodInfo() {
        return callerMethodInfo;
    }

    public void setCallerMethodInfo(MethodInfo callerMethodInfo) {
        this.callerMethodInfo = callerMethodInfo;
    }

    public MethodInfo getCalleeMethodInfo() {
        return calleeMethodInfo;
    }

    public void setCalleeMethodInfo(MethodInfo calleeMethodInfo) {
        this.calleeMethodInfo = calleeMethodInfo;
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
