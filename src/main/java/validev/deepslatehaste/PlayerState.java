package validev.deepslatehaste;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerState {

    private static final Map<UUID, Boolean> ENABLED = new HashMap<>();

    public static boolean isEnabled(UUID playerUuid) {
        return ENABLED.getOrDefault(playerUuid, false);
    }

    public static void setEnabled(UUID playerUuid, boolean enabled) {
        ENABLED.put(playerUuid, enabled);
    }

    public static void remove(UUID playerUuid) {
        ENABLED.remove(playerUuid);
    }
}