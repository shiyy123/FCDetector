package embedding;

import config.CmdConfig;
import config.PathConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph2Vec {
    private final static Logger logger = LogManager.getLogger(Graph2Vec.class);

    public static File generateGraph2VecFeatureFile(String cfgContentFolderPath, String graphFeaturePath, int featureLength) {
        File graphFeatureFile = new File(graphFeaturePath);
        if (graphFeatureFile.exists()) {
            graphFeatureFile.delete();
        }

        String cmd = PathConfig.getInstance().getGRAPH2VEC_VENV_PATH() + " " + PathConfig.getInstance().getGRAPH2VEC_SCRIPT_PATH() + " --input-path " + cfgContentFolderPath + File.separator +
                " --output-path " + graphFeaturePath + " --dimensions " + featureLength;
        Tool.executeCmdAndSaveLog(cmd, logger);
        return graphFeatureFile;
    }

    public static void generateSemanticFeatureFiles(File featureCsvFile, File dot2cfgFile) {
//        File featureCsvFile = new File("/mnt/share/CloneData/data/graph_feature/feature.csv");

//        File dot2cfgFile = new File("/mnt/share/CloneData/data/dot2cfg.txt");
        List<String> dot2cfgList = null;
        try {
            dot2cfgList = FileUtils.readLines(dot2cfgFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, List<Double>> subPath2CfgVec = new HashMap<>();
        assert dot2cfgList != null;
        for (String line : dot2cfgList) {
            File dotFile = new File(line.split(" ")[0]);
            File cfgFile = new File(line.split(" ")[1]);
            String subPath = Tool.getSrcPath(dotFile);
            String curName = dotFile.getName().substring(0, dotFile.getName().indexOf("."));

            List<Double> cfgVec = Tool.getCfgVecForFeature(featureCsvFile, cfgFile);
            subPath2CfgVec.put(subPath + File.separator + curName, cfgVec);
        }

        for (Map.Entry<String, List<Double>> entry : subPath2CfgVec.entrySet()) {
            String subFolderPath = entry.getKey().substring(0, entry.getKey().lastIndexOf(File.separator));
            String fileName = entry.getKey().substring(entry.getKey().lastIndexOf(File.separator) + 1);
            String semanticFeatureFolderPath = PathConfig.getInstance().getSEMANTIC_FEATURE_FOLDER_PATH() + File.separator + subFolderPath;

            File semanticFeatureFolder = new File(semanticFeatureFolderPath);
            if (!semanticFeatureFolder.exists()) {
                semanticFeatureFolder.mkdirs();
            }
            File semanticFeatureFile = new File(semanticFeatureFolder.getAbsolutePath() + File.separator + fileName + ".txt");
            if (semanticFeatureFile.exists()) {
                semanticFeatureFile.delete();
            }
            try {
                FileUtils.write(semanticFeatureFile, entry.getValue().toString(), StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
