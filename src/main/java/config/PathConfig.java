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
    public static String CFG_CONTENT_FOLDER_PATH = base + File.separator + "cfg_content";
    public static String AST_CONTENT_FOLDER_PATH = base + File.separator + "ast_content";
    public static String TEXT_CONTENT_FOLDER_PATH = base + File.separator + "text_content";

    public static String METHOD_IN_FUNC_FOLDER_PATH = base + File.separator + "methodInFunc";

    public static String SYNTAX_FEATURE_FOLDER_PATH = base + File.separator + "syntax_feature";
    public static String SEMANTIC_FEATURE_FOLDER_PATH = base + File.separator + "semantic_feature";
    public static String TEXT_FEATURE_FOLDER_PATH = base + File.separator + "text_feature";

    private static String TRAINING_DATA_FOLDER = base + File.separator + "training";

    public static String TRAINING_TEXT_DATA_FILE_PATH = TRAINING_DATA_FOLDER + File.separator + "text.csv";
    public static String TRAINING_SYNTAX_DATA_FILE_PATH = TRAINING_DATA_FOLDER + File.separator + "syntax.csv";
    public static String TRAINING_SEMANTIC_DATA_FILE_PATH = TRAINING_DATA_FOLDER + File.separator + "semantic.csv";
    public static String TRAINING_TEXT_SYNTAX_DATA_FILE_PATH = TRAINING_DATA_FOLDER + File.separator + "text_syntax.csv";
    public static String TRAINING_TEXT_SEMANTIC_DATA_FILE_PATH = TRAINING_DATA_FOLDER + File.separator + "text_semantic.csv";
    public static String TRAINING_SYNTAX_SEMANTIC_DATA_FILE_PATH = TRAINING_DATA_FOLDER + File.separator + "syntax_semantic.csv";
    public static String TRAINING_MERGE_DATA_FILE_PATH = TRAINING_DATA_FOLDER + File.separator + "merge.csv";

    private static String AST_WORD2VEC_FOLDER_PATH = base + File.separator + "ast_word2vec";
    public static String AST_WORD2VEC_CORPUS_FILE_PATH = AST_WORD2VEC_FOLDER_PATH + File.separator + "corpus.src";
    public static String AST_WORD2VEC_OUT_FILE_PATH = AST_WORD2VEC_FOLDER_PATH + File.separator + "word2vec.out";

    private static String TEXT_WORD2VEC_FOLDER_PATH = base + File.separator + "text_word2vec";
    public static String TEXT_WORD2VEC_CORPUS_FILE_PATH = TEXT_WORD2VEC_FOLDER_PATH + File.separator + "corpus.src";
    public static String TEXT_WORD2VEC_OUT_FILE_PATH = TEXT_WORD2VEC_FOLDER_PATH + File.separator + "word2vec.out";

    private static String CFG_GRAPH2VEC_FOLDER_PATH = base + File.separator + "cfg_graph2vec";
    public static String CFG_GRAPH2VEC_OUT_PATH = CFG_GRAPH2VEC_FOLDER_PATH + File.separator + "graphFeature.csv";

    private static String MAP_FOLDER_PATH = PathConfig.base + File.separator + "map";
    public static String DOT2CFG_PATH = MAP_FOLDER_PATH + File.separator + "dot2cfg.txt";
    public static String DOT2AST_PATH = MAP_FOLDER_PATH + File.separator + "dot2ast.txt";
    public static String DOT2TEXT_PATH = MAP_FOLDER_PATH + File.separator + "dot2text.txt";

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

        File astCorpusFolder = new File(AST_WORD2VEC_FOLDER_PATH);
        if (!astCorpusFolder.exists()) {
            astCorpusFolder.mkdir();
        }

        File trainingDataFolder = new File(TRAINING_DATA_FOLDER);
        if (!trainingDataFolder.exists()) {
            trainingDataFolder.mkdir();
        }

        File mapFolder = new File(MAP_FOLDER_PATH);
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
        }

        File methodInFuncFolder = new File(METHOD_IN_FUNC_FOLDER_PATH);
        if (!methodInFuncFolder.exists()) {
            methodInFuncFolder.mkdir();
        }

        File textFeatureFolder = new File(TEXT_FEATURE_FOLDER_PATH);
        if (!textFeatureFolder.exists()) {
            textFeatureFolder.mkdir();
        }
    }
}
