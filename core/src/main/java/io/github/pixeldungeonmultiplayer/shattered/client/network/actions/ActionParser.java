package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;

import org.json.JSONException;
import org.json.JSONObject;

public interface ActionParser {
    void parse(ParseThread parseThread, JSONObject action) throws JSONException;
}
