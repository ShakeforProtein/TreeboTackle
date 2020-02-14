package me.shakeforprotein.TreeboTackle.Commands;

import me.shakeforprotein.TreeboTackle.TreeboTackle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.*;

public class DeleteKit implements CommandExecutor, TabCompleter {

    private TreeboTackle pl;

    public DeleteKit(TreeboTackle main){
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
                        logger.warning("No Kits folder was found, so a new one was created.");
                    }
                }
                File file = new File(folder, args[0].toLowerCase() + ".yml");
                if(!file.exists()){
                    p.sendMessage(badge + err + "No such Kit Exists.");
                }
                else{
                    if(file.delete()){
                        p.sendMessage(badge + "Kit file deleted successfully.");
                    }
                    else {
                        p.sendMessage(badge + err + "Failed to delete Kit file. You will need to delete it manually");
                    }
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