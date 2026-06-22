package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class CustomHeroSubClass extends HeroSubClass {

	public CustomHeroSubClass(
			@NotNull String id,
			@NotNull String title,
			@NotNull String shortDesc,
			@NotNull String desc,
			int icon,
			@NotNull List<Talent> talents) {
		super(id, title, shortDesc, desc, icon, talents);
	}

	public static @NotNull HeroSubClass none() {
		return HeroSubClass.NONE;
	}

	public static @NotNull CustomHeroSubClass fromJson(@NotNull JSONObject object) {
		String id = JsonStringHelper.optString(object, "id", "");
		String title = JsonStringHelper.optString(object, "title", id);
		String shortDesc = JsonStringHelper.optString(object, "short_description",
				JsonStringHelper.optString(object, "short_desc", ""));
		String desc = JsonStringHelper.optString(object, "description",
				JsonStringHelper.optString(object, "desc", shortDesc));
		int icon = object.optInt("icon", HeroIcon.NONE);

		ArrayList<Talent> talents = new ArrayList<>();
		JSONArray talentArray = object.optJSONArray("talents");
		if (talentArray != null) {
			for (int i = 0; i < talentArray.length(); i++) {
				talents.add(CustomTalent.fromJson(talentArray.getJSONObject(i)));
			}
		}

		return new CustomHeroSubClass(id, title, shortDesc, desc, icon, talents);
	}
}
