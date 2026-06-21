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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class SentryRoom {

	@SuppressWarnings("unused")
	public static class SentrySprite extends MobSprite {

		private final Animation charging;
		private Emitter chargeParticles;

		public SentrySprite(){
			texture( Assets.Sprites.RED_SENTRY );

			idle = new Animation(1, true);
			idle.frames(texture.uvRect(0, 0, 8, 15));

			run = idle.clone();
			attack = idle.clone();
			charging = idle.clone();
			die = idle.clone();
			zap = idle.clone();

			play( idle );
		}

		@Override
		public void zap( int pos ) {
			idle();
			flash();
			emitter().burst(MagicMissile.WardParticle.UP, 2);
			if (Actor.findChar(pos) != null){
				parent.add(new Beam.DeathRay(center(), Actor.findChar(pos).sprite.center()));
			} else {
				parent.add(new Beam.DeathRay(center(), DungeonTilemap.raisedTileCenterToWorld(pos)));
			}
			//((Sentry)ch).onZapComplete();
		}

		@Override
		public void link(Char ch) {
			super.link(ch);

//			chargeParticles = centerEmitter();
			if(chargeParticles != null) {
				chargeParticles.autoKill = false;
				chargeParticles.pour(MagicMissile.MagicParticle.ATTRACTING, 0.05f);
				chargeParticles.on = false;
			}

		}

		@Override
		public void die() {
			super.die();
			if (chargeParticles != null){
				chargeParticles.on = false;
			}
		}

		@Override
		public void kill() {
			super.kill();
			if (chargeParticles != null){
				chargeParticles.killAndErase();
			}
		}

		public void charge(){
			play(charging);
			if (visible) Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
		}

		@Override
		public void play(Animation anim) {
			if (chargeParticles != null) chargeParticles.on = anim == charging;
			super.play(anim);
		}

		private float baseY = Float.NaN;

		@Override
		public void place(int cell) {
			super.place(cell);
			baseY = y;
		}

		@Override
		public void turnTo(int from, int to) {
			//do nothing
		}

		@Override
		public void update() {
			super.update();
			if (chargeParticles != null){
				chargeParticles.pos( center() );
				chargeParticles.visible = visible;
			}

			if (!paused){
				if (Float.isNaN(baseY)) baseY = y;
				y = baseY + (float) Math.sin(Game.timeTotal);
				shadowOffset = 0.25f - 0.8f*(float) Math.sin(Game.timeTotal);
			}
		}

	}

}
