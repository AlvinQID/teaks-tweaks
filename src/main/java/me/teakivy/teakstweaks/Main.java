package me.teakivy.teakstweaks;

import me.teakivy.teakstweaks.craftingtweaks.CraftingRegister;
import me.teakivy.teakstweaks.packs.hermitcraft.tag.Tag;
import me.teakivy.teakstweaks.utils.*;
import me.teakivy.teakstweaks.utils.datamanager.DataManager;
import me.teakivy.teakstweaks.utils.metrics.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static me.teakivy.teakstweaks.utils.metrics.CustomMetrics.registerCustomMetrics;

public final class Main extends JavaPlugin implements Listener {

    public static ArrayList<UUID> chEnabled = new ArrayList<>();
    public Boolean newVersionAvailable = false;
    public String latestVersion;

    public DataManager data;

    private final ArrayList<String> activePacks = new ArrayList<>();
    private final ArrayList<String> activeCraftingTweaks = new ArrayList<>();

    private Register register;

    public Tag tagListener;

    @Override
    public void onEnable() {
        // Data Manager
        this.data = new DataManager(this);
        data.saveDefaultConfig();

        data.saveDefaultConfig();

        if (getConfig().getBoolean("config.dev-mode")) {
            Logger.log(Logger.LogLevel.INFO, MessageHandler.getMessage("plugin.startup.dev-mode-enabled"));
        }

        // Credits
        try {
            new Credits().createCredits();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Metrics
        Metrics metrics = new Metrics(this, 12001);
        registerCustomMetrics(metrics);

        // Update Config.yml
        if (!getConfig().getBoolean("config.dev-mode")) {
            if (this.getConfig().getInt("config.version") < Objects.requireNonNull(this.getConfig().getDefaults()).getInt("config.version")) {
                try {
                    ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder(), "config.yml"), Collections.emptyList(), true);
                    this.reloadConfig();
                    Logger.log(Logger.LogLevel.INFO, "Dev Mode Enabled!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder(), "config.yml"), Collections.emptyList(), true);
                this.reloadConfig();
                Logger.log(Logger.LogLevel.INFO, "Updated Config to Version: " + this.getConfig().getInt("config.version"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Update Checker
        getServer().getPluginManager().registerEvents(new UpdateJoinAlert(), this);

        String latestVersion = null;
        try {
            latestVersion = new UpdateChecker(this, 94021).getLatestVersion();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            Logger.log(Logger.LogLevel.ERROR, MessageHandler.getMessage("plugin.error.cant-get-latest"));
        }
        String thisVersion = this.getDescription().getVersion();
        if (!thisVersion.equalsIgnoreCase(latestVersion)) {
            Logger.log(Logger.LogLevel.WARNING, "Teak's Tweaks has an update!\nPlease update to the latest version (" + latestVersion + ")\n&ehttps://www.spigotmc.org/resources/teaks-tweaks.94021/");
            newVersionAvailable = true;
            this.latestVersion = latestVersion;
        }


        // Crafting Tweaks
        CraftingRegister.registerAll();

        // Commands
        Register.registerCommands();

        // Config
        this.saveDefaultConfig();

        tagListener = new Tag();

        // Coords HUD
        for (String uuid : data.getConfig().getStringList("chEnabled")) {
            chEnabled.add(UUID.fromString(uuid));
        }

        // Plugin startup logic
        Logger.log(Logger.LogLevel.INFO, MessageHandler.getMessage("plugin.startup.plugin-started"));

        // Packs
        register = new Register();
        register.registerAll();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Logger.log(Logger.LogLevel.INFO, MessageHandler.getMessage("plugin.shutdown.plugin-shutdown"));

        data.reloadConfig();
        List<String> list = new ArrayList<>();
        for (UUID uuid : chEnabled) {
            list.add(uuid.toString());
        }
        data.getConfig().set("chEnabled", list);
        try {
            data.saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getPackName(String pack) {
        if (!data.getConfig().contains("messages.pack." + pack)) return pack;
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.getMessage("pack." + pack + ".name"));
    }

    public void clearPacks() {
        activePacks.clear();
    }

    public void addPack(String name) {
        activePacks.add(name);
    }

    public ArrayList<String> getPacks() {
        return activePacks;
    }

    public void addCraftingTweaks(String name) {
        activeCraftingTweaks.add(name);
    }

    public ArrayList<String> getCraftingTweaks() {
        return activeCraftingTweaks;
    }

    public Register getRegister() {
        return register;
    }

    public static Main getInstance() {
        return getPlugin(Main.class);
    }

    public static ConfigurationSection getPackConfig(String pack) {
        return getInstance().getConfig().getConfigurationSection("packs." + pack);
    }

}