/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CustomBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CustomMob;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class WndDialog extends Window {

	private static final int WIDTH_MIN = 120;
	private static final int WIDTH_MAX = 220;
	private static final int WIDTH_OPTIONS_L = 144;
	private static final int GAP = 2;
	private static final int BUTTON_HEIGHT = 18;
	private static final int ITEM_BUTTON_SIZE = 32;
	private static final int ITEM_BUTTON_GAP = 5;

	private int itemSlotCount;
	private int actionCount;

	public WndDialog(int id, @NotNull JSONObject args) throws JSONException {
		super();
		this.setId(id);

		DialogContract contract = new DialogContract(args);
		itemSlotCount = contract.itemSlots.length;
		actionCount = contract.actions.length;
		layout(contract);
	}

	private void layout(@NotNull DialogContract contract) throws JSONException {
		int width = baseWidth(contract.layout.expandInLandscape);

		layout(contract, width);

		while (PixelScene.landscape()
				&& contract.layout.expandInLandscape
				&& height > targetHeight()
				&& width < WIDTH_MAX) {
			clear();
			width += 20;
			layout(contract, width);
		}
	}

	private void layout(@NotNull DialogContract contract, int width) throws JSONException {
		@NotNull Component titlebar = contract.title.createTitlebar(width);
		float pos = 0;
		titlebar.setRect(0, 0, width, 0);
		add(titlebar);
		pos = titlebar.bottom() + 2 * GAP;

		@Nullable RedButton topRightButton = createTopRightButton(contract.topRightButton);
		if (topRightButton != null) {
			float buttonWidth = Math.max(BUTTON_HEIGHT, Math.min(width / 2f, topRightButton.reqWidth() + 8));
			topRightButton.setRect(width - buttonWidth, 0, buttonWidth, BUTTON_HEIGHT);
			add(topRightButton);
			pos = Math.max(pos, topRightButton.bottom() + 2 * GAP);
		}

		RenderedTextBlock message = PixelScene.renderTextBlock(6);
		if (!contract.layout.highlighting) {
			message.setHightlighting(false);
		}
		message.text(contract.message, width);
		message.setPos(0, pos);
		add(message);
		pos = message.bottom() + 2 * GAP;

		if (contract.itemSlots.length > 0) {
			pos = layoutItemSlots(contract.itemSlots, width, pos);
		}

		for (int i = 0; i < contract.actions.length; i++) {
			@NotNull ActionData action = contract.actions[i];
			final int index = itemSlotCount + i;
			RedButton button = new RedButton(action.text, action.fontSize) {
				@Override
				protected void onClick() {
					sendResult(index);
				}
			};
			button.multiline = true;
			if (action.icon != null) {
				button.icon(action.icon);
			}
			button.enable(action.enabled);
			add(button);

			if (action.hasInfo) {
				button.setRect(0, pos, width - BUTTON_HEIGHT, BUTTON_HEIGHT);
				IconButton info = new IconButton(Icons.get(Icons.INFO)) {
					@Override
					protected void onClick() {
						sendInfo(index);
					}
				};
				info.setRect(width - BUTTON_HEIGHT, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
				info.enable(action.enabled);
				add(info);
			} else {
				button.setRect(0, pos, width, BUTTON_HEIGHT);
			}
			pos += BUTTON_HEIGHT + GAP;
		}

		resize(width, (int)(pos - GAP));
	}

	private float layoutItemSlots(final @NotNull ItemSlotData @NotNull [] itemSlots, int width, float pos) {
		int slotsPerRow = Math.max(1, (width + ITEM_BUTTON_GAP) / (ITEM_BUTTON_SIZE + ITEM_BUTTON_GAP));

		for (int rowStart = 0; rowStart < itemSlots.length; rowStart += slotsPerRow) {
			int rowCount = Math.min(slotsPerRow, itemSlots.length - rowStart);
			float rowWidth = rowCount * ITEM_BUTTON_SIZE + (rowCount - 1) * ITEM_BUTTON_GAP;
			float x = Math.max(0, (width - rowWidth) / 2f);

			for (int rowOffset = 0; rowOffset < rowCount; rowOffset++) {
				int slotIndex = rowStart + rowOffset;
				@NotNull ItemSlotData slotData = itemSlots[slotIndex];
				final int index = slotIndex;
				@NotNull ItemButton slot = new ItemButton() {
					@Override
					protected void onClick() {
						sendResult(index);
					}
				};
				if (slotData.item != null) {
					slot.item(slotData.item);
				} else {
					slot.clear();
				}
				slot.slot().enable(slotData.enabled);
				slot.setRect(x + rowOffset * (ITEM_BUTTON_SIZE + ITEM_BUTTON_GAP), pos, ITEM_BUTTON_SIZE, ITEM_BUTTON_SIZE);
				add(slot);
			}

			pos += ITEM_BUTTON_SIZE + ITEM_BUTTON_GAP;
		}

		return pos + 2 * GAP - ITEM_BUTTON_GAP;
	}

	private int baseWidth(boolean expandInLandscape) {
		if (!PixelScene.landscape()) {
			return WIDTH_MIN;
		}
		return expandInLandscape ? WIDTH_MAX : WIDTH_OPTIONS_L;
	}

	private float targetHeight() {
		return PixelScene.MIN_HEIGHT_L - 10;
	}

	private @Nullable RedButton createTopRightButton(@Nullable TopRightButtonData data) {
		if (data == null) {
			return null;
		}
		final int index = itemSlotCount + actionCount;
		RedButton button = new RedButton(data.text, 9) {
			@Override
			protected void onClick() {
				sendResult(index);
			}
		};
		if (data.icon != null) {
			button.icon(data.icon);
		}
		button.enable(data.enabled);
		return button;
	}

	private void sendResult(int index) {
		if (getId() >= 0) {
			SendData.sendWindowResult(getId(), index);
		}
	}

	private void sendInfo(int index) {
		if (getId() >= 0) {
			try {
				SendData.sendWindowResult(getId(), index, new JSONObject().put("info", true));
			} catch (JSONException e) {
				SendData.sendWindowResult(getId(), index);
			}
		}
	}

	private static class DialogContract {
		final @NotNull TitleData title;
		final @NotNull String message;
		final @NotNull ItemSlotData @NotNull [] itemSlots;
		final @NotNull ActionData @NotNull [] actions;
		final @Nullable TopRightButtonData topRightButton;
		final @NotNull LayoutData layout;

		DialogContract(@NotNull JSONObject object) throws JSONException {
			title = new TitleData(object.optJSONObject("title"));
			message = JsonStringHelper.optString(object, "message", "");
			itemSlots = ItemSlotData.parseArray(object.optJSONArray("item_slots"));
			actions = ActionData.parseArray(object.optJSONArray("actions"));
			topRightButton = object.has("top_right_button") && !object.isNull("top_right_button")
					? new TopRightButtonData(object.getJSONObject("top_right_button"))
					: null;
			layout = new LayoutData(object.optJSONObject("layout"));
		}
	}

	private static class TitleData {
		final @NotNull String text;
		final @Nullable Integer color;
		final @Nullable JSONObject iconObject;

		TitleData(@Nullable JSONObject object) {
			if (object == null) {
				text = "Untitled";
				color = null;
				iconObject = null;
			} else {
				text = JsonStringHelper.optString(object, "text", "Untitled");
				color = object.has("color") && !object.isNull("color") ? object.optInt("color") : null;
				iconObject = object.optJSONObject("title_icon");
			}
		}

		@NotNull Component createTitlebar(int width) throws JSONException {
			@Nullable Component specialTitlebar = DialogIconParser.parseTitlebar(iconObject, text, color);
			if (specialTitlebar != null) {
				return specialTitlebar;
			}

			@Nullable Image icon = DialogIconParser.parseImage(iconObject);
			if (icon != null) {
				@NotNull IconTitle titlebar = new IconTitle(icon, text);
				if (color != null) {
					titlebar.color(color);
				}
				return titlebar;
			}

			@NotNull RenderedTextBlock title = PixelScene.renderTextBlock(text, 9);
			title.hardlight(color == null ? Window.TITLE_COLOR : color);
			title.maxWidth(width - GAP * 2);
			title.setPos(GAP, 0);
			return title;
		}
	}

	private static class ItemSlotData {
		final @Nullable Item item;
		final boolean enabled;

		ItemSlotData(@NotNull JSONObject object) throws JSONException {
			if (object.has("item") && !object.isNull("item")) {
				item = CustomItem.createItem(object.getJSONObject("item"));
			} else {
				item = null;
			}
			enabled = object.optBoolean("enabled", true);
		}

		static @NotNull ItemSlotData @NotNull [] parseArray(@Nullable JSONArray array) throws JSONException {
			if (array == null) {
				return new ItemSlotData[0];
			}
			@NotNull ItemSlotData @NotNull [] slots = new ItemSlotData[array.length()];
			for (int i = 0; i < array.length(); i++) {
				slots[i] = new ItemSlotData(array.getJSONObject(i));
			}
			return slots;
		}
	}

	private static class ActionData {
		final @NotNull String text;
		final boolean hasInfo;
		final boolean enabled;
		final @Nullable Image icon;
		final int fontSize;

		ActionData(@NotNull JSONObject object) throws JSONException {
			text = JsonStringHelper.optString(object, "text", "");
			hasInfo = object.optBoolean("has_info", false);
			enabled = object.optBoolean("enabled", true);
			icon = DialogIconParser.parseImage(object.optJSONObject("icon"));
			fontSize = object.optInt("font_size", 9);
		}

		static @NotNull ActionData @NotNull [] parseArray(@Nullable JSONArray array) throws JSONException {
			if (array == null) {
				return new ActionData[0];
			}
			@NotNull ActionData @NotNull [] actions = new ActionData[array.length()];
			for (int i = 0; i < array.length(); i++) {
				actions[i] = new ActionData(array.getJSONObject(i));
			}
			return actions;
		}
	}

	private static class TopRightButtonData {
		final @NotNull String text;
		final boolean enabled;
		final @Nullable Image icon;

		TopRightButtonData(@NotNull JSONObject object) throws JSONException {
			text = JsonStringHelper.optString(object, "text", "");
			enabled = object.optBoolean("enabled", true);
			icon = DialogIconParser.parseImage(object.optJSONObject("icon"));
		}
	}

	private static class LayoutData {
		final boolean expandInLandscape;
		final boolean highlighting;

		LayoutData(@Nullable JSONObject object) {
			expandInLandscape = object != null && object.optBoolean("expand_in_landscape", false);
			highlighting = object == null || object.optBoolean("highlighting", true);
		}
	}

	private static class DialogIconParser {

		static @Nullable Image parseImage(@Nullable JSONObject object) throws JSONException {
			if (object == null || object.isNull("type")) {
				return null;
			}

			switch (JsonStringHelper.optString(object, "type", "none").toLowerCase(Locale.ENGLISH)) {
				case "none":
					return null;
				case "ui_icon":
					return Icons.get(Icons.valueOf(JsonStringHelper.getString(object, "name").toUpperCase(Locale.ENGLISH)));
				case "item_sprite":
					return new ItemSprite(
							object.optInt("image"),
							object.has("glowing") && !object.isNull("glowing")
									? new ItemSprite.Glowing(object.getJSONObject("glowing"))
									: null);
				case "tile_image":
					return TerrainFeaturesTilemap.tile(object.getInt("tile"));
				case "asset_image":
					return parseAssetImage(object);
				case "char_sprite":
					if (object.has("sprite_asset") && !object.isNull("sprite_asset")) {
						return new CustomCharSprite(JsonStringHelper.getString(object, "sprite_asset"));
					}
					if (object.has("sprite_class") && !object.isNull("sprite_class")) {
						return CharSprite.spriteFromClass(CharSprite.spriteClassFromName(
								(JsonStringHelper.getString(object, "sprite_class")), true));
					}
					return null;
				case "buff_titlebar":
					return new BuffIcon(parseBuff(object.getJSONObject("buff")), true);
				default:
					return null;
			}
		}

		private static @NotNull Image parseAssetImage(@NotNull JSONObject object) throws JSONException {
			@NotNull Image image = new Image(parseTextureSource(object));
			if (object.has("frame") && !object.isNull("frame")) {
				image.frame(parseRectF(object.getJSONObject("frame")));
			} else if (object.has("left")) {
				image.frame(parseRectF(object));
			}
			return image;
		}

		private static @NotNull Object parseTextureSource(@NotNull JSONObject object) throws JSONException {
			@NotNull JSONObject source = object.getJSONObject("source");
			switch (JsonStringHelper.getString(source, "type")) {
				case "file":
					return JsonStringHelper.getString(source, "path");
				case "solid":
					return TextureCache.createSolid(source.getInt("color"));
				case "gradient":
					return TextureCache.createGradient(parseIntArray(source.getJSONArray("colors")));
				default:
					throw new JSONException("Unknown texture source type: " + JsonStringHelper.getString(source, "type"));
			}
		}

		private static int @NotNull [] parseIntArray(@NotNull JSONArray array) throws JSONException {
			int[] result = new int[array.length()];
			for (int i = 0; i < array.length(); i++) {
				result[i] = array.getInt(i);
			}
			return result;
		}

		private static @NotNull RectF parseRectF(@NotNull JSONObject object) throws JSONException {
			return new RectF(
					(float)object.getDouble("left"),
					(float)object.getDouble("top"),
					(float)object.getDouble("right"),
					(float)object.getDouble("bottom"));
		}

		static @Nullable Component parseTitlebar(@Nullable JSONObject object, @NotNull String fallbackTitle, @Nullable Integer color) throws JSONException {
			if (object == null || object.isNull("type")) {
				return null;
			}

			String type = JsonStringHelper.optString(object, "type", "none").toLowerCase(Locale.ENGLISH);
			if ("mob_titlebar".equals(type)) {
				return new DialogMobTitle(object.getJSONObject("mob"), object.optJSONArray("buff"), fallbackTitle, color);
			} else if ("buff_titlebar".equals(type)) {
				@NotNull Buff buff = parseBuff(object.getJSONObject("buff"));
				@NotNull IconTitle titlebar = new IconTitle(new BuffIcon(buff, true), fallbackTitle);
				if (color != null) {
					titlebar.color(color);
				}
				return titlebar;
			}
			return null;
		}

		static @NotNull Buff parseBuff(@NotNull JSONObject object) throws JSONException {
			return new CustomBuff(object);
		}
	}

	private static class DialogMobTitle extends Component {

		private static final int TITLE_GAP = 2;

		private final @NotNull Image image;
		private final @NotNull RenderedTextBlock name;
		private final @NotNull HealthBar health;
		private final @NotNull BuffIndicator buffs;

		DialogMobTitle(@NotNull JSONObject mobObject, @Nullable JSONArray buffObjects, @NotNull String fallbackTitle, @Nullable Integer color) throws JSONException {
			@NotNull CustomMob mob = parseMob(mobObject);
			if (buffObjects != null) {
				for (int i = 0; i < buffObjects.length(); i++) {
					mob.add(DialogIconParser.parseBuff(buffObjects.getJSONObject(i)));
				}
			}

			@NotNull String title = mobObject.has("name") && !mobObject.isNull("name")
					? JsonStringHelper.getString(mobObject, "name")
					: fallbackTitle;
			name = PixelScene.renderTextBlock(title, 9);
			name.hardlight(color == null ? Window.TITLE_COLOR : color);
			add(name);

			image = createMobImage(mobObject, mob);
			add(image);

			health = new HealthBar();
			health.level(mob);
			add(health);

			buffs = new BuffIndicator(mob, false);
			add(buffs);
		}

		private static @NotNull CustomMob parseMob(@NotNull JSONObject object) throws JSONException {
			@NotNull CustomMob mob = new CustomMob(object.optInt("id", -1));
			if (object.has("hp")) {
				mob.HP = object.getInt("hp");
			}
			if (object.has("max_hp")) {
				mob.HT = object.getInt("max_hp");
			}
			if (object.has("shield")) {
				mob.shielding = object.getInt("shield");
			}
			if (object.has("description")) {
				mob.setDesc(JsonStringHelper.getString(object, "description"));
			}
			return mob;
		}

		private static @NotNull Image createMobImage(@NotNull JSONObject object, @NotNull CustomMob mob) throws JSONException {
			if (object.has("sprite_asset") && !object.isNull("sprite_asset")) {
				return new CustomCharSprite(JsonStringHelper.getString(object, "sprite_asset"));
			}
			if (object.has("sprite_class") && !object.isNull("sprite_class")) {
				return CharSprite.spriteFromClass(CharSprite.spriteClassFromName(
						(JsonStringHelper.getString(object, "sprite_class")), true));
			}
			if (object.has("sprite_name") && !object.isNull("sprite_name")) {
				return CharSprite.spriteFromClass(CharSprite.spriteClassFromName(
						(JsonStringHelper.getString(object, "sprite_name")), true));
			}
			return mob.sprite();
		}

		@Override
		protected void layout() {
			image.x = x;
			image.y = y + Math.max(0, name.height() + health.height() - image.height());

			float textWidth = width - image.width() - TITLE_GAP;
			name.maxWidth((int)textWidth);
			name.setPos(x + image.width() + TITLE_GAP,
					image.height() > name.height() ? y + (image.height() - name.height()) / 2f : y);

			health.setRect(image.width() + TITLE_GAP, name.bottom() + TITLE_GAP, textWidth, health.height());

			buffs.maxBuffs = 50;
			buffs.setRect(name.right(), name.bottom() - BuffIndicator.SIZE_SMALL - 2, textWidth - name.width(), 8);
			if (!buffs.allBuffsVisible()) {
				buffs.setRect(0, health.bottom(), width, 8);
				height = Math.max(image.y + image.height(), buffs.bottom());
			} else {
				height = Math.max(image.y + image.height(), health.bottom());
			}
		}
	}
}
