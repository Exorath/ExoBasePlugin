package com.exorath.plugin.base;

import com.exorath.plugin.base.chat.ChatManager;
import com.exorath.plugin.base.connectorService.ConnectorServiceProvider;
import com.exorath.plugin.base.playersService.SimplePlayersServiceProvider;
import com.exorath.plugin.base.serverId.ServerIdProvider;
import com.exorath.plugin.base.serverId.ServerUUIDProvider;
import com.exorath.service.players.api.PlayersServiceAPI;
import com.exorath.service.rank.api.RankServiceAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by toonsev on 2/4/2017.
 */
public class Main extends JavaPlugin implements Listener {
    private ServerIdProvider serverIdProvider;
    private PlayersServiceAPI playersServiceAPI;
    private ConnectorServiceProvider connectorServiceProvider;
    private ChatManager chatManager;

    private static ExoBaseAPI exoBaseAPI;
    private static Main instance;

    @Override
    public void onEnable() {
        Main.instance = this;
        this.serverIdProvider = new ServerUUIDProvider();
        this.playersServiceAPI = new PlayersServiceAPI(getPlayersServiceAddress());
        this.connectorServiceProvider = new ConnectorServiceProvider(getConnectorServiceAddress(), this);
        this.chatManager = new ChatManager(new RankServiceAPI(getRankServiceAddress()));
        Bukkit.getPluginManager().registerEvents(chatManager, this);
        Bukkit.getPluginManager().registerEvents(connectorServiceProvider, this);


        Bukkit.getPluginManager().registerEvents(this, this);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> Bukkit.getScheduler().runTask(this, () -> connectorServiceProvider.setOverrideJoinable(true))));
    }

    private String getConnectorServiceAddress() {
        String address = System.getenv("CONNECTOR_SERVICE_ADDRESS");
        if (address == null) {
            System.out.println("ExoBasePlugin: Fatal error: " + "No CONNECTOR_SERVICE_ADDRESS env var provided.");
            Bukkit.shutdown();
        }
        return address;
    }

    private String getPlayersServiceAddress() {
        String address = System.getenv("PLAYERS_SERVICE_ADDRESS");
        if (address == null) {
            System.out.println("ExoBasePlugin: Fatal error: " + "No PLAYERS_SERVICE_ADDRESS env var provided.");
            Bukkit.shutdown();
        }
        return address;
    }
    private String getRankServiceAddress() {
        String address = System.getenv("RANK_SERVICE_ADDRESS");
        if (address == null) {
            System.out.println("ExoBasePlugin: Fatal error: " + "No RANK_SERVICE_ADDRESS env var provided.");
            Bukkit.shutdown();
        }
        return address;
    }

    public static Main getInstance() {
        return instance;
    }

    public static synchronized ExoBaseAPI getAPI() {
        if (exoBaseAPI == null) {
            SimpleExoBaseAPI exoBaseAPI = new SimpleExoBaseAPI(
                    Main.getInstance().serverIdProvider,
                    new SimplePlayersServiceProvider(Main.getInstance(), getInstance().serverIdProvider, Main.getInstance().playersServiceAPI),
                    Main.getInstance().connectorServiceProvider
            );
            Bukkit.getPluginManager().registerEvents(exoBaseAPI, Main.getInstance());
            Main.exoBaseAPI = exoBaseAPI;
        }
        return Main.exoBaseAPI;
    }
}
