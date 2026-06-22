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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class Talent {

	private static final @NotNull LinkedHashMap<String, Talent> VALUES = new LinkedHashMap<>();

	public static final @NotNull Talent HEARTY_MEAL = legacy("HEARTY_MEAL", 0);
	public static final @NotNull Talent VETERANS_INTUITION = legacy("VETERANS_INTUITION", 1);
	public static final @NotNull Talent PROVOKED_ANGER = legacy("PROVOKED_ANGER", 2);
	public static final @NotNull Talent IRON_WILL = legacy("IRON_WILL", 3);
	public static final @NotNull Talent IRON_STOMACH = legacy("IRON_STOMACH", 4);
	public static final @NotNull Talent LIQUID_WILLPOWER = legacy("LIQUID_WILLPOWER", 5);
	public static final @NotNull Talent RUNIC_TRANSFERENCE = legacy("RUNIC_TRANSFERENCE", 6);
	public static final @NotNull Talent LETHAL_MOMENTUM = legacy("LETHAL_MOMENTUM", 7);
	public static final @NotNull Talent IMPROVISED_PROJECTILES = legacy("IMPROVISED_PROJECTILES", 8);
	public static final @NotNull Talent HOLD_FAST = legacy("HOLD_FAST", 9, 3);
	public static final @NotNull Talent STRONGMAN = legacy("STRONGMAN", 10, 3);
	public static final @NotNull Talent ENDLESS_RAGE = legacy("ENDLESS_RAGE", 11, 3);
	public static final @NotNull Talent DEATHLESS_FURY = legacy("DEATHLESS_FURY", 12, 3);
	public static final @NotNull Talent ENRAGED_CATALYST = legacy("ENRAGED_CATALYST", 13, 3);
	public static final @NotNull Talent CLEAVE = legacy("CLEAVE", 14, 3);
	public static final @NotNull Talent LETHAL_DEFENSE = legacy("LETHAL_DEFENSE", 15, 3);
	public static final @NotNull Talent ENHANCED_COMBO = legacy("ENHANCED_COMBO", 16, 3);
	public static final @NotNull Talent BODY_SLAM = legacy("BODY_SLAM", 17, 4);
	public static final @NotNull Talent IMPACT_WAVE = legacy("IMPACT_WAVE", 18, 4);
	public static final @NotNull Talent DOUBLE_JUMP = legacy("DOUBLE_JUMP", 19, 4);
	public static final @NotNull Talent EXPANDING_WAVE = legacy("EXPANDING_WAVE", 20, 4);
	public static final @NotNull Talent STRIKING_WAVE = legacy("STRIKING_WAVE", 21, 4);
	public static final @NotNull Talent SHOCK_FORCE = legacy("SHOCK_FORCE", 22, 4);
	public static final @NotNull Talent SUSTAINED_RETRIBUTION = legacy("SUSTAINED_RETRIBUTION", 23, 4);
	public static final @NotNull Talent SHRUG_IT_OFF = legacy("SHRUG_IT_OFF", 24, 4);
	public static final @NotNull Talent EVEN_THE_ODDS = legacy("EVEN_THE_ODDS", 25, 4);
	public static final @NotNull Talent HEROIC_ENERGY = legacy("HEROIC_ENERGY", 26, 4);
	public static final @NotNull Talent EMPOWERING_MEAL = legacy("EMPOWERING_MEAL", 32);
	public static final @NotNull Talent SCHOLARS_INTUITION = legacy("SCHOLARS_INTUITION", 33);
	public static final @NotNull Talent LINGERING_MAGIC = legacy("LINGERING_MAGIC", 34);
	public static final @NotNull Talent BACKUP_BARRIER = legacy("BACKUP_BARRIER", 35);
	public static final @NotNull Talent ENERGIZING_MEAL = legacy("ENERGIZING_MEAL", 36);
	public static final @NotNull Talent INSCRIBED_POWER = legacy("INSCRIBED_POWER", 37);
	public static final @NotNull Talent WAND_PRESERVATION = legacy("WAND_PRESERVATION", 38);
	public static final @NotNull Talent ARCANE_VISION = legacy("ARCANE_VISION", 39);
	public static final @NotNull Talent SHIELD_BATTERY = legacy("SHIELD_BATTERY", 40);
	public static final @NotNull Talent DESPERATE_POWER = legacy("DESPERATE_POWER", 41, 3);
	public static final @NotNull Talent ALLY_WARP = legacy("ALLY_WARP", 42, 3);
	public static final @NotNull Talent EMPOWERED_STRIKE = legacy("EMPOWERED_STRIKE", 43, 3);
	public static final @NotNull Talent MYSTICAL_CHARGE = legacy("MYSTICAL_CHARGE", 44, 3);
	public static final @NotNull Talent EXCESS_CHARGE = legacy("EXCESS_CHARGE", 45, 3);
	public static final @NotNull Talent SOUL_EATER = legacy("SOUL_EATER", 46, 3);
	public static final @NotNull Talent SOUL_SIPHON = legacy("SOUL_SIPHON", 47, 3);
	public static final @NotNull Talent NECROMANCERS_MINIONS = legacy("NECROMANCERS_MINIONS", 48, 3);
	public static final @NotNull Talent BLAST_RADIUS = legacy("BLAST_RADIUS", 49, 4);
	public static final @NotNull Talent ELEMENTAL_POWER = legacy("ELEMENTAL_POWER", 50, 4);
	public static final @NotNull Talent REACTIVE_BARRIER = legacy("REACTIVE_BARRIER", 51, 4);
	public static final @NotNull Talent WILD_POWER = legacy("WILD_POWER", 52, 4);
	public static final @NotNull Talent FIRE_EVERYTHING = legacy("FIRE_EVERYTHING", 53, 4);
	public static final @NotNull Talent CONSERVED_MAGIC = legacy("CONSERVED_MAGIC", 54, 4);
	public static final @NotNull Talent TELEFRAG = legacy("TELEFRAG", 55, 4);
	public static final @NotNull Talent REMOTE_BEACON = legacy("REMOTE_BEACON", 56, 4);
	public static final @NotNull Talent LONGRANGE_WARP = legacy("LONGRANGE_WARP", 57, 4);
	public static final @NotNull Talent CACHED_RATIONS = legacy("CACHED_RATIONS", 64);
	public static final @NotNull Talent THIEFS_INTUITION = legacy("THIEFS_INTUITION", 65);
	public static final @NotNull Talent SUCKER_PUNCH = legacy("SUCKER_PUNCH", 66);
	public static final @NotNull Talent PROTECTIVE_SHADOWS = legacy("PROTECTIVE_SHADOWS", 67);
	public static final @NotNull Talent MYSTICAL_MEAL = legacy("MYSTICAL_MEAL", 68);
	public static final @NotNull Talent INSCRIBED_STEALTH = legacy("INSCRIBED_STEALTH", 69);
	public static final @NotNull Talent WIDE_SEARCH = legacy("WIDE_SEARCH", 70);
	public static final @NotNull Talent SILENT_STEPS = legacy("SILENT_STEPS", 71);
	public static final @NotNull Talent ROGUES_FORESIGHT = legacy("ROGUES_FORESIGHT", 72);
	public static final @NotNull Talent ENHANCED_RINGS = legacy("ENHANCED_RINGS", 73, 3);
	public static final @NotNull Talent LIGHT_CLOAK = legacy("LIGHT_CLOAK", 74, 3);
	public static final @NotNull Talent ENHANCED_LETHALITY = legacy("ENHANCED_LETHALITY", 75, 3);
	public static final @NotNull Talent ASSASSINS_REACH = legacy("ASSASSINS_REACH", 76, 3);
	public static final @NotNull Talent BOUNTY_HUNTER = legacy("BOUNTY_HUNTER", 77, 3);
	public static final @NotNull Talent EVASIVE_ARMOR = legacy("EVASIVE_ARMOR", 78, 3);
	public static final @NotNull Talent PROJECTILE_MOMENTUM = legacy("PROJECTILE_MOMENTUM", 79, 3);
	public static final @NotNull Talent SPEEDY_STEALTH = legacy("SPEEDY_STEALTH", 80, 3);
	public static final @NotNull Talent HASTY_RETREAT = legacy("HASTY_RETREAT", 81, 4);
	public static final @NotNull Talent BODY_REPLACEMENT = legacy("BODY_REPLACEMENT", 82, 4);
	public static final @NotNull Talent SHADOW_STEP = legacy("SHADOW_STEP", 83, 4);
	public static final @NotNull Talent FEAR_THE_REAPER = legacy("FEAR_THE_REAPER", 84, 4);
	public static final @NotNull Talent DEATHLY_DURABILITY = legacy("DEATHLY_DURABILITY", 85, 4);
	public static final @NotNull Talent DOUBLE_MARK = legacy("DOUBLE_MARK", 86, 4);
	public static final @NotNull Talent SHADOW_BLADE = legacy("SHADOW_BLADE", 87, 4);
	public static final @NotNull Talent CLONED_ARMOR = legacy("CLONED_ARMOR", 88, 4);
	public static final @NotNull Talent PERFECT_COPY = legacy("PERFECT_COPY", 89, 4);
	public static final @NotNull Talent NATURES_BOUNTY = legacy("NATURES_BOUNTY", 96);
	public static final @NotNull Talent SURVIVALISTS_INTUITION = legacy("SURVIVALISTS_INTUITION", 97);
	public static final @NotNull Talent FOLLOWUP_STRIKE = legacy("FOLLOWUP_STRIKE", 98);
	public static final @NotNull Talent NATURES_AID = legacy("NATURES_AID", 99);
	public static final @NotNull Talent INVIGORATING_MEAL = legacy("INVIGORATING_MEAL", 100);
	public static final @NotNull Talent LIQUID_NATURE = legacy("LIQUID_NATURE", 101);
	public static final @NotNull Talent REJUVENATING_STEPS = legacy("REJUVENATING_STEPS", 102);
	public static final @NotNull Talent HEIGHTENED_SENSES = legacy("HEIGHTENED_SENSES", 103);
	public static final @NotNull Talent DURABLE_PROJECTILES = legacy("DURABLE_PROJECTILES", 104);
	public static final @NotNull Talent POINT_BLANK = legacy("POINT_BLANK", 105, 3);
	public static final @NotNull Talent SEER_SHOT = legacy("SEER_SHOT", 106, 3);
	public static final @NotNull Talent FARSIGHT = legacy("FARSIGHT", 107, 3);
	public static final @NotNull Talent SHARED_ENCHANTMENT = legacy("SHARED_ENCHANTMENT", 108, 3);
	public static final @NotNull Talent SHARED_UPGRADES = legacy("SHARED_UPGRADES", 109, 3);
	public static final @NotNull Talent DURABLE_TIPS = legacy("DURABLE_TIPS", 110, 3);
	public static final @NotNull Talent BARKSKIN = legacy("BARKSKIN", 111, 3);
	public static final @NotNull Talent SHIELDING_DEW = legacy("SHIELDING_DEW", 112, 3);
	public static final @NotNull Talent FAN_OF_BLADES = legacy("FAN_OF_BLADES", 113, 4);
	public static final @NotNull Talent PROJECTING_BLADES = legacy("PROJECTING_BLADES", 114, 4);
	public static final @NotNull Talent SPIRIT_BLADES = legacy("SPIRIT_BLADES", 115, 4);
	public static final @NotNull Talent GROWING_POWER = legacy("GROWING_POWER", 116, 4);
	public static final @NotNull Talent NATURES_WRATH = legacy("NATURES_WRATH", 117, 4);
	public static final @NotNull Talent WILD_MOMENTUM = legacy("WILD_MOMENTUM", 118, 4);
	public static final @NotNull Talent EAGLE_EYE = legacy("EAGLE_EYE", 119, 4);
	public static final @NotNull Talent GO_FOR_THE_EYES = legacy("GO_FOR_THE_EYES", 120, 4);
	public static final @NotNull Talent SWIFT_SPIRIT = legacy("SWIFT_SPIRIT", 121, 4);
	public static final @NotNull Talent STRENGTHENING_MEAL = legacy("STRENGTHENING_MEAL", 128);
	public static final @NotNull Talent ADVENTURERS_INTUITION = legacy("ADVENTURERS_INTUITION", 129);
	public static final @NotNull Talent PATIENT_STRIKE = legacy("PATIENT_STRIKE", 130);
	public static final @NotNull Talent AGGRESSIVE_BARRIER = legacy("AGGRESSIVE_BARRIER", 131);
	public static final @NotNull Talent FOCUSED_MEAL = legacy("FOCUSED_MEAL", 132);
	public static final @NotNull Talent LIQUID_AGILITY = legacy("LIQUID_AGILITY", 133);
	public static final @NotNull Talent WEAPON_RECHARGING = legacy("WEAPON_RECHARGING", 134);
	public static final @NotNull Talent LETHAL_HASTE = legacy("LETHAL_HASTE", 135);
	public static final @NotNull Talent SWIFT_EQUIP = legacy("SWIFT_EQUIP", 136);
	public static final @NotNull Talent PRECISE_ASSAULT = legacy("PRECISE_ASSAULT", 137, 3);
	public static final @NotNull Talent DEADLY_FOLLOWUP = legacy("DEADLY_FOLLOWUP", 138, 3);
	public static final @NotNull Talent VARIED_CHARGE = legacy("VARIED_CHARGE", 139, 3);
	public static final @NotNull Talent TWIN_UPGRADES = legacy("TWIN_UPGRADES", 140, 3);
	public static final @NotNull Talent COMBINED_LETHALITY = legacy("COMBINED_LETHALITY", 141, 3);
	public static final @NotNull Talent UNENCUMBERED_SPIRIT = legacy("UNENCUMBERED_SPIRIT", 142, 3);
	public static final @NotNull Talent MONASTIC_VIGOR = legacy("MONASTIC_VIGOR", 143, 3);
	public static final @NotNull Talent COMBINED_ENERGY = legacy("COMBINED_ENERGY", 144, 3);
	public static final @NotNull Talent CLOSE_THE_GAP = legacy("CLOSE_THE_GAP", 145, 4);
	public static final @NotNull Talent INVIGORATING_VICTORY = legacy("INVIGORATING_VICTORY", 146, 4);
	public static final @NotNull Talent ELIMINATION_MATCH = legacy("ELIMINATION_MATCH", 147, 4);
	public static final @NotNull Talent ELEMENTAL_REACH = legacy("ELEMENTAL_REACH", 148, 4);
	public static final @NotNull Talent STRIKING_FORCE = legacy("STRIKING_FORCE", 149, 4);
	public static final @NotNull Talent DIRECTED_POWER = legacy("DIRECTED_POWER", 150, 4);
	public static final @NotNull Talent FEIGNED_RETREAT = legacy("FEIGNED_RETREAT", 151, 4);
	public static final @NotNull Talent EXPOSE_WEAKNESS = legacy("EXPOSE_WEAKNESS", 152, 4);
	public static final @NotNull Talent COUNTER_ABILITY = legacy("COUNTER_ABILITY", 153, 4);
	public static final @NotNull Talent SATIATED_SPELLS = legacy("SATIATED_SPELLS", 160);
	public static final @NotNull Talent HOLY_INTUITION = legacy("HOLY_INTUITION", 161);
	public static final @NotNull Talent SEARING_LIGHT = legacy("SEARING_LIGHT", 162);
	public static final @NotNull Talent SHIELD_OF_LIGHT = legacy("SHIELD_OF_LIGHT", 163);
	public static final @NotNull Talent ENLIGHTENING_MEAL = legacy("ENLIGHTENING_MEAL", 164);
	public static final @NotNull Talent RECALL_INSCRIPTION = legacy("RECALL_INSCRIPTION", 165);
	public static final @NotNull Talent SUNRAY = legacy("SUNRAY", 166);
	public static final @NotNull Talent DIVINE_SENSE = legacy("DIVINE_SENSE", 167);
	public static final @NotNull Talent BLESS = legacy("BLESS", 168);
	public static final @NotNull Talent CLEANSE = legacy("CLEANSE", 169, 3);
	public static final @NotNull Talent LIGHT_READING = legacy("LIGHT_READING", 170, 3);
	public static final @NotNull Talent HOLY_LANCE = legacy("HOLY_LANCE", 171, 3);
	public static final @NotNull Talent HALLOWED_GROUND = legacy("HALLOWED_GROUND", 172, 3);
	public static final @NotNull Talent MNEMONIC_PRAYER = legacy("MNEMONIC_PRAYER", 173, 3);
	public static final @NotNull Talent LAY_ON_HANDS = legacy("LAY_ON_HANDS", 174, 3);
	public static final @NotNull Talent AURA_OF_PROTECTION = legacy("AURA_OF_PROTECTION", 175, 3);
	public static final @NotNull Talent WALL_OF_LIGHT = legacy("WALL_OF_LIGHT", 176, 3);
	public static final @NotNull Talent DIVINE_INTERVENTION = legacy("DIVINE_INTERVENTION", 177, 4);
	public static final @NotNull Talent JUDGEMENT = legacy("JUDGEMENT", 178, 4);
	public static final @NotNull Talent FLASH = legacy("FLASH", 179, 4);
	public static final @NotNull Talent BODY_FORM = legacy("BODY_FORM", 180, 4);
	public static final @NotNull Talent MIND_FORM = legacy("MIND_FORM", 181, 4);
	public static final @NotNull Talent SPIRIT_FORM = legacy("SPIRIT_FORM", 182, 4);
	public static final @NotNull Talent BEAMING_RAY = legacy("BEAMING_RAY", 183, 4);
	public static final @NotNull Talent LIFE_LINK = legacy("LIFE_LINK", 184, 4);
	public static final @NotNull Talent STASIS = legacy("STASIS", 185, 4);
	public static final @NotNull Talent RATSISTANCE = legacy("RATSISTANCE", 215, 4);
	public static final @NotNull Talent RATLOMACY = legacy("RATLOMACY", 216, 4);
	public static final @NotNull Talent RATFORCEMENTS = legacy("RATFORCEMENTS", 217, 4);

	public static final int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};
	public static final int MAX_TALENT_TIERS = 4;

	private final @NotNull String id;

	protected Talent(@NotNull String id) {
		this.id = id;
	}

	private static @NotNull Talent legacy(@NotNull String id, int icon) {
		return legacy(id, icon, 2);
	}

	private static @NotNull Talent legacy(@NotNull String id, int icon, int maxPoints) {
		Talent talent = new CustomTalent(id, icon, maxPoints);
		VALUES.put(id, talent);
		return talent;
	}

	public static @NotNull Talent valueOf(@NotNull String id) {
		Talent talent = VALUES.get(id);
		if (talent == null) {
			throw new IllegalArgumentException("Unknown talent: " + id);
		}
		return talent;
	}

	public static @NotNull Talent[] values() {
		return VALUES.values().toArray(new Talent[0]);
	}

	public @NotNull String id() {
		return id;
	}

	public @NotNull String name() {
		return id;
	}

	public abstract int icon();

	public abstract int maxPoints();

	public abstract @NotNull String title();

	public final @NotNull String desc(){
		return desc(false);
	}

	public abstract @NotNull String desc(boolean metamorphed);

	public static void onTalentUpgraded(@NotNull Hero hero, @NotNull Talent talent) {
		// Gameplay effects are server-owned in multiplayer client.
	}

	public static void initClassTalents(@NotNull Hero hero) {
		initClassTalents(hero.heroClass, hero.talents, hero.metamorphedTalents);
	}

	public static void initClassTalents(@NotNull HeroClass cls, @NotNull ArrayList<LinkedHashMap<Talent, Integer>> talents) {
		initClassTalents(cls, talents, new LinkedHashMap<>());
	}

	public static void initClassTalents(@NotNull HeroClass cls, @NotNull ArrayList<LinkedHashMap<Talent, Integer>> talents, @NotNull LinkedHashMap<Talent, Talent> replacements) {
		talents.clear();
		for (LinkedHashMap<Talent, Integer> sourceTier : cls.talentTiers()) {
			LinkedHashMap<Talent, Integer> targetTier = new LinkedHashMap<>();
			for (Talent talent : sourceTier.keySet()) {
				if (replacements.containsKey(talent)) {
					talent = replacements.get(talent);
				}
				targetTier.put(talent, 0);
			}
			talents.add(targetTier);
		}
		while (talents.size() < MAX_TALENT_TIERS) {
			talents.add(new LinkedHashMap<>());
		}
	}

	public static void initSubclassTalents(@NotNull HeroSubClass cls, @NotNull ArrayList<LinkedHashMap<Talent, Integer>> talents) {
		if (cls.isNone()) return;

		while (talents.size() < MAX_TALENT_TIERS) {
			talents.add(new LinkedHashMap<>());
		}

		LinkedHashMap<Talent, Integer> previousTier = talents.get(2);
		LinkedHashMap<Talent, Integer> newTier = new LinkedHashMap<>();
		ArrayList<Talent> tierTalents = new ArrayList<>(cls.talents());
		for (Talent talent : tierTalents) {
			int points = 0;
			for (Talent previousTalent : previousTier.keySet()) {
				if (previousTalent.equals(talent)) {
					points = previousTier.get(previousTalent);
					break;
				}
			}
			newTier.put(talent, points);
		}
		talents.set(2, newTier);
	}

	public static void initArmorTalents(ArmorAbility abil, @NotNull ArrayList<LinkedHashMap<Talent, Integer>> talents) {
		if (abil == null) return;

		while (talents.size() < MAX_TALENT_TIERS) {
			talents.add(new LinkedHashMap<>());
		}

		for (Talent t : abil.talents()) {
			talents.get(3).put(t, 0);
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Talent && id.equals(((Talent) obj).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public @NotNull String toString() {
		return id;
	}
}
