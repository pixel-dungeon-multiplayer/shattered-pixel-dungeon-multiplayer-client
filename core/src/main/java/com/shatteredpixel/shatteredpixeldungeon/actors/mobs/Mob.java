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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSpriteFactory;

public abstract class Mob extends Char {

    {
        actPriority = MOB_PRIO;

        alignment = Alignment.ENEMY;
    }


    public CharSpriteFactory spriteFactory = CharSpriteFactory.missing("Mob.spriteFactory", "mob has no sprite data yet");

    public int defenseSkill = 0;

    public CharSprite sprite() {
        return spriteFactory.create();
    }

    @Override
    public void destroy() {

        super.destroy();

        Dungeon.level.mobs.remove(this);

    }

    public String description() {
        if (desc == null) {
            return Messages.get(this, "desc");
        }
        return desc;

    }

    public String info() {
        return description();
    }

    public void notice() {
        sprite.showAlert();
    }

    //some mobs have an associated landmark entry, which is added when the hero sees them
    //mobs may also remove this landmark in some cases, such as when a quest is complete or they die
    public Notes.Landmark landmark() {
        return null;
    }


    String desc = null;

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
