package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ActionParserRegistry {

    private final Map<String, NavigableMap<Integer, ActionParser>> parsers = new HashMap<>();

    public ActionParser register(String actionName, int protocolVersion, ActionParser parser) {
        NavigableMap<Integer, ActionParser> versions =
                parsers.computeIfAbsent(actionName, ignored -> new TreeMap<>());
        return versions.put(protocolVersion, parser);
    }

    public ActionParser get(String actionName, int protocolVersion) {
        NavigableMap<Integer, ActionParser> versions = parsers.get(actionName);
        if (versions == null) {
            return null;
        }

        Map.Entry<Integer, ActionParser> entry = versions.floorEntry(protocolVersion);
        return entry == null ? null : entry.getValue();
    }

    public ActionParser getExact(String actionName, int protocolVersion) {
        NavigableMap<Integer, ActionParser> versions = parsers.get(actionName);
        return versions == null ? null : versions.get(protocolVersion);
    }
}
