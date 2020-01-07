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

        String cmd = CmdConfig.GRAPH2VEC_VENV_PATH + " " + CmdConfig.GRAPH2VEC_SCRIPT_PATH + " --input-path " + cfgContentFolderPath + File.separator +
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

            List<Double> cfgVec = Tool.getCfgVecForFeature(featureCsvFile, cfgFile);
            subPath2CfgVec.put(subPath, cfgVec);
        }

        for (Map.Entry<String, List<Double>> entry : subPath2CfgVec.entrySet()) {
            File semanticFeatureFolder = new File(PathConfig.SEMANTIC_FEATURE_FOLDER_PATH + File.separator + entry.getKey());
            if (!semanticFeatureFolder.exists()) {
                semanticFeatureFolder.mkdirs();
            }
            File semanticFeatureFile = new File(semanticFeatureFolder.getAbsolutePath() + File.separator + "semantic.txt");
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
