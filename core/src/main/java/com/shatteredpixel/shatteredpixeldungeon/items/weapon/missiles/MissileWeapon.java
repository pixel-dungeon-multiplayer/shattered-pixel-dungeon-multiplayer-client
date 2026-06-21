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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.jetbrains.annotations.Contract;

abstract public class MissileWeapon extends Weapon {

	{
		stackable = true;
		levelKnown = true;

		defaultAction = AC_THROW;
		usesTargeting = true;
	}

	//whether or not this instance of the item exists purely to trigger its effect. i.e. no dropping
	public boolean spawnedForEffect = false;
	
	protected boolean sticky = true;
	
	public static final float MAX_DURABILITY = 100;
	protected float durability = MAX_DURABILITY;
	protected float baseUses = 10;
	
	public boolean holster;
	
	//used to reduce durability from the source weapon stack, rather than the one being thrown.
	public MissileWeapon parent;
	
	public int tier;

	@Override
	public int min(int lvl) {
		return  1;      //level scaling
	}
	
	@Override
	public int max(int lvl) {
		return  5 * tier +                      //base
				(tier == 1 ? 2*lvl : tier*lvl); //level scaling
	}
	
	public int STRReq(int lvl){
		return STRReq(tier, lvl) - 1; //1 less str than normal for their tier
	}

	//use the parent item if this has been thrown from a parent
	@Contract(pure = true)
    public int buffedLvl(){
		if (parent != null) {
			return parent.buffedLvl();
		} else {
			return super.buffedLvl();
		}
	}
	
	@Override
	//FIXME some logic here assumes the items are in the player's inventory. Might need to adjust
	public Item upgrade() {
		if (!bundleRestoring) {
			durability = MAX_DURABILITY;
			if (quantity > 1) {
				MissileWeapon upgraded = (MissileWeapon) null;
				upgraded.parent = null;
				
				upgraded = (MissileWeapon) upgraded.upgrade();
				
				//try to put the upgraded into inventory, if it didn't already merge
				if (upgraded.quantity() == 1 && !false) {
					Dungeon.level.drop(upgraded, Dungeon.hero.pos);
				}
				updateQuickslot();
				return upgraded;
			} else {
				super.upgrade();
				
				Item similar = Dungeon.hero.belongings.getSimilar(this);
				if (similar != null){
					Item result = similar.merge(this);
					updateQuickslot();
					return result;
				}
				updateQuickslot();
				return this;
			}
			
		} else {
			return super.upgrade();
		}
	}

	public boolean isSimilar( Item item ) {
		return level() == item.level() && getClass() == item.getClass();
	}


	@Override
	public int proc(Char attacker, Char defender, int damage) {

		return super.proc(attacker, defender, damage);
	}

    public String status() {
		//show quantity even when it is 1
		return Integer.toString( quantity );
	}

	protected void rangedHit( Char enemy, int cell ){
		decrementDurability();
		if (durability > 0 && !spawnedForEffect){
			//attempt to stick the missile weapon to the enemy, just drop it if we can't.
			if (sticky && enemy != null && enemy.isActive() && enemy.alignment != Char.Alignment.ALLY){

			}
			Dungeon.level.drop( this, cell ).sprite.drop();
		}
	}
	
	protected void rangedMiss( int cell ) {
		parent = null;
		if (!spawnedForEffect) {

		}
	}

	public float durabilityLeft(){
		return durability;
	}

	public void repair( float amount ){
		durability += amount;
		durability = Math.min(durability, MAX_DURABILITY);
	}
	
	public float durabilityPerUse(){
		//classes that override durabilityPerUse can turn rounding off, to do their own rounding after more logic
		return durabilityPerUse(true);
	}

	protected final float durabilityPerUse( boolean rounded){
		float usages = baseUses * (float)(Math.pow(3, level()));


		//at 100 uses, items just last forever.
		if (usages >= 100f) return 0;

		if (rounded){
			usages = Math.round(usages);
			//add a tiny amount to account for rounding error for calculations like 1/3
			return (MAX_DURABILITY/usages) + 0.001f;
		} else {
			//rounding can be disabled for classes that override durability per use
			return MAX_DURABILITY/usages;
		}
	}
	
	protected void decrementDurability(){
		//if this weapon was thrown from a source stack, degrade that stack.
		//unless a weapon is about to break, then break the one being thrown
		if (parent != null){
			if (parent.durability <= parent.durabilityPerUse()){
				durability = 0;
				parent.durability = MAX_DURABILITY;
				if (parent.durabilityPerUse() < 100f) {
					GLog.n(Messages.get(this, "has_broken"));
				}
			} else {
				parent.durability -= parent.durabilityPerUse();
				if (parent.durability > 0 && parent.durability <= parent.durabilityPerUse()){
					GLog.w(Messages.get(this, "about_to_break"));
				}
			}
			parent = null;
		} else {
			durability -= durabilityPerUse();
			if (durability > 0 && durability <= durabilityPerUse()){
				GLog.w(Messages.get(this, "about_to_break"));
			} else if (durabilityPerUse() < 100f && durability <= 0){
				GLog.n(Messages.get(this, "has_broken"));
			}
		}
	}
	
	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll( owner ));
		
		return damage;
	}
	
	@Override
	public void reset() {
		super.reset();
		durability = MAX_DURABILITY;
	}
	
	@Override
	public Item merge(Item other) {
		super.merge(other);
		if (isSimilar(other)) {
			durability += ((MissileWeapon)other).durability;
			durability -= MAX_DURABILITY;
			while (durability <= 0){
				quantity -= 1;
				durability += MAX_DURABILITY;
			}
		}
		return this;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public String info() {

		String info = super.info();
		
		info += "\n\n" + Messages.get( MissileWeapon.class, "stats",
				tier,
				Math.round(augment.damageFactor(min())),
				Math.round(augment.damageFactor(max())),
				STRReq());

		if (Dungeon.hero != null) {
			if (STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Weapon.class, "too_heavy");
			} else if (Dungeon.hero.STR() > STRReq()) {
				info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
			}
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", "Ench.name");
			info += " " + Messages.get(enchantment, "desc");
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}

		info += "\n\n" + Messages.get(MissileWeapon.class, "distance");
		
		info += "\n\n" + Messages.get(this, "durability");
		
		if (durabilityPerUse() > 0){
			info += " " + Messages.get(this, "uses_left",
					(int)Math.ceil(durability/durabilityPerUse()),
					(int)Math.ceil(MAX_DURABILITY/durabilityPerUse()));
		} else {
			info += " " + Messages.get(this, "unlimited_uses");
		}
		
		
		return info;
	}
	
	@Override
	public int value() {
		return 6 * tier * quantity * (level() + 1);
	}

	private static final String SPAWNED = "spawned";
	private static final String DURABILITY = "durability";

	private static boolean bundleRestoring = false;

	public static class PlaceHolder extends MissileWeapon {

		{
			image = ItemSpriteSheet.MISSILE_HOLDER;
		}

		@Override
		public boolean isSimilar(Item item) {
			return false;
		}

		@Override
		public String info() {
			return "";
		}
		//TODO: check all of this
		@Override
		public boolean doEquip(Hero hero) {
			return false;
		}
	}
}
