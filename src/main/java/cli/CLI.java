package cli;

import config.PathConfig;
import detection.Detection;
import embedding.HOPE;
import embedding.Word2Vec;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * @author cary.shi on 2019/11/29
 */
public class CLI {
    private static Options options = new Options();

    private void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Show how to use Functional Clone Detection.", options);
    }

    void detectClone(List<File> sourceFiles) {
        int len = sourceFiles.size();
        for (int i = 0; i < len; i++) {
            File sourceFile1 = sourceFiles.get(i);
            List<File> word2vecFeatureFileList1 = Word2Vec.getWord2VecBySourceFile(sourceFile1);
            List<File> hopeFeatureFileList1 = HOPE.getHOPEVecBySourceFile(sourceFile1);

            for (int j = i + 1; j < len; j++) {
                File sourceFile2 = sourceFiles.get(j);
                List<File> word2vecFeatureFileList2 = Word2Vec.getWord2VecBySourceFile(sourceFile2);
                List<File> hopeFeatureFileList2 = HOPE.getHOPEVecBySourceFile(sourceFile2);


            }
        }
    }

    public static void main(String[] args) {
        CLI cli = new CLI();

        options.addOption("H", "Help", false, "Print help message");
        options.addOption("F1", "File1", true, "The path of the file to scan");
        options.addOption("F2", "File2", true, "The path of the file to scan");

        options.addOption("show", "show", false, "The file path and id of the feature");

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert commandLine != null;
        if (commandLine.hasOption("H")) {
            cli.printHelp();
            return;
        }
        String path1 = commandLine.getOptionValue("F1");
        File scanFolder1 = new File(path1);
        if (!scanFolder1.exists()) {
            System.out.println("F1 not found");
        }
        File[] scanFiles1;
        if (scanFolder1.isDirectory()) {
            scanFiles1 = scanFolder1.listFiles();
        } else {
            scanFiles1 = new File[]{scanFolder1};
        }

        String path2 = commandLine.getOptionValue("F2");
        File scanFolder2 = new File(path2);
        if (!scanFolder2.exists()) {
            System.out.println("F2 not found");
        }
        File[] scanFiles2;

        if (scanFolder2.isDirectory()) {
            scanFiles2 = scanFolder2.listFiles();
        } else {
            scanFiles2 = new File[]{scanFolder2};
        }

        assert scanFiles1 != null;
        int len1 = scanFiles1.length;
        assert scanFiles2 != null;
        int len2 = scanFiles2.length;
        boolean flag = false;

        for (int i = 0; i < len1; i++) {
            File file1 = scanFiles1[i];

//            List<File> word2vecFeatureFileList1 = Word2Vec.getWord2VecBySourceFile(file1);
//            List<File> hopeFeatureFileList1 = HOPE.getHOPEVecBySourceFile(file1);
            List<File> word2vecFeatureFileList1 = Word2Vec.getEmbeddingFileListBySourceFile(file1);
            List<File> hopeFeatureFileList1 = HOPE.getEmbeddingFileListBySourceFile(file1);

            if (word2vecFeatureFileList1.isEmpty()) {
                System.out.println("w1 empty");
            }
            if (hopeFeatureFileList1.isEmpty()) {
                System.out.println("h1 empty");
            }

            for (int j = 0; j < len2; j++) {
                File file2 = scanFiles2[j];
                if (file1.getAbsolutePath().equals(file2.getAbsolutePath())) {
                    continue;
                }
//                List<File> word2vecFeatureFileList2 = Word2Vec.getWord2VecBySourceFile(file2);
//                List<File> hopeFeatureFileList2 = HOPE.getHOPEVecBySourceFile(file2);
                List<File> word2vecFeatureFileList2 = Word2Vec.getEmbeddingFileListBySourceFile(file2);
                List<File> hopeFeatureFileList2 = HOPE.getEmbeddingFileListBySourceFile(file2);

                if (word2vecFeatureFileList2.isEmpty()) {
                    System.out.println("w2 empty");
                }
                if (hopeFeatureFileList2.isEmpty()) {
                    System.out.println("h2 empty");
                }

                if (Detection.singleFileCloneDetection(file1, file2, word2vecFeatureFileList1, hopeFeatureFileList1, word2vecFeatureFileList2, hopeFeatureFileList2)) {
                    flag = true;
                }
            }
        }
        if(!flag){
            System.out.println("No functional code clone is detected");
            return;
        }
        System.out.println("Please input the feature location and id");
        Scanner scanner = new Scanner(System.in);
        String line;

        while (!(line = scanner.nextLine()).equals("exit")) {
            String[] cols = line.split(":");
            String folderAndFilePath = Tool.getFolderAndFilePath(new File(cols[0]));
            String featureId = cols[1];

            File featureFile = new File(PathConfig.getInstance().getFEATURE_CONTENT_PATH() + File.separator + folderAndFilePath + File.separator + featureId + ".txt");
            try {
                List<String> featureContentList = FileUtils.readLines(featureFile, StandardCharsets.UTF_8);
                featureContentList.forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
