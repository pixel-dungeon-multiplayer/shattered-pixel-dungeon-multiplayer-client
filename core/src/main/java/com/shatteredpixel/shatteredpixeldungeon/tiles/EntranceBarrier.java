package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;

public class EntranceBarrier extends CustomTilemap {

    @Override
    public Tilemap create() {
        if (vis != null && vis.alive) vis.killAndErase();
        vis = new Tilemap(texture, new TextureFilm(texture, SIZE, SIZE)) {
            @Override
            protected NoosaScript script() {
                return NoosaScript.get();
            }

            @Override
            public void update() {
                alpha(0.3f + 0.3f*(float)Math.sin(Game.timeTotal));
                super.update();
            }
        };
        vis.x = tileX*SIZE;
        vis.y = tileY*SIZE;
        if (data != null) {
            vis.map(data, cols > 0 ? cols : tileW);
            vis.alpha(alpha);
        }
        return vis;
    }

    @Override
    public Image image(int tileX, int tileY) {
        return null;
    }
}
