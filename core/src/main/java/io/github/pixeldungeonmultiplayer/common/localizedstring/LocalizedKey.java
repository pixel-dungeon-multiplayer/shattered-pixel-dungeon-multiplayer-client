package io.github.pixeldungeonmultiplayer.common.localizedstring;

import java.util.Arrays;
import java.util.Objects;

public class LocalizedKey {

    private final String[] ownerClasses;
    private final String name;

    public LocalizedKey(String ownerClass, String name) {
        this(ownerClass == null ? null : new String[]{ownerClass}, name);
    }

    public LocalizedKey(String[] ownerClasses, String name) {
        this.ownerClasses = ownerClasses == null ? null : Arrays.copyOf(ownerClasses, ownerClasses.length);
        this.name = name;
    }

    public String ownerClass() {
        return ownerClasses == null || ownerClasses.length == 0 ? null : ownerClasses[0];
    }

    public String[] ownerClasses() {
        return ownerClasses == null ? null : Arrays.copyOf(ownerClasses, ownerClasses.length);
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalizedKey)) {
            return false;
        }
        LocalizedKey other = (LocalizedKey) obj;
        return Arrays.equals(ownerClasses, other.ownerClasses)
                && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(ownerClasses) + Objects.hashCode(name);
    }
}
