package heartin.plugin.crown;

import nemo.mc.item.NemoItemStack;
import nemo.mc.packet.Packet;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

public class GamePlayerManager {

    private GameProcess process;

    private NemoItemStack item = NemoItemStack.newInstance(1);

    private final Map<UUID, GamePlayer> playersByUniqueId;
    private final Map<Player, GamePlayer> playersByPlayer;
    private final Set<GamePlayer> onlineCrown;
    private final Set<GamePlayer> onlinePlayer;
    private Set<GamePlayer> unmodifiablePlayer;
    private Set<GamePlayer> unmodifiableCrown;

    GamePlayerManager(GameProcess process)
    {
        this.process = process;

        Collection<? extends Player> players = process.getPlugin().getServer().getOnlinePlayers();
        int size = players.size();

        Map playersByUniqueId = new HashMap(size);
        Map playersByPlayer = new HashMap(size);

        for (Player player : players)
        {
            GameMode mode = player.getGameMode();

            if (mode == GameMode.SPECTATOR)
            {
                continue;
            }

            GamePlayer gamePlayer = new GamePlayer(this, player);

            playersByUniqueId.put(gamePlayer.getUniqueId(), gamePlayer);
            playersByPlayer.put(player, gamePlayer);


        }
        if (playersByUniqueId.size() < 2)
        {
            throw new IllegalArgumentException("게임에 필요한 인원이 부족합니다 (최소 2명)");
        }
        this.playersByPlayer = playersByPlayer;
        this.playersByUniqueId = playersByUniqueId;
        this.onlineCrown = new HashSet(size);
        this.onlinePlayer = new HashSet(size);
        this.onlinePlayer.addAll(playersByPlayer.values());

    }

    void setEntity()
    {
        for (GamePlayer gamePlayer : this.getOnlinePlayers())
        {
            Player player = gamePlayer.getPlayer();

         if(gamePlayer.isCrown())
            {
                Packet.ENTITY.spawnMob(gamePlayer.getStand().getBukkitEntity()).sendAll();
                Packet.ENTITY.metadata(gamePlayer.getStand().getBukkitEntity()).sendAll();
                Packet.ENTITY.equipment(gamePlayer.getStand().getId(), EquipmentSlot.HEAD, GameConfig.getByOrdinal(gamePlayer.ordinal())).sendAll();
            }
        }
    }


    void registerGamePlayer(Player player)
    {

        GamePlayer gamePlayer = (GamePlayer) this.playersByUniqueId.get(player.getUniqueId());

        if (gamePlayer != null)
        {
            gamePlayer.setPlayer(player);
        }

        for (GamePlayer other : this.onlineCrown)
        {
            Packet.ENTITY.spawnMob(other.getStand().getBukkitEntity()).send(player);
            Packet.ENTITY.metadata(other.getStand().getBukkitEntity()).send(player);
            Packet.ENTITY.equipment(gamePlayer.getStand().getId(), EquipmentSlot.HEAD, GameConfig.getByOrdinal(gamePlayer.ordinal())).sendAll();
        }
    }

    void unreigsterGamePlayer(Player player)
    {
        GamePlayer gamePlayer = (GamePlayer) this.playersByUniqueId.remove(player);

        if (gamePlayer != null)
        {
            gamePlayer.setPlayer(null);
        }
    }

    public GamePlayer setCrown(GamePlayer gamePlayer)
    {
        this.onlinePlayer.remove(gamePlayer);
        this.onlineCrown.add(gamePlayer);
        gamePlayer.Crown();

        return gamePlayer;
    }

    public GamePlayer removeCrown(GamePlayer gamePlayer)
    {
        this.onlineCrown.remove(gamePlayer);
        this.onlinePlayer.add(gamePlayer);

        return gamePlayer;
    }


    public Set<GamePlayer> getOnlineCrown()
    {
        Set crown = this.unmodifiableCrown;

        if (crown == null)
        {
            this.unmodifiableCrown = (crown = Collections.unmodifiableSet(this.onlineCrown));
        }

        return crown;
    }

    public GamePlayer getGamePlayer(Player player)
    {

        return (GamePlayer) this.playersByPlayer.get(player);
    }

    public GamePlayer getGamePlayer(UUID uniqueId)
    {

        return this.playersByUniqueId.get(uniqueId);
    }

    private Collection<GamePlayer> players;

    public Collection<GamePlayer> getPlayers()
    {
        Collection<GamePlayer> players = this.players;

        if (players == null)
            this.players = players = Collections.unmodifiableCollection(this.playersByUniqueId.values());

        return players;
    }

    private Collection<GamePlayer> onlinePlayers;

    public Collection<GamePlayer> getOnlinePlayers()
    {
        Collection<GamePlayer> onlinePlayers = this.onlinePlayers;

        if (onlinePlayers == null)
            this.onlinePlayers = onlinePlayers = Collections.unmodifiableCollection(playersByPlayer.values());

        return onlinePlayers;
    }
}
