package com.exorath.plugin.base;

import com.exorath.plugin.base.playersService.PlayersServiceProvider;
import com.exorath.plugin.base.serverId.ServerIdProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by toonsev on 2/4/2017.
 */
public class SimpleExoBaseAPI implements ExoBaseAPI {
    private ServerIdProvider serverIdProvider;
    private PlayersServiceProvider playersServiceProvider;

    public SimpleExoBaseAPI(ServerIdProvider serverIdProvider, PlayersServiceProvider playersServiceProvider) {
        this.serverIdProvider = serverIdProvider;
        this.playersServiceProvider = playersServiceProvider;
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
