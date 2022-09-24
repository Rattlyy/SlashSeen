package it.rattly.slashseen;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.function.Supplier;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        // Make a plugin that ties in with the towny pluging so when when you do
        // /seen it says what town or nation they are in along with when they were last online

        getCommand("seen").setExecutor((sender, command, label, args) -> {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(text("You can't run this from console!").color(RED));
                return false;
            }

            if (args.length < 1) {
                sender.sendMessage(text("Usage: /seen <player>").color(RED));
                return false;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (target == null) {
                sender.sendMessage(text("Player not found.").color(RED));
                return false;
            }

            Resident res = TownyAPI.getInstance().getResident(target.getName());
            if (res == null) {
                sender.sendMessage(text("Player not found.").color(RED));
                return false;
            }

            sender.sendMessage(
                    text().append(
                            text("Information on ").color(GOLD),
                            text(target.getName()).color(YELLOW).decorate(BOLD),
                            newline(), newline(),

                            text("Nation: ").color(GOLD),
                            text(
                                    // I hate java
                                    ((Supplier<String>) () -> {
                                        Nation nation = res.getNationOrNull();

                                        if (nation == null) {
                                            return "None";
                                        } else {
                                            return nation.getName();
                                        }
                                    }).get()
                            ).color(YELLOW).decorate(BOLD),
                            newline(),

                            text("Town: ").color(GOLD),
                            text(
                                    ((Supplier<String>) () -> {
                                        Town town = res.getTownOrNull();

                                        if (town == null) {
                                            return "None";
                                        } else {
                                            return town.getName();
                                        }
                                    }).get()
                            ).color(YELLOW).decorate(BOLD),
                            newline(),

                            text("Last online: ").color(GOLD),
                            text(new SimpleDateFormat("MMMMM dd '@' HH:mm").format(res.getLastOnline())).
                                    color(YELLOW).decorate(BOLD)
                    )
            );

            return true;
        });
    }
}
