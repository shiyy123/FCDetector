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
}
