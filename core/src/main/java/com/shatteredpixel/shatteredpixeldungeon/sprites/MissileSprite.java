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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import io.github.pixeldungeonmultiplayer.shattered.client.network.utils.TranslationUtils;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread.isConnectedToOldServer;

public class MissileSprite extends ItemSprite implements Tweener.Listener {

	private static final float SPEED	= 240f;
	
	private Callback callback;
	
	public void reset( int from, int to, Item item, Callback listener ) {
		reset(Dungeon.level.solid[from] ? DungeonTilemap.raisedTileCenterToWorld(from) : DungeonTilemap.raisedTileCenterToWorld(from),
				Dungeon.level.solid[to] ? DungeonTilemap.raisedTileCenterToWorld(to) : DungeonTilemap.raisedTileCenterToWorld(to),
				item, listener);
	}

	public void reset( Visual from, int to, Item item, Callback listener ) {
		reset(from.center(),
				Dungeon.level.solid[to] ? DungeonTilemap.raisedTileCenterToWorld(to) : DungeonTilemap.raisedTileCenterToWorld(to),
				item, listener );
	}

	public void reset( int from, Visual to, Item item, Callback listener ) {
		reset(Dungeon.level.solid[from] ? DungeonTilemap.raisedTileCenterToWorld(from) : DungeonTilemap.raisedTileCenterToWorld(from),
				to.center(),
				item, listener );
	}

	public void reset( Visual from, Visual to, Item item, Callback listener ) {
		reset(from.center(), to.center(), item, listener );
	}

	public void reset( PointF from, PointF to, Item item, Callback listener) {
		revive();

		if (item == null)   view(0, null);
		else                view( item );

		setup( from,
				to,
				item,
				listener );
	}
	
	private static final int DEFAULT_ANGULAR_SPEED = 720;
	
	private static final HashMap<Class<?extends Item>, Integer> ANGULAR_SPEEDS = new HashMap<>();
	static {

	}

	//TODO it might be nice to have a source and destination angle, to improve thrown weapon visuals
	private void setup( PointF from, PointF to, Item item, Callback listener ){

		originToCenter();

		//adjust points so they work with the center of the missile sprite, not the corner
		from.x -= width()/2;
		to.x -= width()/2;
		from.y -= height()/2;
		to.y -= height()/2;

		this.callback = listener;

		point( from );

		PointF d = PointF.diff( to, from );
		speed.set(d).normalize().scale(SPEED);
		
		angularSpeed = DEFAULT_ANGULAR_SPEED;
		for (Class<?extends Item> cls : ANGULAR_SPEEDS.keySet()){
			if (cls.isAssignableFrom(item.getClass())){
				angularSpeed = ANGULAR_SPEEDS.get(cls);
				break;
			}
		}
		
		angle = 135 - (float)(Math.atan2( d.x, d.y ) / 3.1415926 * 180);
		
		if (d.x >= 0){
			flipHorizontal = false;
			updateFrame();
			
		} else {
			angularSpeed = -angularSpeed;
			angle += 90;
			flipHorizontal = true;
			updateFrame();
		}
		
		float speed = SPEED;
		if (false){
			speed *= 3f;
			
		} else if (false
				|| false
				|| false){
			speed *= 1.5f;
		}
		
		PosTweener tweener = new PosTweener( this, to, d.length() / speed );
		tweener.listener = this;
		parent.add( tweener );
	}

	@Override
	public void onComplete( Tweener tweener ) {
		kill();
		if (callback != null) {
			callback.call();
		}
	}
	public static void show(JSONObject actionObj) throws JSONException {
		MissileSprite sprite = (MissileSprite) GameScene.recycleSprite( MissileSprite.class );
		if (sprite == null){
			return;
		}

		Glowing glowing = null;
		if (!actionObj.isNull("item_glowing")) {
			glowing = new Glowing(actionObj.getJSONObject("item_glowing"));
		}
		sprite.reset(
				actionObj.getInt("from"),
				actionObj.getInt("to"),
				actionObj.getDouble("speed"),
				actionObj.getDouble("angular_speed"),
				actionObj.getDouble("angle"),
				JsonStringHelper.optString(actionObj, "item_sprite_sheet", Assets.Sprites.ITEMS),
				actionObj.getInt("item_image"),
				glowing
		);
	}
	private void reset(int from, int to, double SPEED, double angular_speed, double angle, String spriteSheet, int image, Glowing glowing) {
		revive();
		if (isConnectedToOldServer()){
			image = TranslationUtils.translateItemImage(image);
			view(Assets.Sprites.ITEMS, image, glowing);
		} else {
			view(spriteSheet, image, glowing);
		}
		this.callback = null;

		point( DungeonTilemap.tileToWorld( from ) );
		PointF dest = DungeonTilemap.tileToWorld( to );

		PointF d = PointF.diff( dest, point() );
		this.speed.set( d ).normalize().scale( (float)SPEED );

		this.angularSpeed = (float)angular_speed;
		this.angle = (float) angle;

		PosTweener tweener = new PosTweener( this, dest, d.length() / (float)SPEED );
		tweener.listener = this;
		parent.add( tweener );
	}

	public void reset(PointF from, PointF to, float SPEED, float angularSpeed, float angle, boolean flipHorizontal, Item item) {
		revive();

		if (item == null) {
			view(0, null);
		} else {
			view(item);
		}
		this.callback = null;

		originToCenter();

		// Adjust points so they work with the center of the missile sprite, not the corner
		from.x -= width() / 2;
		to.x -= width() / 2;
		from.y -= height() / 2;
		to.y -= height() / 2;

		point(from);

		PointF d = PointF.diff(to, from);
		this.speed.set(d).normalize().scale(SPEED);

		this.angularSpeed = angularSpeed;
		this.angle = angle;
		this.flipHorizontal = flipHorizontal;
		updateFrame();

		PosTweener tweener = new PosTweener(this, to, d.length() / SPEED);
		tweener.listener = this;
		parent.add(tweener);
	}

}
