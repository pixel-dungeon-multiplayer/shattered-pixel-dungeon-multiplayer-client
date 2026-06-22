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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.nikita22007.multiplayer.utils.text.LocalizedString;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class Heap {
	public final int pos;
	
	public ItemSprite sprite;

	private @Nullable Item item = null;
	private LocalizedString title = LocalizedString.EMPTY;
	private LocalizedString info = LocalizedString.EMPTY;
	public boolean seen = false;
	public boolean hidden = false; //sets alpha to 15%

	@Contract(pure = true)
	public Heap(int pos) {
		this.pos = pos;
	}

	public void update(@Nullable Item item, LocalizedString title, LocalizedString info, boolean seen, boolean hidden) {
		this.item = item;
		this.title = title;
		this.info = info;
		this.seen =  seen;
		this.hidden = hidden;

		if (sprite != null) {
			sprite.view(this);
		}
	}

	@Contract(pure = true)
	public int size() {
		return item == null ? 0 : 1;
	}

	@Contract(pure = true)
	public Item peek() {
		return item;
	}
	@Contract(pure = true)
	public boolean isEmpty() {
		return item == null;
	}
	
	public void destroy() {
		Dungeon.level.heaps.remove( this.pos );
		if (sprite != null) {
			sprite.kill();
		}
		item = null;
	}

	@Contract(pure = true)
	public String title(){
		return title.resolve();
	}

	@Contract(pure = true)
	public String info(){
		return info.resolve();
	}
	
}
