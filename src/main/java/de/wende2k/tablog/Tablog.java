package de.wende2k.tablog;

import de.wende2k.tablog.command.TablogCommand;
import de.wende2k.tablog.listener.PlayerItemHeldListener;
import de.wende2k.tablog.util.FontInfo;
import de.wende2k.tablog.util.TablistUtils;
import lombok.Getter;
import lombok.Setter;
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
import java.util.*;

@Plugin(name = "Tablog", version = "1.0-SNAPSHOT")
@Author(name = "Wende2k")
@Description(desc = "Allows you to shows the server log in tab.")
@Command(name = "tablog", aliases = {"tl"}, permission = "tablog.use")
@Permission(name = "tablog.use", desc = "Allows the usage of Tablog", defaultValue = PermissionDefault.OP)
@Getter
public class Tablog extends JavaPlugin {

    public static final int AMOUNT_OF_LINES = 25;
    private static final File LOG_FILE = new File("logs/latest.log");

    private static final String PLACEHOLDER_HEAD = "§r\n§r\n§r\n§aScroll up §b§l⇡§a or down §b§l⇣\n§r\n§r\n§7";
    private static final String PLACEHOLDER_FOOT = "§r\n§r\n§r\n§cTablog plugin§r\n§r\n§r\n";

    @Getter private static Tablog instance;

    private Map<UUID, Integer> enabledPlayers;
    @Setter private BukkitRunnable runnable;

    @Override
    public void onEnable() {
        Tablog.instance = this;
        this.enabledPlayers = new HashMap<>();

        super.getCommand("tablog").setExecutor(new TablogCommand());
        super.getServer().getPluginManager().registerEvents(new PlayerItemHeldListener(), this);
    }

    public BukkitRunnable refreshRunnable() {
        if (this.runnable != null) {
            return this.runnable;
        }

        return (this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Tablog.this.enabledPlayers.forEach((uuid, currentLine) -> {
                    Player player = Bukkit.getPlayer(uuid);

                    StringBuilder stringBuilder = new StringBuilder();

                    for (String line : Tablog.this.getTail(currentLine)) {
                        while (FontInfo.getSize(line) < 490 && ((FontInfo.getSize(line) + 3) <= 490)) {
                            line += " ";
                        }
                        stringBuilder.append(line
                                .replace(line.substring(0, 10), "§8" + line.substring(0, 10) + "§7")
                                .replace("INFO", "§eINFO§7")
                                .replace("WARN", "§cWARN§7")).append("\n");
                    }

                    if (player != null && player.isOnline()) {
                        TablistUtils.send(player, PLACEHOLDER_HEAD + stringBuilder.toString()
                                + PLACEHOLDER_FOOT, "");
                    }
                });
            }
        });

    }

    @SneakyThrows
    private String[] getTail(int line) {
        RandomAccessFile fileHandler = new RandomAccessFile(LOG_FILE, "r");
        long fileLength = fileHandler.length() - 1;
        StringBuilder sb = new StringBuilder();

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

        fileHandler.close();
        return sb.reverse().toString().split("\n");
    }

}
