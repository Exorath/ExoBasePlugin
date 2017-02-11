package com.exorath.plugin.base;

import com.exorath.plugin.base.playersService.SimplePlayersServiceProvider;
import com.exorath.plugin.base.serverId.ServerIdProvider;
import com.exorath.plugin.base.serverId.ServerUUIDProvider;
import com.exorath.service.players.api.PlayersServiceAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by toonsev on 2/4/2017.
 */
public class Main extends JavaPlugin implements Listener {
    private ServerIdProvider serverIdProvider;
    private PlayersServiceAPI playersServiceAPI;
    private static ExoBaseAPI exoBaseAPI;
    private static Main instance;

    @Override
    public void onEnable() {
        Main.instance = this;
        this.serverIdProvider = new ServerUUIDProvider();
        this.playersServiceAPI = new PlayersServiceAPI(getPlayersServiceAddress());


        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private String getPlayersServiceAddress() {
        String address = System.getenv("PLAYERS_SERVICE_ADDRESS");
        if (address == null) {
            System.out.println("ExoBasePlugin: Fatal error: " + "No PLAYERS_SERVICE_ADDRESS env var provided.");
            Bukkit.shutdown();
        }
        return address;
    }

    private static Main getInstance() {
        return instance;
    }

    public static synchronized ExoBaseAPI getAPI() {
        if (exoBaseAPI == null)
            Main.exoBaseAPI = new SimpleExoBaseAPI(
                    Main.getInstance().serverIdProvider,
                    new SimplePlayersServiceProvider(Main.getInstance(), getInstance().serverIdProvider, Main.getInstance().playersServiceAPI));
        return Main.exoBaseAPI;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        exoBaseAPI.onJoin(event.getPlayer());
    }
}
