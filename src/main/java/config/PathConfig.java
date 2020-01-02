package config;

import java.io.File;

/**
 * @author cary.shi on 2019/11/28
 */
public class PathConfig {
    public static String LINE_SEP = System.getProperty("line.separator");

    // docker执行存放数据路径
    public static String base = "/mnt/share/CloneData/data/";
//    public static String base = "G:\\share\\CloneData\\data";
//    public static String base = "/workspace/";

    // 项目当前路径
    public static String ROOT_PATH = "/mnt/share/FCDetector/";
//    public static String ROOT_PATH = "/scanner";

    public static String SRC_FOLDER_PATH = base + File.separator + "src";

    public static String CPG_FOLDER_PATH = base + File.separator + "cpg";
    public static String CALL_FOLDER_PATH = base + File.separator + "call";
    public static String CFG_FOLDER_PATH = base + File.separator + "cfg";
    public static String FEATURE_CFG_FOLDER_PATH = base + File.separator + "feature_cfg";
    public static String AST_FOLDER_PATH = base + File.separator + "ast";
    public static String METHOD_INFO_FOLDER_PATH = base + File.separator + "methodInfo";
    public static String FUNC_FOLDER_PATH = base + File.separator + "func";
    public static String SINGLE_FUNC_PATH = base + File.separator + "func.txt";
    public static String FEATURE_FOLDER_PATH = base + File.separator + "feature";
    public static String FUNC_EDGE_PATH = base + File.separator + "func_edge";
    public static String FEATURE_EDGE_PATH = base + File.separator + "feature_edge";
    public static String WORD2VEC_PATH = base + File.separator + "word2vec";
    public static String EMBEDDING_FUNC_WORD2VEC_PATH = base + File.separator + "embedding_func_word2vec";
    public static String EMBEDDING_FEATURE_WORD2VEC_PATH = base + File.separator + "embedding_feature_word2vec";
    public static String EMBEDDING_FUNC_HOPE_PATH = base + File.separator + "embedding_func_HOPE";
    public static String EMBEDDING_FEATURE_HOPE_PATH = base + File.separator + "embedding_feature_HOPE";

    public static String FEATURE_CFG_DOT_FOLDER_PATH = base + File.separator + "feature_dot_img";

    public static String IDENT_EMBED_PATH = base + File.separator + "identEmbed";
    public static String CFG_EMBED_PATH = base + File.separator + "cfgEmbed";

    public static String TMP_PATH = base + File.separator + "tmp";

    public static String FEATURE_CONTENT_PATH = base + File.separator + "feature_content";

    // code property folder
    public static String CODE_PROPERTY_FOLDER = base + File.separator + "property";

    public static String CPG_FILE_PATH = CODE_PROPERTY_FOLDER + File.separator + "cpg.bin.zip";
    public static String FUNC_FILE_PATH = CODE_PROPERTY_FOLDER + File.separator + "func.txt";
    public static String PDG_FILE_PATH = CODE_PROPERTY_FOLDER + File.separator + "pdg.json";
    public static String CFG_FILE_PATH = CODE_PROPERTY_FOLDER + File.separator + "cfg.json";
    public static String AST_FILE_PATH = CODE_PROPERTY_FOLDER + File.separator + "ast.json";
    public static String CALL_FILE_PATH = CODE_PROPERTY_FOLDER + File.separator + "call.txt";

    static {
        File propertyFolder = new File(CODE_PROPERTY_FOLDER);
        if (!propertyFolder.exists()) {
            propertyFolder.mkdir();
        }

        File astFolder = new File(AST_FOLDER_PATH);
        if (!astFolder.exists()) {
            astFolder.mkdir();
        }

        File cfgFolder = new File(CFG_FOLDER_PATH);
        if (!cfgFolder.exists()) {
            cfgFolder.mkdir();
        }

        File cpgFolder = new File(CPG_FOLDER_PATH);
        if (!cpgFolder.exists()) {
            cpgFolder.mkdir();
        }

        File featureCfgDotFolder = new File(FEATURE_CFG_DOT_FOLDER_PATH);
        if (!featureCfgDotFolder.exists()) {
            featureCfgDotFolder.mkdir();
        }
    }
}
