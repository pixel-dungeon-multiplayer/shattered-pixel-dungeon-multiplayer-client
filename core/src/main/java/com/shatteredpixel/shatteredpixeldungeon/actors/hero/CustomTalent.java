package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public final class CustomTalent extends Talent {

	private final int icon;
	private final int maxPoints;
	private final @NotNull String title;
	private final @NotNull String description;
	private final @NotNull String metamorphDescription;

	public CustomTalent(@NotNull String id, int icon, int maxPoints) {
		this(id, icon, maxPoints, id, "", "");
	}

	public CustomTalent(
			@NotNull String id,
			int icon,
			int maxPoints,
			@NotNull String title,
			@NotNull String description,
			@NotNull String metamorphDescription) {
		super(id);
		this.icon = icon;
		this.maxPoints = maxPoints;
		this.title = title;
		this.description = description;
		this.metamorphDescription = metamorphDescription;
	}

	public static @NotNull CustomTalent fromJson(@NotNull JSONObject object) {
		String id = JsonStringHelper.getString(object, "id");
		int icon = object.optInt("icon", 0);
		int maxPoints = object.optInt("max_points", 2);
		String title = JsonStringHelper.optString(object, "title", id);
		String description = JsonStringHelper.optString(object, "description", "");
		String metamorphDescription = JsonStringHelper.optString(object, "metamorph_description", description);
		return new CustomTalent(id, icon, maxPoints, title, description, metamorphDescription);
	}

	@Override
	public int icon() {
		return icon;
	}

	@Override
	public int maxPoints() {
		return maxPoints;
	}

	@Override
	public @NotNull String title() {
		return title;
	}

	@Override
	public @NotNull String desc(boolean metamorphed) {
		if (metamorphed) {
			return metamorphDescription;
		}
		return description;
	}
}
