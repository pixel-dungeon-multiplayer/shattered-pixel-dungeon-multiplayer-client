package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.CustomTalent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class CustomAbility extends ArmorAbility {

	public CustomAbility(
			@NotNull String id,
			@NotNull String name,
			@NotNull String shortDesc,
			@NotNull String desc,
			int icon,
			@NotNull List<Talent> talents) {
		super(id, name, shortDesc, desc, icon, talents);
	}

	public static @NotNull CustomAbility fromJson(@NotNull JSONObject object) {
		String id = JsonStringHelper.optString(object, "id", "");
		String name = JsonStringHelper.optString(object, "name", id);
		String shortDesc = JsonStringHelper.optString(object, "short_description",
				JsonStringHelper.optString(object, "short_desc", ""));
		String desc = JsonStringHelper.optString(object, "description",
				JsonStringHelper.optString(object, "desc", shortDesc));
		int icon = object.optInt("icon", ArmorAbility.defaultIcon());
		ArrayList<Talent> talents = new ArrayList<>();
		JSONArray talentArray = object.optJSONArray("talents");
		if (talentArray != null) {
			for (int i = 0; i < talentArray.length(); i++) {
				talents.add(CustomTalent.fromJson(talentArray.getJSONObject(i)));
			}
		}
		return new CustomAbility(id, name, shortDesc, desc, icon, talents);
	}
}
