package io.github.pixeldungeonmultiplayer.common.localizedstring;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

final class RawLocalizedString extends LocalizedString {

    private final String raw;
    private final Object[] args;

    RawLocalizedString(String raw, Object[] args) {
        this.raw = raw;
        this.args = copy(args);
    }

    @Override
    public Mode mode() {
        return Mode.RAW;
    }

    @Override
    String resolveInternal() {
        Object[] resolvedArgs = resolveArgs(args);
        return resolvedArgs.length > 0 ? Messages.resolveFormat(raw, resolvedArgs) : raw;
    }

    @Override
    JSONObject toJsonObjectInternal() {
        JSONObject object = new JSONObject();
        object.put("type", "raw");
        object.put("raw", raw);
        object.put("args", argsToJson(args));
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof RawLocalizedString
                && Objects.equals(raw, ((RawLocalizedString) obj).raw)
                && Arrays.equals(args, ((RawLocalizedString) obj).args);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(mode(), raw) + Arrays.hashCode(args);
    }
}
