package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.noosa.Game;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class UpdateWindowParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, final JSONObject windowObj) throws JSONException {
        try {
            int id = windowObj.getInt("id");
            String type = JsonStringHelper.getString(windowObj, "type");
            JSONObject args = windowObj.optJSONObject("params");
            if (args == null) {
                args = windowObj.optJSONObject("args");
            }

            /*
+ alchemy_scene
- badge
+ bag_listener
- challenges
- cleric_spells
+ dialog
- guess
- hero
- hero_info
- info_subclass
- metamorph_choose
- metamorph_replace
- text_input
- upgrade
            */
            if (!"alchemy_scene".equals(type)) {
                Window existing = Window.getWindow(id);
                if (existing != null) {
                    existing.hide();
                }
            }
            switch (type) {
                case "alchemy_scene": {
                    if (!(Game.scene() instanceof AlchemyScene)) {
                        Game.switchScene(AlchemyScene.class, new Game.SceneChangeCallback() {
                            @Override
                            public void beforeCreate() {

                            }

                            @Override
                            public void afterCreate() {
                                ((AlchemyScene) Game.scene()).parseJson(windowObj);
                            }
                        });
                    } else {
                        ((AlchemyScene) Game.scene()).parseJson(windowObj);
                    }
                    break;
                }


                case "bag_listener": {
                    String title = JsonStringHelper.getString(args, "title");
                    boolean has_listener = args.getBoolean("has_listener");
                    JSONArray allowed_items = args.optJSONArray("allowed_items");
                    JSONArray last_bag_path = args.optJSONArray("last_bag_path"); // todo
                    if (Game.scene() instanceof GameScene) {
                        GameScene.show(new WndBag(id, hero.belongings.backpack, has_listener, allowed_items, title));
                    } else {
                        Game.scene().addToFront(new WndBag(id, hero.belongings.backpack, has_listener, allowed_items, title));
                    }
                    break;
                }


                case "dialog": {
                    GameScene.show(new WndDialog(id, windowObj.getJSONObject("args")));
                    break;
                }
                case "badge": {
                    GameScene.show(new WndBadge(id, args));
                    break;
                }
                case "challenges": {
                    GameScene.show(new WndChallenges(id, args));
                    break;
                }
                case "cleric_spells": {
                    GameScene.show(new WndClericSpells(id, args));
                    break;
                }
                case "guess": {
                    GameScene.show(new StoneOfIntuition.WndGuess(windowObj));
                    break;
                }
                case "hero": {
                    GameScene.show(new WndHero(id, args));
                    break;
                }
                case "hero_info": {
                    GameScene.show(new WndHeroInfo(id, args));
                    break;
                }
                case "info_subclass": {
                    GameScene.show(new WndInfoSubclass(id, args));
                    break;
                }
                case "metamorph_choose": {
                    GameScene.show(new ScrollOfMetamorphosis.WndMetamorphChoose(id, args));
                    break;
                }
                case "metamorph_replace": {
                    GameScene.show(new ScrollOfMetamorphosis.WndMetamorphReplace(id, args));
                    break;
                }
                case "text_input": {
                    GameScene.show(new WndTextInput(id, args));
                    break;
                }
                case "upgrade": {
                    GameScene.show(new WndUpgrade(id, args));
                    break;
                }

                default: {
                    Log.e("parse_window", String.format("incorrect window type: %s", type));
                }
            }
        } catch (NullPointerException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            Log.e("parse_window", String.format("bad_window. %s\n%s", e.getMessage(), sStackTrace));
        }
    }
}
