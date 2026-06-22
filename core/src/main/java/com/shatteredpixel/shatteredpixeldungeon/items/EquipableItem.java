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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public abstract class 	EquipableItem extends CustomItem {

	protected static int slotOfUnequipped = -1;

	public static void equipCursed( Hero hero ) {
		hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
		Sample.INSTANCE.play( Assets.Sounds.CURSED );
	}

	protected float timeToEquip( Hero hero ) {
		return 1f;
	}

	public abstract boolean doEquip( Hero hero );

	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {

		if (cursed) {
            if ((!hero.belongings.lostInventory() || keptThroughLostInventory())) {
                GLog.w(Messages.get(EquipableItem.class, "unequip_cursed"));
                return false;
            }
        }

		if (single) {
			hero.spendAndNext( timeToEquip( hero ) );
		} else {
			hero.spend( timeToEquip( hero ) );
		}

		slotOfUnequipped = Dungeon.quickslot.getSlot(this);

		//temporarily keep this item so it can be collected
		boolean wasKept = keptThoughLostInvent;
		keptThoughLostInvent = true;
        Dungeon.quickslot.clearItem(this);
        updateQuickslot();
        keptThoughLostInvent = wasKept;

		return true;
	}

	final public boolean doUnequip( Hero hero, boolean collect ) {
		return doUnequip( hero, collect, true );
	}

}
