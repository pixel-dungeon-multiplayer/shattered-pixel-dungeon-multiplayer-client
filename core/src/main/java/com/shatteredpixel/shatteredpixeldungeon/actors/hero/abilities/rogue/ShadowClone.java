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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.Tweener;

public class ShadowClone extends ArmorAbility {

    protected float baseChargeUse = 35;

    {
		baseChargeUse = 35f;
	}

	@Override
	public int icon() {
		return HeroIcon.SHADOW_CLONE;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.SHADOW_BLADE, Talent.CLONED_ARMOR, Talent.PERFECT_COPY, Talent.HEROIC_ENERGY};
	}

    @SuppressWarnings("unused")
	public static class ShadowSprite extends MobSprite {

		private Emitter smoke;

		public ShadowSprite() {
			super();

			texture( HeroClass.ROGUE.spritesheet() );

			TextureFilm film = new TextureFilm( HeroSprite.tiers(), 6, 12, 15 );

			idle = new Animation( 1, true );
			idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

			run = new Animation( 20, true );
			run.frames( film, 2, 3, 4, 5, 6, 7 );

			die = new Animation( 20, false );
			die.frames( film, 0 );

			attack = new Animation( 15, false );
			attack.frames( film, 13, 14, 15, 0 );

			idle();
			resetColor();
		}

		@Override
		public void onComplete(Tweener tweener) {
			super.onComplete(tweener);
		}

		@Override
		public void resetColor() {
			super.resetColor();
			alpha(0.8f);
			brightness(0.0f);
		}

		@Override
		public void link( Char ch ) {
			super.link( ch );
			renderShadow = false;

			if (smoke == null) {
				smoke = emitter();
			}
		}

		@Override
		public void update() {

			super.update();

			if (smoke != null) {
				smoke.visible = visible;
			}
		}

		@Override
		public void kill() {
			super.kill();

			if (smoke != null) {
				smoke.on = false;
			}
		}
	}
}
