package me.shakeforprotein.TreeboTackle.Listeners;

import me.shakeforprotein.TreeboTackle.TreeboTackle;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.*;

public class CreateKitGuiListener implements Listener {

    private TreeboTackle pl;

    public CreateKitGuiListener(TreeboTackle main){
        this.pl = main;
    }
    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().startsWith(badge + " KitCreator:")) {
            Player p = (Player) e.getPlayer();
            File sourceDir = (new File(pl.getDataFolder() +  "Kits"));
            String kitName = e.getView().getTitle().split(":")[1];
            File ymlFile = new File(sourceDir, kitName + ".yml");
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
                yamlYaml.set("Kit.Items.item_" + i + ".Itemstack", e.getInventory().getItem(i));
            }
            try{yamlYaml.save(ymlFile);}
            catch(IOException exception){
                handleException(exception);
            }

            showKitOptions(p, kitName, ymlFile);
        }
    }
}
