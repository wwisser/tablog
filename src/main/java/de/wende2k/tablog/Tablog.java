package de.wende2k.tablog;

import de.wende2k.tablog.util.TablistUtils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Plugin(name = "Tablog", version = "1.0-SNAPSHOT")
@Author(name = "Wende2k")
@Description(desc = "Allows you to shows the server log in tab.")
@Command(name = "tablog", aliases = {"tl"}, permission = "tablog.use")
@Permission(name = "tablog.use", desc = "Allows the usage of Tablog", defaultValue = PermissionDefault.OP)
public class Tablog extends JavaPlugin {

    private static final File LOG_FILE = new File("logs/latest.log");
    private static final int AMOUNT_OF_LINES = 25;
    private static final String PLACEHOLDER = "§r\n§r\n§r\n§7\n";

    private BukkitRunnable runnable;
    private List<UUID> enabledPlayers;

    @Override
    public void onEnable() {
        this.enabledPlayers = new ArrayList<>();

        super.getCommand("tablog").setExecutor((commandSender, command, label, args) -> {
            if (commandSender instanceof Player) {
                UUID uuid = ((Player) commandSender).getUniqueId();

                if (this.enabledPlayers.contains(uuid)) {
                    this.enabledPlayers.remove(uuid);
                    TablistUtils.send((Player) commandSender, "", "");
                    commandSender.sendMessage("§7You have §cdisabled §7your Tablog.");

                    if (this.enabledPlayers.size() < 1) {
                        this.runnable.cancel();
                        this.runnable = null;
                    }
                } else {
                    if (this.runnable == null) {
                        this.initRunnable().runTaskTimer(this, 0L, 3L);
                    }
                    this.enabledPlayers.add(uuid);
                    commandSender.sendMessage("§7You have §aenabled §7your Tablog!");
                }
            } else {
                commandSender.sendMessage("You must be a player to be able to use this command.");
            }
            return true;
        });
    }

    private BukkitRunnable initRunnable() {
        return (this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Tablog.this.enabledPlayers.forEach(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);

                    if (player != null && player.isOnline()) {
                        TablistUtils.send(player, PLACEHOLDER + Tablog.this.getTail() + PLACEHOLDER + PLACEHOLDER,
                                "§cTablog Plugin by Wende2k");
                    }
                });
            }
        });
    }

    @SneakyThrows
    private String getTail() {
        RandomAccessFile fileHandler = new RandomAccessFile(LOG_FILE, "r");
        long fileLength = fileHandler.length() - 1;
        StringBuilder sb = new StringBuilder();
        int line = 0;

        for (long filePointer = fileLength; filePointer != -1; filePointer--) {
            fileHandler.seek(filePointer);
            int readByte = fileHandler.readByte();

            if (readByte == 0xA) {
                if (filePointer < fileLength) {
                    line = line + 1;
                }
            } else if (readByte == 0xD) {
                if (filePointer < fileLength - 1) {
                    line = line + 1;
                }
            }
            if (line >= AMOUNT_OF_LINES) {
                break;
            }

            sb.append((char) readByte);
        }

        return sb.reverse().toString();
    }

}
