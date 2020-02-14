package me.shakeforprotein.TreeboTackle.Listeners;

import me.shakeforprotein.TreeboTackle.TreeboTackle;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.io.File;
import java.io.IOException;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.badge;
import static me.shakeforprotein.TreeboTackle.TreeboTackle.showKitOptions;

public class EditKitListener implements Listener {

    private TreeboTackle pl;

    public EditKitListener(TreeboTackle main){
        this.pl = main;
    }
    @EventHandler
    public void inventoryInteract(InventoryCloseEvent e) {
        if (e.getView().getTitle().startsWith(badge + " KitEditor:")) {
            String kitName = e.getView().getTitle().split(":")[1];
            boolean saveFile = true;
            for(int i = 0; i < e.getInventory().getSize(); i++){
                if(e.getInventory().getItem(i) != null && e.getInventory().getItem(i).getType() != Material.AIR){
                    saveFile = false;
                }
            }
            if(saveFile){
                Player p = (Player) e.getPlayer();
                File sourceDir = (new File(pl.getDataFolder() +  "Kits"));
            File ymlFile = new File(sourceDir, kitName + ".yml");
            YamlConfiguration yamlYaml = YamlConfiguration.loadConfiguration(ymlFile);
            for (int i = 0; i < e.getInventory().getSize(); i++) {
                yamlYaml.set("Kit.Items.item_" + i + ".Itemstack", e.getInventory().getItem(i));
            }
            try {
                yamlYaml.save(ymlFile);
            } catch (IOException exception) {
                pl.getLogger().warning(exception.getCause().getMessage());
            }
            showKitOptions(p, kitName, ymlFile);
            }
            else{
                e.getPlayer().sendMessage(badge + "Editing cancelled for kit " + kitName);
            }
        }
    }

    @EventHandler
    public void inv(InventoryClickEvent e){
        if (e.getView().getTitle().startsWith(badge + " KitEditor:")) {
            if (e.isShiftClick() && e.isRightClick()) {
                e.getInventory().clear();
                e.getWhoClicked().closeInventory();
            }
        }
    }
}
