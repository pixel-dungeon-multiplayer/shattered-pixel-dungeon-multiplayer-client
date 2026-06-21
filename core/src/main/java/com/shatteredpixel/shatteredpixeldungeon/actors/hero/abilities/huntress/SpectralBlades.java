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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

import java.util.HashSet;

public class SpectralBlades extends ArmorAbility {

    protected float baseChargeUse = 35;

    {
		baseChargeUse = 25f;
	}

    private Char findChar(Ballistica path, Hero hero, int wallPenetration, HashSet<Char> existingTargets){
		for (int cell : path.path){
			Char ch = Actor.findChar(cell);
			if (ch != null){
				if (ch == hero || existingTargets.contains(ch) || ch.alignment == Char.Alignment.ALLY){
					continue;
				} else {
					return ch;
				}
			}
			if (Dungeon.level.solid[cell]){
				wallPenetration--;
				if (wallPenetration < 0){
					return null;
				}
			}
		}
		return null;
	}

	@Override
	public int icon() {
		return HeroIcon.SPECTRAL_BLADES;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.FAN_OF_BLADES, Talent.PROJECTING_BLADES, Talent.SPIRIT_BLADES, Talent.HEROIC_ENERGY};
	}
}
