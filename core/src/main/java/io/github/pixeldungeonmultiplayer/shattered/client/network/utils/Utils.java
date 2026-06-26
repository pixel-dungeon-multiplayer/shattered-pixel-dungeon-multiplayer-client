package io.github.pixeldungeonmultiplayer.shattered.client.network.utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private Utils(){

    }
    private static List<Integer> parsePath(JSONArray pathArray){
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < pathArray.length(); i++) {
            result.add(pathArray.getInt(i));

        }
        return result;
    }
    public static List<List<Integer>> parseArrayOfPath(JSONArray arrayOfPath){
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < arrayOfPath.length(); i++) {
            result.add(parsePath(arrayOfPath.getJSONArray(i)));
        }
        return result;
    }

    public static String truncate(String text, int maxLength, String ellipsis) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        int targetLength = Math.max(0, maxLength - (ellipsis != null ? ellipsis.length() : 0));
        return text.substring(0, targetLength) + (ellipsis != null ? ellipsis : "");
    }
}
