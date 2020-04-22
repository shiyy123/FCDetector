package joern;

import config.CmdConfig;
import config.PathConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tool.Tool;

import java.io.File;
import java.util.Objects;


/**
 * @author cary.shi on 2019/12/24
 */
public class CPG {
    private final static Logger logger = LogManager.getLogger(CPG.class);

    public void generateCPGForSourceFolder(String sourceFolderPath) {
        File[] sourceFolders = new File(sourceFolderPath).listFiles();
        assert sourceFolders != null;
        for (File sourceFolder : sourceFolders) {
            System.out.println(sourceFolder.getAbsolutePath());
            for (File sourceFile : Objects.requireNonNull(sourceFolder.listFiles())) {
                String subPath = Tool.getFolderAndFilePath(sourceFile);
                File cpgFolder = new File(PathConfig.getInstance().getCPG_FOLDER_PATH() + File.separator + subPath);
                if (!cpgFolder.exists()) {
                    cpgFolder.mkdirs();
                }
                String cpgPath = cpgFolder.getAbsolutePath() + File.separator + "cpg.bin.zip";
                getCPGFileBySourceFolder(sourceFile, cpgPath);
            }
        }
    }

    public void generateASTForSourceFolder(String sourceFolderPath) {
        File[] sourceFolders = new File(sourceFolderPath).listFiles();
        assert sourceFolders != null;
        for (File sourceFolder : sourceFolders) {
            System.out.println(sourceFolder.getAbsolutePath());
            for (File sourceFile : Objects.requireNonNull(sourceFolder.listFiles())) {
                String subPath = Tool.getFolderAndFilePath(sourceFile);
                File astFolder = new File(PathConfig.getInstance().getAST_FOLDER_PATH() + File.separator + subPath);
                if (!astFolder.exists()) {
                    astFolder.mkdirs();
                }
                File cpgFile = new File(PathConfig.getInstance().getCPG_FOLDER_PATH() + File.separator + subPath + File.separator + "cpg.bin.zip");
                String astPath = astFolder.getAbsolutePath() + File.separator + "ast.dot";
                getASTFileByCPGFile(cpgFile, astPath);
            }
        }
    }

    public void generateCFGForSourceFolder(String sourceFolderPath) {
        File[] sourceFolders = new File(sourceFolderPath).listFiles();
        assert sourceFolders != null;

        for (File sourceFolder : sourceFolders) {
            System.out.println(sourceFolder.getAbsolutePath());
            for (File sourceFile : Objects.requireNonNull(sourceFolder.listFiles())) {
                String subPath = Tool.getFolderAndFilePath(sourceFile);
                File cfgFolder = new File(PathConfig.getInstance().getCFG_FOLDER_PATH() + File.separator + subPath);
                if (!cfgFolder.exists()) {
                    cfgFolder.mkdirs();
                }
                File cpgFile = new File(PathConfig.getInstance().getCPG_FOLDER_PATH() + File.separator + subPath + File.separator + "cpg.bin.zip");
                String cfgPath = cfgFolder.getAbsolutePath() + File.separator + "cfg.dot";
                getCFGFileByCPGFile(cpgFile, cfgPath);
            }
        }
    }

    public void generateCallForSourceFolder(String sourceFolderPath) {
        File[] sourceFolders = new File(sourceFolderPath).listFiles();
        assert sourceFolders != null;

        for (File sourceFolder : sourceFolders) {
            System.out.println(sourceFolder.getAbsolutePath());
            for (File sourceFile : Objects.requireNonNull(sourceFolder.listFiles())) {
                String subPath = Tool.getFolderAndFilePath(sourceFile);
                File callFolder = new File(PathConfig.getInstance().getCALL_FOLDER_PATH() + File.separator + subPath);
                if (!callFolder.exists()) {
                    callFolder.mkdirs();
                }
                File cpgFile = new File(PathConfig.getInstance().getCPG_FOLDER_PATH() + File.separator + subPath + File.separator + "cpg.bin.zip");
                String callPath = callFolder.getAbsolutePath() + File.separator + "call.txt";
                getCallFileByCPGFile(cpgFile, callPath);
            }
        }
    }

    /**
     * 获取CPG (Code Property Graph)文件
     */
    public File getCPGFileBySourceFolder(File sourceFolder, String cpgOutPath) {
        if (!sourceFolder.exists()) {
            return null;
        }
        Tool.createFolderIfNotExist(cpgOutPath);
        String cmd = PathConfig.getInstance().getJOERN_PARSE_CMD_PATH() + sourceFolder.getAbsolutePath() + " --out " + cpgOutPath;
        Tool.executeCmdAndSaveLog(cmd, logger);

        File cpgOutFile = new File(cpgOutPath);
        if (cpgOutFile.exists()) {
            return cpgOutFile;
        } else {
            return null;
        }
    }

    // 获取函数列表文件
    public File getMethodInfoFileByCpgFile(File cpgFile, String funcFilePath) {
        File funcFile = new File(funcFilePath);
        if (funcFile.exists()) {
            funcFile.delete();
        }

        Tool.createFolderIfNotExist(funcFilePath);

        String cmd = PathConfig.getInstance().getJOERN_CMD_PATH() + " --script " + PathConfig.getInstance().getJOERN_DUMP_FUNC_PATH() + " --params cpgFile=" +
                cpgFile.getAbsolutePath() + ",outFile=" + funcFile.getAbsolutePath();

        Tool.executeCmdAndSaveLog(cmd, logger);

        if (!funcFile.exists()) {
            return null;
        }

        return funcFile;
    }

    /**
     * 根据cpg文件获取所有函数的cfg json文件
     */
    public File getCFGFileByCPGFile(File cpgFile, String cfgFilePath) {
        File cfgFile = new File(cfgFilePath);
        if (cfgFile.exists()) {
            cfgFile.delete();
        }

        Tool.createFolderIfNotExist(cfgFilePath);

        // /mnt/share/code/joern-cli/joern --script /mnt/share/code/joern-cli/scripts/cfg-for-funcs-dump.sc --params cpgFile=a.bin.zip,outFile=a.json
        String cmd = PathConfig.getInstance().getJOERN_CMD_PATH() + " --script " + PathConfig.getInstance().getJOERN_DUMP_CFG_PATH() + " --params cpgFile=" +
                cpgFile.getAbsolutePath() + ",outFile=" + cfgFile.getAbsolutePath();

        Tool.executeCmdAndSaveLog(cmd, logger);

        if (cfgFile.exists()) {
            return cfgFile;
        } else {
            return null;
        }
    }

    /**
     * 根据cpg文件生成调用图（初始），被调函数不精确
     */
    public File getCallFileByCPGFile(File cpgFile, String callFilePath) {
        File callFile = new File(callFilePath);
        if (callFile.exists()) {
            callFile.delete();
        }
        Tool.createFolderIfNotExist(callFilePath);

        // /mnt/share/code/joern-cli/joern --script /mnt/share/code/joern-cli/scripts/list-func-call.sc --params cpgFile=a.bin.zip,outFile=a.json
        String cmd = PathConfig.getInstance().getJOERN_CMD_PATH() + " --script " + PathConfig.getInstance().getJOERN_DUMP_FUNC_CALL_PATH() + " --params cpgFile=" +
                cpgFile.getAbsolutePath() + ",outFile=" + callFile.getAbsolutePath();

        Tool.executeCmdAndSaveLog(cmd, logger);

        if (callFile.exists()) {
            return callFile;
        } else {
            return null;
        }
    }

    // 获取函数pdg文件
    public File getPDGFileByCPGFile(File cpgFile, String pdgFilePath) {
        File pdgFile = new File(PathConfig.getInstance().getPDG_FILE_PATH());
        if (pdgFile.exists()) {
            pdgFile.delete();
        }
        // "/mnt/share/code/joern-cli/joern --script /mnt/share/code/joern-cli/scripts/pdg-for-funcs-dump.sc --params cpgFile=a.bin.zip,outFile=a.json"

        String cmd = PathConfig.getInstance().getJOERN_CMD_PATH() + " --script " + PathConfig.getInstance().getJOERN_DUMP_PDG_PATH() + " --params cpgFile=" +
                cpgFile.getAbsolutePath() + ",outFile=" + pdgFile.getAbsolutePath();

        Tool.executeCmdAndSaveLog(cmd, logger);

        if (pdgFile.exists()) {
            return pdgFile;
        } else {
            return null;
        }
    }

    // 获取函数ast文件
    public File getASTFileByCPGFile(File cpgFile, String astFilePath) {
        File astFile = new File(astFilePath);
        if (astFile.exists()) {
            astFile.delete();
        }
        Tool.createFolderIfNotExist(astFilePath);

        String cmd = PathConfig.getInstance().getJOERN_CMD_PATH() + " --script " + PathConfig.getInstance().getJOERN_DUMP_AST_PATH() + " --params cpgFile=" +
                cpgFile.getAbsolutePath() + ",outFile=" + astFile.getAbsolutePath();

        Tool.executeCmdAndSaveLog(cmd, logger);

        if (astFile.exists()) {
            return astFile;
        } else {
            return null;
        }
    }
}
