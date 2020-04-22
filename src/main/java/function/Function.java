package function;

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
 * @author cary.shi on 2019/11/28
 */
public class Function {

    private String name;
    private long id;
    private String location; // 相对路径

    Function(String name, long id, String location) {
        this.name = name;
        this.id = id;
        this.location = location;
    }

    public static List<Function> getAllFunctions(File funcFile) {
        List<Function> functionList = new ArrayList<>();
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(funcFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert lines != null;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] cols = line.split("\t");
            Function function = new Function(cols[0], Long.parseLong(cols[1]), cols[2]);
            functionList.add(function);
        }
        return functionList;
    }

    public static List<Function> getFunctionListBySourceFile(File sourceFile) {
        List<Function> functionList = new ArrayList<>();

        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);

        File func = new File(PathConfig.getInstance().getFUNC_FOLDER_PATH() + File.separator + folderAndFilePath + File.separator + "func.txt");
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(func, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert lines != null;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] cols = line.split("\t");
            Function function = new Function(cols[0], Long.parseLong(cols[1]), cols[2]);
            functionList.add(function);
        }

        return functionList;
    }

    List<File> getFuncEdgeFileListBySourceFile(File sourceFile) {
        List<File> resList = new ArrayList<>();
        String folderAndFilePath = Tool.getFolderAndFilePath(sourceFile);
        File[] files = new File(PathConfig.getInstance().getFUNC_EDGE_PATH() + File.separator + folderAndFilePath).listFiles();
        assert files != null;
        Collections.addAll(resList, files);

        return resList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
