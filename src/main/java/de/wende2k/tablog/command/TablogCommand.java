package de.wende2k.tablog.command;

import de.wende2k.tablog.Tablog;
import de.wende2k.tablog.util.TablistUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class TablogCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            UUID uuid = ((Player) commandSender).getUniqueId();
            Tablog instance = Tablog.getInstance();
            BukkitRunnable runnable = instance.getRunnable();
            Map<UUID, Integer> enabledPlayers = instance.getEnabledPlayers();

            if (enabledPlayers.containsKey(uuid)) {
                enabledPlayers.remove(uuid);
                TablistUtils.send((Player) commandSender, "", "");
                commandSender.sendMessage("§7You have §cdisabled §7your Tablog.");

                if (enabledPlayers.size() < 1) {
                    runnable.cancel();
                    instance.setRunnable(null);
                }
            } else {
                if (runnable == null) {
                    instance.refreshRunnable().runTaskTimer(instance, 0L, 3L);
                }
                enabledPlayers.put(uuid, 0);
                commandSender.sendMessage("§7You have §aenabled §7your Tablog!");
            }
        } else {
            commandSender.sendMessage("You must be a player to be able to use this command.");
        }
        return true;
    }

}
