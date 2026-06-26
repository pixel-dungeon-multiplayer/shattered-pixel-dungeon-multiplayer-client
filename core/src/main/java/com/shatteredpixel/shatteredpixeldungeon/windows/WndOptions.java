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
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread.ToPascalCase;

public class WndOptions extends Window {

	protected static final int WIDTH_P = 120;
	protected static final int WIDTH_L = 144;

	protected static final int MARGIN 		= 2;
	protected static final int BUTTON_HEIGHT	= 18;

	public WndOptions(Image icon, String title, String message, String... options) {
		super();

		layoutAll(icon, TITLE_COLOR, title, message, options);
	}
	
	public WndOptions( String title, String message, String... options ) {
		super();

		layoutAll(null, TITLE_COLOR, title, message, options);
	}

	protected void layoutBody(float pos, String message, String... options){
		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		RenderedTextBlock tfMesage = PixelScene.renderTextBlock( 6 );
		tfMesage.text(message, width);
		tfMesage.setPos( 0, pos );
		add( tfMesage );

		pos = tfMesage.bottom() + 2*MARGIN;

		for (int i=0; i < options.length; i++) {
			final int index = i;
			RedButton btn = new RedButton( options[i] ) {
				@Override
				protected void onClick() {
					hide();
					onSelect( index );
				}
			};
			if (hasIcon(i)) btn.icon(getIcon(i));
			btn.multiline = true;
			add( btn );

			if (!hasInfo(i)) {
				btn.setRect(0, pos, width, BUTTON_HEIGHT);
			} else {
				btn.setRect(0, pos, width - BUTTON_HEIGHT, BUTTON_HEIGHT);
				IconButton info = new IconButton(Icons.get(Icons.INFO)){
					@Override
					protected void onClick() {
						onInfo( index );
					}
				};
				info.setRect(width-BUTTON_HEIGHT, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
				add(info);
			}

			btn.enable(enabled(i));

			pos += BUTTON_HEIGHT + MARGIN;
		}

		resize( width, (int)(pos - MARGIN) );
	}

	protected boolean enabled( int index ){
		return true;
	}
	
	protected void onSelect( int index ) {
		if (getId() >= 0){
			SendData.sendWindowResult(getId(), index);
		}
	}

	protected boolean hasInfo( int index ) {
		return false;
	}

	protected void onInfo( int index ) {}

	protected boolean hasIcon( int index ) {
		return false;
	}

	protected Image getIcon( int index ) {
		return null;
	}
	public WndOptions(int id, String title, String message, String... options ) {
		this(title, message, options);
		this.setId(id);
	}
	public WndOptions(int id, JSONObject args) throws JSONException {
		this.setId(id);
		Image icon = null;
		JSONArray optionsArr = args.getJSONArray("options");
		String[] options = new String[optionsArr.length()];
		for (int i = 0; i < optionsArr.length(); i += 1) {
			options[i] = JsonStringHelper.optString(optionsArr, i);
		}
		String title = JsonStringHelper.getString(args, "title");
		int titleColor = args.optInt("title_color", TITLE_COLOR);
		String text = JsonStringHelper.getString(args, "message");
		if (args.has("item"))
		{
			icon = new ItemSprite(CustomItem.createItem(args.getJSONObject("item")));
		} else if (args.has("sprite_asset")) {
			icon = new CustomCharSprite(JsonStringHelper.getString(args, "sprite_asset"));
		} else if (args.has("sprite_class")) {

			icon = CharSprite.spriteFromClass(
					CharSprite.spriteClassFromName(
							ToPascalCase(JsonStringHelper.getString(args, "sprite_class")
							), true)
			);
		}
		if (args.has("image")) {
			JSONObject image = args.getJSONObject("image");
			icon = new Image();
			icon.fromJson(image);
		}
		layoutAll(icon, titleColor, title, text, options);
	}

	protected void layoutAll(Image icon, Integer titleColor, String title, String message, String... options ){

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		float pos = 0;
		if (icon != null) {
			IconTitle tfTitle = new IconTitle(icon, title);;
			tfTitle.setRect(0, pos, width, 0);
			add(tfTitle);
			pos = tfTitle.bottom() + 2*MARGIN;
		} else if (title != null) {
			RenderedTextBlock tfTitle = PixelScene.renderTextBlock(title, 9);
			tfTitle.hardlight(titleColor);
			tfTitle.setPos(MARGIN, pos);
			tfTitle.maxWidth(width - MARGIN * 2);
			add(tfTitle);
			pos = tfTitle.bottom() + 2*MARGIN;
		}

		layoutBody(pos, message, options);
	}
}
