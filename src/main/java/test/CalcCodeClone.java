package test;

import config.PathConfig;
import org.apache.commons.io.FileUtils;
import tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalcCodeClone {

    public static void main(String[] args) throws IOException {
        String word2vecPath = "/mnt/share/FCDetector/AutoenCODE/out/word2vec/word2vec.out";
        File word2vecFile = new File(word2vecPath);
        Map<String, List<Double>> identifier2Vec = Tool.getIdentifier2Vec(word2vecFile);

        // ast
        Map<String, List<Double>> src2AstVec = new HashMap<>();
        File dot2astFile = new File("/mnt/share/CloneData/data/dot2ast.txt");
        List<String> dot2astList = FileUtils.readLines(dot2astFile, StandardCharsets.UTF_8);
        Map<String, List<Double>> subPath2AstVec = new HashMap<>();
        for (String line : dot2astList) {
            File dotFile = new File(line.split(" ")[0]);
            File astFile = new File(line.split(" ")[1]);
            String subPath = Tool.getSrcPath(dotFile);

            String astIdentifiers = FileUtils.readFileToString(astFile, StandardCharsets.UTF_8);
            List<Double> astVec = Tool.getAstVecForFeature(astIdentifiers, identifier2Vec);

            subPath2AstVec.put(subPath, astVec);
        }

        for (Map.Entry<String, List<Double>> entry : subPath2AstVec.entrySet()) {
            File syntaxFeatureFolder = new File(PathConfig.getInstance().getSYNTAX_FEATURE_FOLDER_PATH()+ File.separator + entry.getKey());
            if (!syntaxFeatureFolder.exists()) {
                syntaxFeatureFolder.mkdirs();
            }
            File syntaxFeatureFile = new File(syntaxFeatureFolder.getAbsolutePath() + File.separator + "syntax.txt");
            if (syntaxFeatureFile.exists()) {
                syntaxFeatureFile.delete();
            }
            FileUtils.write(syntaxFeatureFile, entry.getValue().toString(), StandardCharsets.UTF_8, true);
        }

        // cfg
        File featureCsvFile = new File("/mnt/share/CloneData/data/graph_feature/feature.csv");

        File dot2cfgFile = new File("/mnt/share/CloneData/data/dot2cfg.txt");
        List<String> dot2cfgList = FileUtils.readLines(dot2cfgFile, StandardCharsets.UTF_8);

        Map<String, List<Double>> subPath2CfgVec = new HashMap<>();
        for (String line : dot2cfgList) {
            File dotFile = new File(line.split(" ")[0]);
            File cfgFile = new File(line.split(" ")[1]);
            String subPath = Tool.getSrcPath(dotFile);

            List<Double> cfgVec = Tool.getCfgVecForFeature(featureCsvFile, cfgFile);
            subPath2CfgVec.put(subPath, cfgVec);
        }

        for (Map.Entry<String, List<Double>> entry : subPath2CfgVec.entrySet()) {
            File semanticFeatureFolder = new File(PathConfig.getInstance().getSEMANTIC_FEATURE_FOLDER_PATH() + File.separator + entry.getKey());
            if (!semanticFeatureFolder.exists()) {
                semanticFeatureFolder.mkdirs();
            }
            File semanticFeatureFile = new File(semanticFeatureFolder.getAbsolutePath() + File.separator + "semantic.txt");
            if (semanticFeatureFile.exists()) {
                semanticFeatureFile.delete();
            }
            FileUtils.write(semanticFeatureFile, entry.getValue().toString(), StandardCharsets.UTF_8, true);
        }

    }
}
