package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public abstract class ResourceRepo {
    private static final String CONFIG_PATH = "RecipeSuggestionEngine/config.ini";

    public static HashMap<String, String> props;

    static {
        System.out.println("Initializing resource repo.");
        props = new HashMap<>();
        try {
            BufferedReader bR = new BufferedReader(new FileReader(new File(CONFIG_PATH)));
            String line;
            while ((line = bR.readLine()) != null) {
                props.put(
                        line.substring(0, line.indexOf('=')-1),
                        line.substring(line.indexOf('=')+2)
                );
            }
            bR.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
