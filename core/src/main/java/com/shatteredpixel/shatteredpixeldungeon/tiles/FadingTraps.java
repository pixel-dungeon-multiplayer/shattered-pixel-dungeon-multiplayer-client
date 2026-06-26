package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.tweeners.AlphaTweener;

import java.util.Arrays;

public class FadingTraps extends CustomTilemap {

    {
        texture = Assets.Environment.TERRAIN_FEATURES;
    }

    @Override
    public Tilemap create() {
        Tilemap v = super.create();
        if (data == null) {
            data = new int[tileW * tileH];
            Arrays.fill(data, -1);
            v.map(data, tileW);
            v.alpha(alpha);
        }
        return v;
    }

    @Override
    public String name(int tileX, int tileY) {
        int cell = (this.tileX + tileX) + Dungeon.level.width() * (this.tileY + tileY);
        if (Dungeon.level.traps.get(cell) != null) {
            return Messages.titleCase(Dungeon.level.traps.get(cell).name());
        }
        return super.name(tileX, tileY);
    }

    @Override
    public String desc(int tileX, int tileY) {
        int cell = (this.tileX + tileX) + Dungeon.level.width() * (this.tileY + tileY);
        if (Dungeon.level.traps.get(cell) != null) {
            return Dungeon.level.traps.get(cell).desc();
        }
        return super.desc(tileX, tileY);
    }

    public void fade() {
        runFadeAction();
    }

    private void runFadeAction() {
        Dungeon.level.customTiles.remove(this);
        if (vis != null && vis.parent != null) {
            vis.parent.add(new AlphaTweener(vis, 0f, 2f) {
                @Override
                protected void onComplete() {
                    super.onComplete();
                    finishFade();
                }
            });
        } else {
            finishFade();
        }
    }

    private void finishFade() {
        if (vis != null) {
            vis.killAndErase();
        }
    }
}
