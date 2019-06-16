package heartin.plugin.crown;

import nemo.mc.entity.NemoArmorStand;
import nemo.mc.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GameArmorTask implements  Runnable {


    private final GameProcess process;


    GameArmorTask(GameProcess process)
    {
        this.process = process;
    }

    @Override
    public void run()
    {


        for (Player player : Bukkit.getOnlinePlayers()) {
            for (GamePlayer gamePlayer : this.process.getPlayerManager().getOnlinePlayers()) {

                gamePlayer.onUpdate();

                NemoArmorStand stand = gamePlayer.getStand();

                Vector vec = stand.getBukkitEntity().getEyeLocation().subtract(player.getEyeLocation()).toVector().normalize();
                double yaw = -Math.toDegrees(Math.atan2(vec.getX(), vec.getZ()));
                double pitch = -Math.toDegrees(Math.asin(vec.getY()));
                Location loc = player.getLocation();
                loc.setYaw((float) yaw);
                Packet.ENTITY.teleport(stand.getBukkitEntity(), stand.getPosX(), stand.getPosY(), stand.getPosZ(), (float) yaw, (float) pitch, false).send(player);
            }
        }

    }


}
