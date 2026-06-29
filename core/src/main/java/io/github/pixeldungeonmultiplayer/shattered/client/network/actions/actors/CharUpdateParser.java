package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.actors;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CustomMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite.spriteClassFromName;
import static io.github.pixeldungeonmultiplayer.shattered.client.network.actions.DefaultActionParserRegistry.payloadObject;
import static io.github.pixeldungeonmultiplayer.shattered.client.network.utils.JavaUtils.hasNotNull;

public class CharUpdateParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONObject actorObj = payloadObject(action);
        int id = actorObj.getInt("id");

        Actor actor = Actor.findById(id);
        final Char chr;
        if (!(actor instanceof Char)) {
            chr = new CustomMob(id);
            if (actor != null) {
                Actor.remove(actor);
            }
        } else {
            chr = (Char) actor;
        }
        if (actorObj.has("position")) {
            chr.pos = actorObj.getInt("position");
        }
        boolean wasInLevel = chr instanceof CustomMob && level.mobs.contains(chr);
        CharSpriteFactory spriteFactory = spriteFactoryFrom(actorObj, chr != hero);
        boolean spriteFactoryChanged = false;
        if (chr instanceof Mob && spriteFactory != null) {
            Mob mob = (Mob) chr;
            spriteFactoryChanged = !spriteFactory.equals(mob.spriteFactory);
            if (spriteFactoryChanged) {
                mob.spriteFactory = spriteFactory;
            }
        }
        if (!Actor.all().contains(chr)) {
            Actor.add(chr);
        }
        if (chr instanceof CustomMob) {
            if (!wasInLevel) {
                level.mobs.add((Mob) chr);
                GameScene.add((Mob) chr);
            } else if (spriteFactoryChanged && chr.sprite != null) {
                GameScene.updateCharSprite(chr, spriteFactory);
            }
        }
        for (Iterator<String> it = actorObj.keys(); it.hasNext(); ) {
            String token = it.next();
            switch (token) {
                case "id":
                    continue;
                case "erase_old": //todo
                    continue;
                case "type": {
                    continue; // it parsed before
                }
                case "position": {
                    chr.pos = actorObj.getInt(token);
                    break;
                }
                case "hp": {
                    chr.HP = actorObj.getInt(token);
                    break;
                }
                case "shield" :{
                    chr.shielding = actorObj.getInt("shield");
                    break;
                }
                case "max_hp": {
                    chr.HT = actorObj.getInt(token);
                    break;
                }
                case "name": {
                    chr.name = JsonStringHelper.getString(actorObj, token);
                    break;
                }
                case "sprite_name": {
                    //already parsed
                    break;
                }
                case "sprite_asset":
                {
                    //already parsed
                    break;
                }
                case "animation_name": {
                    assert false : "animation_name";
                    //todo
                    break;
                }
                case "description": {
                    if (chr instanceof CustomMob) {
                        ((Mob) chr).setDesc(JsonStringHelper.getString(actorObj, token));
                    } else {
                        Gdx.app.error("parseActorChar", actorObj.toString(4));
                    }
                    break;
                }
                case "states": {
                    JSONArray statesArr = actorObj.getJSONArray(token);
                    CharSprite sprite = chr.sprite;
                    if (sprite == null) {
                        break;
                    }
                    Set<CharSprite.State> states = sprite.states();
                    Set<CharSprite.State> newStates = new HashSet<>(3);
                    for (int i = 0; i < statesArr.length(); i++) {
                        try {
                            CharSprite.State state = CharSprite.State.valueOf(JsonStringHelper.optString(statesArr, i).toUpperCase());
                            newStates.add(state);
                            if (states.contains(state)) {
                                continue;
                            }
                            sprite.add(state);
                        } catch (IllegalArgumentException e) {
                            GLog.n("Illegal char state: %s", e.getMessage());
                        }
                    }
                    for (CharSprite.State state : states) {
                        if (!newStates.contains(state)) {
                            sprite.remove(state);
                        }
                    }
                    break;
                }
                case "emo": {
                    CharSprite sprite = chr.sprite;
                    if (sprite == null) {
                        break;
                    }
                    JSONObject emoObj = actorObj.getJSONObject(token);
                    sprite.setEmo(emoObj);
                    break;
                }
                case "class" :
                    break;
                    //already parsed
                case "action_name":
                    break;
                case "tier":
                    if (chr.sprite instanceof HeroCustomSprite){
                        ((HeroCustomSprite) chr.sprite).updateTier(actorObj.getInt("tier"));
                    }
                    if (chr.sprite instanceof HeroSprite){
                        ((HeroSprite) chr.sprite).updateTier(actorObj.getInt("tier"));
                    }
                    break;
                default: {
                    GLog.n("Unexpected token \"%s\" in Actor Char. Ignored.", token);
                    break;
                }
            }
        }
    }

    private CharSpriteFactory spriteFactoryFrom(JSONObject actorObj, boolean notHero) throws JSONException {
        if (hasNotNull(actorObj, "sprite_asset")) {
            return CharSpriteFactory.forAsset(JsonStringHelper.getString(actorObj, "sprite_asset"));
        }
        if (hasNotNull(actorObj, "sprite_name")) {
            Class<? extends CharSprite> spriteClass = spriteClassFromName(JsonStringHelper.getString(actorObj, "sprite_name"), notHero);
            Integer tier = actorObj.has("tier") ? actorObj.getInt("tier") : null;
            HeroClass heroClass = actorObj.has("class") ? HeroClass.valueOf(JsonStringHelper.getString(actorObj, "class")) : null;
            return CharSpriteFactory.forClass(spriteClass, tier, heroClass);
        }
        return null;
    }
}
