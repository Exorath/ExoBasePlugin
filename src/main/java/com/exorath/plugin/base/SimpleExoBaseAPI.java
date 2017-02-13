package com.exorath.plugin.base;

import com.exorath.plugin.base.connectorService.ConnectorServiceProvider;
import com.exorath.plugin.base.playersService.PlayersServiceProvider;
import com.exorath.plugin.base.serverId.ServerIdProvider;
import com.exorath.service.connector.res.BasicServer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by toonsev on 2/4/2017.
 */
public class SimpleExoBaseAPI implements ExoBaseAPI {
    private SimpleExoBaseAPI instance;
    private ServerIdProvider serverIdProvider;
    private PlayersServiceProvider playersServiceProvider;
    private ConnectorServiceProvider connectorServiceProvider;

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

    @Override
    public void onJoin(Player player) {
        playersServiceProvider.putPlayer(player).subscribe(success -> {
            if (!success.getSuccess())
                player.sendMessage(ChatColor.RED + "An error occurred while updating the player registry, the network may malfunction.");
        });
    }
}
