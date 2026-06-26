package io.github.pixeldungeonmultiplayer.common.localizedstring;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import org.json.JSONObject;
import java.util.Objects;

final class DecimalFormatLocalizedString extends LocalizedString {

    private final String format;
    private final double number;

    DecimalFormatLocalizedString(String format, double number) {
        this.format = format;
        this.number = number;
    }

    @Override
    public Mode mode() {
        return Mode.DECIMAL_FORMAT;
    }

    @Override
    String resolveInternal() {
        return Messages.decimalFormat(format, number);
    }

    @Override
    JSONObject toJsonObjectInternal() {
        JSONObject object = new JSONObject();
        object.put("type", "decimal_format");
        object.put("format", format);
        object.put("number", number);
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof DecimalFormatLocalizedString
                && Double.compare(((DecimalFormatLocalizedString) obj).number, number) == 0
                && Objects.equals(format, ((DecimalFormatLocalizedString) obj).format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode(), format, number);
    }
}
