package me.shakeforprotein.TreeboTackle.Commands;

import me.shakeforprotein.TreeboTackle.TreeboTackle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.badge;

public class MainCommand implements CommandExecutor, TabCompleter {

    private TreeboTackle pl;

    public MainCommand(TreeboTackle main){
        this.pl = main;
    }


    /*########################################################### Example command ###########################################################

     ThoughtProcess: Explain why this command written like this

     Cons: Explain why this command bad.

      #############################################################################################################################################
     */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length > 0){
            if(args[0].equalsIgnoreCase("reload")){
                pl.reloadRoutine();
                sender.sendMessage(badge + "Plugin reload sequence completed.");
            }
            else{
                sender.sendMessage(badge + "Version: " + pl.getDescription().getVersion());
            }
        }
        else {
            sender.sendMessage(badge + "Version: " + pl.getDescription().getVersion());
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){

        List<String> values = new ArrayList<>();
        List<String> outputStrings = new ArrayList<>();

        values.add("version");
        values.add("reload");

        if(args.length == 1){
            for(String value : values){
                if (value.toLowerCase().startsWith(args[0].toLowerCase())) {
                    outputStrings.add(value);
                }
            }
        }
        return outputStrings;
    }
}