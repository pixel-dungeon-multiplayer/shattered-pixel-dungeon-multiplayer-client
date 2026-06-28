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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JSONObjectDiff;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.NinePatch;
import com.watabou.utils.DeviceCompat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class WndClericSpells extends Window {

	protected static final int WIDTH    = 120;

	public static int BTN_SIZE = 20;
	IconTitle title;
	ArrayList<IconButton> spellBtns = new ArrayList<>();
	int id;
	public WndClericSpells(int id, JSONObject object){
		this.id = id;
		boolean info = object.getBoolean("info");
		if (!info){
			title = new IconTitle(new ItemSprite(ItemSpriteSheet.ARTIFACT_TOME), Messages.titleCase(Messages.get(this, "cast_title")));
		} else {
			title = new IconTitle(Icons.INFO.get(), Messages.titleCase(Messages.get(this, "info_title")));
		}
		title.setRect(0, 0, WIDTH, 0);
		add(title);
		IconButton btnInfo = new IconButton(info ? new ItemSprite(ItemSpriteSheet.ARTIFACT_TOME) : Icons.INFO.get()){
			@Override
			protected void onClick() {
				SendData.sendWindowResult(getId(),0, new JSONObject().put("toggle_info", true));
			}
		};
		btnInfo.setRect(WIDTH-16, 0, 16, 16);
		add(btnInfo);

		RenderedTextBlock msg;
		if (info){
			msg = PixelScene.renderTextBlock( Messages.get( this, "info_desc"), 6);
		} else if (DeviceCompat.isDesktop()){
			msg = PixelScene.renderTextBlock( Messages.get( this, "cast_desc_desktop"), 6);
		} else {
			msg = PixelScene.renderTextBlock( Messages.get( this, "cast_desc_mobile"), 6);
		}
		msg.maxWidth(WIDTH);
		msg.setPos(0, title.bottom()+4);
		add(msg);

		int top = (int)msg.bottom()+4;
		JSONArray buttons = object.getJSONArray("buttons");
		for (int i = 1; i <= Talent.MAX_TALENT_TIERS; i++) {
			ArrayList<Integer> validIndexes = new ArrayList<>();
			for (int index = 0; index < buttons.length(); index++) {
				if (buttons.getJSONObject(index).getInt("tier") == i ){
					validIndexes.add(index);
				}
			}
			if (!validIndexes.isEmpty() && i != 1){
				top += BTN_SIZE + 2;
				ColorBlock sep = new ColorBlock(WIDTH, 1, 0xFF000000);
				sep.y = top;
				add(sep);
				top += 3;
			}
			int left = 2 + (WIDTH - validIndexes.size() * (BTN_SIZE + 4)) / 2;
			for (int spell : validIndexes) {
				SpellButton spellBtn = new SpellButton(buttons.getJSONObject(spell));
				spellBtn.setRect(left, top, BTN_SIZE, BTN_SIZE);
				left += spellBtn.width() + 4;
				add(spellBtn);
				spellBtns.add(spellBtn);

			}
		}

		resize(WIDTH, top + BTN_SIZE);
		if (SPDSettings.interfaceSize() != 2){
			offset(0, (int) (GameScene.uiCamera.height/2 - 30 - height/2));
		}

	}

	public class SpellButton extends IconButton {

//		ClericSpell spell;
//		HolyTome tome;
		boolean info;

		NinePatch bg;
		int spellID = -1;
		String spellName;
		String spellShortDesc;
		public SpellButton(JSONObject object) {
			super(new HeroIcon(object.getInt("icon")));
			info = object.getBoolean("info");
			icon.alpha((float) object.getDouble("alpha"));
			spellName = JsonStringHelper.getString(object, "spell_name");
			spellShortDesc = JsonStringHelper.getString(object, "spell_short_desc");
			spellID = object.getInt("spell_id");
			bg = Chrome.get(Chrome.Type.TOAST);
			addToBack(bg);
		}

		@Override
		protected void onPointerDown() {
//			super.onPointerDown();
//			if (spell == GuidingLight.INSTANCE && spell.chargeUse(Dungeon.hero) == 0){
//				icon.brightness(4);
//			}
		}

		@Override
		protected void onPointerUp() {
			super.onPointerUp();
//			if (tome != null) {
//				if (!tome.canCast(Dungeon.hero, spell)) {
//					icon.alpha(0.3f);
//			} else if (spell == GuidingLight.INSTANCE && spell.chargeUse(Dungeon.hero) == 0){
//				icon.brightness(3);
//				}
//			}
		}

		@Override
		protected void layout() {
			super.layout();

			if (bg != null) {
				bg.size(width, height);
				bg.x = x;
				bg.y = y;
			}
		}

		@Override
		protected void onClick() {
			if (!info) {
				hide();
			}
			SendData.sendWindowResult(WndClericSpells.this.id, spellID, new JSONObject().put("action", "click_spell"));
		}
		@Override
		protected String hoverText() {
			return "_" + Messages.titleCase(spellName) + "_\n" + spellShortDesc;
		}
	}

}
