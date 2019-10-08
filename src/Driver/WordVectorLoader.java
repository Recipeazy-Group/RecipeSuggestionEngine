package Driver;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.File;

public abstract class WordVectorLoader {
    private static Word2Vec vector;

    public void loadWordVectors(String path) {
        if (vector != null) {
            throw new RuntimeException("The word vector model has already been loaded.");
        }
        File model = new File(path);
        System.out.println("WordVectorLoader: loading word vector at path " + path);
        vector = WordVectorSerializer.readWord2VecModel(model);
        System.out.println("WordVectorLoader: vector loaded.");
    }
}
