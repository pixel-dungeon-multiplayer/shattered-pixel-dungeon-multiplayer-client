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

package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoneOfIntuition {

	public static class WndGuess extends Window {
		
		private static final int WIDTH = 120;
		private static final int BTN_SIZE = 20;

		public WndGuess(JSONObject obj) {
			JSONObject args = obj.getJSONObject("args");
			int id = obj.getInt("id");
			Item item = CustomItem.createItem(args.getJSONObject("item"));
			JSONArray iconsJson = args.getJSONArray("icons");
			JSONArray keysJson = args.getJSONArray("keys");
			ArrayList<Integer> icons = new ArrayList<>();
			ArrayList<String> keys = new ArrayList<>();
			for (int i = 0; i < iconsJson.length(); i++) {
				icons.add(iconsJson.getInt(i));
			}
			for (int i = 0; i < keysJson.length(); i++) {
				keys.add(JsonStringHelper.optString(keysJson, i));
			}
			IconTitle titlebar = new IconTitle();
			titlebar.icon( new ItemSprite(ItemSpriteSheet.STONE_INTUITION, null) );
			titlebar.label( Messages.titleCase(Messages.get(StoneOfIntuition.class, "name")) );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );

			RenderedTextBlock text = PixelScene.renderTextBlock(6);
			text.text( Messages.get(this, "text") );
			text.setPos(0, titlebar.bottom());
			text.maxWidth( WIDTH );
			add(text);
			final RedButton guess = new RedButton(""){
				@Override
				protected void onClick() {
					SendData.sendWindowResult(id, 100);
				}
			};
			guess.visible = false;
			guess.icon( new ItemSprite(item) );
			guess.enable(false);
			guess.setRect(0, 80, WIDTH, 20);
			add(guess);
			float left;
			float top = text.bottom() + 5;
			int rows;
			int placed = 0;
			if (icons.size() <= 5){
				rows = 1;
				top += BTN_SIZE/2f;
				left = (WIDTH - BTN_SIZE*icons.size())/2f;
			} else {
				rows = 2;
				left = (WIDTH - BTN_SIZE*((icons.size()+1)/2))/2f;
			}

			for (int i = 0; i < icons.size(); i++){

				int index = i;
				IconButton btn = new IconButton(){
					@Override
					protected void onClick() {
						SendData.sendWindowResult(id, index);
						guess.visible = true;
						String finalKey = (keys.get(index) + ".name").replaceAll("com.shatteredpixel.shatteredpixeldungeon.","");
						guess.text( Messages.titleCase(Messages.get(finalKey)));
						guess.enable(true);
						super.onClick();
					}
				};
				Image im = new Image(Assets.Sprites.ITEM_ICONS);
				im.frame(ItemSpriteSheet.Icons.film.get(icons.get(i)));
				im.scale.set(2f);
				btn.icon(im);
				btn.setRect(left + placed*BTN_SIZE, top, BTN_SIZE, BTN_SIZE);
				add(btn);

				placed++;
				if (rows == 2 && placed == ((icons.size()+1)/2)){
					placed = 0;
					if (icons.size() % 2 == 1){
						left += BTN_SIZE/2f;
					}
					top += BTN_SIZE;
				}
			}

			resize(WIDTH, 100);
		}
    }
}
