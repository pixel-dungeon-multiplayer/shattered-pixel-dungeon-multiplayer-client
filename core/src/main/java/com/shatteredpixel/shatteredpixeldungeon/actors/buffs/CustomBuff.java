package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import io.github.pixeldungeonmultiplayer.common.localizedstring.LocalizedString;
import io.github.pixeldungeonmultiplayer.shattered.client.network.text.LocalizedStringParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import com.watabou.noosa.Image;
import org.json.JSONException;
import org.json.JSONObject;

import static io.github.pixeldungeonmultiplayer.shattered.client.network.utils.TranslationUtils.translateBuffIcon;
import static io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread.isConnectedToOldServer;

public class CustomBuff extends Buff {
    private int icon = 0;

    private LocalizedString desc = LocalizedString.raw("unknown");
    float rm = 0;
    float gm = 0;
    float bm = 0;
    private LocalizedString name = LocalizedString.raw("unknown");
    private float iconFadePercent;

    public CustomBuff(JSONObject obj) throws JSONException {
        //TODO: check this
        // Where is buff ID?
        buff_id = obj.getInt("id");
        update(obj);
    }

    public void update(JSONObject obj) throws JSONException {
        setIcon(obj.optInt("icon", icon));
        if (obj.has("desc")) {
            setDesc(new LocalizedStringParser().parse(obj.opt("desc")));
        }
        if (obj.has("hardlight")){
            JSONObject hardlight = obj.getJSONObject("hardlight");
            rm = (float) hardlight.getDouble("rm");
            gm = (float) hardlight.getDouble("gm");
            bm = (float) hardlight.getDouble("bm");
        }
        if (obj.has("name")) {
            this.name = new LocalizedStringParser().parse(obj.get("name"));
        }
        if (obj.has("fade_percent")) {
            iconFadePercent = Float.parseFloat(JsonStringHelper.getString(obj, "fade_percent"));
        } else {
            iconFadePercent = 0f; //default to no fade
        }
    }

    public int icon() {
        return icon;
    }

    public void setIcon(int icon) {
        if (isConnectedToOldServer()){
         this.icon = translateBuffIcon(icon);
        } else {
            this.icon = icon;
        }
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(rm, gm , bm);
    }

    public void setDesc(LocalizedString desc) {
        this.desc = desc;
    }

    @Override
    public String name(){
        return name != null ? name.toString() : "unknown";
    }

    @Override
    public String desc() {
        return desc != null ? desc.toString() : "unknown";
    }

    @Override
    public float iconFadePercent() {
        return iconFadePercent;
    }
}