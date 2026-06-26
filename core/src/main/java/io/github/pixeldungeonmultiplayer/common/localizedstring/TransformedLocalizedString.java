package io.github.pixeldungeonmultiplayer.common.localizedstring;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;

final class TransformedLocalizedString extends LocalizedString {

    private final Transform transform;
    private final LocalizedString text;

    TransformedLocalizedString(Transform transform, LocalizedString text) {
        this.transform = transform;
        this.text = text;
    }

    @Override
    public Mode mode() {
        return Mode.TRANSFORM;
    }

    @Override
    String resolveInternal() {
        return transform.resolve(text.resolveInternal());
    }

    @Override
    JSONObject toJsonObjectInternal() {
        JSONObject object = new JSONObject();
        object.put("type", "transform");
        object.put("transform", transform.name().toLowerCase(Locale.ENGLISH));
        object.put("text", text.toJsonObject());
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof TransformedLocalizedString
                && transform == ((TransformedLocalizedString) obj).transform
                && Objects.equals(text, ((TransformedLocalizedString) obj).text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode(), transform, text);
    }
}
