package me.shakeforprotein.TreeboTackle.Listeners;

import me.shakeforprotein.TreeboTackle.TreeboTackle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;

import static me.shakeforprotein.TreeboTackle.TreeboTackle.*;

public class ViewKitGui implements Listener {

    private TreeboTackle pl;
    private int lockout = 0;

    public ViewKitGui(TreeboTackle main) {
        this.pl = main;
    }

    @EventHandler
    public void inventoryInteract(InventoryInteractEvent e) {
        if (e.getView().getTitle().startsWith(badge + "KitView")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getView().getTitle().startsWith(badge + "KitView")) {
            e.setCancelled(true);
            File folder = new File(pl.getDataFolder() + File.separator + "Kits");
            File[] files = folder.listFiles();

            File playerFolder = new File(pl.getDataFolder() + File.separator + "Players");
            File playerFile = new File(playerFolder, e.getWhoClicked().getUniqueId().toString() + ".yml");
            YamlConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
            if (folder.mkdirs()) {
                pl.getLogger().info("New kits folder was created");
            }

            if (!e.getView().getTitle().contains(":")) {

                if (e.getClick().isLeftClick()) {

                    boolean passedChecks = true;
                    String itemName = "";

                    if ((e.getInventory().getItem(e.getSlot()) != null) && (e.getInventory().getItem(e.getSlot()).getType() != Material.AIR)) {
                        itemName = e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName();
                    }
                    PlayerInventory newInv = e.getWhoClicked().getInventory();
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                            if (itemName.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', yml.getString("Kit.FriendlyName")))) {
                                boolean proceed = true;
                                if (yml.getBoolean("Kit.RequiresPermission")) {
                                    if (!e.getWhoClicked().hasPermission(yml.getString("Kit.Permission"))) {
                                        proceed = false;
                                    }
                                }
                                if (proceed && econ.getBalance((Player) e.getWhoClicked()) >= yml.getDouble("Kit.Cost")) {

                                } else {
                                    proceed = false;
                                }

                                if (proceed) {
                                    if(yml.getDouble("Kit.Cost") > 0){
                                        econ.withdrawPlayer((Player) e.getWhoClicked(), yml.getDouble("Kit.Cost"));
                                    }
                                    Inventory oldInventory = Bukkit.createInventory(null, 54, "");
                                    oldInventory.setContents(e.getWhoClicked().getInventory().getContents());
                                    int tempNum = 0;

                                    e.getWhoClicked().getInventory().clear();
                                    if (yml.getItemStack("Kit.Armour.Head") != null) {
                                        newInv.setHelmet(yml.getItemStack("Kit.Armour.Head"));
                                    }
                                    if (yml.getItemStack("Kit.Armour.Chest") != null) {
                                        newInv.setChestplate(yml.getItemStack("Kit.Armour.Chest"));
                                    }
                                    if (yml.getItemStack("Kit.Armour.Legs") != null) {
                                        newInv.setLeggings(yml.getItemStack("Kit.Armour.Legs"));
                                    }
                                    if (yml.getItemStack("Kit.Armour.Feet") != null) {
                                        newInv.setBoots(yml.getItemStack("Kit.Armour.Feet"));
                                    }
                                    if (yml.getItemStack("Kit.Armour.Offhand") != null) {
                                        newInv.setItemInOffHand(yml.getItemStack("Kit.Armour.Offhand"));
                                    }
                                    for (String key : yml.getConfigurationSection("Kit.Items").getKeys(false)) {
                                        int pos = Integer.parseInt(key.split("_")[1]);
                                        ItemStack slottable = yml.getItemStack("Kit.Items." + key + ".Itemstack");
                                        newInv.setItem(pos, slottable);
                                    }
                                    long lastClaimed = 0;

                                    playerYml.set("Kits." + file.getName().replace(".yml", "") + ".LastClaimed", System.currentTimeMillis() / 1000);
                                    saveYml(playerYml, playerFile);
                                    e.getWhoClicked().closeInventory();

                                    for(ItemStack oldItem : oldInventory.getContents()){
                                        for(ItemStack newItem : e.getWhoClicked().getInventory().getContents()){
                                            if(!(oldItem == null)
                                                    && !oldItem.getType().isAir()
                                                    && newItem != null
                                                    && !newItem.getType().isAir()
                                                    && newItem.getType() == oldItem.getType()
                                                    && newItem.getItemMeta() == oldItem.getItemMeta()
                                                    && newItem.getAmount() < newItem.getMaxStackSize()
                                                    && oldItem.getAmount() > 0){
                                                int available = newItem.getMaxStackSize() - newItem.getAmount();
                                                if(oldItem.getAmount() <= available){newItem.setAmount(newItem.getAmount() + oldItem.getAmount());}
                                                else{oldItem.setAmount(oldItem.getAmount() - available); newItem.setAmount(newItem.getMaxStackSize());}
                                            }
                                        }
                                    }
                                    for(ItemStack oldItem : oldInventory.getContents()){
                                        if(oldItem != null) {
                                            if (e.getWhoClicked().getInventory().firstEmpty() != -1) {
                                                e.getWhoClicked().getInventory().setItem(e.getWhoClicked().getInventory().firstEmpty(), oldItem);
                                            } else {
                                                e.getWhoClicked().getWorld().dropItemNaturally(e.getWhoClicked().getLocation(), oldItem);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (e.getClick().isRightClick()) {
                    if(e.getClickedInventory() == e.getView().getTopInventory()) {
                        e.setCancelled(true);
                        String itemName = "";
                        if ((e.getInventory().getItem(e.getSlot()) != null) && (e.getInventory().getItem(e.getSlot()).getType() != Material.AIR)) {
                            itemName = e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName();
                        }
                        Inventory newInv = pl.createShakevintory("KitView:" + itemName);
                        if (files != null && files.length > 0) {
                            for (File file : files) {
                                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                                if (itemName.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', yml.getString("Kit.FriendlyName")))) {
                                    if (yml.getItemStack("Kit.Armour.Head") != null) {
                                        newInv.setItem(2, yml.getItemStack("Kit.Armour.Head"));
                                    }
                                    if (yml.getItemStack("Kit.Armour.Chest") != null) {
                                        newInv.setItem(3, yml.getItemStack("Kit.Armour.Chest"));
                                    }
                                    if (yml.getItemStack("Kit.Armour.Legs") != null) {
                                        newInv.setItem(4, yml.getItemStack("Kit.Armour.Legs"));
                                    }
                                    if (yml.getItemStack("Kit.Armour.Feet") != null) {
                                        newInv.setItem(5, yml.getItemStack("Kit.Armour.Feet"));
                                    }
                                    if (yml.getItemStack("Kit.Armour.Offhand") != null) {
                                        newInv.setItem(7, yml.getItemStack("Kit.Armour.Offhand"));
                                    }
                                    for (String key : yml.getConfigurationSection("Kit.Items").getKeys(false)) {
                                        int pos = Integer.parseInt(key.split("_")[1]);
                                        ItemStack slottable = yml.getItemStack("Kit.Items." + key + ".Itemstack");
                                        if (pos < 9) {
                                            newInv.setItem(pos + 45, slottable);
                                        } else {
                                            newInv.setItem(pos, slottable);
                                        }
                                    }
                                }
                            }
                            e.getWhoClicked().openInventory(newInv);
                        }
                    }
                }
            } else {
                e.setCancelled(true);
            }
        }
    }




    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().startsWith(badge + "KitView:")) {
            if (lockout == 0) {
                lockout = 1;
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    @Override
                    public void run() {
                        lockout = 0;
                        Bukkit.dispatchCommand(e.getPlayer(), "kit");
                    }
                }, 2L);
            }
        }
    }
}

