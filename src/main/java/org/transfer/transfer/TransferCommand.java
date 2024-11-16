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
import java.util.UUID;
import org.bukkit.ChatColor;

public class TransferCommand implements CommandExecutor {

    private final Transfer plugin;

    public TransferCommand(Transfer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /transfer <mending/help>");
            return true;
        }

        if (args[0].equalsIgnoreCase("mending")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.GREEN + "Only players can run this command.");
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("transfer.mending")) {
                player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
                return true;
            }

            if (player.getTargetBlockExact(5) != null && player.getTargetBlockExact(5).getState() instanceof Chest) {
                Chest chest = (Chest) player.getTargetBlockExact(5).getState();
                Inventory chestInventory = chest.getInventory();

                int requiredMendingItems = plugin.getConfig().getInt("requiredMendingItems", 10);
                int requiredFishingRods = plugin.getConfig().getInt("requiredFishingRods", 15);

                int mendingItemCount = 0;
                int fishingRodCount = 0;
                List<ItemStack> itemsToTransfer = new ArrayList<>();
                boolean hasEnchantedBooks = false;
                boolean hasOtherItems = false;

                for (ItemStack item : chestInventory.getContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        // Check if it's an enchanted book with mending
                        if (item.getType() == Material.ENCHANTED_BOOK) {
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                            if (meta != null && meta.hasStoredEnchant(org.bukkit.enchantments.Enchantment.MENDING)) {
                                hasEnchantedBooks = true;
                                continue;
                            }
                        }

                        // Check if the item contains mending enchantment
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
                    player.sendMessage(ChatColor.GOLD + "The chest contains enchanted books with mending, which cannot be transformed.");
                } else if (hasOtherItems) {
                    player.sendMessage( ChatColor.DARK_RED + "The chest contains invalid items. Only fishing rods with mending or other items with mending enchantment are allowed.");
                } else if (mendingItemCount > requiredMendingItems || fishingRodCount > requiredFishingRods) {
                    player.sendMessage(ChatColor.DARK_RED + "The chest contains more than the required items:\n- " +
                            mendingItemCount + " items with mending\n- " +
                            fishingRodCount + " fishing rods with mending.");
                } else if (mendingItemCount >= requiredMendingItems || fishingRodCount >= requiredFishingRods) {
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
                    ConfigHandler.logTransfer(playerUUID, player.getName(), playerUUID.toString(),
                            itemsToTransfer, chestLocation, playerLocation);

                    player.sendMessage(ChatColor.GREEN + "Transfer completed successfully!");
                    Bukkit.getConsoleSender().sendMessage("Transfer completed by " + player.getName() + " with items: " + itemsToTransfer);
                } else {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Not enough items with mending in the chest.");
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "You must be looking at a chest.");
            }
        } else if (args[0].equalsIgnoreCase("help")) {
            if (sender.hasPermission("transfer.help")) {
                sender.sendMessage(ChatColor.RED + "Usage of /transfer command:\n- /transfer mending: Transforms items in a chest.\n- /transfer help: Shows this help message.");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to view help.");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "Invalid subcommand. Use /transfer help for help.");
        }

        return true;
    }
}
