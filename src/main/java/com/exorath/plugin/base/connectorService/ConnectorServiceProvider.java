package com.exorath.plugin.base.connectorService;

import com.exorath.service.connector.api.ConnectorServiceAPI;
import com.exorath.service.connector.res.BasicServer;
import com.exorath.service.connector.res.Server;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by toonsev on 2/12/2017.
 */
public class ConnectorServiceProvider implements Listener {
    private static long INTERVAL = 10000l;
    private static long REPOST_DELAY = 5000l;

    private Plugin plugin;
    private ConnectorServiceAPI connectorServiceAPI;

    private BasicServer basicServer;
    private boolean joinable = false;
    private ArrayList<Player> players = new ArrayList<>();
    private int maxPlayers = 0;

    private boolean shuttingDown = false;

    private ServerPoster serverPoster;

    public boolean overrideJoinable = false;

    public ConnectorServiceProvider(String address, Plugin plugin) {
        this.connectorServiceAPI = new ConnectorServiceAPI(address);
        this.plugin = plugin;
        this.serverPoster = new ServerPoster();
        serverPoster.runTaskTimer(plugin, 0l, REPOST_DELAY * 20 / 1000);
        setupShutdownHook();
    }

    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shuttingDown = true;
            }
        });
    }

    public void setOverrideJoinable(boolean overrideJoinable) {
        this.overrideJoinable = overrideJoinable;
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
    }

    public void addPlayer(Player player) {
        players.add(player);
        serverPoster.post();
    }

    public void removePlayer(Player player) {
        players.remove(player);
        serverPoster.post();
    }

    public void setup(BasicServer basicServer) {
        this.basicServer = basicServer;
        this.joinable = true;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    private class ServerPoster extends BukkitRunnable {
        @Override
        public void run() {
            if (!shuttingDown)
                post();
        }

        public void post() {
            if (basicServer == null)
                return;
            boolean canJoin = overrideJoinable == true ? false : joinable;
            String[] ids = players.stream().map(player -> player.getUniqueId().toString()).collect(Collectors.toList()).toArray(new String[players.size()]);
            Server server = new Server(basicServer,
                    canJoin,
                    getExpiry(),
                    ids,
                    ids.length,
                    maxPlayers);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    connectorServiceAPI.putServer(server));
        }
    }

    private long getExpiry() {
        return System.currentTimeMillis() + INTERVAL;
    }

}
