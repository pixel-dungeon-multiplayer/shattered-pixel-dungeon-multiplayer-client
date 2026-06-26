package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import org.json.JSONObject;

//todo should we reuse it in io.github.pixeldungeonmultiplayer.shattered.server.traps.actions.network.TrapUpdateParser?
public class CustomTrap extends Trap {
    {
        visible = true;
    }
    String name;
    @Override
    public void activate() {
        active = false;
    }

    @Override
    public String name() {
        return Messages.get(name + ".name");
    }
    @Override
    public String desc() {
        return Messages.get(name + ".desc");
    }

    public CustomTrap(int color, int shape, String name, boolean active, int pos) {
        this.color = color;
        this.shape = shape;
        this.name = "com.shatteredpixel.shatteredpixeldungeon.level.traps." + name;
        this.active = active;
        this.pos = pos;
    }

    public CustomTrap(JSONObject object) {
        this.pos = object.getInt("pos");
        JSONObject params = object.getJSONObject("trap_info");
        this.color = params.getInt("color");
        this.shape = params.getInt("shape");
        this.name = "com.shatteredpixel.shatteredpixeldungeon.level.traps."+ JsonStringHelper.getString(params, "name");
        this.active = params.getBoolean("active");
    }
}
