package org.transfer.transfer;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.io.File;
import java.util.UUID;
public final class Transfer extends JavaPlugin {
    private static Transfer pluginInstance;
    private static final Logger logger = Logger.getLogger(Transfer.class.getName());

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
    public boolean checkIsConsoleLoggingEnabled() {
        return pluginInstance.getConfig().getBoolean("settings.enable-console-logging");
    }
    public Boolean checkHasPermission(CommandSender playerToCheck, String permissionToCheck, String missingPermissionsMessage, boolean isConsoleLoggingEnabled, String missingPermissionsConsoleMessage) {
        if (!(playerToCheck.hasPermission(permissionToCheck))) {
            if (isConsoleLoggingEnabled) { logger.warning(missingPermissionsConsoleMessage);}
            playerToCheck.sendMessage(missingPermissionsMessage);
            return false;
        }
        return true;
    }
}
