package com.exorath.plugin.base;


import com.exorath.plugin.base.manager.Manager;
import com.exorath.service.connector.res.BasicServer;
import org.bukkit.entity.Player;

/**
 * Created by toonsev on 2/4/2017.
 */
public interface ExoBaseAPI {
    String getServerId();


    void onGameJoin(Player player);

    void onGameLeave(Player player);

    void setupGame(BasicServer basicServer);

    static ExoBaseAPI getInstance(){
        return Main.getAPI();
    }

    /**
     * If the manager is a listener, it will be automatically registered to bukkit
     * @param manager
     */
    void registerManager(Manager manager);

    <T extends Manager> T  getManager(Class<T> clazz);
}
