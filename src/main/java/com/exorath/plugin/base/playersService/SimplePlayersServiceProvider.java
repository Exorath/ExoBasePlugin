package com.exorath.plugin.base.playersService;

import com.exorath.plugin.base.serverId.ServerIdProvider;
import com.exorath.service.players.api.PlayersServiceAPI;
import com.exorath.service.players.res.Success;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by toonsev on 2/4/2017.
 */
public class SimplePlayersServiceProvider implements PlayersServiceProvider {
    public static final Long JOIN_RECORD_EXPIRY = 20000l;
    private Plugin plugin;
    private ServerIdProvider serverIdProvider;
    private PlayersServiceAPI playersServiceAPI;

    public SimplePlayersServiceProvider(Plugin plugin, ServerIdProvider serverIdProvider, PlayersServiceAPI playersServiceAPI) {
        this.plugin = plugin;
        this.serverIdProvider = serverIdProvider;
        this.playersServiceAPI = playersServiceAPI;
    }

    @Override
    public Observable<Success> putPlayer(Player player) {

        com.exorath.service.players.res.Player servicePlayer =
                new com.exorath.service.players.res.Player(
                        true,
                        player.getUniqueId().toString(),
                        player.getName(),
                        serverIdProvider.getServerId(),
                        System.currentTimeMillis(),
                        null,
                        getExpiry()
                        );
        new PlayerUpdater(player).runTaskTimer(plugin, getDelay(), getDelay());
        return update(servicePlayer);
    }

    public Observable<Success> removePlayer(Player player) {
        com.exorath.service.players.res.Player servicePlayer =
                new com.exorath.service.players.res.Player(
                        false,
                        player.getUniqueId().toString(),
                        player.getName(),
                        serverIdProvider.getServerId(),
                        null,
                        System.currentTimeMillis(),
                        null
                );
        return update(servicePlayer);
    }

    private Long getExpiry(){
        return JOIN_RECORD_EXPIRY;
    }

    private Long getDelay(){
        Long expiry = getExpiry();
        if(expiry < 5000)
            return 2000l;
        else if(expiry < 10.000)
            return 4000l;
        else
            return 6000l;
    }

    private Observable<Success> update(com.exorath.service.players.res.Player player){
        return Observable.<Success>create((s) -> {
            s.onNext(playersServiceAPI.updatePlayer(player));
        }).subscribeOn(Schedulers.io());
    }
    private class PlayerUpdater extends BukkitRunnable {
        private Player player;

        public PlayerUpdater(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            if(player.isOnline())
                putPlayer(player);
            else {
                super.cancel();
                removePlayer(player);
            }
        }
    }
}
