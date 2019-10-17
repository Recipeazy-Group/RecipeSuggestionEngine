package Util.WordVectorization;

import Util.Math.Vector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class SimpleWordVector implements WordVector{
    private String dataPath;

    private HashMap<String, double[]> vector;

    public SimpleWordVector() {
    }

    public SimpleWordVector(String dataPath) {
        this.dataPath = dataPath;
    }

    public void load() {
        if (dataPath != null) load(dataPath);
        else throw new RuntimeException("SimpleWordVector: load without path set attempted.");
    }

    public void load(String dataPath) {
        File toLoad = new File(dataPath);
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
            double[] wordVec = new double[vec.length()];
            for (int i = 0; i < wordVec.length; i++) {
                wordVec[i] = vec.getDouble(i);
            }
            vector.put(term, wordVec);
        }
        try {
            bR.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<String> getClosestMatches(String word, int numReturn) {
PriorityQueue<CosineResult> results = new PriorityQueue(new CosineResultComparator)



    }

    @Override
    public double[] getWordVector(String word) {
        return vector.get(word).clone();
    }

    @Override
    public double calcWordSimilarity(String word1, String word2) {
        return 0;
    }


    protected class CosineResult{
        public CosineResult(Vector a, Vector b){
            this.a=a;
            this.b=b;
            cosDistance = Vector.dotProduct(a, b) / (a.getNorm() * b.getNorm()); //cos(theta) = ( <a, b> ) / ( |a| * |b| )
        }
        Vector a, b;
        double cosDistance;
    }

}
