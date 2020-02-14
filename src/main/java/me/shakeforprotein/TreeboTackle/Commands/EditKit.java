package me.shakeforprotein.TreeboTackle.Commands;

import me.shakeforprotein.TreeboTackle.TreeboTackle;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.*;

public class EditKit implements CommandExecutor, TabCompleter {

    private TreeboTackle pl;

    public EditKit(TreeboTackle main){
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
                 File folder = new File(pl.getDataFolder() +  "Kits");
                 if(!folder.exists()){
                     if(folder.mkdirs()){
                         logger.warning("No Kits folder was found, so a new one was created.");
                     }
                 }
                 File file = new File(folder, args[0].toLowerCase() + ".yml");
                 if(!file.exists()){
                     p.sendMessage(badge + err + "No such Kit Exists.");
                 }
                 else{
                     YamlConfiguration kitYml = YamlConfiguration.loadConfiguration(file);
                     Inventory inv = Bukkit.createInventory(null, InventoryType.PLAYER, badge + "KitEditor:" + args[0].toLowerCase());

                     for(int i=0; i < inv.getSize(); i++){
                         inv.setItem(i, kitYml.getItemStack("Kit.Items.item_" + i + ".Itemstack"));
                     }

                     p.openInventory(inv);
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
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){

        List<String> outputStrings = new ArrayList<>();
        File folder = new File(pl.getDataFolder() + File.separator + "Kits");
        if(!folder.exists()){
            if(folder.mkdirs()){
                logger.warning("No Kits folder was found, so a new one was created.");
            }
        }

        FilenameFilter filter = (dir, name) -> false;

        File[] files = folder.listFiles(filter);

        if(files != null && files.length > 0) {
            for (File file : files) {
                String name = file.getName().replace(".yml", "");
                outputStrings.add(name);
            }
        }

        if(outputStrings.size() > 0){

            return outputStrings;
        }
        return null;
    }

}