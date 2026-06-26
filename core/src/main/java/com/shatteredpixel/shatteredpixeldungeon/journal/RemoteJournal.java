package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import io.github.pixeldungeonmultiplayer.common.localizedstring.LocalizedString;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JSONObjectDiff;
import io.github.pixeldungeonmultiplayer.shattered.client.network.text.LocalizedStringParser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoteJournal {

	@NotNull
	private static final ArrayList<Tab> tabs = new ArrayList<>();
	@NotNull
	private static final LocalizedStringParser TEXT_PARSER = new LocalizedStringParser();
	private static JSONObject currentJournalJson = null;

	@Contract(pure = true)
	public static boolean hasData() {
		return !tabs.isEmpty();
	}

	@Contract(pure = true)
	public static @NotNull @UnmodifiableView List<Tab> tabs() {
		return Collections.unmodifiableList(tabs);
	}

	@Contract(pure = true)
	public static @Nullable Tab findTab(String id) {
		for (Tab tab : tabs) {
			if (tab.id.equals(id)) {
				return tab;
			}
		}
		return null;
	}

	public static void update(@NotNull JSONObject payload) throws JSONException {
		currentJournalJson = payload;
		tabs.clear();
		JSONArray tabArray = payload.getJSONArray("tabs");
		for (int i = 0; i < tabArray.length(); i++) {
			tabs.add(new Tab(tabArray.getJSONObject(i)));
		}
	}

	public static void patch(@NotNull JSONObject patch) throws JSONException {
		if (currentJournalJson == null) {
			currentJournalJson = new JSONObject();
		}
		patch.remove("action_name");
		JSONObjectDiff.applyPatch(currentJournalJson, patch);
		update(currentJournalJson);
	}

	public static class Tab {
		public final String id;
		public final String layout;
		public final LocalizedString title;
		public final Icon icon;
		public final ArrayList<Entry> entries = new ArrayList<>();
		public final ArrayList<Tab> tabs = new ArrayList<>();

		Tab(JSONObject obj) throws JSONException {
			id = JsonStringHelper.optString(obj, "id", "");
			layout = JsonStringHelper.optString(obj, "layout", "grid");
			title = parseText(obj, "title", LocalizedString.raw(id));
			icon = obj.has("icon") && !obj.isNull("icon") ? new Icon(obj.getJSONObject("icon")) : Icon.defaultIcon();

			JSONArray entryArray = obj.optJSONArray("entries");
			if (entryArray != null) {
				for (int i = 0; i < entryArray.length(); i++) {
					entries.add(new Entry(entryArray.getJSONObject(i)));
				}
			}

			JSONArray tabArray = obj.optJSONArray("tabs");
			if (tabArray != null) {
				for (int i = 0; i < tabArray.length(); i++) {
					tabs.add(new Tab(tabArray.getJSONObject(i)));
				}
			}
		}
	}

	public static class Entry {
		public final String id;
		public final String kind;
		public final LocalizedString title;
		public final LocalizedString body;
		public final Icon icon;
		public final Icon titleIcon;
		public final Icon secondIcon;
		public final boolean enabled;
		public final boolean seen;
		public final boolean read;
		public final int headerSize;
		public final boolean headerCenter;
		public final int order;
		public final ArrayList<RemoteRecipe> recipes = new ArrayList<>();

		Entry(@NotNull JSONObject obj) throws JSONException {
			id = JsonStringHelper.optString(obj, "id", "");
			kind = JsonStringHelper.optString(obj, "kind", "item");
			title = parseText(obj, "title", LocalizedString.EMPTY);
			body = parseText(obj, "body", LocalizedString.EMPTY);
			icon = obj.has("icon") && !obj.isNull("icon") ? new Icon(obj.getJSONObject("icon")) : Icon.defaultIcon();
			titleIcon = obj.has("title_icon") && !obj.isNull("title_icon") ? new Icon(obj.getJSONObject("title_icon")) : icon;
			secondIcon = obj.has("second_icon") && !obj.isNull("second_icon") ? new Icon(obj.getJSONObject("second_icon")) : null;
			enabled = !obj.has("enabled") || obj.getBoolean("enabled");
			seen = !obj.has("seen") || obj.getBoolean("seen");
			read = !obj.has("read") || obj.getBoolean("read");
			headerSize = obj.optInt("header_size", 7);
			headerCenter = obj.optBoolean("header_center", false);
			order = obj.optInt("order", 0);

			JSONArray recipesArray = obj.optJSONArray("recipes");
			if (recipesArray != null) {
				for (int i = 0; i < recipesArray.length(); i++) {
					if (recipesArray.isNull(i)) {
						recipes.add(null);
					} else {
						recipes.add(new RemoteRecipe(recipesArray.getJSONObject(i)));
					}
				}
			}
		}
	}

	public static class RemoteRecipe {
		public final ArrayList<Item> ingredients = new ArrayList<>();
		public final Item output;
		public final int cost;

		RemoteRecipe(JSONObject obj) throws JSONException {
			JSONArray ingrArray = obj.optJSONArray("ingredients");
			if (ingrArray != null) {
				for (int i = 0; i < ingrArray.length(); i++) {
					ingredients.add(ingrArray.isNull(i) ? null : CustomItem.createItem(ingrArray.getJSONObject(i)));
				}
			}
			output = obj.has("output") && !obj.isNull("output") ? CustomItem.createItem(obj.getJSONObject("output")) : null;
			cost = obj.optInt("cost", 0);
		}
	}

	public static class Icon {
		public final String type;
		public final String name;
		public final String spriteAsset;
		public final String spriteSheet;
		public final int image;
		public final int terrainFeature;
		public final LocalizedString text;
		public final boolean dark;

		Icon(@NotNull JSONObject obj) throws JSONException {
			type = JsonStringHelper.optString(obj, "type", "icon");
			name = JsonStringHelper.optString(obj, "name", "STAIRS");
			spriteAsset = obj.isNull("sprite_asset") ? null : JsonStringHelper.optString(obj, "sprite_asset", null);
			spriteSheet = obj.isNull("sprite_sheet") ? null : JsonStringHelper.optString(obj, "sprite_sheet", null);
			image = obj.optInt("image", -1);
			terrainFeature = obj.optInt("terrain_feature", -1);
			text = parseText(obj, "text", LocalizedString.EMPTY);
			dark = obj.optBoolean("dark", false);
		}

		static @NotNull Icon defaultIcon() {
			try {
				return new Icon(new JSONObject().put("type", "icon").put("name", "STAIRS"));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static LocalizedString parseText(@NotNull JSONObject obj, String key, LocalizedString fallback) throws JSONException {
		if (!obj.has(key) || obj.isNull(key)) {
			return fallback;
		}
		LocalizedString text = TEXT_PARSER.parse(obj.get(key));
		return text == null ? fallback : text;
	}
}
