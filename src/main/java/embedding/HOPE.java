package embedding;

import config.PathConfig;
import org.apache.commons.io.FileUtils;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cary.shi on 2019/11/29
 */
public class HOPE {
    private static List<File> getFeatureVecFileListBySourceFile(File sourceFile) {
        List<File> res = new ArrayList<>();

        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);
        File vecFolder = new File(PathConfig.getInstance().getEMBEDDING_FEATURE_HOPE_PATH() + File.separator + folderAndFilePath);
        if (!vecFolder.exists()) {
            return null;
        }
        File[] files = vecFolder.listFiles();
        assert files != null;
        Collections.addAll(res, files);
        return res;
    }

    private static List<File> getFuncVecFileListBySourceFile(File sourceFile) {
        List<File> res = new ArrayList<>();

        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);
        File[] files = new File(PathConfig.getInstance().getEMBEDDING_FUNC_HOPE_PATH() + File.separator + folderAndFilePath).listFiles();
        assert files != null;
        Collections.addAll(res, files);
        return res;
    }

    public static List<File> getEmbeddingFileListBySourceFile(File sourceFile) {
        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);
        File[] embedFiles = new File(PathConfig.getInstance().getCFG_EMBED_PATH() + File.separator + folderAndFilePath).listFiles();
        List<File> res = new ArrayList<>();
        assert embedFiles != null;
        Collections.addAll(res, embedFiles);
        return res;
    }

    public static List<File> getHOPEVecBySourceFile(File sourceFile) {
        List<File> featureList = getFeatureVecFileListBySourceFile(sourceFile);
        if (featureList != null) {
            return featureList;
        }
        return getFuncVecFileListBySourceFile(sourceFile);
    }

    public static List<Double> getVecFromEmbeddingFile(File embeddingFile) {
        List<Double> res = new ArrayList<>();

        String content = null;
        try {
            content = FileUtils.readFileToString(embeddingFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert content != null;
        content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
        String[] cols = content.split(" ");
        for (String s : cols) {
            if (s.trim().isEmpty()) {
                continue;
            }
            res.add(Double.parseDouble(s));
        }
        return res;
    }
}
