package org.transfer.transfer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.ChatColor;

public class TransferCommand implements CommandExecutor {

    private final Transfer plugin;
    private boolean isConsoleLoggingEnabled;

    public TransferCommand(Transfer plugin) {
        this.plugin = plugin;

        this.isConsoleLoggingEnabled = plugin.checkIsConsoleLoggingEnabled();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
             if (plugin.checkHasPermission(sender, "transfer.help", plugin.getConfig().getString("messages.player.no-permission-help-message"), isConsoleLoggingEnabled, plugin.getConfig().getString("messages.console.no-permission-help-message"))) {
                 sender.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.system.help-command")));
                 return true;
             }
            return false;
        }
        if (args[0].equalsIgnoreCase("mending")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("not-player-error-message")));
                return false;
            }
            if (plugin.checkHasPermission(sender, "transfer.mending", plugin.getConfig().getString("messages.player.no-permission-message"), isConsoleLoggingEnabled, plugin.getConfig().getString("messages.console.no-permission-message"))) {
                sender.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.player.success-message")));
                return true;
            }
            Player player = (Player) sender;
            if (!(player.getTargetBlockExact(5) != null && player.getTargetBlockExact(5).getState() instanceof Chest)) {
                sender.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.player.not-looking-at-chest")));
                return false;
            }
            Chest chest = (Chest) player.getTargetBlockExact(5).getState();
            Inventory chestInventory = chest.getInventory();
            int requiredMendingItems = plugin.getConfig().getInt("settings.amounts.required-mending-items", 10);
            int requiredFishingRods = plugin.getConfig().getInt("settings.amounts.required-rending-rods", 15);

            List<ItemStack> itemsToTransfer = new ArrayList<>();
            int mendingItemCount = 0;
            int fishingRodCount = 0;
            boolean hasEnchantedBooks = false;
            boolean hasOtherItems = false;

            for (ItemStack item : chestInventory.getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (item.getType() == Material.ENCHANTED_BOOK) {
                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                        if (meta != null && meta.hasStoredEnchant(org.bukkit.enchantments.Enchantment.MENDING)) {
                            hasEnchantedBooks = true;
                            continue;
                        }
                    }
                    if (item.containsEnchantment(org.bukkit.enchantments.Enchantment.MENDING)) {
                        if (item.getType() == Material.FISHING_ROD) {
                            fishingRodCount += item.getAmount();
                        } else {
                            mendingItemCount += item.getAmount();
                        }
                        itemsToTransfer.add(item);
                    } else {
                        hasOtherItems = true;
                    }
                }
            }

            if (hasEnchantedBooks) {
                player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.player.has-mending-books")));
                if (plugin.checkIsConsoleLoggingEnabled()) { plugin.getLogger().info(Objects.requireNonNull(plugin.getConfig().getString("messages.console.has-mending-books")));}
                return false;
            }
            if (hasOtherItems) {
                player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.player.wrong-items-message")));
                if (plugin.checkIsConsoleLoggingEnabled()) { plugin.getLogger().info(Objects.requireNonNull(plugin.getConfig().getString("messages.console.wrong-items-message")));}
                return false;
            }
            if (mendingItemCount > requiredMendingItems || fishingRodCount > requiredFishingRods) {
                player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.player.too-many-items-message")));
                if (plugin.checkIsConsoleLoggingEnabled()) { plugin.getLogger().info(Objects.requireNonNull(plugin.getConfig().getString("messages.console.too-many-items-message")));}
                return false;
            }
            if (!(mendingItemCount >= requiredMendingItems || fishingRodCount >= requiredFishingRods)) {
                player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.player.not-enough-items-message")));
                if (plugin.checkIsConsoleLoggingEnabled()) { plugin.getLogger().info(Objects.requireNonNull(plugin.getConfig().getString("messages.console.not-enough-items-message")));}
                return false;
            }
            chestInventory.removeItem(itemsToTransfer.toArray(new ItemStack[0]));
            ItemStack mendingBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) mendingBook.getItemMeta();
            if (meta != null) {
                meta.addStoredEnchant(org.bukkit.enchantments.Enchantment.MENDING, 1, true);
                mendingBook.setItemMeta(meta);
            }
            chestInventory.addItem(mendingBook);

            // Logging the transaction
            UUID playerUUID = player.getUniqueId();
            Location chestLocation = chest.getLocation();
            Location playerLocation = player.getLocation();
            ConfigHandler.logTransfer(playerUUID, player.getName(), playerUUID.toString(), itemsToTransfer, chestLocation, playerLocation);
            player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.player.success-message")));
            if (plugin.checkIsConsoleLoggingEnabled()) { plugin.getLogger().info(Objects.requireNonNull(plugin.getConfig().getString("messages.console.success-message")));}
            return true;

        } else {
            sender.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.player.failure-message")));
            if (plugin.checkIsConsoleLoggingEnabled()) { plugin.getLogger().info(Objects.requireNonNull(plugin.getConfig().getString("messages.console.failure-message")));}
        }

        return true;
    }


}
