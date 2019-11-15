package Util.RecipeUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public abstract class CuisineTool {

    public static final Map<CUISINE, String> CUISINE_NAMES = Map.ofEntries(
            entry(CUISINE.NORTH_AMERICAN, "north american"),
            entry(CUISINE.LATIN_AMERICAN, "latin american"),
            entry(CUISINE.WESTERN_EUROPEAN, "western european"),
            entry(CUISINE.SOUTHERN_EUROPEAN, "southern european"),
            entry(CUISINE.EASTERN_EUROPEAN, "eastern european"),
            entry(CUISINE.NORTHERN_EUROPEAN, "northern european"),
            entry(CUISINE.MIDDLE_EASTERN, "middle eastern"),
            entry(CUISINE.AFRICAN, "african"),
            entry(CUISINE.EAST_ASIAN, "east asian"),
            entry(CUISINE.SOUTHEAST_ASIAN, "southeast asian"),
            entry(CUISINE.SOUTH_ASIAN, "south asian")
    );

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
