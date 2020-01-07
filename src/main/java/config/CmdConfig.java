package config;

import java.io.File;

/**
 * @author cary.shi on 2019/12/24
 */
public class CmdConfig {
    public static String JOERN_CMD_PATH = PathConfig.ROOT_PATH + File.separator + "/joern/joern-cli/joern ";
    public static String JOERN_PARSE_CMD_PATH = PathConfig.ROOT_PATH + File.separator + "/joern/joern-cli/joern-parse ";
    private static String JOERN_SCRIPT_PATH = PathConfig.ROOT_PATH + File.separator + "/joern/joern-cli/scripts/";
    public static String JOERN_DUMP_FUNC_PATH = JOERN_SCRIPT_PATH + File.separator + "list-funcs.sc";
    public static String JOERN_DUMP_PDG_PATH = JOERN_SCRIPT_PATH + File.separator + "pdg-for-funcs-dump.sc";
    public static String JOERN_DUMP_CFG_PATH = JOERN_SCRIPT_PATH + File.separator + "cfg-for-funcs-dump.sc";
    public static String JOERN_DUMP_AST_PATH = JOERN_SCRIPT_PATH + File.separator + "ast-for-funcs-dump.sc";
    public static String JOERN_DUMP_FUNC_CALL_PATH = JOERN_SCRIPT_PATH + File.separator + "list-func-call.sc";

    private static String AUTOENCODE_PATH = PathConfig.ROOT_PATH + File.separator + "AutoenCODE";
    public static String WORD2VEC_SHELL_PATH = AUTOENCODE_PATH + File.separator + "bin" + File.separator + "run_word2vec.sh";
    public static String WORD2VEC_CMD_PATH = AUTOENCODE_PATH + File.separator + "bin" + File.separator + "word2vec" + File.separator + "word2vec";

    private static String GRAPH2VEC_FOLDER_PATH = PathConfig.ROOT_PATH + File.separator + "graph2vec";
    public static String GRAPH2VEC_VENV_PATH = GRAPH2VEC_FOLDER_PATH + File.separator + "venv" + File.separator + "bin" + File.separator + "python3.5";
    public static String GRAPH2VEC_SCRIPT_PATH = GRAPH2VEC_FOLDER_PATH + File.separator + "src" + File.separator + "graph2vec.py";
}
