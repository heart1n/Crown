package heartin.plugin.crown;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameListener implements Listener {


    private final GameProcess process;

    GameListener(GameProcess process)
    {
        this.process = process;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        process.getPlayerManager().registerGamePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        process.getPlayerManager().unreigsterGamePlayer(event.getPlayer());
    }

    // @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        Entity entity = event.getEntity();

        if (entity instanceof Player)
        {
            Player player = (Player) entity;

            GamePlayer gamePlayer = process.getPlayerManager().getGamePlayer(player);

            if (gamePlayer != null)
            {
                Entity damager = event.getDamager();

                if (damager instanceof Player)
                {
                    Player attacker = (Player) damager;
                    GamePlayer damagerPlayer = process.getPlayerManager().getGamePlayer(player);

                    if (damagerPlayer != null)
                    {
                        // if not crown player -> attack cancel;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        event.setDeathMessage(null);

        Player player = event.getEntity();
        GamePlayer gamePlayer = process.getPlayerManager().getGamePlayer(player);

        if (gamePlayer != null)
        {
            Player killer = player.getKiller();

            if (killer != null)
            {
                GamePlayer gameKiller = process.getPlayerManager().getGamePlayer(killer);

                if (gameKiller != null && !gameKiller.isCrown())
                {
                    if (gamePlayer.isCrown())
                    {
                        Bukkit.broadcastMessage(gameKiller.getName() + "님이 " + gamePlayer.getName() + "님의 왕관을 뺐어갔습니다");

                        gameKiller.setCrown();
                        gamePlayer.setCitizen();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();

        GamePlayer gamePlayer = (GamePlayer) process.getPlayerManager().getGamePlayer(player);

        process.getPlayerManager().setEntity();
    }
}