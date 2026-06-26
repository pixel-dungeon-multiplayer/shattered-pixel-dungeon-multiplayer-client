package io.github.pixeldungeonmultiplayer.common.localizedstring;

import io.github.pixeldungeonmultiplayer.shattered.client.network.utils.Utils;
import org.json.JSONObject;

import java.util.Objects;

final class TruncatedLocalizedString extends LocalizedString {

    private final LocalizedString text;
    private final int maxLength;
    private final String ellipsis;

    TruncatedLocalizedString(LocalizedString text, int maxLength, String ellipsis) {
        this.text = text;
        this.maxLength = maxLength;
        this.ellipsis = ellipsis;
    }

    @Override
    public Mode mode() {
        return Mode.TRUNCATE;
    }

    @Override
    String resolveInternal() {
        return Utils.truncate(text.resolveInternal(), maxLength, ellipsis);
    }

    @Override
    JSONObject toJsonObjectInternal() {
        JSONObject object = new JSONObject();
        object.put("type", "truncate");
        object.put("text", text.toJsonObject());
        object.put("max_length", maxLength);
        object.put("ellipsis", ellipsis);
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof TruncatedLocalizedString
                && maxLength == ((TruncatedLocalizedString) obj).maxLength
                && Objects.equals(text, ((TruncatedLocalizedString) obj).text)
                && Objects.equals(ellipsis, ((TruncatedLocalizedString) obj).ellipsis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode(), text, maxLength, ellipsis);
    }
}
