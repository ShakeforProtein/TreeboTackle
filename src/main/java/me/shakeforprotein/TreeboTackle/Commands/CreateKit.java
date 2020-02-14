package me.shakeforprotein.TreeboTackle.Commands;

import me.shakeforprotein.TreeboTackle.TreeboTackle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.*;

public class CreateKit implements CommandExecutor, TabCompleter {

    private TreeboTackle pl;

    public CreateKit(TreeboTackle main){
        this.pl = main;
    }

     /*########################################################### Example command ###################################################################

     ThoughtProcess: Explain why this command written like this

     Cons: Explain why this command bad.

      #############################################################################################################################################
      */

     @Override
     public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(args.length == 1) {
                File folder = new File(pl.getDataFolder() + File.separator + "Kits");
                if(!folder.exists()){
                  if(folder.mkdirs()){
                   pl.getLogger().info("New kits folder was created");
                  }
                }

                File file = new File(folder, args[0] + ".yml");
                if(!file.exists()){
                    //Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER, badge + " KitCreator:" + args[0].toLowerCase());
                    ItemStack[] kitItems = p.getInventory().getContents().clone();
                    String kitName = args[0];
                    File ymlFile = new File(folder, kitName.toLowerCase() + ".yml");
                    YamlConfiguration yamlYaml = YamlConfiguration.loadConfiguration(ymlFile);
                    ItemStack icon = new ItemStack(Material.NAME_TAG, 1);
                    ItemMeta iconMeta = icon.getItemMeta();
                    iconMeta.setDisplayName(kitName);
                    icon.setItemMeta(iconMeta);
                    if(p.getInventory().getItemInMainHand().getType() != Material.AIR){
                        icon = p.getInventory().getItemInMainHand();
                    }
                    yamlYaml.set("Kit.Icon", icon);
                    yamlYaml.set("Kit.FriendlyName", kitName);
                    yamlYaml.set("Kit.Permission", "treebotackle." + kitName);
                    yamlYaml.set("Kit.RequiresPermission", false);
                    yamlYaml.set("Kit.Cost", 10000);
                    yamlYaml.set("Kit.Available", "1.0");

                    if(p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR){
                        yamlYaml.set("Kit.Armour.Head", p.getInventory().getHelmet());
                    }
                    if(p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() != Material.AIR){
                        yamlYaml.set("Kit.Armour.Chest", p.getInventory().getChestplate());
                    }
                    if(p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() != Material.AIR){
                        yamlYaml.set("Kit.Armour.Legs", p.getInventory().getLeggings());
                    }
                    if(p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() != Material.AIR){
                        yamlYaml.set("Kit.Armour.Feet", p.getInventory().getBoots());
                    }
                    if(p.getInventory().getItemInOffHand().getType() != Material.AIR){
                        yamlYaml.set("Kit.Armour.Offhand", p.getInventory().getItemInOffHand());
                    }
                    for(int i=0; i < 36; i++){
                        yamlYaml.set("Kit.Items.item_" + i + ".Itemstack", p.getInventory().getItem(i));
                    }
                    saveYml(yamlYaml, ymlFile);
                    showKitOptions(p, kitName, ymlFile);
                }
                else{
                    p.sendMessage(badge + err + "That kit already exists!");
                }
            }
            else{
                p.sendMessage(badge + err + "This command uses a single <kit name> argument,");
            }
        }
        else{
            sender.sendMessage(badge + err + " This command opens a gui and as such can only be run by a player.");
        }
         return true;
     }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        List<String> kits = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<String> outputStrings = new ArrayList<>();

        File folder = new File(pl.getDataFolder() + File.separator + "Kits");

        if (folder.mkdirs()) {
            pl.getLogger().info("New kits folder was created");
        }
        File[] files = folder.listFiles();


        if (args.length == 1) {
            for (File file : files) {
                kits.add(file.getName().replace(".yml", ""));
            }
            for (String kit : kits) {
                if (kit.toLowerCase().startsWith(args[0].toLowerCase())) {
                    outputStrings.add(kit);
                }

            }
        }
        return outputStrings;
    }

}