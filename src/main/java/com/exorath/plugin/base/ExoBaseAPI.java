package com.exorath.plugin.base;


import com.exorath.service.connector.res.BasicServer;
import org.bukkit.entity.Player;

/**
 * Created by toonsev on 2/4/2017.
 */
public interface ExoBaseAPI {
    String getServerId();

    void onJoin(Player player);

    void onGameJoin(Player player);

    void onGameLeave(Player player);

    void setupGame(BasicServer basicServer);

    static ExoBaseAPI getInstance(){
        return Main.getAPI();
    }
}
