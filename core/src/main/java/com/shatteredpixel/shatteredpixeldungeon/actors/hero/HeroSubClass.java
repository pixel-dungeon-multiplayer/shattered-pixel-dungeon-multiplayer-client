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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class HeroSubClass {

	public static final @NotNull HeroSubClass NONE = new HeroSubClass("NONE", "", "", "", 0, Collections.emptyList()) {};

	private final @NotNull String id;
	private final @NotNull String title;
	private final @NotNull String shortDesc;
	private final @NotNull String desc;
	private final int icon;
	private final @NotNull List<Talent> talents;

	protected HeroSubClass(
			@NotNull String id,
			@NotNull String title,
			@NotNull String shortDesc,
			@NotNull String desc,
			int icon,
			@NotNull List<Talent> talents) {
		this.id = id;
		this.title = title;
		this.shortDesc = shortDesc;
		this.desc = desc;
		this.icon = icon;
		this.talents = Collections.unmodifiableList(new ArrayList<>(talents));
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

	public final @NotNull String shortDesc() {
		return shortDesc;
	}

	public final @NotNull String desc() {
		return desc;
	}

	public final int icon() {
		return icon;
	}

	public final @NotNull List<Talent> talents() {
		return talents;
	}

	public final boolean isNone() {
		return "NONE".equals(id);
	}
}
