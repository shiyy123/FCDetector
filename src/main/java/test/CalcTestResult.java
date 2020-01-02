package test;

import java.io.File;
import java.util.Objects;

public class CalcTestResult {
    public static void main(String[] args) {
        String path = "/mnt/share/CloneData/data/feature_cfg";
        File[] folders = new File(path).listFiles();
        assert folders != null;
        for (File subFolder : folders) {
            File[] subFiles = subFolder.listFiles();
            assert subFiles != null;
            for (File file : subFiles) {
                if (Objects.requireNonNull(file.listFiles()).length == 1) {
                    System.out.println(file.getAbsolutePath());
                }
            }
        }
    }
}
