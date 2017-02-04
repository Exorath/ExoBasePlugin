package com.exorath.plugin.base.playersService;

import com.exorath.service.players.res.Success;
import io.reactivex.Observable;
import org.bukkit.entity.Player;


/**
 * Created by toonsev on 2/4/2017.
 */
public interface PlayersServiceProvider {
    Observable<Success> putPlayer(Player player);

}
