package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;


import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSpriteFactory;
//FIXME
public class CustomMob extends Mob {
    @Override
    public String name() {
        String messageName = Messages.get(name);
        if (Messages.NO_TEXT_FOUND.equals(messageName)) {
            return name;
        }
        return messageName;
    }

    public CustomMob(int id) {
        name = "unknown";
        spriteFactory = CharSpriteFactory.missing("CustomMob.constructor", "actor was created before sprite data arrived");

        HP = HT = 1;
        defenseSkill = 1;

        this.setId(id);
    }

}
