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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WildMagic extends ArmorAbility {

	protected float baseChargeUse = 35;

	{
		baseChargeUse = 25f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}


	Actor wildMagicActor = null;

	private void zapWand( ArrayList<Wand> wands, Hero hero, int cell){
		Wand cur = wands.remove(0);

		Ballistica aim = new Ballistica(hero.pos, cell, cur.collisionProperties(cell));

		hero.sprite.zap(cell);

		float startTime = Game.timeTotal;
		if (cur.tryToZap(hero, cell)) {
			if (!cur.cursed) {
				cur.fx(aim, new Callback() {
					@Override
					public void call() {
						cur.onZap(aim);
                        boolean alsoCursedZap = Random.Float() < (float) 0;
						if (Game.timeTotal - startTime < 0.33f) {
							hero.sprite.parent.add(new Delayer(0.33f - (Game.timeTotal - startTime)) {
								@Override
								protected void onComplete() {
									if (alsoCursedZap){
										new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT);

                                    } else {
										afterZap(cur, wands, hero, cell);
									}
								}
							});
						} else {
							if (alsoCursedZap){
								new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT);

                            } else {
								afterZap(cur, wands, hero, cell);
							}
						}
					}
				});

			} else {
                new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT);

            }
		} else {
			afterZap(cur, wands, hero, cell);
		}
	}

	private void afterZap( Wand cur, ArrayList<Wand> wands, Hero hero, int target){
		cur.partialCharge -= 0.5f * (float)Math.pow(0.67f, hero.pointsInTalent(Talent.CONSERVED_MAGIC));
		if (cur.partialCharge < 0) {
			cur.partialCharge++;
			cur.curCharges--;
		}
		if (wildMagicActor != null){
			wildMagicActor.next();
			wildMagicActor = null;
		}

		Char ch = Actor.findChar(target);
		if (!wands.isEmpty() && hero.isAlive()) {
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO-1;
				}

				@Override
				protected boolean act() {
					wildMagicActor = this;
					zapWand(wands, hero, ch == null ? target : ch.pos);
					Actor.remove(this);
					return false;
				}
			});
			hero.next();
		} else {
            Item.updateQuickslot();
            if (Random.Int(4) >= hero.pointsInTalent(Talent.CONSERVED_MAGIC)) {
                hero.spendAndNext(Actor.TICK);
            } else {
                hero.next();
            }
        }
	}

	@Override
	public int icon() {
		return HeroIcon.WILD_MAGIC;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.WILD_POWER, Talent.FIRE_EVERYTHING, Talent.CONSERVED_MAGIC, Talent.HEROIC_ENERGY};
	}
}
