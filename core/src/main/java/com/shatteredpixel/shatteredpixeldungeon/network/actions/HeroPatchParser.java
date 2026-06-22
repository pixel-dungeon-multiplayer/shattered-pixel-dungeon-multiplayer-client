package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.CustomHeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.CustomHeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.CustomTalent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class HeroPatchParser implements ActionParser {

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONObject heroObj = action;
        Hero hero = Dungeon.hero;
        if (hero == null) {
            return;
        }
        for (Iterator<String> it = heroObj.keys(); it.hasNext(); ) {
            String token = it.next();
            switch (token) {
                case "strength": {
                    hero.STR = heroObj.getInt(token);
                    break;
                }
                case "lvl": {
                    hero.lvl = heroObj.getInt(token);
                    break;
                }
                case "exp": {
                    hero.exp = heroObj.getInt(token);
                    break;
                }
                case "class": {
                    hero.heroClass = CustomHeroClass.fromJson(heroObj.getJSONObject(token));
                    hero.talents = copyTalentTiers(hero.heroClass.talentTiers());
                    if (hero.sprite instanceof HeroSprite) {
                        ((HeroSprite) hero.sprite).disguise(hero.heroClass);
                    }
                    break;
                }


                case "talents": {
                    JSONArray talentsArray = heroObj.getJSONArray("talents");
                    for (int i = 0; i < talentsArray.length(); i++) {
                        while (hero.talents.size() <= i) {
                            hero.talents.add(new LinkedHashMap<>());
                        }
                        JSONArray talentRow = talentsArray.getJSONArray(i);
                        LinkedHashMap<Talent, Integer> talentIntMap = new LinkedHashMap<>();
                        for (int index = 0; index < talentRow.length(); index++) {
                            JSONObject talentObject = talentRow.getJSONObject(index);
                            int points = talentObject.getInt("points");
                            Talent talent = CustomTalent.fromJson(talentObject);
                            talentIntMap.put(talent, points);
                        }
                        hero.talents.set(i, talentIntMap);
                    }
                    break;
                }
                case "subclass": {
                    hero.subClass = CustomHeroSubClass.fromJson(heroObj.getJSONObject(token));
                    break;
                }
            }
        }
    }

    private static @org.jetbrains.annotations.NotNull ArrayList<LinkedHashMap<Talent, Integer>> copyTalentTiers(
            @org.jetbrains.annotations.NotNull ArrayList<LinkedHashMap<Talent, Integer>> source) {
        ArrayList<LinkedHashMap<Talent, Integer>> result = new ArrayList<>();
        for (LinkedHashMap<Talent, Integer> tier : source) {
            result.add(new LinkedHashMap<>(tier));
        }
        return result;
    }
}
