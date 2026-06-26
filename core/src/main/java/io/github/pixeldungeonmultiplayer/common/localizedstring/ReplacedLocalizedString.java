package io.github.pixeldungeonmultiplayer.common.localizedstring;

import org.json.JSONObject;

import java.util.Objects;

final class ReplacedLocalizedString extends LocalizedString {

    private final LocalizedString text;
    private final char oldChar;
    private final char newChar;

    ReplacedLocalizedString(LocalizedString text, char oldChar, char newChar) {
        this.text = text;
        this.oldChar = oldChar;
        this.newChar = newChar;
    }

    @Override
    public Mode mode() {
        return Mode.REPLACE;
    }

    @Override
    String resolveInternal() {
        return text.resolveInternal().replace(oldChar, newChar);
    }

    @Override
    JSONObject toJsonObjectInternal() {
        JSONObject object = new JSONObject();
        object.put("type", "replace");
        object.put("text", text.toJsonObject());
        object.put("old_char", String.valueOf(oldChar));
        object.put("new_char", String.valueOf(newChar));
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof ReplacedLocalizedString
                && oldChar == ((ReplacedLocalizedString) obj).oldChar
                && newChar == ((ReplacedLocalizedString) obj).newChar
                && Objects.equals(text, ((ReplacedLocalizedString) obj).text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode(), text, oldChar, newChar);
    }
}
