package test;

import com.opencsv.CSVWriter;
import config.PathConfig;
import tool.Tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GenerateTrainingData {
    public static void main(String[] args) {
        String trainingCsvPath = PathConfig.TRAINING_DATA_FOLDER + File.separator + "data.csv";
        File trainingCsvFile = new File(trainingCsvPath);

        if (trainingCsvFile.exists()) {
            trainingCsvFile.delete();
        }

        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(trainingCsvFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // clone data
        File[] syntaxFolders = new File(PathConfig.SYNTAX_FEATURE_FOLDER_PATH).listFiles();
        assert syntaxFolders != null;

        Map<String, List<File>> class2SyntaxFileList = new HashMap<>();
        Map<String, List<File>> class2SemanticFileList = new HashMap<>();

        for (File syntaxFolder : syntaxFolders) {
            String className = syntaxFolder.getName();
            for (File syntaxSubFolder : Objects.requireNonNull(syntaxFolder.listFiles())) {
                File syntaxFile = new File(syntaxSubFolder.getAbsolutePath() + File.separator + "syntax.txt");
                String subPath = Tool.getSrcPath(syntaxFile);
                File semanticFile = new File(PathConfig.SEMANTIC_FEATURE_FOLDER_PATH + File.separator + subPath + File.separator + "semantic.txt");

                List<File> syntaxFileList = class2SyntaxFileList.getOrDefault(className, new ArrayList<>());
                syntaxFileList.add(syntaxFile);
                class2SyntaxFileList.put(className, syntaxFileList);

                List<File> semanticFileList = class2SemanticFileList.getOrDefault(className, new ArrayList<>());
                semanticFileList.add(semanticFile);
                class2SemanticFileList.put(className, semanticFileList);

            }
        }

        List<String> classNameList = new ArrayList<>();
        // similar
        int similarCnt = 0;
        for (Map.Entry<String, List<File>> entry : class2SyntaxFileList.entrySet()) {
            classNameList.add(entry.getKey());
            List<File> syntaxFileList = entry.getValue();
            List<File> semanticFileList = class2SemanticFileList.get(entry.getKey());
            int len = syntaxFileList.size();

            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    similarCnt++;
                    List<Double> syntaxVecLeft = Tool.getDoubleListFromFile(syntaxFileList.get(i));
                    List<Double> semanticVecLeft = Tool.getDoubleListFromFile(semanticFileList.get(i));

                    List<Double> syntaxVecRight = Tool.getDoubleListFromFile(syntaxFileList.get(j));
                    List<Double> semanticVecRight = Tool.getDoubleListFromFile(semanticFileList.get(j));

                    int vecLen = syntaxVecLeft.size() + semanticVecLeft.size() + syntaxVecRight.size() + semanticVecRight.size() + 1;
                    String[] line = new String[vecLen];
                    int index = 0;
                    for (Double d : syntaxVecLeft) {
                        line[index++] = d.toString();
                    }
                    for (Double d : semanticVecLeft) {
                        line[index++] = d.toString();
                    }
                    for (Double d : syntaxVecRight) {
                        line[index++] = d.toString();
                    }
                    for (Double d : semanticVecRight) {
                        line[index++] = d.toString();
                    }
                    line[index] = "1";
                    assert csvWriter != null;
                    csvWriter.writeNext(line);
                }
            }
        }

        int notSimilarCnt = 0;
        boolean flag = true;
        int classCnt = classNameList.size();
        for (int i = 0; i < classCnt; i++) {
            if (!flag) {
                break;
            }
            for (int j = i + 1; j < classCnt; j++) {
                if (!flag) {
                    break;
                }
                List<File> syntaxFileListLeft = class2SyntaxFileList.get(classNameList.get(i));
                List<File> semanticFileListLeft = class2SemanticFileList.get(classNameList.get(i));

                List<File> syntaxFileListRight = class2SyntaxFileList.get(classNameList.get(j));
                List<File> semanticFileListRight = class2SemanticFileList.get(classNameList.get(j));

                int len1 = syntaxFileListLeft.size();
                int len2 = syntaxFileListRight.size();

                for (int k = 0; k < len1; k++) {
                    if (!flag) {
                        break;
                    }
                    for (int l = 0; l < len2; l++) {
                        if (notSimilarCnt > similarCnt) {
                            flag = false;
                            break;
                        }
                        if ((k + l) % 10 < 5) {
                            continue;
                        }
                        notSimilarCnt++;

                        List<Double> syntaxVecLeft = Tool.getDoubleListFromFile(syntaxFileListLeft.get(k));
                        List<Double> semanticVecLeft = Tool.getDoubleListFromFile(semanticFileListLeft.get(k));

                        List<Double> syntaxVecRight = Tool.getDoubleListFromFile(syntaxFileListRight.get(l));
                        List<Double> semanticVecRight = Tool.getDoubleListFromFile(semanticFileListRight.get(l));

                        int vecLen = syntaxVecLeft.size() + semanticVecLeft.size() + syntaxVecRight.size() + semanticVecRight.size() + 1;
                        String[] line = new String[vecLen];
                        int index = 0;
                        for (Double d : syntaxVecLeft) {
                            line[index++] = d.toString();
                        }
                        for (Double d : semanticVecLeft) {
                            line[index++] = d.toString();
                        }
                        for (Double d : syntaxVecRight) {
                            line[index++] = d.toString();
                        }
                        for (Double d : semanticVecRight) {
                            line[index++] = d.toString();
                        }
                        line[index] = "0";
                        assert csvWriter != null;
                        csvWriter.writeNext(line);
                    }
                }

            }
        }

        try {
            assert csvWriter != null;
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
