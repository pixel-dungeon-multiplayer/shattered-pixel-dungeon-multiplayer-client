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

package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Notes {
	
	public static abstract class Record implements Comparable<Record>, Bundlable {

		//TODO currently notes can only relate to branch = 0, add branch support here if that changes
		protected int depth;

		public int depth(){
			return depth;
		}

		public Image icon() { return Icons.STAIRS.get(); }

		public Visual secondIcon() { return null; }

		public int quantity() { return 1; }

		protected abstract int order();
		
		public abstract String title();

		public abstract String desc();
		
		@Override
		public abstract boolean equals(Object obj);
		
		@Override
		public int compareTo( Record another ) {
			return another.depth() - depth();
		}
		
		private static final String DEPTH	= "depth";
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			depth = bundle.getInt( DEPTH );
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
			bundle.put( DEPTH, depth );
		}
	}
	
	public enum Landmark {
		CHASM_FLOOR,
		WATER_FLOOR,
		GRASS_FLOOR,
		DARK_FLOOR,
		LARGE_FLOOR,
		TRAPS_FLOOR,
		SECRETS_FLOOR,

		SHOP,
		ALCHEMY,
		GARDEN,
		DISTANT_WELL,
		WELL_OF_HEALTH,
		WELL_OF_AWARENESS,
		SACRIFICIAL_FIRE,
		STATUE,

		LOST_PACK,
		BEACON_LOCATION,

		GHOST,
		RAT_KING,
		WANDMAKER,
		TROLL,
		IMP,

		DEMON_SPAWNER;
	}

	public static class LandmarkRecord extends Record {
		
		protected Landmark landmark;
		
		public LandmarkRecord() {}
		
		public LandmarkRecord(Landmark landmark, int depth ) {
			this.landmark = landmark;
			this.depth = depth;
		}

		public Image icon(){
			switch (landmark){
				default:
					return Icons.STAIRS.get();
			}
		}

		@Override
		public String title() {
			switch (landmark) {
				default:            return Messages.get(Landmark.class, landmark.name());
			}
		}

		@Override
		public String desc() {
			switch (landmark) {
				default:            return "";
			}
		}

		@Override
		protected int order(){
			return landmark.ordinal();
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof LandmarkRecord)
					&& landmark == ((LandmarkRecord) obj).landmark
					&& depth() == ((LandmarkRecord) obj).depth();
		}

	}

	public enum CustomType {
		TEXT,
		DEPTH,
		ITEM_TYPE,
		SPECIFIC_ITEM,
		ITEM //for pre-3.1 save conversion
	}

	public static class CustomRecord extends Record {

		protected CustomType type;

		protected int ID = -1;
		protected Class itemClass;

		protected String title;
		protected String body;

		public CustomRecord() {}

		public CustomRecord(String title, String desc) {
			type = CustomType.TEXT;
			this.title = title;
			body = desc;
		}

		public CustomRecord(int depth, String title, String desc) {
			type = CustomType.DEPTH;
			this.depth = depth;
			this.title = title;
			body = desc;
		}

		public CustomRecord(Class itemCls, String title, String desc) {
			type = CustomType.ITEM_TYPE;
			itemClass = itemCls;
			this.title = title;
			body = desc;
		}

		public CustomRecord(Item item, String title, String desc) {
			type = CustomType.SPECIFIC_ITEM;
			itemClass = item.getClass();
			this.title = title;
			body = desc;
		}

		public void assignID(){
			if (ID == -1) {
				ID = nextCustomID++;
			}
		}

		public int ID(){
			return ID;
		}

		@Override
		public int depth() {
			if (type == CustomType.DEPTH){
				return depth;
			} else {
				return 0;
			}
		}

		@Override
		public Image icon() {
			switch (type){
				case TEXT: default:
					return Icons.SCROLL_COLOR.get();
				case DEPTH:
					return Icons.STAIRS.get();
				case ITEM_TYPE:
				case SPECIFIC_ITEM:
					Item i = (Item) Reflection.newInstance(itemClass);
					return new ItemSprite(i);
			}
		}

		@Override
		public Visual secondIcon() {
			switch (type){
				case TEXT: default:
					return null;
				case DEPTH:
					BitmapText text = new BitmapText(Integer.toString(depth()), PixelScene.pixelFont);
					text.measure();
					return text;
				case ITEM_TYPE:
				case SPECIFIC_ITEM:
					Item item = (Item) Reflection.newInstance(itemClass);
					if (item.isIdentified() && item.icon != -1) {
						Image secondIcon = new Image(Assets.Sprites.ITEM_ICONS);
						secondIcon.frame(ItemSpriteSheet.Icons.film.get(item.icon));
						return secondIcon;
					}
					return null;
			}
		}

		@Override
		protected int order() {
			return 2000 + ID;
		}

		public void editText(String title, String desc){
			this.title = title;
			this.body = desc;
		}

		@Override
		public String title() {
			return title;
		}

		@Override
		public String desc() {
			return body;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof CustomRecord && ((CustomRecord) obj).ID == ID;
		}

		private static final String TYPE        = "type";
		private static final String ID_NUMBER   = "id_number";

		private static final String ITEM_CLASS   = "item_class";

		private static final String TITLE       = "title";
		private static final String BODY        = "body";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TYPE, type);
			bundle.put(ID_NUMBER, ID);
			if (itemClass != null) bundle.put(ITEM_CLASS, itemClass);
			bundle.put(TITLE, title);
			bundle.put(BODY, body);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			type = bundle.getEnum(TYPE, CustomType.class);
			ID = bundle.getInt(ID_NUMBER);

			if (bundle.contains(ITEM_CLASS)) {
				itemClass = bundle.getClass(ITEM_CLASS);
				if (type == CustomType.ITEM){
					//prior to v3.1 specific item notes and item type notes were the same
					//we assume notes are for a specific item if they're for an equipment
					if (EquipableItem.class.isAssignableFrom(itemClass)){
						type = CustomType.SPECIFIC_ITEM;
					} else {
						type = CustomType.ITEM_TYPE;
					}
				}
			}

			title = bundle.getString(TITLE);
			body = bundle.getString(BODY);
		}
	}
	
	private static ArrayList<Record> records;
	
	public static void reset() {
		records = new ArrayList<>();
	}

	protected static int nextCustomID = 0;

	public static boolean add( Landmark landmark ) {
		return add( landmark, Dungeon.depth );
	}
	
	public static boolean add( Landmark landmark, int depth ) {
		LandmarkRecord l = new LandmarkRecord( landmark, depth );
		if (!records.contains(l)) {
			boolean result = records.add(l);
			Collections.sort(records, comparator);
			return result;
		}
		return false;
	}

	public static boolean contains( Landmark landmark ){
		return contains( landmark, Dungeon.depth );
	}

	public static boolean contains( Landmark landmark, int depth ){
		return records.contains(new LandmarkRecord( landmark, depth));
	}

	public static boolean add( CustomRecord rec ){
		rec.assignID();
		if (!records.contains(rec)){
			boolean result = records.add(rec);
			Collections.sort(records, comparator);
			return result;
		}
		return false;
	}

	public static boolean remove( CustomRecord rec ){
		if (records.contains(rec)){
			records.remove(rec);
			return true;
		}
		return false;
	}
	
	public static <T extends Record> ArrayList<T> getRecords( Class<T> recordType ){
		ArrayList<T> filtered = new ArrayList<>();
		for (Record rec : records){
			if (recordType.isInstance(rec)){
				filtered.add((T)rec);
			}
		}
		return filtered;
	}

	public static ArrayList<Record> getRecords(int depth){
		ArrayList<Record> filtered = new ArrayList<>();
		for (Record rec : records){
			if (rec.depth() == depth && !(rec instanceof CustomRecord)){
				filtered.add(rec);
			}
		}

		Collections.sort(filtered, comparator);

		return filtered;
	}

	public static CustomRecord findCustomRecord( int ID ){
		for (Record rec : records){
			if (rec instanceof CustomRecord && ((CustomRecord) rec).ID == ID) {
				return (CustomRecord) rec;
			}
		}
		return null;
	}

	public static CustomRecord findCustomRecord( Class itemClass ){
		for (Record rec : records){
			if (rec instanceof CustomRecord
					&& ((CustomRecord) rec).type == CustomType.ITEM_TYPE
					&& ((CustomRecord) rec).itemClass == itemClass) {
				return (CustomRecord) rec;
			}
		}
		return null;
	}

	public static int customRecordLimit(){
		return 5;
	}

	private static final Comparator<Record> comparator = new Comparator<Record>() {
		@Override
		public int compare(Record r1, Record r2) {
			return r1.order() - r2.order();
		}
	};
	
}
