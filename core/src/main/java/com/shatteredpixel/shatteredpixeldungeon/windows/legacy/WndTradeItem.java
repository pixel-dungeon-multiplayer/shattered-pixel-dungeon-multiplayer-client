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

package com.shatteredpixel.shatteredpixeldungeon.windows.legacy;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import io.github.pixeldungeonmultiplayer.shattered.client.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.CurrencyIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import org.json.JSONObject;

public class WndTradeItem extends WndInfoItem {

	private static final float GAP		= 2;
	private static final int BTN_HEIGHT	= 18;

	public WndTradeItem(JSONObject windowObj) {
		//would love java 23 statements before super
		super(CustomItem.createItem(windowObj.getJSONObject("args").getJSONObject("item")));
		setId(windowObj.getInt("id"));
		JSONObject args = windowObj.getJSONObject("args");
        boolean selling = args.getBoolean("selling");
		if (selling) {
			createSelling(args);
		} else {
			createBuying(args);
		}
	}
	public void createSelling(JSONObject args) {
		int value = args.getInt("price");
		Item item = CustomItem.createItem(args.getJSONObject("item"));
		float pos = height;
		if (item.quantity() == 1) {

			RedButton btnSell = new RedButton( Messages.get(this, "sell", value) ) {
				@Override
				protected void onClick() {
					SendData.sendWindowResult(getId(), 1);
					hide();
				}
			};
			btnSell.setRect( 0, pos + GAP, width, BTN_HEIGHT );
			btnSell.icon(new ItemSprite(ItemSpriteSheet.GOLD));
			add( btnSell );

			pos = btnSell.bottom();

		} else {

			final int priceAll= value;
			RedButton btnSell1 = new RedButton( Messages.get(this, "sell_1", priceAll / item.quantity()) ) {
				@Override
				protected void onClick() {
					SendData.sendWindowResult(getId(), 0);

					hide();
				}
			};
			btnSell1.setRect( 0, pos + GAP, width, BTN_HEIGHT );
			btnSell1.icon(new ItemSprite(ItemSpriteSheet.GOLD));
			add( btnSell1 );
			RedButton btnSellAll = new RedButton( Messages.get(this, "sell_all", priceAll ) ) {
				@Override
				protected void onClick() {
					SendData.sendWindowResult(getId(), 1);
					hide();
				}
			};
			btnSellAll.setRect( 0, btnSell1.bottom() + 1, width, BTN_HEIGHT );
			btnSellAll.icon(new ItemSprite(ItemSpriteSheet.GOLD));
			add( btnSellAll );

			pos = btnSellAll.bottom();

		}

		resize( width, (int)pos );

	}
	public void createBuying(JSONObject args) {
		CustomItem item = CustomItem.createItem(args.getJSONObject("item"));

		int price = args.getInt("price");
		boolean steal = args.optBoolean("steal");



		float pos = height;


		RedButton btnBuy = new RedButton( Messages.get(this, "buy", price) ) {
			@Override
			protected void onClick() {
				hide();
				SendData.sendWindowResult(getId(), 0);
			}
		};
		btnBuy.setRect( 0, pos + GAP, width, BTN_HEIGHT );
		btnBuy.icon(new ItemSprite(ItemSpriteSheet.GOLD));
		btnBuy.enable( price <= Dungeon.hero.gold );
		add( btnBuy );

		pos = btnBuy.bottom();
		if (steal) {
			final int chargesToUse = args.getInt("charges");
			int chance = args.getInt("chance");
			RedButton btnSteal = new RedButton(Messages.get(this, "steal", chance, chargesToUse), 6) {
				@Override
				protected void onClick() {
					if (chance >= 1) {
						SendData.sendWindowResult(getId(), 1);
						hide();
					} else {
						GameScene.show(new WndOptions(new ItemSprite(ItemSpriteSheet.ARTIFACT_ARMBAND),
								//Messages.titleCase(Messages.get(MasterThievesArmband.class, "name")),
								Messages.get(WndTradeItem.class, "steal_warn"),
								Messages.get(WndTradeItem.class, "steal_warn_yes"),
								Messages.get(WndTradeItem.class, "steal_warn_no")) {
							@Override
							protected void onSelect(int index) {
								super.onSelect(index);
								if (index == 0) {
									SendData.sendWindowResult(getId(), 1);
								}
							}
						});
					}
				}
			};

			btnSteal.setRect(0, pos + 1, width, BTN_HEIGHT);
			btnSteal.icon(new ItemSprite(ItemSpriteSheet.ARTIFACT_ARMBAND));
			add(btnSteal);

			pos = btnSteal.bottom();
		}
		resize(width, (int) pos);
	}

    @Override
	public void hide() {
		
		super.hide();
		CurrencyIndicator.showGold = false;
	}

}
