package io.github.pixeldungeonmultiplayer.common.localizedstring;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import org.jetbrains.annotations.CheckReturnValue;
import org.json.JSONArray;
import org.json.JSONString;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

public abstract class LocalizedString implements JSONString {

    static final Object[] EMPTY_OBJECTS = new Object[0];

    public static final LocalizedString EMPTY = LocalizedString.raw("");

    public enum Mode {
        KEY,
        RAW,
        TRANSFORM,
        CONCAT,
        TRUNCATE,
        REPLACE,
        DECIMAL_FORMAT
    }

    public enum Transform {
        CAPITALIZE,
        TITLE_CASE,
        UPPER_CASE,
        LOWER_CASE;

        String resolve(String text) {
            switch (this) {
                case CAPITALIZE:
                    return Messages.resolveCapitalize(text);
                case TITLE_CASE:
                    return Messages.resolveTitleCase(text);
                case UPPER_CASE:
                    return Messages.resolveUpperCase(text);
                case LOWER_CASE:
                    return Messages.resolveLowerCase(text);
                default:
                    return text;
            }
        }
    }

    LocalizedString() {
    }

    @CheckReturnValue
    public static LocalizedString key(LocalizedKey key, Object... args) {
        return new KeyLocalizedString(key, args);
    }

    @CheckReturnValue
    public static LocalizedString raw(String raw, Object... args) {
        return new RawLocalizedString(raw, args);
    }

    @CheckReturnValue
    public static LocalizedString transform(Transform transform, LocalizedString text) {
        return new TransformedLocalizedString(transform, text);
    }

    @CheckReturnValue
    public static LocalizedString truncate(LocalizedString text, int maxLength, String ellipsis) {
        return new TruncatedLocalizedString(text, maxLength, ellipsis);
    }

    @CheckReturnValue
    public LocalizedString replace(char oldChar, char newChar) {
        return new ReplacedLocalizedString(this, oldChar, newChar);
    }

    @CheckReturnValue
    public static LocalizedString concat(Object... parts) {
        return ConcatLocalizedString.of(parts);
    }

    @CheckReturnValue
    public static LocalizedString decimalFormat(String format, double number) {
        return new DecimalFormatLocalizedString(format, number);
    }

    @CheckReturnValue
    public abstract Mode mode();

    @CheckReturnValue
    public final String resolve() {
        return resolveInternal();
    }

    abstract String resolveInternal();

    public final JSONObject toJsonObject() {
        return toJsonObjectInternal();
    }

    abstract JSONObject toJsonObjectInternal();

    @CheckReturnValue
    public LocalizedString toUpperCase(Locale locale) {
        return transform(Transform.UPPER_CASE, this);
    }

    @CheckReturnValue
    public boolean isEmpty() {
        return equals(EMPTY);
    }

    @Override
    public final String toString() {
        return resolve();
    }

    @Override
    public final String toJSONString() {
        return toJsonObject().toString();
    }

    static Object[] copy(Object[] values) {
        return values == null || values.length == 0 ? EMPTY_OBJECTS : Arrays.copyOf(values, values.length);
    }

    static Object[] resolveArgs(Object[] args) {
        Object[] resolved = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            resolved[i] = args[i] instanceof LocalizedString ? ((LocalizedString) args[i]).resolveInternal() : args[i];
        }
        return resolved;
    }

    static JSONObject keyToJson(LocalizedKey key) {
        JSONObject object = new JSONObject();
        object.put("type", "localized_key");
        String[] ownerClasses = key.ownerClasses();
        if (ownerClasses != null && ownerClasses.length > 0) {
            JSONArray owners = new JSONArray();
            for (String ownerClass : ownerClasses) {
                owners.put(ownerClass);
            }
            object.put("owner", owners);
        }
        object.put("name", key.name());
        return object;
    }

    static Object toJsonValue(Object value) {
        if (value instanceof LocalizedString) {
            return ((LocalizedString) value).toJsonObject();
        }
        return value == null ? JSONObject.NULL : value;
    }

    static JSONArray argsToJson(Object[] args) {
        JSONArray array = new JSONArray();
        for (Object arg : args) {
            array.put(toJsonValue(arg));
        }
        return array;
    }
}
