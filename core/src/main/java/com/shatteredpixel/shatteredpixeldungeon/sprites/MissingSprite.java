package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class MissingSprite extends RatSprite {

	@Override
	public void link(Char ch) {
		super.link(ch);
		GLog.w("Actor with id %s has no sprite. Use default", ch.id());
	}
}
