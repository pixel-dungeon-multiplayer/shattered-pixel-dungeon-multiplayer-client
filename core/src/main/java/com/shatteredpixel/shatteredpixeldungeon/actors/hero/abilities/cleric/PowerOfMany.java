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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

public class PowerOfMany extends ArmorAbility {

	protected float baseChargeUse = 35;

	@Override
	public String targetingPrompt() {
        Char ally = getPoweredAlly();

        boolean allyExists = ally != null;


        if (!allyExists) {
            return Messages.get(this, "prompt_default");
        } else {
            return null;
        }
    }

	public boolean useTargeting(){
		return false;
	}

	@Override
	public int icon() {
		return HeroIcon.POWER_OF_MANY;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.BEAMING_RAY, Talent.LIFE_LINK, Talent.STASIS, Talent.HEROIC_ENERGY};
	}

	public static Char getPoweredAlly(){
		for (Char ch : Actor.chars()) {
        }
		return null;
	}

	@SuppressWarnings("unued")
	public static class LightAllySprite extends MobSprite {

		public LightAllySprite() {
			super();

			setup(HeroClass.values()[Random.Int(5)]);
		}

		public void setup(HeroClass cls){
			texture(cls.spritesheet());

			TextureFilm film = new TextureFilm( HeroSprite.tiers(), 6, 12, 15 );

			idle = new Animation( 1, true );
			idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

			run = new Animation( 20, true );
			run.frames( film, 2, 3, 4, 5, 6, 7 );

			die = new Animation( 20, false );
			die.frames( film, 0 );

			attack = new Animation( 15, false );
			attack.frames( film, 13, 14, 15, 0 );

			play(idle, true);
			resetColor();
		}

		@Override
		public void link(Char ch) {
			super.link(ch);
		}

		@Override
		public void resetColor() {
			super.resetColor();
			alpha(0.8f);
			tint(1.33f, 1.33f, 0.8f, 0.6f);
			rm = gm = bm = 0;
		}

		@Override
		public void die() {
			super.die();
			emitter().start( ShaftParticle.FACTORY, 0.3f, 4 );
			emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
		}

		@Override
		public void draw() {
			if (alpha() >= 0.8f) alpha(0.8f);
			rm = gm = bm = 0; //always flat and transparent
			super.draw();
		}
	}

}
