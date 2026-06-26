package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.CustomAbility;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public final class CustomHeroClass extends HeroClass {

	public CustomHeroClass(
			@NotNull String id,
			@NotNull String title,
			@NotNull String shortDesc,
			@NotNull String desc,
			@NotNull String spritesheet,
			@NotNull String splashArt,
			boolean unlocked,
			@NotNull String unlockMsg,
			@NotNull ArrayList<HeroSubClass> subClasses,
			@NotNull ArrayList<ArmorAbility> armorAbilities,
			@NotNull ArrayList<LinkedHashMap<Talent, Integer>> talentTiers) {
		super(id, title, shortDesc, desc, spritesheet, splashArt, unlocked, unlockMsg, subClasses, armorAbilities, talentTiers);
	}

	public static @NotNull CustomHeroClass fromJson(@NotNull JSONObject object) {
		String id = JsonStringHelper.optString(object, "id", "");
		String title = JsonStringHelper.optString(object, "title", id);
		String shortDesc = JsonStringHelper.optString(object, "short_description",
				JsonStringHelper.optString(object, "short_desc", ""));
		String desc = JsonStringHelper.optString(object, "description",
				JsonStringHelper.optString(object, "desc", shortDesc));
		String spritesheet = JsonStringHelper.optString(object, "spritesheet", "");
		String splashArt = JsonStringHelper.optString(object, "splash_art", "");
		boolean unlocked = object.optBoolean("unlocked", true);
		String unlockMsg = JsonStringHelper.optString(object, "unlock_message", shortDesc);

		ArrayList<HeroSubClass> subClasses = new ArrayList<>();
		JSONArray subclassArray = object.optJSONArray("subclasses");
		if (subclassArray != null) {
			for (int i = 0; i < subclassArray.length(); i++) {
				subClasses.add(CustomHeroSubClass.fromJson(subclassArray.getJSONObject(i)));
			}
		}

		ArrayList<ArmorAbility> armorAbilities = new ArrayList<>();
		JSONArray abilityArray = object.optJSONArray("armor_abilities");
		if (abilityArray == null) {
			abilityArray = object.optJSONArray("abilities");
		}
		if (abilityArray != null) {
			for (int i = 0; i < abilityArray.length(); i++) {
				armorAbilities.add(CustomAbility.fromJson(abilityArray.getJSONObject(i)));
			}
		}

		ArrayList<LinkedHashMap<Talent, Integer>> talentTiers = new ArrayList<>();
		JSONArray tierArray = object.optJSONArray("talent_tiers");
		if (tierArray != null) {
			for (int i = 0; i < tierArray.length(); i++) {
				JSONObject tier = tierArray.getJSONObject(i);
				LinkedHashMap<Talent, Integer> talents = new LinkedHashMap<>();
				JSONArray talentArray = tier.optJSONArray("talents");
				if (talentArray != null) {
					for (int j = 0; j < talentArray.length(); j++) {
						JSONObject talent = talentArray.getJSONObject(j);
						talents.put(CustomTalent.fromJson(talent), talent.optInt("points", 0));
					}
				}
				talentTiers.add(talents);
			}
		}

		return new CustomHeroClass(id, title, shortDesc, desc, spritesheet, splashArt, unlocked, unlockMsg, subClasses, armorAbilities, talentTiers);
	}
}
