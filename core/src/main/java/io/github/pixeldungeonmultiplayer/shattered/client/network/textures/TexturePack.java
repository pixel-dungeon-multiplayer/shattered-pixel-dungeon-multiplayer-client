package io.github.pixeldungeonmultiplayer.shattered.client.network.textures;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.files.FileHandleStream;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Point;

import io.github.pixeldungeonmultiplayer.shattered.client.network.utils.JavaUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class TexturePack {
    private boolean isServerTexturePack = false;
    ZipFile file;
    String name;
    String description;
    String version;

    Point frameSize;
    public static List<String> extractedFiles = new ArrayList<>();
    public TexturePack(InputStream stream, boolean isServerTexturePack) throws IOException {
        this.isServerTexturePack = isServerTexturePack;
        try {
            File tmpFile = File.createTempFile("texturePack-", ".zip");
            tmpFile.deleteOnExit();
            FileOutputStream filestream = new FileOutputStream(tmpFile);
            byte[] buf = new byte[8192];
            int length;
            while ((length = stream.read(buf)) != -1) {
                filestream.write(buf, 0, length);
            }
            file = new ZipFile(tmpFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadTexturePack();
    }

    public TexturePack(ZipFile file) throws IOException {
        this.file = file;
        loadTexturePack();
    }

    public void unload() {
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isServerTexturePack() {
        return isServerTexturePack;
    }

    private void loadManifest() throws IOException {
        try {
            JSONObject reader = new JSONObject(
                    JavaUtils.StringFromInputStream(
                            file.getInputStream(
                                    file.getEntry("manifest.json")
                            )
                    )
            );
            name = JsonStringHelper.optString(reader, "name", file.getName());
            description = JsonStringHelper.optString(reader, "description", "No description");
            version = JsonStringHelper.optString(reader, "version", "0.0.0");
            frameSize = new Point(
                    reader.optInt("frame_size_x", 16),
                    reader.optInt("frame_size_y", 16)
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadTexturePack() throws IOException {
        loadManifest();
        Enumeration<? extends ZipEntry> entries = file.entries();
        ZipEntry entry;
        while (entries.hasMoreElements()){
            entry = entries.nextElement();
            if (entry.getName().startsWith("sounds/") && (entry.getName().endsWith(".mp3") || entry.getName().endsWith(".ogg"))){
                ZipEntry finalEntry = entry;
                Game.runOnRenderThread( ()-> {
                            Sample.INSTANCE.unload(finalEntry.getName());
                            Sample.INSTANCE.load(finalEntry.getName());
                        });
            }
        }
    }

    public boolean hasAsset(String src) {
        return (file.getEntry("assets/" + src) != null);
    }
    public boolean hasSound(String src){
        return (file.getEntry("sounds/" + src) != null);
    }
    public Sound getSound(String src){
        try {
            if (DeviceCompat.isDesktop()) {
                return Gdx.audio.newSound(new FileHandleByteStream(src, file.getInputStream(file.getEntry("sounds/" + src))));
            } else {
                return Gdx.audio.newSound(getExtracted("sounds/" + src));
            }
        } catch (IOException e) {
            Gdx.app.error("TexturePack", "Failed to load sound", e);
        }
        return null;
    }
    public boolean hasMusic(String src) {
        return (file.getEntry("music/" + src) != null);
    }
    public Music getMusic(String src){
        try {
            if (DeviceCompat.isDesktop()) {
                return Gdx.audio.newMusic(new FileHandleByteStream(src, file.getInputStream(file.getEntry("music/" + src))));
            } else {
                return Gdx.audio.newMusic(getExtracted("music/" + src));
            }
        } catch (IOException e) {
            Gdx.app.error("TexturePack", "Failed to load music", e);
        }
        return null;
    }
    private boolean isExtracted(String src){
        return (extractedFiles.contains(name +"-" +  src));
    }
    private FileHandle getExtracted(String src){
        if (!isExtracted(src)){
            extract(src);
        }
        return Gdx.files.local(name + "-" + src);
    }
    //on iOS and Android audio files need to be extracted out of zip to be loaded
    private void extract(String src){
        extractedFiles.add(src);
        FileHandle fileHandle = Gdx.files.local(name + "-" + src);
        fileHandle.write(getStream(src), false);
        fileHandle.file().deleteOnExit();
    }

    protected InputStream getStream(String path) {
        ZipEntry entry = file.getEntry(path);
        if (entry == null) {
            return null;
        }
        try {
            return file.getInputStream(entry);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputStream getAssetStream(String src) {
        return getStream("assets/" + src);
    }

    public InputStream getAnimationStream(String animationsFile) {
        return getStream("animations/" + animationsFile);
    }


    //optimisation for desktop
    private static class FileHandleByteStream extends FileHandleStream {

        private final InputStream data;

        public FileHandleByteStream(String path, InputStream data) {
            super(path);
            this.data = data;
        }

        @Override
        public InputStream read() {
            return data;
        }

        public void close() throws Exception {
            data.close();
        }
    }
}