package Util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class SimpleWordVector {
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

    }
}
