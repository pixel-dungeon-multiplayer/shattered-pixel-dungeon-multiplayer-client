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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ArmorAbility {

	private final @NotNull String id;
	private final @NotNull String name;
	private final @NotNull String shortDesc;
	private final @NotNull String desc;
	private final int icon;
	private final @NotNull List<Talent> talents;

	protected ArmorAbility(
			@NotNull String id,
			@NotNull String name,
			@NotNull String shortDesc,
			@NotNull String desc,
			int icon,
			@NotNull List<Talent> talents) {
		this.id = id;
		this.name = name;
		this.shortDesc = shortDesc;
		this.desc = desc;
		this.icon = icon;
		this.talents = Collections.unmodifiableList(new ArrayList<>(talents));
	}

	public final @NotNull String id() {
		return id;
	}

	public final @NotNull String name(){
		return name;
	}

	public final @NotNull String shortDesc(){
		return shortDesc;
	}

	public final @NotNull String desc(){
		return desc;
	}

	public final int icon(){
		return icon;
	}

	public final @NotNull List<Talent> talents() {
		return talents;
	}

	public static int defaultIcon() {
		return HeroIcon.NONE;
	}
}
