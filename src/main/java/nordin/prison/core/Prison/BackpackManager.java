package nordin.prison.core.Prison;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackpackManager {
    private final Map<UUID, Backpack> playerBackpacks = new HashMap<>();

    public Backpack getBackpack(UUID uuid) {
        return playerBackpacks.computeIfAbsent(uuid, id -> new Backpack());
    }
}
