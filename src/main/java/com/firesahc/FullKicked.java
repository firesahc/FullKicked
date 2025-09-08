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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FullKicked implements Listener , CommandExecutor {

    private final main main;
    private final Set<String> whitelist = Collections.synchronizedSet(new HashSet<>());
    private final Set<Player> nonWhitelistPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private FileConfiguration config;

    public FullKicked(main main) {
        this.main = main;
    }

    // 动态更新玩家状态
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!whitelist.contains(player.getName())) {
            nonWhitelistPlayers.add(player);
            System.out.println("§e玩家 "+player.getName()+" 加入非白名单列表");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!whitelist.contains(player.getName())) {
            nonWhitelistPlayers.remove(player);
            System.out.println("§e玩家 "+player.getName()+" 移除非白名单列表");
        }
    }

    // 登录事件处理
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.KICK_FULL) return;

        Player joiningPlayer = event.getPlayer();
        String lowerName = joiningPlayer.getName();

        if (whitelist.contains(lowerName)) {
            if (!nonWhitelistPlayers.isEmpty()) {
                Player toKick = nonWhitelistPlayers.iterator().next();
                nonWhitelistPlayers.remove(toKick);
                toKick.kickPlayer("§e玩家 "+toKick.getName()+" 因满人而被挤出");
                event.setResult(PlayerLoginEvent.Result.ALLOWED);
            } else {
                event.setKickMessage("§e服务器已满，且所有玩家均在白名单中");
            }
        } else {
            event.setKickMessage("§e服务器已满，且您不在白名单中");
        }
    }

    //获取白名单
    public void getWhitelist(){
        try {
            if (!whitelist.isEmpty()) {
                whitelist.clear();
            }
            config = main.getConfig();
            whitelist.addAll(config.getStringList("whitelist"));
            System.out.println("§e获取白名单成功");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("§e获取白名单失败");
        }
    }

    // 重载非白名单玩家
    public void reloadNonWhitelistPlayers() {
        try {
            if (!nonWhitelistPlayers.isEmpty()) {
                nonWhitelistPlayers.clear();
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!whitelist.contains(player.getName())) {
                    nonWhitelistPlayers.add(player);
                }
            });
            System.out.println("§e重载非白名单玩家成功");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("§4重载非白名单玩家失败");
        }
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (s.equalsIgnoreCase("reload_fk")) {
            main.reloadConfig();
            getWhitelist();
            reloadNonWhitelistPlayers();
        }
        else if (s.equalsIgnoreCase("whitelist")) {
            System.out.println("§e当前白名单："+whitelist);
        }
        else if (s.equalsIgnoreCase("add_whitelist")) {
            whitelist.add(strings[0]);
            config.set("whitelist", new ArrayList<>(whitelist));
            main.saveConfig();
            reloadNonWhitelistPlayers();
        }
        else if (s.equalsIgnoreCase("remove_whitelist")) {
            whitelist.remove(strings[0]);
            config.set("whitelist", new ArrayList<>(whitelist));
            main.saveConfig();
            reloadNonWhitelistPlayers();
        }
        return true;
    }

}