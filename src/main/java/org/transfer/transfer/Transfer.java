package org.transfer.transfer;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.UUID;
public final class Transfer extends JavaPlugin {
    private static Transfer pluginInstance;

    @Override
    public void onEnable() {
        pluginInstance = this;
        saveDefaultConfig();
        this.getCommand("transfer").setExecutor(new TransferCommand(this));
        this.getCommand("transfer").setTabCompleter(new TransferTabCompleter());

        getLogger().info("Transfer Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Transfer Plugin Disabled!");
    }

    public static Transfer getInstance() {
        return pluginInstance;
    }
    public File getPlayerLogFile(UUID playerUUID) {
        File logDir = new File(getDataFolder(), "player_logs");
        if (!logDir.exists()) logDir.mkdirs();
        return new File(logDir, playerUUID.toString() + ".yml");
    }
}
