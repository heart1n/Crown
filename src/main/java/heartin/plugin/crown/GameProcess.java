package heartin.plugin.crown;

import nemo.mc.event.ASMEventExecutor;
import nemo.mc.packet.Packet;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

public class GameProcess {

    private final GamePlugin plugin;
    private final GamePlayerManager manager;
    private final GameListener listener;
    private final GameScheduler scheduler;
    private final GameArmorTask armorTask;
    private final BukkitTask bukkitTask;
    private final BukkitTask task;


    GameProcess(GamePlugin plugin)
    {
        this.plugin = plugin;
        this.manager = new GamePlayerManager(this);
        this.listener = new GameListener(this);
        this.scheduler = new GameScheduler(this);
        this.armorTask = new GameArmorTask(this);
        this.bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this.scheduler, 0L, 1L);
        this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, new GameArmorTask(this), 0L,  1L);
        ASMEventExecutor.registerEvents(this.listener, plugin);
    }

    public  GamePlugin getPlugin()
    {
        return this.plugin;
    }

    public GameListener getListener()
    {
        return this.listener;
    }

    public GamePlayerManager getPlayerManager()
    {
        return this.manager;
    }

    public GameScheduler getScheduler()
    {
        return this.scheduler;
    }

    void unregister()
    {
        HandlerList.unregisterAll(this.listener);
        this.bukkitTask.cancel();
        this.task.cancel();
        this.scheduler.timebar.removeAll();

        for (GamePlayer gamePlayer : this.getPlayerManager().getOnlinePlayers())
        {
            Packet.ENTITY.destroy(gamePlayer.getStand().getBukkitEntity().getEntityId()).sendAll();
        }
    }

}
