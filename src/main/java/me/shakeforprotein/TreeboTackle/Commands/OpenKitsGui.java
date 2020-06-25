package me.shakeforprotein.TreeboTackle.Commands;

import me.shakeforprotein.TreeboTackle.TreeboTackle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.*;

public class OpenKitsGui implements CommandExecutor {

    private TreeboTackle pl;

    public OpenKitsGui(TreeboTackle main) {
        this.pl = main;
    }
     /*########################################################### Example command ###################################################################

     ThoughtProcess: Explain why this command written like this

     Cons: Explain why this command bad.

      #############################################################################################################################################
      */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            File folder = new File(pl.getDataFolder() + File.separator + "Kits");
            File playerFolder = new File(pl.getDataFolder() + File.separator + "Players");
            File playerFile = new File(playerFolder, p.getUniqueId().toString() + ".yml");
            YamlConfiguration playerYaml;
            if(playerFile.exists()){playerYaml = YamlConfiguration.loadConfiguration(playerFile);}
            else{
                playerYaml = new YamlConfiguration();
            }
            if (folder.mkdirs()) {
                pl.getLogger().info("New kits folder was created");
            }
            if (playerFolder.mkdirs()) {
                pl.getLogger().info("New player folder was created");
            }
            Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER, badge + "KitView");

            File[] files = folder.listFiles();
            int i = 0;
            if (files != null && files.length > 0) {
                for (File file : files) {
                    Boolean addToGui = true;
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                    ItemStack item = yaml.getItemStack("Kit.Icon");
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', yaml.getString("Kit.FriendlyName")));
                    List<String> lore = new ArrayList<>();
                    long lastClaimed = 0;
                    if(playerYaml.getLong("Kits." + file.getName().replace(".yml", "") + ".LastClaimed") != 0){
                        lastClaimed = playerYaml.getLong("Kits." + file.getName().replace(".yml", "") + ".LastClaimed");
                    }
                    if ((yaml.getBoolean("Kit.RequiresPermission") && sender.hasPermission(yaml.getString("Kit.Permission"))) || !yaml.getBoolean("Kit.Permission")) {
                        if (econ.getBalance((Player) sender) >= yaml.getDouble("Kit.Cost")) {
                            lore.add(ChatColor.GOLD + "Cost: " + ChatColor.GREEN + "$" + yaml.getDouble("Kit.Cost"));
                        } else {
                            lore.add(ChatColor.GOLD + "Cost: " + ChatColor.RED + "$" + yaml.getDouble("Kit.Cost"));
                        }
                        if (yaml.get("Kit.Available") != null) {
                            if (playerYaml.getString(file.getName().replace(".yml", "")) == null) {
                                if (yaml.getDouble("Kit.Available") > 0) {
                                    if(!moreThanXDays(lastClaimed, Double.parseDouble(yaml.getString("Kit.Available")))){
                                        addToGui = false;
                                    }
                                }
                            }
                            lore.add(ChatColor.GOLD + "Available: " + yaml.getString("Kit.Available") + " days");
                        }
                        if (yaml.get("Kit.Description") != null) {
                            for (String line : yaml.getStringList("Kit.Description")) {
                                lore.add(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                        if (yaml.getBoolean("Kit.RequiresPermission")) {
                            if (sender.hasPermission(yaml.getString("Kit.Permission")) && addToGui) {
                                inventory.addItem(item);
                            }
                        } else if(addToGui) {
                            inventory.addItem(item);
                        }
                    }
                }
            }
            p.openInventory(inventory);
        } else {
            sender.sendMessage(badge + err + "This command opens a gui and as such can only be used by a player");
        }
        return true;
    }


    private boolean moreThanXDays(long configTime, double multiplier){
        long now = System.currentTimeMillis() / 1000;
        if (now - configTime > (86400 * multiplier)){
            System.out.println("Was true --  " + now + "-" + configTime + " > (86400 * " + multiplier +")" );

            return true;
        }
        else{
            return false;
        }
    }


    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return (sb.toString());
    }

}