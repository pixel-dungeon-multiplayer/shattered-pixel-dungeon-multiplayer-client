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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public abstract class HeroClass {

	public static final @NotNull HeroClass NONE = new HeroClass(
			"NONE",
			"",
			"",
			"",
			Assets.Sprites.WARRIOR,
			"",
			false,
			"",
			Collections.emptyList(),
			Collections.emptyList(),
			new ArrayList<java.util.LinkedHashMap<Talent, Integer>>()) {};

	private static final @NotNull HeroClass WARRIOR = local("WARRIOR", Assets.Sprites.WARRIOR, Assets.Splashes.WARRIOR);
	private static final @NotNull HeroClass MAGE = local("MAGE", Assets.Sprites.MAGE, Assets.Splashes.MAGE);
	private static final @NotNull HeroClass ROGUE = local("ROGUE", Assets.Sprites.ROGUE, Assets.Splashes.ROGUE);
	private static final @NotNull HeroClass HUNTRESS = local("HUNTRESS", Assets.Sprites.HUNTRESS, Assets.Splashes.HUNTRESS);
	private static final @NotNull HeroClass DUELIST = local("DUELIST", Assets.Sprites.DUELIST, Assets.Splashes.DUELIST);
	private static final @NotNull HeroClass CLERIC = local("CLERIC", Assets.Sprites.CLERIC, Assets.Splashes.CLERIC);
	private static final @NotNull HeroClass[] VALUES = new HeroClass[]{WARRIOR, MAGE, ROGUE, HUNTRESS, DUELIST, CLERIC};
	private static final @NotNull LinkedHashMap<String, HeroClass> REGISTRY = new LinkedHashMap<>();

	static {
		REGISTRY.put(NONE.id(), NONE);
		for (HeroClass heroClass : VALUES) {
			REGISTRY.put(heroClass.id(), heroClass);
		}
	}

	private final @NotNull String id;
	private final @NotNull String title;
	private final @NotNull String shortDesc;
	private final @NotNull String desc;
	private final @NotNull String spritesheet;
	private final @NotNull String splashArt;
	private final boolean unlocked;
	private final @NotNull String unlockMsg;
	private final @NotNull List<HeroSubClass> subClasses;
	private final @NotNull List<ArmorAbility> armorAbilities;
	private final @NotNull ArrayList<java.util.LinkedHashMap<Talent, Integer>> talentTiers;

	protected HeroClass(
			@NotNull String id,
			@NotNull String title,
			@NotNull String shortDesc,
			@NotNull String desc,
			@NotNull String spritesheet,
			@NotNull String splashArt,
			boolean unlocked,
			@NotNull String unlockMsg,
			@NotNull List<HeroSubClass> subClasses,
			@NotNull List<ArmorAbility> armorAbilities,
			@NotNull ArrayList<java.util.LinkedHashMap<Talent, Integer>> talentTiers) {
		this.id = id;
		this.title = title;
		this.shortDesc = shortDesc;
		this.desc = desc;
		this.spritesheet = spritesheet;
		this.splashArt = splashArt;
		this.unlocked = unlocked;
		this.unlockMsg = unlockMsg;
		this.subClasses = Collections.unmodifiableList(new ArrayList<>(subClasses));
		this.armorAbilities = Collections.unmodifiableList(new ArrayList<>(armorAbilities));
		this.talentTiers = talentTiers;
	}

	private static @NotNull HeroClass local(@NotNull String id, @NotNull String spritesheet, @NotNull String splashArt) {
		return new HeroClass(
				id,
				Messages.get(HeroClass.class, id),
				Messages.get(HeroClass.class, id + "_desc_short"),
				Messages.get(HeroClass.class, id + "_desc"),
				spritesheet,
				splashArt,
				true,
				Messages.get(HeroClass.class, id + "_desc_short") + "\n\n" + Messages.get(HeroClass.class, id + "_unlock"),
				Collections.emptyList(),
				Collections.emptyList(),
				new ArrayList<java.util.LinkedHashMap<Talent, Integer>>()) {};
	}

	public static @NotNull HeroClass[] values() {
		return VALUES.clone();
	}

	public static @NotNull HeroClass valueOf(@NotNull String id) {
		HeroClass heroClass = REGISTRY.get(id.toUpperCase(Locale.ENGLISH));
		if (heroClass == null) {
			throw new IllegalArgumentException("Unknown hero class: " + id);
		}
		return heroClass;
	}

	public static @NotNull HeroClass defaultClass() {
		return ROGUE;
	}

	public final @NotNull String id() {
		return id;
	}

	public final @NotNull String name() {
		return id;
	}

	public final @NotNull String title() {
		return title;
	}

	public final @NotNull String desc() {
		return desc;
	}

	public final @NotNull String shortDesc() {
		return shortDesc;
	}

	public final @NotNull List<HeroSubClass> subClasses() {
		return subClasses;
	}

	public final @NotNull List<ArmorAbility> armorAbilities() {
		return armorAbilities;
	}

	public final @NotNull ArrayList<java.util.LinkedHashMap<Talent, Integer>> talentTiers() {
		return talentTiers;
	}

	public final @NotNull String spritesheet() {
		return spritesheet;
	}

	public final @NotNull String splashArt() {
		return splashArt;
	}

	public final boolean isUnlocked() {
		return unlocked;
	}

	public final @NotNull String unlockMsg() {
		return unlockMsg;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof HeroClass && id.equals(((HeroClass) obj).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
