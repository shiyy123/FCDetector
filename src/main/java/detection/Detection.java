package detection;

import config.PathConfig;
import embedding.HOPE;
import embedding.Word2Vec;
import org.apache.commons.io.FileUtils;
import process.ProcessExecutorList;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author cary.shi on 2019/11/29
 */
public class Detection {

    public static boolean singleFileCloneDetection(File file1, File file2,
                                                   List<File> word2vecFeatureFileList1, List<File> hopeFeatureFileList1,
                                                   List<File> word2vecFeatureFileList2, List<File> hopeFeatureFileList2) {
        boolean flag = false;
        int len1 = word2vecFeatureFileList1.size(), len2 = word2vecFeatureFileList2.size();
        for (int i = 0; i < len1; i++) {
            File word2vec1 = word2vecFeatureFileList1.get(i);
            File hope1 = hopeFeatureFileList1.get(i);

            List<Double> word2vecVec1 = Word2Vec.getVecFromEmbeddingFile(word2vec1);
            List<Double> hopeVec1 = HOPE.getVecFromEmbeddingFile(hope1);
            List<Double> feature1 = new ArrayList<>(word2vecVec1);
            feature1.addAll(hopeVec1);

            for (int j = 0; j < len2; j++) {
                File word2vec2 = word2vecFeatureFileList2.get(j);
                File hope2 = hopeFeatureFileList2.get(j);

                List<Double> word2vecVec2 = Word2Vec.getVecFromEmbeddingFile(word2vec2);
                List<Double> hopeVec2 = HOPE.getVecFromEmbeddingFile(hope2);

                List<Double> feature2 = new ArrayList<>(word2vecVec2);
                feature2.addAll(hopeVec2);

                if (isClone(feature1, feature2)) {
                    flag = true;
                    System.out.println(file1.getAbsolutePath() + " feature " + i + "[" + getFuncNamesByFeatureId(file1, i) + "] and "
                            + file2.getAbsolutePath() + " feature " + j + "[" + getFuncNamesByFeatureId(file2, j) +
                            "] is functional code clone");
                }
            }
        }
        return flag;
    }

    private static String getFuncNamesByFeatureId(File sourceCodeFile, int id) {
        String folderAndFilePath = Tool.getFolderAndFilePath(sourceCodeFile);
        File featureFile = new File(PathConfig.getInstance().getFEATURE_FOLDER_PATH() + File.separator + folderAndFilePath + File.separator + "feature.txt");
        File funcFile = new File(PathConfig.getInstance().getFUNC_FOLDER_PATH() + File.separator + folderAndFilePath + File.separator + "func.txt");

        HashMap<Integer, String> map = new HashMap<>();
        List<String> funcList = new ArrayList<>();

        try {
            funcList = FileUtils.readLines(funcFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String row : funcList) {
            String[] cols = row.split("\t");
            map.put(Integer.parseInt(cols[1]), cols[0]);
        }

        List<String> featureStringList = new ArrayList<>();
        try {
            featureStringList = FileUtils.readLines(featureFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String funcIdLine = featureStringList.get(id);
        funcIdLine = funcIdLine.substring(funcIdLine.indexOf('[') + 1, funcIdLine.indexOf(']'));
        String[] funcIds = funcIdLine.split(",");

        StringBuilder res = new StringBuilder();
        for (String funcId : funcIds) {
            res.append(map.getOrDefault(Integer.parseInt(funcId.trim()), "unknown"));
            res.append("(").append(funcId.trim()).append(")").append(",");
        }
        return res.toString().substring(0, res.length() - 1);
    }

    private static boolean isClone(List<Double> feature1, List<Double> feature2) {
        boolean clone = false;

        File tmpFeatureFile = new File(PathConfig.getInstance().getTMP_PATH() + File.separator + "feature1.txt");
        if (tmpFeatureFile.exists()) {
            tmpFeatureFile.delete();
        }

        StringBuilder sb = new StringBuilder();
        for (Double aDouble : feature1) {
//            sb.append(String.format("%.6f", aDouble));
            sb.append(aDouble);
            sb.append(" ");
        }
        for (int i = 0; i < feature2.size() - 1; i++) {
//            sb.append(String.format("%.6f", feature2.get(i)));
            sb.append(feature2.get(i));
            sb.append(" ");
        }
//        sb.append(String.format("%.6f", feature2.get(feature2.size() - 1)));
        sb.append(feature2.get(feature2.size() - 1));
        try {
            FileUtils.write(tmpFeatureFile, sb.toString(), StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Process process = Runtime.getRuntime().exec("python3 /cdetector/script/CloneDetect.py " + tmpFeatureFile.getAbsolutePath());
            ProcessExecutorList processExecutorList = new ProcessExecutorList(process);
            processExecutorList.execute();

            List<String> errorList = processExecutorList.getErrorList();
            List<String> outList = processExecutorList.getOutputList();

            StringBuilder stringBuilder = new StringBuilder();
            for (String s : errorList) {
                stringBuilder.append(s);
            }
            for (String s : outList) {
                stringBuilder.append(s);
            }

            if (stringBuilder.toString().contains("0")) {
                clone = true;
            } else if (stringBuilder.toString().contains("1")) {
                clone = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
