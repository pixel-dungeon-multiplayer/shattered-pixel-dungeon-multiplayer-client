package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.buff;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CustomBuff;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.json.JSONException;
import org.json.JSONObject;

public class BuffUpdateParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int id = action.getInt("id");
        int targetId = action.optInt("target_id", -1);
        if (targetId == -1) {
            Buff.detach(id);
            return;
        }

        Actor targetActor = Actor.findById(targetId);
        if (!(targetActor instanceof Char)) {
            Buff.detach(id);
            return;
        }
        Char target = (Char) targetActor;

        Buff oldBuf = Buff.get(id);
        if ((oldBuf instanceof CustomBuff) && (oldBuf.target == target)) {
            ((CustomBuff) oldBuf).update(action);
            return;
        }

        CustomBuff buff = new CustomBuff(action);
        if (!buff.attachTo(target)) {
            GLog.n("failed to attach buf. Buf id: %d; bug name: %s", buff.buff_id, buff.toString());
        }
    }
}
