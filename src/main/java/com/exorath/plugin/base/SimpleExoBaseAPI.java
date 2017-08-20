package com.exorath.plugin.base;

import com.exorath.plugin.base.connectorService.ConnectorServiceProvider;
import com.exorath.plugin.base.manager.Manager;
import com.exorath.plugin.base.playersService.PlayersServiceProvider;
import com.exorath.plugin.base.serverId.ServerIdProvider;
import com.exorath.service.connector.res.BasicServer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by toonsev on 2/4/2017.
 */
public class SimpleExoBaseAPI implements ExoBaseAPI, Listener {
    private SimpleExoBaseAPI instance;
    private ServerIdProvider serverIdProvider;
    private PlayersServiceProvider playersServiceProvider;
    private ConnectorServiceProvider connectorServiceProvider;

    private Map<Class, Manager> managers = new HashMap<>();

    public SimpleExoBaseAPI(ServerIdProvider serverIdProvider, PlayersServiceProvider playersServiceProvider, ConnectorServiceProvider connectorServiceProvider) {
        this.serverIdProvider = serverIdProvider;
        this.playersServiceProvider = playersServiceProvider;
        this.connectorServiceProvider = connectorServiceProvider;
    }

    @Override
    public void onGameJoin(Player player) {
        connectorServiceProvider.addPlayer(player);
    }

    @Override
    public void onGameLeave(Player player) {
        connectorServiceProvider.removePlayer(player);
    }

    @Override
    public void setupGame(BasicServer basicServer) {
        connectorServiceProvider.setup(basicServer);
    }

    @Override
    public String getServerId() {
        return serverIdProvider.getServerId();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        playersServiceProvider.putPlayer(joinEvent.getPlayer()).subscribe(success -> {
            if (!success.getSuccess())
                joinEvent.getPlayer().sendMessage(ChatColor.RED + "An error occurred while updating the player registry, the network may malfunction.");
        });
    }

    @Override
    public void registerManager(Manager manager) {
        if(managers.containsKey(manager.getClass())){
            System.out.println("Manager " + manager.getClass().getName() + " already registered.");
            return;
        }
        managers.put(manager.getClass(), manager);
        if(manager instanceof Listener)
            Bukkit.getPluginManager().registerEvents((Listener) manager, Main.getInstance());
    }

    @Override
    public <T extends Manager> T getManager(Class<T> clazz) {
        return (T) managers.get(clazz);
    }
}
