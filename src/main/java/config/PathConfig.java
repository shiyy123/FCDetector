package config;

import java.io.File;

/**
 * @author cary.shi on 2019/11/28
 */
public class PathConfig {

    private PathConfig() {
    }

    private static class PathConfigHolder {
        private static final PathConfig instance = new PathConfig();
    }

    public static PathConfig getInstance() {
        return PathConfigHolder.instance;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getROOT_PATH() {
        return ROOT_PATH;
    }

    public void setROOT_PATH(String ROOT_PATH) {
        this.ROOT_PATH = ROOT_PATH;
    }

    // docker执行存放数据路径
    public String base;

//    public static String base = "G:\\share\\CloneData\\data";
//    public static String base = "/workspace/";

    // 项目当前路径
    public String ROOT_PATH;
//    public static String ROOT_PATH = "/scanner";


    public String getSRC_FOLDER_PATH() {
        return getBase() + File.separator + "src";
    }

    public String getCPG_FOLDER_PATH() {
        return getBase() + File.separator + "cpg";
    }

    public String getCALL_FOLDER_PATH() {
        return getBase() + File.separator + "call";
    }

    public String getCFG_FOLDER_PATH() {
        return getBase() + File.separator + "cfg";
    }

    public String getFEATURE_CFG_FOLDER_PATH() {
        return getBase() + File.separator + "feature_cfg";
    }

    public String getAST_FOLDER_PATH() {
        return getBase() + File.separator + "ast";
    }

    public String getMETHOD_INFO_FOLDER_PATH() {
        return getBase() + File.separator + "methodInfo";
    }

    public String getFUNC_FOLDER_PATH() {
        return getBase() + File.separator + "func";
    }

    public String getCFG_CONTENT_FOLDER_PATH() {
        return getBase() + File.separator + "cfg_content";
    }

    public String getAST_CONTENT_FOLDER_PATH() {
        return getBase() + File.separator + "ast_content";
    }

    public String getTEXT_CONTENT_FOLDER_PATH() {
        return getBase() + File.separator + "text_content";
    }

    public String getMETHOD_IN_FUNC_FOLDER_PATH() {
        return getBase() + File.separator + "methodInFunc";
    }

    public String getSYNTAX_FEATURE_FOLDER_PATH() {
        return getBase() + File.separator + "syntax_feature";
    }

    public String getSEMANTIC_FEATURE_FOLDER_PATH() {
        return getBase() + File.separator + "semantic_feature";
    }

    public String getTEXT_FEATURE_FOLDER_PATH() {
        return getBase() + File.separator + "text_feature";
    }

    public String getTRAINING_DATA_FOLDER() {
        return getBase() + File.separator + "training";
    }

    public String getTRAINING_TEXT_DATA_FILE_PATH() {
        return getTRAINING_DATA_FOLDER() + File.separator + "text.csv";
    }

    public String getTRAINING_SYNTAX_DATA_FILE_PATH() {
        return getTRAINING_DATA_FOLDER() + File.separator + "syntax.csv";
    }

    public String getTRAINING_SEMANTIC_DATA_FILE_PATH() {
        return getTRAINING_DATA_FOLDER() + File.separator + "semantic.csv";
    }

    public String getTRAINING_TEXT_SYNTAX_DATA_FILE_PATH() {
        return getTRAINING_DATA_FOLDER() + File.separator + "text_syntax.csv";
    }

    public String getTRAINING_TEXT_SEMANTIC_DATA_FILE_PATH() {
        return getTRAINING_DATA_FOLDER() + File.separator + "text_semantic.csv";
    }

    public String getTRAINING_SYNTAX_SEMANTIC_DATA_FILE_PATH() {
        return getTRAINING_DATA_FOLDER() + File.separator + "syntax_semantic.csv";
    }

    public String getTRAINING_MERGE_DATA_FILE_PATH() {
        return getTRAINING_DATA_FOLDER() + File.separator + "merge.csv";
    }

    public String getAST_WORD2VEC_FOLDER_PATH() {
        return getBase() + File.separator + "ast_word2vec";
    }

    public String getAST_WORD2VEC_CORPUS_FILE_PATH() {
        return getAST_WORD2VEC_FOLDER_PATH() + File.separator + "corpus.src";
    }

    public String getAST_WORD2VEC_OUT_FILE_PATH() {
        return getAST_WORD2VEC_FOLDER_PATH() + File.separator + "word2vec.out";
    }

    public String getTEXT_WORD2VEC_FOLDER_PATH() {
        return getBase() + File.separator + "text_word2vec";
    }

    public String getTEXT_WORD2VEC_CORPUS_FILE_PATH() {
        return getTEXT_WORD2VEC_FOLDER_PATH() + File.separator + "corpus.src";
    }

    public String getTEXT_WORD2VEC_OUT_FILE_PATH() {
        return getTEXT_WORD2VEC_FOLDER_PATH() + File.separator + "word2vec.out";
    }

    public String getCFG_GRAPH2VEC_FOLDER_PATH() {
        return getBase() + File.separator + "cfg_graph2vec";
    }

    public String getCFG_GRAPH2VEC_OUT_PATH() {
        return getCFG_GRAPH2VEC_FOLDER_PATH() + File.separator + "graphFeature.csv";
    }

    public String getMAP_FOLDER_PATH() {
        return getBase() + File.separator + "map";
    }

    public String getDOT2CFG_PATH() {
        return getMAP_FOLDER_PATH() + File.separator + "dot2cfg.txt";
    }

    public String getDOT2AST_PATH() {
        return getMAP_FOLDER_PATH() + File.separator + "dot2ast.txt";
    }

    public String getDOT2TEXT_PATH() {
        return getMAP_FOLDER_PATH() + File.separator + "dot2text.txt";
    }

    public String getSINGLE_FUNC_PATH() {
        return getBase() + File.separator + "func.txt";
    }

    public String getFEATURE_FOLDER_PATH() {
        return getBase() + File.separator + "feature";
    }

    public String getFUNC_EDGE_PATH() {
        return getBase() + File.separator + "func_edge";
    }

    public String getFEATURE_EDGE_PATH() {
        return getBase() + File.separator + "feature_edge";
    }

    public String getWORD2VEC_PATH() {
        return getBase() + File.separator + "word2vec";
    }

    public String getEMBEDDING_FUNC_WORD2VEC_PATH() {
        return getBase() + File.separator + "embedding_func_word2vec";
    }

    public String getEMBEDDING_FEATURE_WORD2VEC_PATH() {
        return getBase() + File.separator + "embedding_feature_word2vec";
    }

    public String getEMBEDDING_FUNC_HOPE_PATH() {
        return getBase() + File.separator + "embedding_func_HOPE";
    }

    public String getEMBEDDING_FEATURE_HOPE_PATH() {
        return getBase() + File.separator + "embedding_feature_HOPE";
    }

    public String getFEATURE_CFG_DOT_FOLDER_PATH() {
        return getBase() + File.separator + "feature_dot_img";
    }

    public String getIDENT_EMBED_PATH() {
        return getBase() + File.separator + "identEmbed";
    }

    public String getCFG_EMBED_PATH() {
        return getBase() + File.separator + "cfgEmbed";
    }

    public String getTMP_PATH() {
        return getBase() + File.separator + "tmp";
    }

    public String getFEATURE_CONTENT_PATH() {
        return getBase() + File.separator + "feature_content";
    }

    public String getCODE_PROPERTY_FOLDER() {
        return getBase() + File.separator + "property";
    }

    public String getCPG_FILE_PATH() {
        return getCODE_PROPERTY_FOLDER() + File.separator + "cpg.bin.zip";
    }

    public String getFUNC_FILE_PATH() {
        return getCODE_PROPERTY_FOLDER() + File.separator + "func.txt";
    }

    public String getPDG_FILE_PATH() {
        return getCODE_PROPERTY_FOLDER() + File.separator + "pdg.json";
    }

    public String getCFG_FILE_PATH() {
        return getCODE_PROPERTY_FOLDER() + File.separator + "cfg.json";
    }

    public String getAST_FILE_PATH() {
        return getCODE_PROPERTY_FOLDER() + File.separator + "ast.json";
    }

    public String getCALL_FILE_PATH() {
        return getCODE_PROPERTY_FOLDER() + File.separator + "call.txt";
    }

    public String SRC_FOLDER_PATH;
    public String CPG_FOLDER_PATH;
    public String CALL_FOLDER_PATH;
    public String CFG_FOLDER_PATH;
    public String FEATURE_CFG_FOLDER_PATH;
    public String AST_FOLDER_PATH;
    public String METHOD_INFO_FOLDER_PATH;
    public String FUNC_FOLDER_PATH;
    public String CFG_CONTENT_FOLDER_PATH;
    public String AST_CONTENT_FOLDER_PATH;
    public String TEXT_CONTENT_FOLDER_PATH;

    public String METHOD_IN_FUNC_FOLDER_PATH;

    public String SYNTAX_FEATURE_FOLDER_PATH;
    public String SEMANTIC_FEATURE_FOLDER_PATH;
    public String TEXT_FEATURE_FOLDER_PATH;

    private String TRAINING_DATA_FOLDER;

    public String TRAINING_TEXT_DATA_FILE_PATH;
    public String TRAINING_SYNTAX_DATA_FILE_PATH;
    public String TRAINING_SEMANTIC_DATA_FILE_PATH;
    public String TRAINING_TEXT_SYNTAX_DATA_FILE_PATH;
    public String TRAINING_TEXT_SEMANTIC_DATA_FILE_PATH;
    public String TRAINING_SYNTAX_SEMANTIC_DATA_FILE_PATH;
    public String TRAINING_MERGE_DATA_FILE_PATH;

    private String AST_WORD2VEC_FOLDER_PATH;
    public String AST_WORD2VEC_CORPUS_FILE_PATH;
    public String AST_WORD2VEC_OUT_FILE_PATH;

    private String TEXT_WORD2VEC_FOLDER_PATH;
    public String TEXT_WORD2VEC_CORPUS_FILE_PATH;
    public String TEXT_WORD2VEC_OUT_FILE_PATH;

    private String CFG_GRAPH2VEC_FOLDER_PATH;
    public String CFG_GRAPH2VEC_OUT_PATH;

    private String MAP_FOLDER_PATH;
    public String DOT2CFG_PATH;
    public String DOT2AST_PATH;
    public String DOT2TEXT_PATH;

    public String SINGLE_FUNC_PATH;
    public String FEATURE_FOLDER_PATH;
    public String FUNC_EDGE_PATH;
    public String FEATURE_EDGE_PATH;
    public String WORD2VEC_PATH;
    public String EMBEDDING_FUNC_WORD2VEC_PATH;
    public String EMBEDDING_FEATURE_WORD2VEC_PATH;
    public String EMBEDDING_FUNC_HOPE_PATH;
    public String EMBEDDING_FEATURE_HOPE_PATH;

    public String FEATURE_CFG_DOT_FOLDER_PATH;

    public String IDENT_EMBED_PATH;
    public String CFG_EMBED_PATH;

    public String TMP_PATH;

    public String FEATURE_CONTENT_PATH;

    // code property folder
    public String CODE_PROPERTY_FOLDER;

    public String CPG_FILE_PATH;
    public String FUNC_FILE_PATH;
    public String PDG_FILE_PATH;
    public String CFG_FILE_PATH;
    public String AST_FILE_PATH;
    public String CALL_FILE_PATH;


    public String getJOERN_CMD_PATH() {
        return getROOT_PATH() + File.separator + "/joern/joern-cli/joern ";
    }

    public String getJOERN_PARSE_CMD_PATH() {
        return getROOT_PATH() + File.separator + "/joern/joern-cli/joern-parse ";
    }

    public String getJOERN_SCRIPT_PATH() {
        return getROOT_PATH() + File.separator + "/joern/joern-cli/scripts/";
    }

    public String getJOERN_DUMP_FUNC_PATH() {
        return getJOERN_SCRIPT_PATH() + File.separator + "list-funcs.sc";
    }

    public String getJOERN_DUMP_PDG_PATH() {
        return getJOERN_SCRIPT_PATH() + File.separator + "pdg-for-funcs-dump.sc";
    }

    public String getJOERN_DUMP_CFG_PATH() {
        return getJOERN_SCRIPT_PATH() + File.separator + "cfg-for-funcs-dump.sc";
    }

    public String getJOERN_DUMP_AST_PATH() {
        return getJOERN_SCRIPT_PATH() + File.separator + "ast-for-funcs-dump.sc";
    }

    public String getJOERN_DUMP_FUNC_CALL_PATH() {
        return getJOERN_SCRIPT_PATH() + File.separator + "list-func-call.sc";
    }

    public String getAUTOENCODE_PATH() {
        return getROOT_PATH() + File.separator + "AutoenCODE";
    }

    public String getWORD2VEC_SHELL_PATH() {
        return getAUTOENCODE_PATH() + File.separator + "bin" + File.separator + "run_word2vec.sh";
    }

    public String getWORD2VEC_CMD_PATH() {
        return getAUTOENCODE_PATH() + File.separator + "bin" + File.separator + "word2vec" + File.separator + "word2vec";
    }

    public String getGRAPH2VEC_FOLDER_PATH() {
        return getROOT_PATH() + File.separator + "graph2vec";
    }

    public String getGRAPH2VEC_VENV_PATH() {
        return getGRAPH2VEC_FOLDER_PATH() + File.separator + "venv" + File.separator + "bin" + File.separator + "python3.5";
    }

    public String getGRAPH2VEC_SCRIPT_PATH() {
        return getGRAPH2VEC_FOLDER_PATH() + File.separator + "src" + File.separator + "graph2vec.py";
    }

    public String JOERN_CMD_PATH;
    public String JOERN_PARSE_CMD_PATH;
    private String JOERN_SCRIPT_PATH;
    public String JOERN_DUMP_FUNC_PATH;
    public String JOERN_DUMP_PDG_PATH;
    public String JOERN_DUMP_CFG_PATH;
    public String JOERN_DUMP_AST_PATH;
    public String JOERN_DUMP_FUNC_CALL_PATH;

    private String AUTOENCODE_PATH;
    public String WORD2VEC_SHELL_PATH;
    public String WORD2VEC_CMD_PATH;

    private String GRAPH2VEC_FOLDER_PATH;
    public String GRAPH2VEC_VENV_PATH;
    public String GRAPH2VEC_SCRIPT_PATH;

    public void init() {
        File propertyFolder = new File(getCODE_PROPERTY_FOLDER());
        if (!propertyFolder.exists()) {
            propertyFolder.mkdir();
        }

        File astFolder = new File(getAST_FOLDER_PATH());
        if (!astFolder.exists()) {
            astFolder.mkdir();
        }

        File cfgFolder = new File(getCFG_FOLDER_PATH());
        if (!cfgFolder.exists()) {
            cfgFolder.mkdir();
        }

        File cpgFolder = new File(getCPG_FOLDER_PATH());
        if (!cpgFolder.exists()) {
            cpgFolder.mkdir();
        }

        File featureCfgDotFolder = new File(getFEATURE_CFG_DOT_FOLDER_PATH());
        if (!featureCfgDotFolder.exists()) {
            featureCfgDotFolder.mkdir();
        }

        File astCorpusFolder = new File(getAST_WORD2VEC_FOLDER_PATH());
        if (!astCorpusFolder.exists()) {
            astCorpusFolder.mkdir();
        }

        File trainingDataFolder = new File(getTRAINING_DATA_FOLDER());
        if (!trainingDataFolder.exists()) {
            trainingDataFolder.mkdir();
        }

        File mapFolder = new File(getMAP_FOLDER_PATH());
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
        }

        File methodInFuncFolder = new File(getMETHOD_IN_FUNC_FOLDER_PATH());
        if (!methodInFuncFolder.exists()) {
            methodInFuncFolder.mkdir();
        }

        File textFeatureFolder = new File(getTEXT_FEATURE_FOLDER_PATH());
        if (!textFeatureFolder.exists()) {
            textFeatureFolder.mkdir();
        }

        File cfgGraph2VecFolder = new File(getCFG_GRAPH2VEC_FOLDER_PATH());
        if(!cfgGraph2VecFolder.exists()){
            cfgGraph2VecFolder.mkdir();
        }
    }
}
