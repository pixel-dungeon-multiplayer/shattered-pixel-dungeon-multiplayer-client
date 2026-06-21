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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.TextureFilm;

public class SmokeBomb extends ArmorAbility {

	protected float baseChargeUse = 35;

	{
		baseChargeUse = 50;
	}

	@Override
	public int icon() {
		return HeroIcon.SMOKE_BOMB;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.HASTY_RETREAT, Talent.BODY_REPLACEMENT, Talent.SHADOW_STEP, Talent.HEROIC_ENERGY};
	}

	@SuppressWarnings("unused")
	public static class NinjaLogSprite extends MobSprite {

		public NinjaLogSprite(){
			super();

			texture( Assets.Sprites.NINJA_LOG );

			TextureFilm frames = new TextureFilm( texture, 11, 12 );

			idle = new Animation( 0, true );
			idle.frames( frames, 0 );

			run = idle.clone();
			attack = idle.clone();
			zap = attack.clone();

			die = new Animation( 12, false );
			die.frames( frames, 1, 2, 3, 4 );

			play( idle );

		}

		@Override
		public void showAlert() {
			//do nothing
		}

		@Override
		public int blood() {
			return 0xFF966400;
		}

	}
}
