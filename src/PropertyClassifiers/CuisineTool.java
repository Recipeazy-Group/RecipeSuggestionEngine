package PropertyClassifiers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CuisineTool {

    public static Map<CUISINE, String> CUISINE_NAMES;

    static {
        CUISINE_NAMES = new HashMap<>();
        CUISINE_NAMES.put(CUISINE.NORTH_AMERICAN, "north american");
        CUISINE_NAMES.put(CUISINE.LATIN_AMERICAN, "latin american");
        CUISINE_NAMES.put(CUISINE.WESTERN_EUROPEAN, "western european");
        CUISINE_NAMES.put(CUISINE.SOUTHERN_EUROPEAN, "southern european");
        CUISINE_NAMES.put(CUISINE.EASTERN_EUROPEAN, "eastern european");
        CUISINE_NAMES.put(CUISINE.NORTHERN_EUROPEAN, "northern european");
        CUISINE_NAMES.put(CUISINE.MIDDLE_EASTERN, "middle eastern");
        CUISINE_NAMES.put(CUISINE.AFRICAN, "african");
        CUISINE_NAMES.put(CUISINE.EAST_ASIAN, "east asian");
        CUISINE_NAMES.put(CUISINE.SOUTHEAST_ASIAN, "southeast asian");
        CUISINE_NAMES.put(CUISINE.SOUTH_ASIAN, "south asian");
    }


    public static final Map<String, CUISINE> NAME_CUISINES = CUISINE_NAMES.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));         //Represents cuisineNames with keys / values flipped

    public enum CUISINE {
        NORTH_AMERICAN,
        LATIN_AMERICAN,
        WESTERN_EUROPEAN,
        SOUTHERN_EUROPEAN,
        EASTERN_EUROPEAN,
        NORTHERN_EUROPEAN,
        MIDDLE_EASTERN,
        AFRICAN,
        EAST_ASIAN,
        SOUTHEAST_ASIAN,
        SOUTH_ASIAN
    }

    public String getNaturalLanguage(CUISINE cuisine) {
        return CUISINE_NAMES.get(cuisine);
    }

    public CUISINE getEnum(String naturalLanguage) {
        return NAME_CUISINES.get(naturalLanguage);
    }
}
