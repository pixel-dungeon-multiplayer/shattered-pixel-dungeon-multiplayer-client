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
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Reflection;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class Item  {
	protected String spriteSheet = Assets.Sprites.ITEMS;
	public String spriteSheet() {
		return this.spriteSheet;
	}

	protected static final String TXT_TO_STRING_LVL		= "%s %+d";
	protected static final String TXT_TO_STRING_X		= "%s x%d";

	public static final String AC_DROP		= "DROP";
	public static final String AC_THROW		= "THROW";
	
	protected String defaultAction;
	public boolean usesTargeting;

	//TODO should these be private and accessed through methods?
	public int image = 0;
	public int icon = -1; //used as an identifier for items with randomized images
	
	public boolean stackable = false;
	protected int quantity = 1;
	public boolean dropsDownHeap = false;
	
	private int level = 0;

	public boolean levelKnown = false;
	
	public boolean cursed;
	public boolean cursedKnown;
	
	// Unique items persist through revival
	public boolean unique = false;

	// These items are preserved even if the hero's inventory is lost via unblessed ankh
	// this is largely set by the resurrection window, items can override this to always be kept
	public boolean keptThoughLostInvent = false;

	public int customNoteID = -1;

	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = new ArrayList<>();
		actions.add( AC_DROP );
		actions.add( AC_THROW );
		return actions;
	}

	@Contract(pure=true)
	public String actionName(String action, Hero hero){
		return Messages.get(this, "ac_" + action);
	}

	//resets an item's properties, to ensure consistency between runs
	public void reset(){
		keptThoughLostInvent = false;
	}


	@Contract(pure = true)
	public boolean keptThroughLostInventory(){
		return keptThoughLostInvent;
	}

	public void execute( Hero hero, String action ) {

		GameScene.cancel();
		curUser = hero;
		curItem = this;

    }

	//can be overridden if default action is variable
	@Contract(pure = true)
	public String defaultAction(){
		return defaultAction;
	}
	
	public final void execute( Hero hero ) {
		String action = defaultAction();
		if (action != null) {
			execute(hero, defaultAction());
		}
	}

	//takes two items and merges them (if possible)
	public Item merge( Item other ){
		if (isSimilar( other )){
			quantity += other.quantity;
			other.quantity = 0;
		}
		return this;
	}

	//returns a new item if the split was sucessful and there are now 2 items, otherwise null

	public boolean isSimilar( Item item ) {
		return false;
	}

    //returns the true level of the item, ignoring all modifiers aside from upgrades
	@Contract(pure=true)
	public final int trueLevel(){
		return level;
	}

	//returns the persistant level of the item, only affected by modifiers which are persistent (e.g. curse infusion)
	@Contract(pure=true)
	public int level(){
		return level;
	}
	
	//returns the level of the item, after it may have been modified by temporary boosts/reductions
	//note that not all item properties should care about buffs/debuffs! (e.g. str requirement)
	@Contract(pure=true)
	public int buffedLvl() {
		return level();
	}

	public void level( int value ){
		level = value;

		updateQuickslot();
	}
	
	public Item upgrade() {
		
		this.level++;

		updateQuickslot();
		
		return this;
	}
	
	final public Item upgrade( int n ) {
		for (int i=0; i < n; i++) {
			upgrade();
		}
		
		return this;
	}
	
	public Item degrade() {
		
		this.level--;
		
		return this;
	}
	
	final public Item degrade( int n ) {
		for (int i=0; i < n; i++) {
			degrade();
		}
		
		return this;
	}

	@Contract(pure=true)
	public int visiblyUpgraded() {
		return levelKnown ? level() : 0;
	}

	@Contract(pure=true)
	public int buffedVisiblyUpgraded() {
		return levelKnown ? buffedLvl() : 0;
	}

	@Contract(pure=true)
	public boolean visiblyCursed() {
		return cursed && cursedKnown;
	}

	@Contract(pure=true)
	public boolean isIdentified() {
		return levelKnown && cursedKnown;
	}

	@Contract(pure = true)
	public boolean isEquipped( Hero hero ) {
		return false;
	}

	@Contract(pure = true)
	public String title() {

		String name = name();

		if (visiblyUpgraded() != 0)
			name = Messages.format( TXT_TO_STRING_LVL, name, visiblyUpgraded()  );

		if (quantity > 1)
			name = Messages.format( TXT_TO_STRING_X, name, quantity );

		return name;

	}

	@Contract(pure = true)
	public String name() {
		return trueName();
	}

	@Contract(pure = true)
	public final String trueName() {
		return Messages.get(this, "name");
	}

	@Contract(pure = true)
	public int image() {
		return image;
	}

	@Contract(pure = true)
	public ItemSprite.Glowing glowing() {
		return null;
	}

	public Emitter emitter() { return null; }

	@Contract(pure = true)
	public String info() {

		if (Dungeon.hero != null) {
			Notes.CustomRecord note = Notes.findCustomRecord(customNoteID);
			if (note != null) {
				//we swap underscore(0x5F) with low macron(0x2CD) here to avoid highlighting in the item window
				return Messages.get(this, "custom_note", note.title().replace('_', 'ˍ')) + "\n\n" + desc();
			} else {
				note = Notes.findCustomRecord(getClass());
				if (note != null) {
					//we swap underscore(0x5F) with low macron(0x2CD) here to avoid highlighting in the item window
					return Messages.get(this, "custom_note_type", note.title().replace('_', 'ˍ')) + "\n\n" + desc();
				}
			}
		}

		return desc();
	}

	@Contract(pure = true)
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Contract(pure = true)
	public int quantity() {
		return quantity;
	}

	public Item quantity( int value ) {
		quantity = value;
		return this;
	}

	//item's value in gold coins
	@Contract(pure = true)
	public int value() {
		return 0;
	}

	//item's value in energy crystals
	@Contract(pure = true)
	public int energyVal() {
		return 0;
	}
	
	public Item virtual(){
		Item item = Reflection.newInstance(getClass());
		if (item == null) return null;
		
		item.quantity = 0;
		item.level = level;
		return item;
	}

	@Contract(pure = true)
	public String status() {
		return quantity != 1 ? Integer.toString( quantity ) : null;
	}

	public static void updateQuickslot() {
		GameScene.updateItemDisplays = true;
	}


	public int targetingPos( Hero user, int dst ){
		return throwPos( user, dst );
	}

	public int throwPos( Hero user, int dst){
		return new Ballistica( user.pos, dst, Ballistica.PROJECTILE ).collisionPos;
	}

	@Contract(pure = true)
	public void throwSound(){
		Sample.INSTANCE.play(Assets.Sounds.MISS, 0.6f, 0.6f, 1.5f);
	}

	protected static Hero curUser = null;
	protected static Item curItem = null;
	public void setCurrent( Hero hero ){
		curUser = hero;
		curItem = this;
	}

	protected static CellSelector.Listener thrower = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
			}
		}
		@Override
		public String prompt() {
			return Messages.get(Item.class, "prompt");
		}
	};
}
