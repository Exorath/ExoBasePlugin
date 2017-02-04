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
public class Main extends JavaPlugin implements Listener{
    private ServerIdProvider serverIdProvider;
    private PlayersServiceAPI playersServiceAPI;
    private ExoBaseAPI exoBaseAPI;

    @Override
    public void onEnable() {
        this.serverIdProvider = new ServerUUIDProvider();
        this.playersServiceAPI = new PlayersServiceAPI(getPlayersServiceAddress());
        this.exoBaseAPI = new SimpleExoBaseAPI(
                serverIdProvider,
                new SimplePlayersServiceProvider(this, serverIdProvider, playersServiceAPI));

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private String getPlayersServiceAddress(){
        String address = System.getenv("PLAYERS_SERVICE_ADDRESS");
        if(address == null){
            System.out.println("ExoBasePlugin: Fatal error: " + "No PLAYERS_SERVICE_ADDRESS env var provided.");
            Bukkit.shutdown();
        }
        return address;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        exoBaseAPI.onJoin(event.getPlayer());
    }
}
