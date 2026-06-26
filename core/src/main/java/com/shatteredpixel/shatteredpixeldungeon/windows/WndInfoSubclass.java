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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.CustomTalent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class WndInfoSubclass extends WndTitledMessage {

	public WndInfoSubclass(int id, @NotNull JSONObject args) {
		super(new IconTitle(Icons.get(Icons.TALENT), JsonStringHelper.getString(args, "title")), JsonStringHelper.getString(args, "description"));
		setId(id);

		LinkedHashMap<Talent, Integer> talents = new LinkedHashMap<>();
		JSONArray talentArray = args.optJSONArray("talents");
		if (talentArray != null) {
			for (int i = 0; i < talentArray.length(); i++) {
				JSONObject talent = talentArray.getJSONObject(i);
				talents.put(CustomTalent.fromJson(talent), talent.optInt("points", 0));
			}
		}
		if (!talents.isEmpty()) {
			TalentsPane.TalentTierPane talentPane = new TalentsPane.TalentTierPane(talents, 3, TalentButton.Mode.INFO);
			talentPane.title.text(Messages.titleCase(Messages.get(WndHeroInfo.class, "talents")));
			talentPane.setRect(0, height + 5, width, talentPane.height());
			add(talentPane);
			resize(width, (int)talentPane.bottom());
		}
	}

	public WndInfoSubclass(HeroClass cls, HeroSubClass subCls){
		super( new HeroIcon(subCls), Messages.titleCase(subCls.title()), subCls.desc());

		LinkedHashMap<Talent, Integer> talents = new LinkedHashMap<>();
		for (Talent talent : subCls.talents()) {
			talents.put(talent, 0);
		}

		TalentsPane.TalentTierPane talentPane = new TalentsPane.TalentTierPane(talents, 3, TalentButton.Mode.INFO);
		talentPane.title.text( Messages.titleCase(Messages.get(WndHeroInfo.class, "talents")));
		talentPane.setRect(0, height + 5, width, talentPane.height());
		add(talentPane);
		resize(width, (int) talentPane.bottom());

	}

	@Override
	protected float targetHeight() {
		return super.targetHeight()-40;
	}

}
