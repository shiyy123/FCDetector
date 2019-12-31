package test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class TestFeatureCFG {
    public static void main(String[] args) throws IOException {
        File[] featureFolders = new File("/mnt/share/CloneData/data/feature_cfg").listFiles();
        assert featureFolders != null;
        for (File featureFolder : featureFolders) {
            if (featureFolder.getName().equals("0")) {
                continue;
            }
            File[] subFolders = Objects.requireNonNull(featureFolder.listFiles());
//            System.out.println(featureFolder.getName());
//            System.out.println(subFolders.length);
            long smallest = Integer.MAX_VALUE;
            for (File subFolder : subFolders) {
                int len = Objects.requireNonNull(subFolder.listFiles()).length;
                if (len > 1) {
                    System.out.println(featureFolder.getName());
                    System.out.println(subFolder.getName());
                    System.out.println(len);
                }
                File feature_cfg = Objects.requireNonNull(subFolder.listFiles())[0];
//                System.out.println(feature_cfg.length());
                List<String> contents = FileUtils.readLines(feature_cfg, StandardCharsets.UTF_8);
                smallest = Math.min(smallest, contents.size());
            }
        }
    }
}
