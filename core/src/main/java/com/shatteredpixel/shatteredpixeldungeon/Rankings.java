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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public enum Rankings {
	
	INSTANCE;
	
	public static final int TABLE_SIZE	= 11;
	
	public static final String RANKINGS_FILE = "rankings.dat";
	
	public ArrayList<Record> records;
	public int lastRecord;
	public int totalNumber;
	public int wonNumber;

	public Record latestDaily;
	public Record latestDailyReplay = null; //not stored, only meant to be temp
	public LinkedHashMap<Long, Integer> dailyScoreHistory = new LinkedHashMap<>();

	public static class Record implements Bundlable {

		private static final String CAUSE   = "cause";
		private static final String WIN		= "win";
		private static final String SCORE	= "score";
		private static final String CLASS	= "class";
		private static final String TIER	= "tier";
		private static final String LEVEL	= "level";
		private static final String DEPTH	= "depth";
		private static final String ASCEND	= "ascending";
		private static final String DATA	= "gameData";
		private static final String ID      = "gameID";
		private static final String SEED    = "custom_seed";
		private static final String DAILY   = "daily";

		private static final String DATE    = "date";
		private static final String VERSION = "version";

		public Class cause;
		public boolean win;

		public HeroClass heroClass;
		public int armorTier;
		public int herolevel;
		public int depth;
		public boolean ascending;

		public Bundle gameData;
		public String gameID;

		//Note this is for summary purposes, visible score should be re-calculated from game data
		public int score;

		public String customSeed;
		public boolean daily;

		public String date;
		public String version;

		public String desc(){
			if (win){
				if (ascending){
					return Messages.get(this, "ascended");
				} else {
					return Messages.get(this, "won");
				}
			} else if (cause == null) {
				return Messages.get(this, "something");
			} else {
				String result = Messages.get(cause, "rankings_desc", (Messages.get(cause, "name")));
				if (result.contains(Messages.NO_TEXT_FOUND)){
					return Messages.get(this, "something");
				} else {
					return result;
				}
			}
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			
			if (bundle.contains( CAUSE )) {
				cause = bundle.getClass( CAUSE );
			} else {
				cause = null;
			}
			
			win		    = bundle.getBoolean( WIN );
			score	    = bundle.getInt( SCORE );
			customSeed  = bundle.getString( SEED );
			daily       = bundle.getBoolean( DAILY );

			armorTier	= bundle.getInt( TIER );
			herolevel   = bundle.getInt( LEVEL );
			depth       = bundle.getInt( DEPTH );
			ascending   = bundle.getBoolean( ASCEND );

			if (bundle.contains( DATE )){
				date = bundle.getString( DATE );
				version = bundle.getString( VERSION );
			} else {
				date = version = null;
			}

			if (bundle.contains(DATA))  gameData = bundle.getBundle(DATA);
			if (bundle.contains(ID))   gameID = bundle.getString(ID);
			
			if (gameID == null) gameID = UUID.randomUUID().toString();

		}
		
		@Override
		public void storeInBundle( Bundle bundle ) {
			
			if (cause != null) bundle.put( CAUSE, cause );

			bundle.put( WIN, win );
			bundle.put( SCORE, score );
			bundle.put( SEED, customSeed );
			bundle.put( DAILY, daily );

			bundle.put( TIER, armorTier );
			bundle.put( LEVEL, herolevel );
			bundle.put( DEPTH, depth );
			bundle.put( ASCEND, ascending );

			bundle.put( DATE, date );
			bundle.put( VERSION, version );

			if (gameData != null) bundle.put( DATA, gameData );
			bundle.put( ID, gameID );
		}
	}

}
