package Util.WordVectorization;

import Util.Math.Vector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

public class SimpleWordVectorModel implements WordVectorModel, Serializable {
    private String dataPath;

    private HashMap<String, Vector<Double>> vector;

    public SimpleWordVectorModel(String dataPath) {
        this.dataPath = dataPath;
        vector = new HashMap<>();
        load();
    }

    public SimpleWordVectorModel(HashMap<String, Vector<Double>> v){
        vector = v;
        dataPath = "local";
    }

    public void load() {
        if (dataPath != null) load(dataPath);
        else throw new RuntimeException("SimpleWordVector: load without path set attempted.");
    }

    public void load(String dataPath) {
        File toLoad = new File(dataPath);
        System.out.println(toLoad.getAbsolutePath());
        BufferedReader bR = null;
        try {
            bR = new BufferedReader(new FileReader(toLoad));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONTokener vectorTokener = new JSONTokener(bR);
        JSONObject vectors = new JSONObject(vectorTokener);
        for (String term : vectors.keySet()) {
            JSONArray vec = vectors.getJSONArray(term);
            Double[] wordVec = new Double[vec.length()];
            for (int i = 0; i < wordVec.length; i++) {
                wordVec[i] = vec.getDouble(i);
            }
            vector.put(term.toLowerCase(), new Vector(wordVec));
        }
        try {
            bR.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<String> getClosestMatches(String word, int numReturn) {
        Vector thisVector = vector.get(word);
        if (thisVector == null)
            throw new RuntimeException(word + " was not contained in food2vec model.");
        return getClosestMatches(thisVector, numReturn);
    }

    @Override
    public Collection<String> getClosestMatches(Vector<Double> word, int numReturn) {
        PriorityQueue<CosineResult> results = new PriorityQueue(new CosineResultComparator());
        for (Map.Entry<String, Vector<Double>> vec : vector.entrySet()) {
            results.add(new CosineResult(vec.getValue(), word, vec.getKey()));
        }
        ArrayList<String> toReturn = new ArrayList<>(numReturn);
        for (int i = 0; i < numReturn && !results.isEmpty(); i++) {
            toReturn.add(i, results.remove().word);
        }
        return toReturn;
    }

    @Override
    public Vector<Double> getWordVector(String word) {
        return vector.containsKey(word) ? vector.get(word).clone() : Vector.zeros(getItemDimension());
    }

    public int getItemDimension(){
        return vector.entrySet().iterator().next().getValue().getSize();

    }

    public boolean contains(String word){
        return vector.containsKey(word);
    }

    @Override
    public double calcWordSimilarity(String word1, String word2) {
        Vector w1 = vector.get(word1), w2 = vector.get(word2);
        if (w1 == null || w2 == null)
            throw new RuntimeException(word1 +" or " + word2 + " was not contained in food2vec model.");
        return new CosineResult(w1, w2, "N/A").cosDistance;
    }

    public HashSet<String> getWordVocab(){
        HashSet<String> toReturn = new HashSet<>();
        for(Map.Entry<String, Vector<Double>> entry : vector.entrySet()){
            toReturn.add(entry.getKey());
        }
        return toReturn;
    }

    @Override
    public int getDimension() {
        return vector.size();
    }


    protected class CosineResult {
        //Note that a cosDistance of 1 is "most similar"
        public CosineResult(Vector a, Vector b, String word) {
            this.a = a;
            this.b = b;
            this.word = word;
            cosDistance = Vector.dotProduct(a, b) / (a.getNorm() * b.getNorm()); //cos(theta) = ( <a, b> ) / ( |a| * |b| )
        }

        String word;
        Vector a, b;
        double cosDistance;
    }

    protected class CosineResultComparator implements Comparator<CosineResult> {
        public int compare(CosineResult s1, CosineResult s2) {
            return s1.cosDistance < s2.cosDistance ? 1 : -1;
        }
    }

}
