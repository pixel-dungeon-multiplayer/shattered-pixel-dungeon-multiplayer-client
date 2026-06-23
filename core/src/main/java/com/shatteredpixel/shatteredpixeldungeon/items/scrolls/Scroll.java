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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.watabou.utils.Random;

public abstract class Scroll extends Item {
	
	public static final String AC_READ	= "READ";
	
	protected static final float TIME_TO_READ	= 1f;
	
	protected String rune;

	//affects how strongly on-scroll talents trigger from this scroll
	public float talentFactor = 1;
	//the chance (0-1) of whether on-scroll talents trigger from this potion
	public float talentChance = 1;
	
	{
		stackable = true;
		defaultAction = AC_READ;
	}


	public Scroll() {
		super();
		reset();
	}
	
	//anonymous scrolls are always IDed, do not affect ID status,
	//and their sprite is replaced by a placeholder if they are not known,
	//useful for items that appear in UIs, or which are only spawned for their effects
	protected boolean anonymous = false;


	public void readAnimation() {
        curUser.spend( TIME_TO_READ );
		curUser.busy();
		((HeroSprite)curUser.sprite).read();

        if (Random.Float() < talentChance) {
		}

	}
	
	public boolean isKnown() {
        return anonymous;
    }

	@Override
	public String name() {
		return isKnown() ? super.name() : Messages.get(this, rune);
	}


	@Override
	public String desc() {
		return isKnown() ? super.desc() : Messages.get(this, "unknown_desc");
	}

	@Override
	public boolean isIdentified() {
		return isKnown();
	}

	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 6 * quantity;
	}

}
