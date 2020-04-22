package test;

import config.PathConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tool.Tool;

import java.io.File;
import java.util.Objects;

// generate dot png for all cfg dot file
public class GenerateDotGraph {
    private final static Logger logger = LogManager.getLogger(GenerateDotGraph.class);

    public static void main(String[] args) {
        File[] folders = new File(PathConfig.getInstance().getFEATURE_CFG_FOLDER_PATH()).listFiles();
        assert folders != null;
        for (File folder : folders) {
            for (File subFolder : Objects.requireNonNull(folder.listFiles())) {
                File dotFile = Objects.requireNonNull(subFolder.listFiles())[0];

                String subPath = folder.getName() + File.separator + subFolder.getName();

                File feature_dot_folder = new File(PathConfig.getInstance().getFEATURE_CFG_DOT_FOLDER_PATH() + File.separator + subPath);
                if (!feature_dot_folder.exists()) {
                    feature_dot_folder.mkdirs();
                }

                StringBuilder cmd = new StringBuilder();
                cmd.append("dot ").append(dotFile.getAbsolutePath()).append(" -Tpng -o ").
                        append(PathConfig.getInstance().getFEATURE_CFG_DOT_FOLDER_PATH()).append(File.separator).append(subPath).append(File.separator).
                        append("cfg.png");
                System.out.println(cmd.toString());

                Tool.executeCmdAndSaveLog(cmd.toString(), logger);
            }
        }
    }
}
