package com.exorath.plugin.base.chat;

import com.exorath.plugin.base.Main;
import com.exorath.service.rank.api.RankServiceAPI;
import com.exorath.service.rank.res.Rank;
import com.exorath.service.rank.res.RankPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by toonsev on 5/17/2017.
 */
public class ChatManager implements Listener {
    private Map<String, Rank> ranks = new HashMap<>();
    private RankServiceAPI rankServiceAPI;
    private Set<Player> spamBuffer = new HashSet<>();

    public ChatManager(RankServiceAPI rankServiceAPI) {
        this.rankServiceAPI = rankServiceAPI;
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChatLowest(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        Player p = event.getPlayer();

        if (playerIsInSpam(p)) {
            p.sendMessage(ChatColor.RED + "Don't spam! Buy a Rank at https://store.exorath.com to reduce delay.");
            return;
        }
        addPlayerToSpam(p);
        RankPlayer rankPlayer = rankServiceAPI.getPlayer(p.getUniqueId().toString());
        Rank rank = getRank(rankPlayer.getId());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            removePlayerFromSpam(p);
        }, getRemovalDelay(rank));

        String message = event.getMessage();
        if (rankServiceAPI.inheritsFromRank(p.getUniqueId().toString(), "VIP"))
            message = ChatColor.translateAlternateColorCodes('&', message);
        String prefix = rank == null ? ChatColor.GRAY + ChatColor.BOLD.toString() + "M" : rank.getName();
        message = prefix + " " + ChatColor.GRAY + message;
        event.setMessage(message);
    }

    private long getRemovalDelay(Rank rank) {
        if (rank == null || rank.getId().equals("member"))
            return 60;
        return 10;
    }

    private Rank getRank(String rankId) {
        Rank rank = rankServiceAPI.getRank(rankId);
        if (rank != null)
            ranks.put(rankId, rank);
        return rank;
    }

    private synchronized void addPlayerToSpam(Player player) {
        spamBuffer.add(player);
    }

    private synchronized void removePlayerFromSpam(Player player) {
        spamBuffer.remove(player);
    }


    private synchronized boolean playerIsInSpam(Player player) {
        return spamBuffer.contains(player);
    }
}
