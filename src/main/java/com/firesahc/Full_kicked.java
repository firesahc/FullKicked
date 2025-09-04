package com.firesahc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Full_kicked extends JavaPlugin {

    static Full_kicked main;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        main= this;
        WhitelistKick whitelistKick = new WhitelistKick();
        whitelistKick.reloadWhitelist();
        getServer().getPluginManager().registerEvents(whitelistKick, this);
        Bukkit.getPluginCommand("reload_full_kicked").setExecutor(whitelistKick);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
