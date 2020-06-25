package me.shakeforprotein.TreeboTackle.Commands;

import me.shakeforprotein.TreeboTackle.TreeboTackle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.*;

public class ForceKit implements CommandExecutor {


    private TreeboTackle pl;

    public ForceKit(TreeboTackle main) {
        this.pl = main;
    }
     /*########################################################### Example command ###################################################################

     ThoughtProcess: Explain why this command written like this

     Cons: Explain why this command bad.

      #############################################################################################################################################
      */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 2 && Bukkit.getPlayer(args[0]) != null) {
            Player p = Bukkit.getPlayer(args[0]);
            File folder = new File(pl.getDataFolder() + File.separator + "Kits");
            File file = new File(folder, args[1] + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            File playerFolder = new File(pl.getDataFolder() + File.separator + "Players");
            File playerFile = new File(folder, p.getUniqueId().toString() + ".yml");
            YamlConfiguration playerYml = YamlConfiguration.loadConfiguration(file);

            if (file.exists()) {
                String itemName = "";

                boolean proceed = true;
                for (ItemStack item : p.getInventory().getContents()) {
                    p.getWorld().dropItem(p.getLocation(), item);
                }
                p.getInventory().clear();

                if (yml.getItemStack("Kit.Armour.Head") != null) {
                    p.getInventory().setHelmet(yml.getItemStack("Kit.Armour.Head"));
                }
                if (yml.getItemStack("Kit.Armour.Chest") != null) {
                    p.getInventory().setChestplate(yml.getItemStack("Kit.Armour.Chest"));
                }
                if (yml.getItemStack("Kit.Armour.Legs") != null) {
                    p.getInventory().setLeggings(yml.getItemStack("Kit.Armour.Legs"));
                }
                if (yml.getItemStack("Kit.Armour.Feet") != null) {
                    p.getInventory().setBoots(yml.getItemStack("Kit.Armour.Feet"));
                }
                if (yml.getItemStack("Kit.Armour.Offhand") != null) {
                    p.getInventory().setItemInOffHand(yml.getItemStack("Kit.Armour.Offhand"));
                }
                for (String key : yml.getConfigurationSection("Kit.Items").getKeys(false)) {
                    int pos = Integer.parseInt(key.split("_")[1]);
                    ItemStack slottable = yml.getItemStack("Kit.Items." + key + ".Itemstack");
                    p.getInventory().setItem(pos, slottable);
                }
                long lastClaimed = 0;
                playerYml.set("Kits." + file.getName().replace(".yml", "") + ".LastClaimed", System.currentTimeMillis() / 1000);
                saveYml(playerYml, playerFile);
            } else {
                sender.sendMessage(badge + err + " Kit not found");
            }
        } else {
            sender.sendMessage(badge + cmd.getUsage());
        }
        return true;
    }
}
