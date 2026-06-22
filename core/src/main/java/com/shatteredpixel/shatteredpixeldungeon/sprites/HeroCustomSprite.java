/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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


import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.RectF;

public class HeroCustomSprite extends CharSprite implements TieredSprite, ClassSprite{
    private int tier = 1;
    private HeroClass heroClass = HeroClass.NONE;
    private static final int FRAME_WIDTH	= 12;
    private static final int FRAME_HEIGHT	= 15;

    private static final int RUN_FRAMERATE	= 20;

    private static TextureFilm tiers;

    private Animation fly;
    private Animation read;

    public HeroCustomSprite() {
        super();

        texture( heroClass.spritesheet() );
        updateArmor();
        idle();
    }

    public void updateArmor() {

        TextureFilm film = new TextureFilm( tiers(), tier, FRAME_WIDTH, FRAME_HEIGHT );

        idle = new Animation( 1, true );
        idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

        run = new Animation( RUN_FRAMERATE, true );
        run.frames( film, 2, 3, 4, 5, 6, 7 );

        die = new Animation( 20, false );
        die.frames( film, 8, 9, 10, 11, 12, 11 );

        attack = new Animation( 15, false );
        attack.frames( film, 13, 14, 15, 0 );

        zap = attack.clone();

        operate = new Animation( 8, false );
        operate.frames( film, 16, 17, 16, 17 );

        fly = new Animation( 1, true );
        fly.frames( film, 18 );

        read = new Animation( 20, false );
        read.frames( film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19 );
    }
    public void updateTier( int tier ){
        this.tier = tier;
        updateArmor();
    }
    public void updateHeroClass(HeroClass heroClass){
        this.heroClass = heroClass;
        texture(heroClass.spritesheet());
    }

    @Override
    public void place( int p ) {
        super.place( p );
    }

    @Override
    public void move( int from, int to ) {
        super.move( from, to );
        if (ch.flying) {
            play( fly );
        }
    }

    @Override
    public void jump( int from, int to, Callback callback ) {
        super.jump( from, to, callback );
        play( fly );
    }

    public void read() {
        play( read );
    }

    @Override
    public void update() {

        super.update();
    }

    public boolean sprint( boolean on ) {
        run.delay = on ? 0.625f / RUN_FRAMERATE : 1f / RUN_FRAMERATE;
        return on;
    }

    public static TextureFilm tiers() {
        if (tiers == null) {
            SmartTexture texture = TextureCache.get(Assets.Sprites.ROGUE );
            tiers = new TextureFilm( texture, texture.width, FRAME_HEIGHT );
        }

        return tiers;
    }

    public static Image avatar(HeroClass cl, int armorTier ) {

        RectF patch = tiers().get( armorTier );
        Image avatar = new Image( cl.spritesheet() );
        RectF frame = avatar.texture.uvRect( 1, 0, FRAME_WIDTH, FRAME_HEIGHT );
        //TODO: check this
        frame.right += patch.left;
        frame.bottom += patch.top;
        //frame.offset( patch.left, patch.top );
        avatar.frame( frame );

        return avatar;
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (ch == null) return;
        if (ch.isAlive()){
            idle();
        } else {
            die();
        }
    }

    @Override
    public int tier() {
        return tier;
    }

    @Override
    public void tier(int tier) {
        this.tier = tier;
    }

    @Override
    public void updateTier() {
        updateArmor();
    }

    @Override
    public HeroClass heroClass() {
        return heroClass;
    }

    @Override
    public void heroClass(HeroClass heroClass) {
        this.heroClass = heroClass;
    }

    @Override
    public void updateHeroClass() {
        updateArmor();
    }
}