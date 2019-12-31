package embedding;

import config.PathConfig;
import org.apache.commons.io.FileUtils;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author cary.shi on 2019/11/29
 */
public class Word2Vec {
    public static HashMap<String, List<Double>> getAllVec(File word2vecOutFile) {
        HashMap<String, List<Double>> map = new HashMap<>();
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(word2vecOutFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert lines != null;
        for (String line : lines) {
            String[] cols = line.split(" ");
            List<Double> valueList = new ArrayList<>();
            for (int i = 1; i < cols.length; i++) {
                valueList.add(Double.parseDouble(cols[i]));
            }
            map.put(cols[0], valueList);
        }
        return map;
    }


    private static List<File> getFeatureVecFileListBySourceFile(File sourceFile) {
        List<File> res = new ArrayList<>();

        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);
        File vecFolder = new File(PathConfig.EMBEDDING_FEATURE_WORD2VEC_PATH + File.separator + folderAndFilePath);
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
        File[] files = new File(PathConfig.EMBEDDING_FUNC_WORD2VEC_PATH + File.separator + folderAndFilePath).listFiles();
        assert files != null;
        Collections.addAll(res, files);
        return res;
    }

    public static List<File> getEmbeddingFileListBySourceFile(File sourceFile) {
        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);
        File[] embedFiles = new File(PathConfig.IDENT_EMBED_PATH + File.separator + folderAndFilePath).listFiles();
        List<File> res = new ArrayList<>();
        assert embedFiles != null;
        Collections.addAll(res, embedFiles);
        return res;
    }

    public static List<File> getWord2VecBySourceFile(File sourceFile) {
        List<File> featureList = getFeatureVecFileListBySourceFile(sourceFile);
        if (featureList != null) {
            return featureList;
        }
        return getFuncVecFileListBySourceFile(sourceFile);
    }

    public static List<Double> getVecFromEmbeddingFile(File embeddingFile) {
        List<Double> res = new ArrayList<>();
        String s = null;
        try {
            s = FileUtils.readFileToString(embeddingFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert s != null;
        String[] cols = s.split(" ");
        for (String col : cols) {
            if (col.trim().isEmpty()) {
                continue;
            }
            res.add(Double.parseDouble(col));
        }

        return res;
    }

}
