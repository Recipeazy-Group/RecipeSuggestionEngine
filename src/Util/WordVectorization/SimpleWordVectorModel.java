package Util.WordVectorization;

import Util.Math.Vector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

public class SimpleWordVectorModel implements WordVectorModel {
    private String dataPath;

    private HashMap<String, Vector<Double>> vector;

    public SimpleWordVectorModel() {
    }

    public SimpleWordVectorModel(String dataPath) {
        this.dataPath = dataPath;
        vector = new HashMap<>();
        load();
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
        PriorityQueue<CosineResult> results = new PriorityQueue(new CosineResultComparator());
        Vector thisVector = vector.get(word);
        if(thisVector==null)throw new RuntimeException(word + " was not contained in food2vec model.");
        for(Map.Entry<String, Vector<Double>> vec : vector.entrySet()){
            results.add(new CosineResult(vec.getValue(), thisVector, vec.getKey()));
        }
        ArrayList<String> toReturn = new ArrayList<>(numReturn);
        for(int i=0; i<numReturn; i++){
            toReturn.add(i, results.remove().word);
        }
        return toReturn;
    }

    @Override
    public Vector<Double> getWordVector(String word) {
        return vector.get(word).clone();
    }

    @Override
    public double calcWordSimilarity(String word1, String word2) {
        return 0;
    }


    protected class CosineResult {
        public CosineResult(Vector a, Vector b, String word) {
            this.a = a;
            this.b = b;
            this.word=word;
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
