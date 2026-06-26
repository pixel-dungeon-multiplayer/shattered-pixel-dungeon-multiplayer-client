package io.github.pixeldungeonmultiplayer.common.localizedstring;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

final class ConcatLocalizedString extends LocalizedString {

    private final Object[] parts;

    private ConcatLocalizedString(Object[] parts) {
        this.parts = copy(parts);
    }

    static LocalizedString of(Object[] parts) {
        ArrayList<Object> flattened = new ArrayList<>();
        flatten(flattened, parts);
        return new ConcatLocalizedString(flattened.toArray(new Object[0]));
    }

    private static void flatten(ArrayList<Object> flattened, Object[] parts) {
        if (parts == null) {
            return;
        }
        for (Object part : parts) {
            if (part instanceof ConcatLocalizedString) {
                flatten(flattened, ((ConcatLocalizedString) part).parts);
            } else {
                flattened.add(part);
            }
        }
    }

    @Override
    public Mode mode() {
        return Mode.CONCAT;
    }

    @Override
    String resolveInternal() {
        StringBuilder result = new StringBuilder();
        for (Object part : parts) {
            if (part instanceof LocalizedString) {
                result.append(((LocalizedString) part).resolveInternal());
            } else if (part != null) {
                result.append(part);
            }
        }
        return result.toString();
    }

    @Override
    JSONObject toJsonObjectInternal() {
        JSONObject object = new JSONObject();
        object.put("type", "concat");
        object.put("parts", argsToJson(parts));
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof ConcatLocalizedString
                && Arrays.equals(parts, ((ConcatLocalizedString) obj).parts);
    }

    @Override
    public int hashCode() {
        return 31 * mode().hashCode() + Arrays.hashCode(parts);
    }
}
