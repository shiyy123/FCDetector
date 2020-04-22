package test;

import config.CmdConfig;
import config.PathConfig;
import embedding.Word2Vec;

import java.io.File;

public class GenerateText {
    public static void main(String[] args) {
        File textCorpusFile = Word2Vec.generateTextCorpus(PathConfig.getInstance().getSRC_FOLDER_PATH(), PathConfig.getInstance().getTEXT_WORD2VEC_CORPUS_FILE_PATH());
        File word2vecOutFile = Word2Vec.generateWord2vecFile(textCorpusFile, PathConfig.getInstance().getAST_WORD2VEC_OUT_FILE_PATH(), PathConfig.getInstance().getWORD2VEC_CMD_PATH(), 16);

    }
}
