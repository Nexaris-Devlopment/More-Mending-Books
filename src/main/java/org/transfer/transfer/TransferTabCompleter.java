package org.transfer.transfer;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class TransferTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (commandSender.hasPermission("transfer")) {
                if (commandSender.hasPermission("transfer.mending")) {
                    completions.add("mending");
                }
                if (commandSender.hasPermission("transfer.help")) {
                    completions.add("help");
                }

            }
            return filterSuggestions(completions, args[0]);
        }
        return null;
    }
    private List<String> filterSuggestions(List<String> suggestions, String input) {
        List<String> filtered = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                filtered.add(suggestion);
            }
        }
        return filtered;
    }
}
