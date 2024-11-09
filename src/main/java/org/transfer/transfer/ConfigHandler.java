package org.transfer.transfer;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ConfigHandler {

    public static void logTransfer(UUID playerUUID, String playerName, String playerUUIDString, List<ItemStack> items, Location chestLocation, Location playerLocation) {
        File file = Transfer.getInstance().getPlayerLogFile(playerUUID);
        YamlConfiguration logConfig = YamlConfiguration.loadConfiguration(file);

        int totalTransfers = logConfig.getInt("totalTransfers", 0) + 1;
        logConfig.set("displayName", playerName);
        logConfig.set("uuid", playerUUIDString);
        logConfig.set("totalTransfers", totalTransfers);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStamp = formatter.format(new Date());
        String basePath = "transfers." + totalTransfers + ".";

        logConfig.set(basePath + "time", timeStamp);
        logConfig.set(basePath + "chestLocation", chestLocation);
        logConfig.set(basePath + "playerLocation", playerLocation);

        for (int i = 0; i < items.size(); i++) {
            logConfig.set(basePath + "items." + i, items.get(i));
        }

        try {
            logConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
