package heartin.plugin.crown;

import nemo.mc.entity.NemoArmorStand;
import nemo.mc.entity.NemoEntity;
import nemo.mc.entity.NemoPlayer;
import nemo.mc.packet.Packet;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayer {

    private final GamePlayerManager manager;
    private final UUID uniqueId;
    private final String name;
    private Player player;
    private boolean crown;
    private final NemoArmorStand stand = NemoEntity.createEntity(ArmorStand.class);
    private int ordinal;

    GamePlayer(GamePlayerManager manager, Player player)
    {
        this.manager = manager;
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();

        setPlayer(player);
        onUpdate();
        stand.setInvisible(true);
    }

    void setPlayer(Player player)
    {
        this.player = player;
    }

    public int ordinal()
    {
        return this.ordinal;
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public UUID getUniqueId()
    {
        return this.uniqueId;
    }

    public String getName()
    {
        return this.name;
    }

    public NemoArmorStand getStand()
    {
        return this.stand;
    }

    public void onUpdate()
    {
        if (player == null)
            return;

        NemoPlayer np = NemoPlayer.fromPlayer(this.player);
        this.stand.setPositionAndRotation(np.getPosX(), np.getPosY() + GameConfig.standOffsetY, np.getPosZ(), 0F, 0F);
    }

    public boolean isOnline()
    {
        return this.player != null;
    }

    public boolean isCrown()
    {
        return this.crown;
    }

    public boolean Crown()
    {
        return this.crown = true;
    }

    void setCitizen()
    {
        if (!this.crown)
            return;

        this.crown = false;
        this.manager.removeCrown(this);
        Packet.ENTITY.destroy(this.getStand().getBukkitEntity().getEntityId()).sendAll();
        player.sendMessage("이제 시민입니다.");
    }


    void setCrown()
    {
        if (this.crown)
            return;

        this.crown = true;
        this.manager.setCrown(this);
        this.manager.setEntity();
        player.sendMessage("당신이 왕관입니다.");
    }

}
