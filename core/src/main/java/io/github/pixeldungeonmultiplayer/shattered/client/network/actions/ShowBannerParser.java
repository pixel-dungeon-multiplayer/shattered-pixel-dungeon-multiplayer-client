package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ShowBannerParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        BannerSprites.Type bannerType = BannerSprites.Type.valueOf(JsonStringHelper.getString(action, "banner").toUpperCase(Locale.ROOT));
        Banner banner = new Banner(BannerSprites.get(bannerType));
        banner.show(
            action.getInt("color"), 
            (float) action.getDouble("fade_time"), 
            (float) action.getDouble("show_time")
        );
        GameScene.showBannerStatic(banner);
    }
}
