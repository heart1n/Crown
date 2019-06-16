package heartin.plugin.crown;

import com.google.common.collect.ImmutableList;
import nemo.mc.item.NemoItemStack;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {


    public static double standOffsetY;
    public static int timeTicks;
    public static List<NemoItemStack> blocksById;

    public static void load(Configuration config)
    {
        standOffsetY = config.getDouble("stand-offset-y");
        timeTicks = config.getInt("time-ticks");

        List<String> list = config.getStringList("blocks");
        List<NemoItemStack> blocks = new ArrayList<>();

        for (String s : list)
        {
            int id;
            int data;

            if (s.contains(":"))
            {
                String[] split = s.split(":");
                id = Integer.parseInt(split[0]);
                data = Integer.parseInt(split[1]);
            }
            else
            {
                id = Integer.parseInt(s);
                data = 0;
            }
            blocks.add(NemoItemStack.newInstance(id, 1, data));
        }

        blocksById = ImmutableList.copyOf(blocks);
    }

    public static NemoItemStack getByOrdinal(int ordinal)
    {
        return blocksById.get(ordinal);
    }

}
