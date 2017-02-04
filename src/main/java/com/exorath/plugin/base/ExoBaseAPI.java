package com.exorath.plugin.base;


import org.bukkit.entity.Player;

/**
 * Created by toonsev on 2/4/2017.
 */
public interface ExoBaseAPI {
    String getServerId();

    void onJoin(Player player);
}
