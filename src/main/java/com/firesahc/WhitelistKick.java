package com.firesahc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WhitelistKick implements Listener , CommandExecutor {

    private final Set<String> whitelist = Collections.synchronizedSet(new HashSet<>());
    private final Set<Player> nonWhitelistedPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 动态更新玩家状态
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!whitelist.contains(player.getName().toLowerCase())) {
            nonWhitelistedPlayers.add(player);
            System.out.println("§e非白名单列表："+nonWhitelistedPlayers);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        nonWhitelistedPlayers.remove(event.getPlayer());
        System.out.println("§e非白名单列表："+nonWhitelistedPlayers);
    }


    // 优化后的登录事件处理
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.KICK_FULL) return;

        Player joiningPlayer = event.getPlayer();
        String lowerName = joiningPlayer.getName().toLowerCase();

        if (whitelist.contains(lowerName)) {
            if (!nonWhitelistedPlayers.isEmpty()) {
                Player toKick = nonWhitelistedPlayers.iterator().next();
                nonWhitelistedPlayers.remove(toKick);
                toKick.kickPlayer("§e您的小号因满人而被挤出");
                event.setResult(PlayerLoginEvent.Result.ALLOWED);
            } else {
                event.setKickMessage("§e服务器已满，且所有玩家均在白名单中");
            }
        } else {
            event.setKickMessage("§e服务器已满，且您不在白名单中");
        }
    }

    // 优化后的白名单重载
    public void reloadWhitelist() {
        FileConfiguration config = Full_kicked.main.getConfig();
        whitelist.clear();
        config.getStringList("whitelist").forEach(name -> whitelist.add(name.toLowerCase()));

        // 更新在线玩家状态
        nonWhitelistedPlayers.clear();
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!whitelist.contains(player.getName().toLowerCase())) {
                nonWhitelistedPlayers.add(player);
            }
        });
        System.out.println("§e重载成功，非白名单列表："+nonWhitelistedPlayers);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (s.equalsIgnoreCase("reload_full_kicked")) {
            Full_kicked.main.reloadConfig();
            reloadWhitelist();
        }
        return true;
    }

}