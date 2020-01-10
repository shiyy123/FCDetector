package test;

import config.CmdConfig;
import config.PathConfig;
import embedding.Graph2Vec;
import embedding.Word2Vec;

import java.io.File;

/**
 * @author cary.shi on 2019/12/24
 */
public class Test {
    public static void main(String[] args) {
//        File cfgDotFilePath2graphJsonFile = new File(PathConfig.DOT2CFG_PATH);
//        File dotFile2astContentFile = new File(PathConfig.DOT2AST_PATH);
//
//        // generate ast corpus
//        File astCorpusFile = Word2Vec.generateCorpusFromFolder(PathConfig.AST_CONTENT_FOLDER_PATH, PathConfig.AST_WORD2VEC_CORPUS_FILE_PATH);
//        // generate word2vec vectors for corpus
//        File word2vecOutFile = Word2Vec.generateWord2vecFile(astCorpusFile, PathConfig.AST_WORD2VEC_OUT_FILE_PATH, CmdConfig.WORD2VEC_CMD_PATH, 100);
//        // generate syntax feature, according to the folder structure
//        Word2Vec.generateSyntaxFeatureFiles(word2vecOutFile, dotFile2astContentFile);
//
//        // generate graph2vec vectors for cfg
//        File graph2vecOutFile = Graph2Vec.generateGraph2VecFeatureFile(PathConfig.CFG_CONTENT_FOLDER_PATH, PathConfig.CFG_GRAPH2VEC_OUT_PATH, 16);
//        // generate semantic feature, according to the folder structure
//        Graph2Vec.generateSemanticFeatureFiles(graph2vecOutFile, cfgDotFilePath2graphJsonFile);

    }
}
