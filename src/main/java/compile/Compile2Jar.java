package compile;

import org.apache.commons.io.FileUtils;
import process.ProcessExecutor;
import process.ProcessExecutorList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cary.shi on 2019/12/21
 */
public class Compile2Jar {

    // 获取所有的文件列表
    public List<File> getAllFiles(String path) {
        List<File> fileList = new ArrayList<>();
        File[] folders = new File(path).listFiles();
        assert folders != null;
        for (File folder : folders) {
//            System.out.println(folder.getAbsolutePath());
            File defaultFolder = new File(folder.getAbsolutePath() + File.separator + "default");
            File sampleFolder = new File(folder.getAbsolutePath() + File.separator + "sample");
            File selectedFolder = new File(folder.getAbsolutePath() + File.separator + "selected");
            if (defaultFolder.exists()) {
                fileList.addAll(Arrays.asList(Objects.requireNonNull(defaultFolder.listFiles())));
            }
            if (sampleFolder.exists()) {
                fileList.addAll(Arrays.asList(Objects.requireNonNull(sampleFolder.listFiles())));
            }
            if (selectedFolder.exists()) {
                fileList.addAll(Arrays.asList(Objects.requireNonNull(selectedFolder.listFiles())));
            }
        }
        return fileList;
    }


    // 根据正则表达式获取内容
    public static String getSubUtilSimple(String soap, String rgex) {
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            return m.group(1);
        }
        return "";
    }


    public String getMainClassName(File javaSourceFile) {
        String mainClassName = null;
        try {
            String content = FileUtils.readFileToString(javaSourceFile, StandardCharsets.UTF_8);
            String rgex = "public class(.*)\\{";
            String middle = getSubUtilSimple(content, rgex);
            int extendsIndex = middle.indexOf("extends");
            int implementsIndex = middle.indexOf("implements");
            int index = middle.length();
            if (extendsIndex != -1 && implementsIndex != -1) {
                index = Math.min(extendsIndex, implementsIndex);
            } else if (extendsIndex != -1) {
                index = extendsIndex;
            } else if (implementsIndex != -1) {
                index = implementsIndex;
            }
            mainClassName = middle.substring(0, index).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mainClassName;
    }

    public int[] storePublicFileList(String path, String storePath) {
        int success = 0, fail = 0;

        List<File> fileList = getAllFiles(path);

        List<File> publicFileList = new ArrayList<>();

        for (File javaSourceFile : fileList) {
            String name = getMainClassName(javaSourceFile);
            if (name.isEmpty()) {
                fail++;
            } else {
                publicFileList.add(javaSourceFile);
                success++;
            }
        }

        try {
            FileUtils.writeLines(new File(storePath), publicFileList, System.getProperty("line.separator"), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new int[]{success, fail};
    }

    public String getFolderName(File javaSourceFile) {
        System.out.println(javaSourceFile.getAbsolutePath());
        String[] ss = javaSourceFile.getAbsolutePath().split("\\\\");
        return ss[ss.length - 3] + File.separator + ss[ss.length - 2];
    }

    public boolean canCompile(File javaSourceFile) {
        String compilePath = "G:\\data\\compile";
        String subFolderName = getFolderName(javaSourceFile);
        String name = javaSourceFile.getName().substring(0, javaSourceFile.getName().indexOf("."));
        File javaSourceFolder = new File(compilePath + File.separator + subFolderName + File.separator + name);
        if (!javaSourceFolder.exists()) {
            javaSourceFolder.mkdirs();
        }

        File buildFile = new File(javaSourceFolder.getAbsolutePath() + File.separator + "build");
        if (buildFile.exists()) {
            buildFile.delete();
        }
        if (!buildFile.exists()) {
            buildFile.mkdir();
        }

        String newName = getMainClassName(javaSourceFile);
        File renameJavaSourceFile = new File(javaSourceFolder.getAbsolutePath() + File.separator + newName + ".java");
        try {
            FileUtils.copyFile(javaSourceFile, renameJavaSourceFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File outFile = new File(javaSourceFolder.getAbsolutePath() + File.separator + "out.txt");
        File errorFile = new File(javaSourceFolder.getAbsolutePath() + File.separator + "error.txt");

        String cmd = "javac -d " + buildFile.getAbsolutePath() + " " + renameJavaSourceFile.getAbsolutePath();

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            ProcessExecutorList processExecutorList = new ProcessExecutorList(process);
            processExecutorList.execute();

            List<String> errorList = processExecutorList.getErrorList();
            List<String> outList = processExecutorList.getOutputList();
            FileUtils.writeLines(outFile, outList, true);
            FileUtils.writeLines(errorFile, errorList, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(buildFile.listFiles()).length > 0;
    }

    public List<File> getHaveClassFolderList(String path) {
        List<File> fileList = new ArrayList<>();
        List<File> folderList = getAllFiles(path);
        for (File folder : folderList) {
            File buildFolder = new File(folder.getAbsolutePath() + File.separator + "build");
            if (Objects.requireNonNull(buildFolder.listFiles()).length > 0) {
                fileList.add(folder);
            }
        }
        return fileList;
    }

    public boolean class2Jar(File sourceFolder) {
        String name = sourceFolder.getName();
        File buildFile = new File(sourceFolder.getAbsolutePath() + File.separator + "build");

        File outFile = new File(sourceFolder.getAbsolutePath() + File.separator + "jarOut.txt");
        File errorFile = new File(sourceFolder.getAbsolutePath() + File.separator + "jarError.txt");

        String cmd = "jar cvf " + sourceFolder.getAbsolutePath() + File.separator + name + ".jar "
                + buildFile.getAbsolutePath() + "\\*";
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            ProcessExecutorList processExecutorList = new ProcessExecutorList(process);
            processExecutorList.execute();

            List<String> errorList = processExecutorList.getErrorList();
            List<String> outList = processExecutorList.getOutputList();
            FileUtils.writeLines(outFile, outList, true);
            FileUtils.writeLines(errorFile, errorList, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        File[] files = sourceFolder.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getName().contains(".jar")) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        Compile2Jar compile2Jar = new Compile2Jar();

        // 生成可以找到文件名的java文件
        // compile2Jar.storePublicFileList("G:\\data\\bcb_reduced", "G:\\data\\publicFileList.txt");

        // 产生class
//        int success = 0, fail = 0;
//        try {
//            List<String> publicFilePathList = FileUtils.readLines(new File("G:\\data\\publicFileList.txt"), StandardCharsets.UTF_8);
//            for (String publicFilePath : publicFilePathList) {
//                File publicFile = new File(publicFilePath);
//                if (compile2Jar.canCompile(publicFile)) {
//                    success++;
//                } else {
//                    fail++;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(success);
//        System.out.println(fail);

        int success = 0, fail = 0;
        List<File> haveClassFileList = compile2Jar.getHaveClassFolderList("G:\\data\\compile");

        try {
            FileUtils.writeLines(new File("G:\\data\\jarFileList.txt"), haveClassFileList, System.getProperty("line.separator"), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        for (File haveClassFile : haveClassFileList) {
//            System.out.println(haveClassFile.getAbsolutePath());
//            if (compile2Jar.class2Jar(haveClassFile)) {
//                success++;
//            } else {
//                fail++;
//            }
//        }
//        System.out.println(success);
//        System.out.println(fail);
    }
}
