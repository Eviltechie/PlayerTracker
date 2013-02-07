package to.joe.playertracker;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import to.joe.j2mc.core.J2MC_Manager;

public class PlayerTracker extends JavaPlugin implements Listener {

    public void onEnable() {
        getConfig().set("online", true);
        getConfig().set("maxplayers", getServer().getMaxPlayers());

        getServer().getPluginManager().registerEvents(this, this);
        updatePlayers();

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                getConfig().set("updated", System.currentTimeMillis() / 1000);
                saveConfig();
            }
        }, 200, 200);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                updatePlayers();
            }
        }, 1200, 1200);
    }

    public void onDisable() {
        getConfig().set("online", false);
        updatePlayers();
    }

    private void updatePlayers() {
        ArrayList<String> players = new ArrayList<String>();
        ArrayList<String> admins = new ArrayList<String>();
        ArrayList<String> vanished = new ArrayList<String>();
        ArrayList<String> trusted = new ArrayList<String>();
        for (Player p : getServer().getOnlinePlayers()) {
            String name = p.getName();
            players.add(name);
            if (p.hasPermission("j2mc.core.admin")) {
                admins.add(name);
            }
            if (J2MC_Manager.getPermissions().hasFlag(name, 't')) {
                trusted.add(name);
            }
            try {
                if (VanishNoPacket.isVanished(name)) {
                    vanished.add(name);
                }
            } catch (VanishNotLoadedException e) {
                getLogger().severe("Vanish not loaded!");
            }
        }
        getConfig().set("updated", System.currentTimeMillis() / 1000);
        getConfig().set("players", players);
        getConfig().set("admins", admins);
        getConfig().set("vanished", vanished);
        getConfig().set("trusted", trusted);
        saveConfig();
    }

    private void update() {
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                updatePlayers();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        update();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        update();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(VanishStatusChangeEvent event) {
        update();
    }
}