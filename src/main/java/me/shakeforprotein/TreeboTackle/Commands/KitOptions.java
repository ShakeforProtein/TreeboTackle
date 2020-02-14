package me.shakeforprotein.TreeboTackle.Commands;

import me.shakeforprotein.TreeboTackle.TreeboTackle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.*;
import static org.apache.commons.lang.StringUtils.isNumeric;

public class KitOptions implements CommandExecutor, TabCompleter {

    private TreeboTackle pl;

    public KitOptions(TreeboTackle main){
        this.pl = main;
    }
     /*########################################################### Example command ###################################################################

     ThoughtProcess: Explain why this command written like this

     Cons: Explain why this command bad.

      #############################################################################################################################################
      */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        cmd.setUsage("/" + label + " <kit> <option> <value>");
        File folder = new File(pl.getDataFolder() + File.separator + "Kits");
        if(args.length == 1){
            File file = new File(folder, args[0] + ".yml");
            if (file.exists()) {
                showKitOptions((Player) sender, args[0], file);
            }
        }
        else if (args.length != 2 && args.length != 3) {
            sender.sendMessage(badge + err + cmd.getUsage());
        } else {
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    logger.warning("No Kits folder was found, so a new one was created.");
                }
            }

            File file = new File(folder, args[0] + ".yml");
            if (file.exists()) {
                YamlConfiguration ymlFile = YamlConfiguration.loadConfiguration(file);

                if (args.length == 2 && sender instanceof Player) {
                    if(args[1].equalsIgnoreCase("setIcon")){
                        if(((Player) sender).getInventory().getItemInMainHand().getType() != Material.AIR){
                            ItemStack icon = ((Player) sender).getInventory().getItemInMainHand();
                            ItemMeta iconMeta = icon.getItemMeta();
                            iconMeta.setDisplayName(ymlFile.getString("Kit.FriendlyName"));
                            icon.setItemMeta(iconMeta);
                            ymlFile.set("Kit.Icon", icon);
                            saveYml(ymlFile, file);
                        }
                        else{
                            sender.sendMessage(badge + err + "You must be holding an item in your main hand to set the icon.");
                        }
                    }
                    else{
                        sender.sendMessage(badge + err + "Unexpected input '" + args[1] + "'");
                    }
                } else if (args.length == 3){
                    if (args[1].equalsIgnoreCase("requirepermission")) {
                        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                            if(args[2].equalsIgnoreCase("true")){
                                ymlFile.set("Kit.RequiresPermission", true);
                            }
                            else if (args[2].equalsIgnoreCase("false")){
                                ymlFile.set("Kit.RequiresPermission", false);
                            }
                            saveYml(ymlFile, file);
                        } else {
                            sender.sendMessage(badge + err + "requirepermission requires a boolean input (true/false)");
                        }
                    } else if (args[1].equalsIgnoreCase("setpermissionnode")) {
                        ymlFile.set("Kit.Permission", args[2]);
                    } else if (args[1].equalsIgnoreCase("setfriendlyname")) {
                        ymlFile.set("Kit.FriendlyName", args[2]);
                    } else if (args[1].equalsIgnoreCase("setcost")) {
                        if (isNumeric(args[2])) {
                            ymlFile.set("Kit.Cost", args[2]);
                        }
                    }
                    else if(args[1].equalsIgnoreCase("kitFrequency")){
                        if(isNumeric(args[2])){
                            ymlFile.set("Kit.Available", Integer.parseInt(args[2]));
                        }
                    }
                    saveYml(ymlFile, file);
                }
                showKitOptions((Player) sender, args[0],file);
            }
            else {
                sender.sendMessage(badge + err + "Could not find Kit with name " + args[0].toLowerCase());
            }
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        List<String> kits = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<String> outputStrings = new ArrayList<>();

        File folder = new File(pl.getDataFolder() + File.separator + "Kits");

        if(folder.mkdirs()){
            pl.getLogger().info("New kits folder was created");
        }
        File[] files = folder.listFiles();


        if(args.length == 1){
            for (File file : files){
                kits.add(file.getName().replace(".yml", ""));
            }
            for(String kit : kits){
                if (kit.toLowerCase().startsWith(args[0].toLowerCase())) {
                    outputStrings.add(kit);
                }
            }
        }

        else if (args.length == 2) {
            values.add("RequirePermission");
            values.add("SetPermissionNode");
            values.add("setFriendlyName");
            values.add("setCost");
            values.add("setIcon");
            values.add("kitFrequency");
            for(String value : values){
                if (value.toLowerCase().startsWith(args[1].toLowerCase())) {
                    outputStrings.add(value);
                }
            }
        }

        else if (args.length == 3) {
            if (args[1].toLowerCase().equalsIgnoreCase("requirepermission")) {
                values.add("true");
                values.add("false");
                for(String value : values){
                    if (value.toLowerCase().startsWith(args[2].toLowerCase())) {
                        outputStrings.add(value);
                    }
                }
            } else if (args[1].equalsIgnoreCase("setpermissionnode")) {
                values.add(pl.getName().toLowerCase() + ".player");
                values.add(pl.getName().toLowerCase() + ".vip+");
                values.add(pl.getName().toLowerCase() + ".PVip");
                values.add(pl.getName().toLowerCase() + ".staff");
                values.add(pl.getName().toLowerCase() + ".admin");
                values.add(pl.getName().toLowerCase() + "." + args[0].toLowerCase());
                for(String value : values){
                    if (value.toLowerCase().startsWith(args[2].toLowerCase())) {
                        outputStrings.add(value);
                    }
                }
            } else if (args[1].equalsIgnoreCase("setcost")) {
                values.add("10");
                values.add("50");
                values.add("100");
                values.add("500");
                values.add("1000");
                values.add("10000");
                values.add("50000");
                values.add("100000");
                values.add("1000000");
                values.add("100000000");
                values.add("1000000000");
                for(String value : values){
                    if (value.toLowerCase().startsWith(args[2].toLowerCase())) {
                        outputStrings.add(value);
                    }
                }
            }
            else if(args[1].equalsIgnoreCase("kitFrequency")){
                values.add("0.5");
                values.add("1");
                values.add("7");
                values.add("28");
                values.add("365");
                for(String value : values){
                    if (value.toLowerCase().startsWith(args[2].toLowerCase())) {
                        outputStrings.add(value);
                    }
                }
            }
        }
        return outputStrings;
    }

}