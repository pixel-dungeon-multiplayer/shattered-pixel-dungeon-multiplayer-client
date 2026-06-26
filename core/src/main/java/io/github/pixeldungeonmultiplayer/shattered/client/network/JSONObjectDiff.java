package io.github.pixeldungeonmultiplayer.shattered.client.network;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;

public class JSONObjectDiff {

    public static void applyPatch(JSONObject source, JSONObject patch) {
        Iterator<String> keys = patch.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object patchVal = patch.get(key);

            if (patchVal == JSONObject.NULL) {
                source.remove(key);
                continue;
            }

            if (patchVal instanceof JSONObject) {
                JSONObject patchObj = (JSONObject) patchVal;
                Object sourceVal = source.opt(key);

                if (isArrayPatch(patchObj)) {
                    if (!(sourceVal instanceof JSONArray)) {
                        throw new IllegalStateException("Expected JSONArray at key: " + key);
                    }

                    applyArrayPatch((JSONArray) sourceVal, patchObj);
                    continue;
                }

                if (sourceVal instanceof JSONObject) {
                    applyPatch((JSONObject) sourceVal, patchObj);
                } else {
                    source.put(key, patchVal);
                }

                continue;
            }

            source.put(key, patchVal);
        }
    }

    private static boolean isArrayPatch(JSONObject obj) {
        return obj.has("$updates") || obj.has("$removals");
    }

    private static void applyArrayPatch(JSONArray sourceArr, JSONObject patchObj) {
        JSONObject updates = patchObj.optJSONObject("$updates");
        JSONArray removals = patchObj.optJSONArray("$removals");

        // 1. Index source items by ID
        java.util.Map<String, JSONObject> sourceMap = new java.util.HashMap<>();
        for (int i = 0; i < sourceArr.length(); i++) {
            Object item = sourceArr.opt(i);
            if (item instanceof JSONObject) {
                JSONObject jsonItem = (JSONObject) item;
                if (jsonItem.has("id")) {
                    sourceMap.put(jsonItem.getString("id"), jsonItem);
                }
            }
        }

        // 2. Apply removals
        if (removals != null) {
            for (int i = 0; i < removals.length(); i++) {
                sourceMap.remove(removals.getString(i));
            }
        }

        // 3. Apply updates
        if (updates != null) {
            Iterator<String> keys = updates.keys();
            while (keys.hasNext()) {
                String id = keys.next();
                JSONObject updateVal = updates.getJSONObject(id);

                if (sourceMap.containsKey(id)) {
                    applyPatch(sourceMap.get(id), updateVal);
                } else {
                    if (!updateVal.has("id")) {
                        updateVal.put("id", id);
                    }
                    sourceMap.put(id, updateVal);
                }
            }
        }

        // 4. Reconstruct order based on the 'after' field
        java.util.List<JSONObject> list = new java.util.ArrayList<>();
        java.util.Map<String, JSONObject> nextMap = new java.util.HashMap<>();
        JSONObject firstItem = null;

        for (JSONObject item : sourceMap.values()) {
            Object afterVal = item.opt("after");
            if (afterVal == null || afterVal == JSONObject.NULL || (afterVal instanceof String && ((String) afterVal).isEmpty())) {
                firstItem = item;
            } else {
                nextMap.put((String) afterVal, item);
            }
        }

        JSONObject current = firstItem;
        java.util.Set<String> visited = new java.util.HashSet<>();
        while (current != null) {
            String currentId = current.getString("id");
            if (!visited.add(currentId)) {
                break; // Cycle detected
            }
            list.add(current);
            current = nextMap.get(currentId);
        }

        // Fallback: append unvisited elements if chain is broken
        if (list.size() < sourceMap.size()) {
            for (JSONObject item : sourceMap.values()) {
                if (!visited.contains(item.getString("id"))) {
                    list.add(item);
                }
            }
        }

        // 5. Rewrite source array
        while (sourceArr.length() > 0) {
            sourceArr.remove(0);
        }
        for (JSONObject item : list) {
            sourceArr.put(item);
        }
    }
}
