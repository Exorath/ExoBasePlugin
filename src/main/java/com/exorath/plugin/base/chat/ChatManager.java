package com.exorath.plugin.base.chat;

import com.exorath.service.rank.api.RankServiceAPI;
import com.exorath.service.rank.res.Rank;
import com.exorath.service.rank.res.RankPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by toonsev on 5/17/2017.
 */
public class ChatManager implements Listener {
    private Map<String, Rank> ranks = new HashMap<>();
    private RankServiceAPI rankServiceAPI;

    public ChatManager(RankServiceAPI rankServiceAPI) {
        this.rankServiceAPI = rankServiceAPI;
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChatLowest(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        Player p = event.getPlayer();
        String message = event.getMessage();
        if (rankServiceAPI.inheritsFromRank(p.getUniqueId().toString(), "VIP"))
            message = ChatColor.translateAlternateColorCodes('&', message);
        RankPlayer rankPlayer = rankServiceAPI.getPlayer(p.getUniqueId().toString());
        Rank rank = getRank(rankPlayer.getId());
        String prefix = rank == null ? ChatColor.GRAY + ChatColor.BOLD.toString() + "M" : rank.getName();
        message = prefix + " " + ChatColor.GRAY + message;
        event.setMessage(message);
    }

    private Rank getRank(String rankId) {
        Rank rank = rankServiceAPI.getRank(rankId);
        if (rank != null)
            ranks.put(rankId, rank);
        return rank;
    }


}
