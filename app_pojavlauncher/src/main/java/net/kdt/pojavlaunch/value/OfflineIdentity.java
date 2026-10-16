package net.kdt.pojavlaunch.value;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Offline identity rules; no network or Android services are required. */
public final class OfflineIdentity {
    private OfflineIdentity() {}

    public static boolean isValidUsername(String username) {
        return username != null && username.matches("[a-zA-Z0-9_]{3,16}");
    }

    public static String profileId(String username) {
        if (!isValidUsername(username)) throw new IllegalArgumentException("Invalid offline username");
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8))
                .toString().replace("-", "");
    }
}
