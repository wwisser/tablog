package de.wende2k.tablog.api;

import de.wende2k.tablog.Tablog;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.util.UUID;

@UtilityClass
public class TablogApi {

    public boolean hasEnabled(Player player) {
        return Tablog.getInstance().getEnabledPlayers().containsKey(player.getUniqueId());
    }

    public boolean hasEnabled(UUID uuid) {
        return Tablog.getInstance().getEnabledPlayers().containsKey(uuid);
    }

}
