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

package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuickBag;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Bag extends CustomItem implements Iterable<Item> {
	/*
	* recursive search of Item.
	* */
	@Contract(pure = true)
	public List<Integer> pathOfItem(Item item) {
		assert (item != null) : "path of null item";
		for (int i = 0; i < items.size(); i++) { //check all items
			Item cur_item = items.get(i);
			if (cur_item == null) {
				continue;
			}
			if (cur_item == item) { //found, return path
				List<Integer> path = new ArrayList<>(2);
				path.add(i);
				return path;
			}
			if (cur_item instanceof Bag) { //check in internal bag
				List<Integer> path = ((Bag) cur_item).pathOfItem(item);
				if (path != null) {
					path.add(0, i);
					return path;
				}
			}
		}
		return null; //not found
	}
	public static final String AC_OPEN	= "OPEN";
	
	{
		image = 11;
		
		defaultAction = AC_OPEN;

		unique = true;
	}

	public Bag(){

	}

	public Char owner;

	public ArrayList<Item> items = new ArrayList<>();

	public int capacity(){
		return 20; // default container size
	}

	//if an item is being quick-used from the bag, the bag should take on its targeting properties
	public Item quickUseItem;

	@Override
	public int targetingPos(Hero user, int dst) {
		if (quickUseItem != null){
			return quickUseItem.targetingPos(user, dst);
		} else {
			return super.targetingPos(user, dst);
		}
	}

	@Override
	public void execute( Hero hero, String action ) {
		quickUseItem = null;

		super.execute( hero, action );

		if (action.equals( AC_OPEN ) && !items.isEmpty()) {
			
			GameScene.show( new WndQuickBag( this ) );
			
		}
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
	
	public void clear() {
		items.clear();
	}

	@Contract(pure = true)
	public boolean contains( Item item ) {
		for (Item i : items) {
			if (i == item) {
				return true;
			} else if (i instanceof Bag && ((Bag)i).contains( item )) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<Item> iterator() {
		return new ItemIterator();
	}

	private class ItemIterator implements Iterator<Item> {

		private int index = 0;
		private Iterator<Item> nested = null;
		
		@Override
		public boolean hasNext() {
			if (nested != null) {
				return nested.hasNext() || index < items.size();
			} else {
				return index < items.size();
			}
		}

		@Override
		public Item next() {
			if (nested != null && nested.hasNext()) {
				
				return nested.next();
				
			} else {
				
				nested = null;
				
				Item item = items.get( index++ );
				if (item instanceof Bag) {
					nested = ((Bag)item).iterator();
				}
				
				return item;
			}
		}

		@Override
		public void remove() {
			if (nested != null) {
				nested.remove();
			} else {
				items.remove( index );
			}
		}
	}
	public Bag(JSONObject obj){
		super(obj);
	}


	public void putItemIntoSlot(@NotNull List<Integer> slotPath, @Nullable Item item, boolean replace) {
		// replace==false: move other items to the next slot
		if (slotPath.size() > 1) {
			((Bag) items.get(slotPath.get(0))).putItemIntoSlot(slotPath.subList(1, slotPath.size()), item, replace);
		} else {
			int slot = slotPath.get(0);
			if (items.size() < slot) {
				items.add(null);
			}
			if (items.size() == slot) {
				items.add(item);
			} else {
				if (replace) {
					items.set(slot, item);
				} else {
					items.add(slot, item);
				}
			}
		}
	}

	@Contract(pure = true)
	public Item getItemInSlot(@NotNull List<Integer> slotPath) {
		if (slotPath.size() > 1) {
			return ((Bag) items.get(slotPath.get(0))).getItemInSlot(slotPath.subList(1, slotPath.size()));
		}
		switch (slotPath.get(0)) {
			case -1: return Dungeon.hero.belongings.weapon();
			case -2: return Dungeon.hero.belongings.armor();
			case -3: return Dungeon.hero.belongings.artifact();
			case -4: return Dungeon.hero.belongings.misc();
			case -5: return Dungeon.hero.belongings.ring();
			default:	return items.get(slotPath.get(0));
		}
	}
	public void removeItemFromSlot(List<Integer> slotPath) {
		if (slotPath.size()>1){
			((Bag) items.get(slotPath.get(0))).removeItemFromSlot(slotPath.subList(1, slotPath.size()));
		} else {
			items.remove((int)slotPath.get(0));
		}
	}

}
