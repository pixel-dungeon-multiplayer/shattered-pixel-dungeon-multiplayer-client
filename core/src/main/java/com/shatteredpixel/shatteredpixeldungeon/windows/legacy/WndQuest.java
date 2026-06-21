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

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.utils.Reflection;
import org.json.JSONObject;

public class WndQuest extends WndTitledMessage {

	public WndQuest(int id, JSONObject object) {
		super((CharSprite) Reflection.newInstance(Reflection.forName(JsonStringHelper.getString(object, "sprite_name"))), Messages.titleCase(JsonStringHelper.getString(object, "char_name")), JsonStringHelper.getString(object, "text"));
		this.setId(id);
    }

	@Override
	public void hide() {
		SendData.sendWindowResult(getId(), -1);
		super.hide();
	}
}
