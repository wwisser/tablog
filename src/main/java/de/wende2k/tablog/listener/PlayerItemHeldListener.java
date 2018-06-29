package de.wende2k.tablog.listener;

import de.wende2k.tablog.Tablog;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerItemHeldListener implements Listener {

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Map<UUID, Integer> enabledPlayers = Tablog.getInstance().getEnabledPlayers();
        UUID uuid = event.getPlayer().getUniqueId();
        int currentLine = enabledPlayers.get(uuid);

        if (enabledPlayers.containsKey(uuid)) {
            if (event.getNewSlot() > event.getPreviousSlot()) {
                if (currentLine < (Tablog.AMOUNT_OF_LINES - 1)) {
                    enabledPlayers.put(uuid, ++currentLine);
                }
            } else {
                enabledPlayers.put(uuid, --currentLine);
            }
        }
    }
}
