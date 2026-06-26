package io.github.pixeldungeonmultiplayer.common.localizedstring;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

final class KeyLocalizedString extends LocalizedString {

    private final LocalizedKey key;
    private final Object[] args;

    KeyLocalizedString(LocalizedKey key, Object[] args) {
        this.key = key;
        this.args = copy(args);
    }

    @Override
    public Mode mode() {
        return Mode.KEY;
    }

    @Override
    String resolveInternal() {
        return Messages.resolve(key, resolveArgs(args));
    }

    @Override
    JSONObject toJsonObjectInternal() {
        JSONObject object = new JSONObject();
        object.put("type", "key");
        object.put("key", keyToJson(key));
        object.put("args", argsToJson(args));
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof KeyLocalizedString
                && Objects.equals(key, ((KeyLocalizedString) obj).key)
                && Arrays.equals(args, ((KeyLocalizedString) obj).args);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(mode(), key) + Arrays.hashCode(args);
    }
}
