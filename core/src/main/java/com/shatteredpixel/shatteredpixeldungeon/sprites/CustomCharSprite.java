package com.shatteredpixel.shatteredpixeldungeon.sprites;

import static io.github.pixeldungeonmultiplayer.shattered.client.network.utils.JavaUtils.hasNotNull;

import io.github.pixeldungeonmultiplayer.shattered.client.network.utils.JavaUtils;
import io.github.pixeldungeonmultiplayer.shattered.client.network.textures.TextureManager;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.TextureFilm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomCharSprite extends MobSprite {

    Map<String,Animation> animations = new HashMap<>(4);
    int frameSizeX,FrameSizeY;
    String spriteAsset = null;

    public CustomCharSprite(final String animationsFile)
    {
        spriteAsset = animationsFile;
        JSONObject animationsObj = TextureManager.INSTANCE.getAnimationsJsonObject(animationsFile);
        Objects.requireNonNull(animationsObj, "Null animation obj for animation file:" + animationsFile);
        try {
            if (JavaUtils.hasNotNull(animationsObj,"default_texture"))
            {
                String textureAsset = JsonStringHelper.getString(animationsObj, "default_texture");
                texture(textureAsset);
            }
            if (JavaUtils.hasNotNull(animationsObj,"default_frame_size_x"))
            {
                frameSizeX = animationsObj.getInt("default_frame_size_x");
                FrameSizeY = animationsObj.getInt("default_frame_size_y");
            }
            JSONArray animationsArr = animationsObj.getJSONArray("animations");
            int len = animationsArr.length();
            for (int i= 0; i < len; i++)
            {
                addAnimation(animationsArr.getJSONObject(i));
            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addAnimation(final JSONObject animationObj) throws JSONException {
        SmartTexture animationTexture = texture;
        if (hasNotNull(animationObj, "texture")) {
            String textureAsset = JsonStringHelper.getString(animationObj, "texture");
            texture = TextureCache.get(textureAsset);
        }
        assert animationTexture != null;
        //Asserts.checkNotNull(animationTexture);

        String name = JsonStringHelper.getString(animationObj, "animation");
        boolean looped = animationObj.getBoolean("looped");
        JSONArray framesObj = animationObj.getJSONArray("frames");
        int fps = animationObj.getInt("fps");
        Animation animation = new Animation(fps, looped);
        int frameSizeX = animationObj.optInt("frame_size_x", this.frameSizeX);
        int frameSizeY = animationObj.optInt("frame_size_y", this.FrameSizeY);

        TextureFilm textureFilm = new TextureFilm(animationTexture, frameSizeX, frameSizeY);
        animation.frames(textureFilm, JavaUtils.JSONArrayToIntegerClassArray(framesObj));

        animations.put(name,animation);

        switch (name) {
            case ("idle"): {
                idle = animation;
                break;
            }
            case ("run"):
            {
                run = animation;
                break;
            }
            case ("attack"):
            {
                attack = animation;
                break;
            }
            case ("operate"):
            {
                operate = animation;
                break;
            }
            case ("zap"):
            {
                zap = animation;
                break;
            }
            case ("die"):
            {
                die = animation;
                break;
            }
        }
    }

    public CustomCharSprite() {
        //This constructor exists because LibGDX reflection would throw an exception
    }

    public String getSpriteAsset() {
        return spriteAsset;
    }
}