package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONException;
import org.json.JSONObject;

import static io.github.pixeldungeonmultiplayer.shattered.client.network.actions.DefaultActionParserRegistry.payloadObject;

public class CharUpdateParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONObject actorObj = payloadObject(action);
        int id = actorObj.getInt("id");

        Actor actor = Actor.findById(id);
        parseThread.parseActorChar(actorObj, id, actor);
    }
}
