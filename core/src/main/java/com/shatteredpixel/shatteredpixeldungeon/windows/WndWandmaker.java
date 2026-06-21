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

import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import org.json.JSONObject;

public class WndWandmaker extends Window {

	private static final int WIDTH		= 120;
	private static final int BTN_SIZE	= 32;
	private static final int BTN_GAP	= 5;
	private static final int GAP		= 2;

	public WndWandmaker(JSONObject object){
		setId(object.getInt("id"));
		IconTitle titlebar = new IconTitle();
		JSONObject args = object.getJSONObject("args");
		Item item = CustomItem.createItem(args.getJSONObject("quest_item"));
		Item wand1 = CustomItem.createItem(args.getJSONObject("wand1"));
		Item wand2 = CustomItem.createItem(args.getJSONObject("wand2"));
		String key = JsonStringHelper.getString(args, "quest_item_class");
		titlebar.icon(new ItemSprite(item.image(), null));
		titlebar.label(Messages.titleCase(item.name()));
		titlebar.setRect(0, 0, WIDTH, 0);
		add( titlebar );

		String msg = "";
		//hack
		if (key.equals("com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust")){
			msg = Messages.get(this, "dust");
		} else if (key.equals("com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers")){
			msg = Messages.get(this, "ember");
		} else if (key.equals("com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry")){
			msg = Messages.get(this, "berry");
		}

		RenderedTextBlock message = PixelScene.renderTextBlock( msg, 6 );
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );

		ItemButton btnWand1 = new ItemButton(){
			@Override
			protected void onClick() {
					GameScene.show(new RewardWindow(item(), 0));
			}
		};
		btnWand1.item(wand1);
		btnWand1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
		add( btnWand1 );

		ItemButton btnWand2 = new ItemButton(){
			@Override
			protected void onClick() {
				GameScene.show(new RewardWindow(item(), 1));
			}
		};
		btnWand2.item(wand2);
		btnWand2.setRect( btnWand1.right() + BTN_GAP, btnWand1.top(), BTN_SIZE, BTN_SIZE );
		add(btnWand2);

		resize(WIDTH, (int) btnWand2.bottom());
	}

	private void selectReward( int index ) {
		SendData.sendWindowResult(getId(), index);
	}

	private class RewardWindow extends WndInfoItem {

		public RewardWindow(Item item, int index ) {
			super(item);

			RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm")){
				@Override
				protected void onClick() {
					RewardWindow.this.hide();
					WndWandmaker.this.hide();
					selectReward( index );
				}
			};
			btnConfirm.setRect(0, height+2, width/2-1, 16);
			add(btnConfirm);

			RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel")){
				@Override
				protected void onClick() {
					hide();
				}
			};
			btnCancel.setRect(btnConfirm.right()+2, height+2, btnConfirm.width(), 16);
			add(btnCancel);

			resize(width, (int)btnCancel.bottom());
		}
	}

}
