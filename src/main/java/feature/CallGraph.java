package feature;

import config.PathConfig;
import tool.Tool;

import java.io.File;

/**
 * @author cary.shi on 2019/11/28
 */
public class CallGraph {
    File sourceCodeFile2CallGraphFile(File sourceCodeFile) {
        String folderAndFilePath = Tool.getFolderAndFilePath(sourceCodeFile);
        return new File(PathConfig.getInstance().getCALL_FOLDER_PATH() + File.separator + folderAndFilePath + File.separator + "call.txt");
    }

}
