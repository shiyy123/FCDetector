package method;

import ast.AST;
import cfg.CFG;

import java.io.File;
import java.util.*;

/**
 * @author cary.shi on 2019/12/24
 */
public class Method {

    private String id;
    private String name;
    private CFG cfg;
    private AST ast;

    public Method() {
    }

    public Method(String id, String name, CFG cfg) {
        this.id = id;
        this.name = name;
        this.cfg = cfg;
    }

    public Method(String id, String name, AST ast) {
        this.id = id;
        this.name = name;
        this.ast = ast;
    }

    public Method(String id, String name, AST ast, CFG cfg) {
        this.id = id;
        this.name = name;
        this.ast = ast;
        this.cfg = cfg;
    }

    // 将ast和cfg两个method合并
    public Method mergeASTMethodAndCFGMethod(Method astMethod, Method cfgMethod) {
        if (astMethod.id.equals(cfgMethod.id) &&
                astMethod.name.equals(cfgMethod.name)) {
            return new Method(astMethod.id, astMethod.name, cfgMethod.ast, astMethod.cfg);
        } else {
            System.out.println("合并method的ast和cfg失败");
            return null;
        }
    }

    public List<Method> getMethodList(File astFile, File cfgFile) {
        CFG cfg = new CFG();
        List<Method> cfgMethodList = cfg.getMethodCFGListFromCFGFile(cfgFile);

        AST ast = new AST();
        List<Method> astMethodList = ast.getMethodASTListFromASTFile(astFile);

        return mergeASTMethodListAndCFGMethodList(astMethodList, cfgMethodList);
    }

    // 将ast和cfg的method列表合并
    public List<Method> mergeASTMethodListAndCFGMethodList(List<Method> astMethodList, List<Method> cfgMethodList) {
        List<Method> methodList = new ArrayList<>();

        boolean match = true;
        Map<String, Method> id2CFGMethod = new HashMap<>();

        Set<String> astIdSet = new HashSet<>();
        Set<String> astNameSet = new HashSet<>();
        astMethodList.forEach(astMethod -> {
            astIdSet.add(astMethod.getId());
            astNameSet.add(astMethod.getName());
        });

        for (Method cfgMethod : cfgMethodList) {
            if (!astIdSet.contains(cfgMethod.getId()) || !astNameSet.contains(cfgMethod.getName())) {
                match = false;
                break;
            }
            id2CFGMethod.put(cfgMethod.getId(), cfgMethod);
        }
        if (astMethodList.size() != cfgMethodList.size()) {
            match = false;
        }
        if (!match) {
            return methodList;
        }
        for (Method astMethod : astMethodList) {
            Method method = new Method(astMethod.getId(), astMethod.getName(), astMethod.ast, id2CFGMethod.get(astMethod.getId()).cfg);
            methodList.add(method);
        }
        return methodList;
    }

    // io.shiftleft.codepropertygraph.generated.nodes.Method@65
    public Set<String> getMethodIdSet(List<Method> methodList) {
        Set<String> idSet = new HashSet<>();
        for (Method method : methodList) {
            String id = method.getId();
            String[] cols = id.split("\\.");
            idSet.add(cols[cols.length - 2] + "." + cols[cols.length - 1].substring(0, cols[cols.length - 1].indexOf("@")));
        }
        return idSet;
    }

    @Override
    public String toString() {
        return "Method{" +
                "id='" + this.getId() + '\'' +
                ", name='" + this.getName() + '\'' +
                ",cfg=" + this.getCfg() + '\'' +
                ",ast=" + this.getAst() + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CFG getCfg() {
        return cfg;
    }

    public void setCfg(CFG cfg) {
        this.cfg = cfg;
    }


    public AST getAst() {
        return ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }
}
