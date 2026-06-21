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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.MindForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Trinity extends ArmorAbility {

	protected float baseChargeUse = 35;

	{
		baseChargeUse = 25;
	}


	private static final String BODY = "body_form";
	private static final String MIND = "mind_form";
	private static final String SPIRIT = "spirit_form";

	@Override
	public int icon() {
		return HeroIcon.TRINITY;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.BODY_FORM, Talent.MIND_FORM, Talent.SPIRIT_FORM, Talent.HEROIC_ENERGY};
	}

	public static class WndItemtypeSelect extends WndTitledMessage {

		//probably want a callback here?
		public WndItemtypeSelect(HolyTome tome, ClericSpell spell) {
			super(new HeroIcon(spell), Messages.titleCase(spell.name()), Messages.get(WndItemtypeSelect.class, "text"));

			//start by filtering and sorting
			ArrayList<Class<?>> discoveredClasses = new ArrayList<>();
			if (spell == BodyForm.INSTANCE) {
				for (Class<?> cls : Catalog.ENCHANTMENTS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.GLYPHS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
			} else if (spell == MindForm.INSTANCE){
				for (Class<?> cls : Catalog.WANDS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.THROWN_WEAPONS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.TIPPED_DARTS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
			} else if (spell == SpiritForm.INSTANCE){
				for (Class<?> cls : Catalog.RINGS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.ARTIFACTS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
					//no tome specifically
					discoveredClasses.remove(HolyTome.class);
				}
			}

			ArrayList<Item> options = new ArrayList<>();


			int top = height + 2;
			int left = 0;

			for (Item item : options){
				ItemButton btn = new ItemButton(){
					@Override
					protected void onClick() {
						GameScene.show(new WndItemConfirm(WndItemtypeSelect.this, item, tome, spell));
					}
				};
				btn.item(item);
				btn.slot().textVisible(false);
				btn.setRect(left, top, 19, 19);
				add(btn);

				left += 20;
				if (left >= width - 19){
					top += 20;
					left = 0;
				}
			}

			if (left > 0){
				top += 20;
				left = 0;
			}

			resize(width, top);

		}

	}

	public static class WndItemConfirm extends WndTitledMessage {

		public WndItemConfirm(Window parentWnd, Item item, HolyTome tome, ClericSpell spell){
			super(new ItemSprite(item),  Messages.titleCase(getName(item)), getText(item));

			String text;
			if (spell == BodyForm.INSTANCE){
				text = Messages.get(this, "body");
			} else if (spell == MindForm.INSTANCE){
				text = Messages.get(this, "mind");
			} else {
				text = Messages.get(this, "spirit");
			}

			RedButton btnConfirm = new RedButton(text){
				@Override
				protected void onClick() {
					parentWnd.hide();
					WndItemConfirm.this.hide();


					spell.onSpellCast(tome, Dungeon.hero);

					Dungeon.hero.sprite.operate(Dungeon.hero.pos);
					Enchanting.show(Dungeon.hero, item);
					Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				}
			};
			btnConfirm.setRect(0, height+2, width, 16);
			add(btnConfirm);

			resize(width, (int)btnConfirm.bottom());

		}

		private static String getName(Item item){
			return item.name();
		}

		private static String getText(Item item){
            return item.desc() + "\n\n" + trinityItemUseText(item.getClass());
        }

	}

	public static String trinityItemUseText(Class<?> cls ){
		return "error!";

	}

}
