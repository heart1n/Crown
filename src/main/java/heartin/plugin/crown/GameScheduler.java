package heartin.plugin.crown;

import nemo.mc.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScheduler implements  Runnable{


    public BossBar timebar = Bukkit.createBossBar("§c남은 시간 : §e<remainTime> §7초", BarColor.WHITE, BarStyle.SOLID, new BarFlag[]{BarFlag.PLAY_BOSS_MUSIC});
    private final GameProcess process;
    private GameTask task;

    GameScheduler(GameProcess process)
    {
        this.process = process;
        this.task = new SelectCrown();
    }

    public void run()
    {
        this.task = this.task.run();
    }

    private class SelectCrown  implements  GameTask {
        public SelectCrown()
        {
            for (GamePlayer gamePlayer : process.getPlayerManager().getOnlinePlayers())
            {
                gamePlayer.onUpdate();
            }
        }

        public GameTask run() {


            GameProcess process = GameScheduler.this.process;

            List<GamePlayer> players = new ArrayList(process.getPlayerManager().getOnlinePlayers());

            Random random = new Random();
            GamePlayer crown =  (GamePlayer)players.remove(random.nextInt(players.size()));

            if(crown.isOnline())
            {
                Player player = crown.getPlayer();
                GamePlayer gamePlayer = (GamePlayer) process.getPlayerManager().getGamePlayer(player);

                process.getPlayerManager().setCrown(gamePlayer);

               player.sendMessage("§cset Entity");
            }

            for (GamePlayer gamePlayer : process.getPlayerManager().getOnlinePlayers())
            {

                Player player = gamePlayer.getPlayer();

                Packet titlePacket = Packet.TITLE.compound("§6왕관 쟁탈을 시작합니다!", "§7", 5, 60, 10);
                titlePacket.send(player);
            }

            process.getPlayerManager().setEntity();

            return new MainTask();
            }
            //task run

    }

    private class MainTask implements  GameTask {

        private int remainTicks = GameConfig.timeTicks;


        MainTask()
        {
            updateTime();

        }

        @Override
        public GameTask run()
        {
            if (--this.remainTicks > 0)
            {
                updateTime();

                return this;
            }


            return this;
        }



        private void updateTime() {
            int remainTicks = this.remainTicks;

            if (remainTicks >= 100) {
                if (remainTicks % 2 == 0) {
                    int seconds = remainTicks / 20;

                    for (GamePlayer gamePlayer : process.getPlayerManager().getOnlinePlayers()) {
                        Player player = gamePlayer.getPlayer();

                        timebar.addPlayer(player);
                    }

                    timebar.setProgress(seconds / 30.0);

                    timebar.setColor(BarColor.BLUE);
                    timebar.setTitle(String.format("§d남은시간§r§l %02d:%02d§r ", new Object[]{Integer.valueOf(seconds / 60), Integer.valueOf(seconds % 60)}));
                }
            } else {
                remainTicks += 19;

                if (remainTicks % 5 == 0) {
                    char color = remainTicks / 5 % 2 == 0 ? 'f' : 'c';
                    int seconds = remainTicks / 20;

                    /*if ( seconds / 5.0 < 0.6){
                        timebar.setColor(BarColor.YELLOW);
                    }else if ( seconds / 5.0 < 0.3){
                        timebar.setColor(BarColor.RED);
                    }*/
                    timebar.setProgress(seconds / 30.0);
                    timebar.setTitle(String.format("§d남은시간 §%c§l%02d:%02d§r ", new Object[]{Character.valueOf(color), Integer.valueOf(seconds / 60), Integer.valueOf(seconds % 60)}));
                }
            }
        }

    }




}
