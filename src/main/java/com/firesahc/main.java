package com.firesahc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin {

    static main main;

    @Override
    public void onEnable() {
        // Plugin startup logic
        main= this;
        saveDefaultConfig();
        FullKicked fullKicked = new FullKicked();
        fullKicked.getWhitelist();

        getServer().getPluginManager().registerEvents(fullKicked, this);

        Bukkit.getPluginCommand("reload_fk").setExecutor(fullKicked);
        Bukkit.getPluginCommand("whitelist").setExecutor(fullKicked);
        Bukkit.getPluginCommand("add_whitelist").setExecutor(fullKicked);
        Bukkit.getPluginCommand("remove_whitelist").setExecutor(fullKicked);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
