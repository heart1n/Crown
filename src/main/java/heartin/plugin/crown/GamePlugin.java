package heartin.plugin.crown;

import heartin.plugin.crown.command.CommandGameStart;
import heartin.plugin.crown.command.CommandGameStop;
import nemo.mc.command.bukkit.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GamePlugin extends JavaPlugin {


    private static GamePlugin instance;

    private GameProcess process;

    public static GamePlugin getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
      instance = this;
        setupCommand();
        saveDefaultConfig();
        reloadConfig();
    }

    private void setupCommand()
    {
        new CommandManager().addHelp("help").addComponent("start", new CommandGameStart()).addComponent("stop", new CommandGameStop()).register(getCommand("game"));
    }

    public boolean processStart()
    {
        if(this.process != null)
        {
            return false;
        }

        saveDefaultConfig();
        reloadConfig();
        GameConfig.load(getConfig());

        this.process = new GameProcess(this);

        return true;
    }

    public boolean processStop()
    {
        if (this.process == null) {
            return false;
        }
        this.process.unregister();
        this.process = null;

        return true;
    }

    public GameProcess getProcess()
    {
        return process;
    }


}
