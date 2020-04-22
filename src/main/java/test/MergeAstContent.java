package test;

import config.PathConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MergeAstContent {
    public static void main(String[] args) {
        File[] astContentFiles = new File(PathConfig.getInstance().getAST_CONTENT_FOLDER_PATH()).listFiles();
        assert astContentFiles != null;
        List<String> contentList = new ArrayList<>();
        for (File astContentFile : astContentFiles) {
            try {
                String content = FileUtils.readFileToString(astContentFile, StandardCharsets.UTF_8);
                String[] cols = content.split(" ");
                for (String col : cols) {
                    System.out.println(col.length());
                }
                contentList.add(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File astContentFile = new File(PathConfig.getInstance().getBase() + File.separator + "corpus.src");
        if (astContentFile.exists()) {
            astContentFile.delete();
        }
        try {
            FileUtils.writeLines(astContentFile, contentList, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
