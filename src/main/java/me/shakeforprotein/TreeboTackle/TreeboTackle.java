package me.shakeforprotein.TreeboTackle;

import me.shakeforprotein.TreeboTackle.Commands.*;
import me.shakeforprotein.TreeboTackle.Listeners.CreateKitGuiListener;
import me.shakeforprotein.TreeboTackle.Listeners.EditKitListener;
import me.shakeforprotein.TreeboTackle.Listeners.ViewKitGui;
import me.shakeforprotein.TreeboTackle.Updaters.GitHubUpdateChecker;
import me.shakeforprotein.TreeboTackle.Updaters.SpigotUpdateChecker;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public final class TreeboTackle extends JavaPlugin {

    private static TreeboTackle instance;
    public static String badge;
    public static String err;
    public static boolean needsUpdate;
    public static String newVersion;
    public static Economy econ;
    public static Logger logger;

    public CreateKit createKit = new CreateKit(this);
    //public EditKit editKit = new EditKit(this);
    public DeleteKit deleteKit = new DeleteKit(this);
    public KitOptions kitOptions = new KitOptions(this);
    public OpenKitsGui openKitsGui = new OpenKitsGui(this);
    public MainCommand mainCommand = new MainCommand(this);

    public HashMap<Player, PlayerInventory> playerInventoryHashMap = new HashMap<>();

    //TODO: Document this plugin
    //TODO: Add Comments, ThoughtProcess, Cons

    @Override
    public void onEnable() {
        logger = this.getLogger();

        setInstance(this);
        badge = instance.getConfig().getString("general.messages.badge") == null ? ChatColor.translateAlternateColorCodes('&', "&3&l[&2Set the badge in the config.yml&3&l]&r") : ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("general.messages.badge"));
        err = instance.getConfig().getString("general.messages.error") == null ? ChatColor.translateAlternateColorCodes('&', "&4Set the error header in config.yml") : ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("general.messages.error"));


        copyDefaultConfig();
        setupUpdater();
        integrateBstats();
        registerCommands();
        registerTabCompleters();
        registerListeners();
        economySetup();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public void reloadRoutine() {
        reloadConfig();
    }

    public static TreeboTackle getInstance() {
        return instance;
    }

    private static void setInstance(TreeboTackle instance) {
        TreeboTackle.instance = instance;
    }

    public static void handleException(Exception exception) {
        exception.printStackTrace();
    }


    //ThoughtProcess: Required for hoooking Vault.
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static void saveYml(YamlConfiguration yml, File file) {
        try {
            yml.save(file);
        } catch (IOException exception) {
            logger.warning(exception.getCause().getMessage());
        }
    }


    private void registerCommands() {
        //ThoughtProcess: The onEnable is much easier to follow if we split things into their own methods.
        this.getCommand("createkit").setExecutor(createKit);
        //this.getCommand("editkit").setExecutor(editKit);
        this.getCommand("deletekit").setExecutor(deleteKit);
        this.getCommand("kitoptions").setExecutor(kitOptions);
        this.getCommand("kit").setExecutor(openKitsGui);
        this.getCommand("treebotackle").setExecutor(mainCommand);
    }

    private void registerTabCompleters() {
        //ThoughtProcess: The onEnable is much easier to follow if we split things into their own methods.
        this.getCommand("createkit").setTabCompleter(createKit);
        //this.getCommand("editkit").setTabCompleter(editKit);
        this.getCommand("deletekit").setTabCompleter(deleteKit);
        this.getCommand("kitoptions").setTabCompleter(kitOptions);
        this.getCommand("treebotackle").setTabCompleter(mainCommand);
    }


    private void setupUpdater() {
        //ThoughtProcess: This plugin may appear on Spigot at some point, but given the license applied to my plugins, someone may wish to fork it, and as such, this is a convenience method to choose between github or spigot.
        if (getConfig().getBoolean("externalFeatures.updates.enabled")) {
            if (getConfig().get("externalFeatures.updates.method") != null && getConfig().getString("externalFeatures.updates.method").equalsIgnoreCase("spigot")) {
                /*If updates are enabled and update method is spigot*/

                new SpigotUpdateChecker(this, getConfig().getInt("externalFeatures.updates.spigotID")).getVersion(version -> {
                    if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                        logger.info("There is not a new update available.");
                        needsUpdate = true;
                        newVersion = version;
                    }
                });
            } else if (getConfig().get("externalFeatures.updates.method") != null &&  getConfig().getString("externalFeatures.updates.method").equalsIgnoreCase("github")) {
                    /*If updates are enabled and update method is github*/
                    new GitHubUpdateChecker(this).getCheckDownloadURL();

            }
        }
    }

    private void integrateBstats() {
        //ThoughtProcess: Usage statistics help determine what I should prioritise.
        if (getConfig().get("bstatsIntegration") != null) {
            if (getConfig().getBoolean("externalFeatures.bstatsIntegration")) {
                logger.info(this.getName() + " has enabled bStats metric collection");
                new Metrics(this);
            }
        }
    }

    private void copyDefaultConfig() {
        //ThoughtProcess: Stores the default config file on the user server and sets the version so I can know what version the user was.
        //Cons: User configuration opens the plugin up to errors that would not be experienced if I did not give them options.
        getConfig().options().copyDefaults(true);
        getConfig().set("version", this.getDescription().getVersion());
        saveConfig();
        reloadConfig();
    }

    private void registerListeners() {
        //ThoughtProcess: We need these to know when things happen, unless I override the server classes.
        //Bukkit.getPluginManager().registerEvents(new CreateKitGuiListener(instance), instance);
        //Bukkit.getPluginManager().registerEvents(new EditKitListener(instance), instance);
        Bukkit.getPluginManager().registerEvents(new ViewKitGui(instance), instance);
    }

    private void economySetup() {
        //ThoughtProcess: Given this is a kits plugin, it it likely that there will be a cost associated with certain kits. Also it makes the plugin more robust if it gets stolen. I still want my work to be as professional as possible, even if someone steals it.
        if (!setupEconomy()) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        } else {
            logger.info(this.getName() + " has hooked Vaults Economy");
        }
    }

    public static void showKitOptions(Player p, String kitName, File file){
        //ThoughtProcess: Displays a clickable in chat menu for prefilling commands. Easier to put it here that write it twice.
        YamlConfiguration yamlYaml = YamlConfiguration.loadConfiguration(file);
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(ChatColor.BLACK + "" + ChatColor.MAGIC + "-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        TextComponent msg = new TextComponent(badge + ChatColor.UNDERLINE + " Set options for kit: " + ChatColor.translateAlternateColorCodes('&', kitName));
        p.spigot().sendMessage(msg);


        //Change Kit name
        msg = new TextComponent("Friendly Name: " + ChatColor.GREEN + yamlYaml.getString("Kit.FriendlyName") + " ");
        TextComponent changeButton = new TextComponent(ChatColor.GOLD+ "[CHANGE]");
        ClickEvent nameChangeClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/kitoptions " + kitName + " setFriendlyName " );
        changeButton.setClickEvent(nameChangeClickEvent);
        msg.addExtra(changeButton);
        p.spigot().sendMessage(msg);

        //Change kit icon
        msg = new TextComponent("Kit Icon: " + ChatColor.GREEN + yamlYaml.getItemStack("Kit.Icon").getType() + " ");
        TextComponent iconButton = new TextComponent(ChatColor.GOLD + "[CHANGE]");
        ClickEvent iconChangeClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/kitoptions " + kitName + " seticon" );
        iconButton.setClickEvent(iconChangeClickEvent);
        msg.addExtra(iconButton);
        p.spigot().sendMessage(msg);

        //Set whether a permission node is checked or not
        TextComponent permissionFalse = new TextComponent(ChatColor.RED + " [False] ");
        TextComponent permissionTrue = new TextComponent(ChatColor.GREEN + " [True] ");
        TextComponent permissionString = new TextComponent("Require a permission? : ");
        if(!yamlYaml.getBoolean("Kit.RequiresPermission")) {
            permissionFalse = new TextComponent(ChatColor.GREEN + " [False] ");
            permissionTrue = new TextComponent(ChatColor.RED + " [True] ");
        }
        ClickEvent permissionFalseClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/kitoptions " + kitName + " requirepermission false");
        ClickEvent permissionTrueClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/kitoptions " + kitName + " requirepermission true");
        permissionFalse.setClickEvent(permissionFalseClickEvent);
        permissionTrue.setClickEvent(permissionTrueClickEvent);
        permissionString.addExtra(permissionFalse);
        permissionString.addExtra(permissionTrue);
        permissionString.addExtra(" - Currently " + ChatColor.GOLD + yamlYaml.getBoolean("Kit.RequiresPermission"));
        p.spigot().sendMessage(permissionString);

        //Set the permission node that will be checked if checked.
        msg = new TextComponent("Current permission node: " + ChatColor.RED + yamlYaml.getString("Kit.Permission") + ChatColor.GOLD + " [CHANGE]");
        ClickEvent changePermissionClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/kitoptions " + kitName + " setpermissionnode " );
        msg.setClickEvent(changePermissionClickEvent);
        p.spigot().sendMessage(msg);

        //Set the kit price.
        msg = new TextComponent("Current Kit cost: " + yamlYaml.getString("Kit.Cost") + ChatColor.GOLD + " [ADJUST]");
        ClickEvent costClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/kitoptions " + kitName + " setcost " );
        msg.setClickEvent(costClickEvent);
        p.spigot().sendMessage(msg);

        //Set Kit Availability
        msg = new TextComponent("Current Availability: " + ChatColor.GOLD + yamlYaml.getInt("Kit.Available"));
        ClickEvent availableClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/kitoptions " + kitName + " kitFrequency ");
        msg.setClickEvent(availableClickEvent);
        p.spigot().sendMessage(msg);

        //Terminate Options Box
        p.sendMessage(ChatColor.BLACK + "" + ChatColor.MAGIC + "-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        p.sendMessage(" ");
        p.sendMessage(" ");

    }

    public Inventory createShakevintory(String title){
        Inventory shakeInv = Bukkit.createInventory(null, 54, badge + title);
        ItemStack glasspane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

        shakeInv.setItem(0, glasspane);
        shakeInv.setItem(1, glasspane);
        shakeInv.setItem(6, glasspane);
        shakeInv.setItem(8, glasspane);
        for(int i = 36; i < 45; i++) {
            shakeInv.setItem(i,glasspane);
        }
        return shakeInv;
    }
}
