package io.github.pixeldungeonmultiplayer.shattered.client.network.textures;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import com.watabou.gltextures.TextureCache;
import com.watabou.gltextures.TextureManagerInterface;
//import com.watabou.gltextures.TextureManagerInterface;

import io.github.pixeldungeonmultiplayer.shattered.client.network.utils.JavaUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextureManager extends TextureManagerInterface {
    public static TextureManager INSTANCE = new TextureManager();
    private final LinkedHashMap<String,TexturePack> texturePacks = new LinkedHashMap<String, TexturePack>();
    private final LinkedHashMap<String, File> cachedTextureFiles = new LinkedHashMap<>();

    TextureManager()
    {
        TextureManagerInterface.INSTANCE = this;
    }

    @Override
    public void loadTexturePack(InputStream stream) throws IOException {
        TexturePack texturePack = new TexturePack(stream, true);
        texturePacks.put(texturePack.name, texturePack);
        updateTextureCache();
    }

    @Override
    public boolean hasAsset(String src) {
        for (TexturePack texturePack : texturePacks.values())
        {
            if (texturePack.hasAsset(src))
                return true;
        }
        return false;
    }

    @Override
    public InputStream getAssetStream(String s) {
        for (TexturePack texturePack : texturePacks.values())
        {
            InputStream stream = texturePack.getAssetStream(s);
            if (stream != null)
            {
                return stream;
            }
        }
        return null;
    }


    @Override
    public File getAssetFile(String s) {
        if (cachedTextureFiles.containsKey(s)){
            return cachedTextureFiles.get(s);
        }
        try {
            try (InputStream inputStream = getAssetStream(s)) {
                if (inputStream == null) {
                    cachedTextureFiles.put(s, null);
                    return null;
                }
                File file = File.createTempFile("TextureManager-", "-asset");
                file.deleteOnExit();
                try (OutputStream outputStream = new FileOutputStream(file)) {

                    byte[] buff = new byte[1024 * 1024 * 2];
                    int count = inputStream.read(buff);
                    while (count > 0) {
                        outputStream.write(buff, 0, count);
                        count = inputStream.read(buff);
                    }
                }
                cachedTextureFiles.put(s, file);
                return file;
            }
        } catch (IOException e) {
            Log.e("TextureManager", String.format("Io Exception during loading %s: %s", s, e));
            return null;
        }
    }

    @Override
    public boolean hasSound(String src) {
        for (TexturePack texturePack : texturePacks.values())
        {
            if (texturePack.hasSound(src))
                return true;
        }
        return false;
    }

    @Override
    public Sound getSound(String src) {
        for (TexturePack texturePack: texturePacks.values()){
            if (texturePack.hasSound(src)){
                return texturePack.getSound(src);
            }
        }
        return null;
    }
    @Override
    public boolean hasMusic(String src) {
        for (TexturePack texturePack : texturePacks.values())
        {
            if (texturePack.hasMusic(src))
                return true;
        }
        return false;
    }

    @Override
    public Music getMusic(String src) {
        for (TexturePack texturePack: texturePacks.values()){
            if (texturePack.hasMusic(src)){
                return texturePack.getMusic(src);
            }
        }
        return null;
    }
    public JSONObject getAnimationsJsonObject(String animationsFile) {
        for (TexturePack texturePack : texturePacks.values())
        {
            InputStream stream = texturePack.getAnimationStream(animationsFile);
            if (stream != null)
            {
                JSONObject object = JavaUtils.JSONObjectFromInputStream(stream);
                if (object != null) {
                    return object;
                }
            }
        }
        return null;
    }

    public void unloadServerTexturePacks() {
        boolean invalidateChache = false;
        for (Iterator<Map.Entry<String, TexturePack>> it = texturePacks.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<String, TexturePack> entry = it.next();
            if (entry.getValue().isServerTexturePack())
            {
                entry.getValue().unload();
                it.remove();
                invalidateChache = true;
            }
        }
        if (invalidateChache)
        {
            updateTextureCache();
        }
    }
    
    protected void updateTextureCache()
    {
        cachedTextureFiles.clear();
        TextureCache.reloadFromAssets();
    }
}