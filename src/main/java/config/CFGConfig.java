package config;

/**
 * @author cary.shi on 2019/12/9
 */
public class CFGConfig {
    // node type in cfg
    public static String CALL_NODE = "Call";
    public static String BLOCK_NODE = "Block";
    public static String IDENTIFIER_NODE = "Identifier";
    public static String LITERAL_NODE = "Literal";
    public static String RETURN_NODE = "Return";
    public static String UNKNOWN_NODE = "Unknown";
    // entrance of function
    public static String METHOD = "Method";
    // exit of function
    public static String METHOD_RETURN = "MethodReturn";

    // property type in cfg
    public static String CODE_PROPERTY = "CODE";
    public static String ORDER_PROPERTY = "ORDER";
    public static String LINE_NUMBER_PROPERTY = "LINE_NUMBER";
    public static String COLUMN_NUMBER_PROPERTY = "COLUMN_NUMBER";
}
