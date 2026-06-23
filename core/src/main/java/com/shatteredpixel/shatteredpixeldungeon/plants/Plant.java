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

package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public abstract class Plant implements Bundlable {
	
	public int image;
	public int pos;

	protected Class<? extends Plant.Seed> seedClass;

	public void wither() {
		Dungeon.level.uproot( pos );

		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get( pos ).burst( LeafParticle.GENERAL, 6 );
		}

		float seedChance = 0f;


		if (Random.Float() < seedChance){

		}
		
	}
	
	private static final String POS	= "pos";

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		pos = bundle.getInt( POS );
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, pos );
	}

	public String name(){
		return Messages.get(this, "name");
	}

	public abstract String desc();
	
	public static class Seed extends Item {

		public static final String AC_PLANT	= "PLANT";
		
		private static final float TIME_TO_PLANT = 1f;
		
		{
			stackable = true;
			defaultAction = AC_THROW;
		}
		
		protected Class<? extends Plant> plantClass;
		
		public Plant couch( int pos, Level level ) {
			if (level != null && level.heroFOV != null && level.heroFOV[pos]) {
				Sample.INSTANCE.play(Assets.Sounds.PLANT);
			}
			Plant plant = Reflection.newInstance(plantClass);
			plant.pos = pos;
			return plant;
		}

        @Override
		public boolean isIdentified() {
			return true;
		}
		
		@Override
		public int value() {
			return 10 * quantity;
		}

		@Override
		public int energyVal() {
			return 2 * quantity;
		}

		@Override
		public String desc() {
			return "";
		}

		@Override
		public String info() {
			return Messages.get( Seed.class, "info", super.info() );
		}
		
		public static class PlaceHolder extends Seed {
			
			{
				image = ItemSpriteSheet.SEED_HOLDER;
			}
			
			@Override
			public boolean isSimilar(Item item) {
				return false;
			}
			
			@Override
			public String info() {
				return "";
			}
		}
	}
}
