package fr.areku.mc.CubeSpawner;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnDirection;
import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnMode;
import fr.areku.mc.CubeSpawner.commands.CubespawnEdit;
import fr.areku.mc.CubeSpawner.commands.CubespawnPlace;
import fr.areku.mc.CubeSpawner.commands.CubespawnMultiple;
import fr.areku.mc.CubeSpawner.listeners.EventListener;

public class CubeSpawner extends JavaPlugin {
    private static CubeSpawner instance = null;

    public static boolean DEBUG = false;

    public static void d(String message) {
        if (DEBUG)
            instance.getLogger().log(Level.OFF, "[DEBUG] " + message);
    }

    public enum NextActionType {
        PLACING, PLACING_MULTIPLE, EDITING, NONE
    }

    private SpawnerList spawnerList;

    private NextActionType nextAction = NextActionType.NONE;

    private boolean dataAvailable;
    private Material spM;
    private byte spB;
    private SpawnDirection spD;
    private SpawnMode spMd;
    private int spDl;
    private boolean spRe;

    @Override
    public void onDisable() {
        spawnerList.saveToFlatFile(new File(getDataFolder(), "spawners.txt"));
        getLogger().log(Level.INFO,
                "Saved " + spawnerList.getSpawnerListSize() + " Cube spawner");
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists())
            getDataFolder().mkdirs();

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);


        PluginCommand c = getCommand("cubespawner-place");
        Object assign = new CubespawnPlace();
        c.setExecutor((CommandExecutor) assign);
        c.setTabCompleter((TabCompleter) assign);

        c = getCommand("cubespawner-multiple");
        assign = new CubespawnMultiple();
        c.setExecutor((CommandExecutor) assign);
        c.setTabCompleter((TabCompleter) assign);

        c = getCommand("cubespawner-edit");
        assign = new CubespawnEdit();
        c.setExecutor((CommandExecutor) assign);
        c.setTabCompleter((TabCompleter) assign);

        spawnerList = new SpawnerList();
        int purged = spawnerList.loadFormFlatFile(new File(getDataFolder(),
                "spawners.txt"));
        getLogger().log(Level.INFO,
                "Loaded " + spawnerList.getSpawnerListSize() + " Cube spawner");
        if (purged > 0)
            getLogger().log(Level.INFO,
                    "Removed " + purged + " non-existant Cube spawner");

        dataAvailable = false;
    }

    public void setNextSpawnerData(Material m, byte data,
                                   SpawnDirection direction, SpawnMode mode, int delay, boolean redstone) {
        spM = m;
        spB = data;
        spD = direction;
        spDl = delay;
        spMd = mode;
        spRe = redstone;
        dataAvailable = true;
    }

    public CubeSpawnerBlock getNextSpawnerData(Location loc) {
        if(!dataAvailable)
            return null;
        double multiplier = spDl;
        if (spDl == 0)
            multiplier = 0.5;
        CubeSpawnerBlock block =  new CubeSpawnerBlock(spM, spB, loc, spD, spMd,
                (int) (multiplier * 20), spRe);
        dataAvailable = false;
        return block;
    }

    public static NextActionType getNextAction() {
        return instance.nextAction;
    }

    public static void setPlacingSpawner(Material m, byte data,
                                         SpawnDirection direction, SpawnMode mode, int delay,
                                         boolean multiple, boolean redstone) {
        instance.nextAction = NextActionType.PLACING;
        if (multiple)
            instance.nextAction = NextActionType.PLACING_MULTIPLE;
        instance.setNextSpawnerData(m, data, direction, mode, delay, redstone);
    }

    public static void setStopMultiple() {
        instance.nextAction = NextActionType.NONE;
    }

    public static void setEditingSpawner(Material m, byte data,
                                         SpawnDirection direction, SpawnMode mode, int delay, boolean redstone) {
        instance.nextAction = NextActionType.EDITING;
        instance.setNextSpawnerData(m, data, direction, mode, delay, redstone);
    }

    public static boolean handleSpawnerPlacing(Block b) {
        if (b.getType().isBlock() && b.getType().isSolid())
            if ((instance.nextAction == NextActionType.PLACING)
                    || (instance.nextAction == NextActionType.PLACING_MULTIPLE)) {
                // System.out.println("handleSpawnerPlacing:" + b.toString());
                if (instance.nextAction == NextActionType.PLACING)
                    instance.nextAction = NextActionType.NONE;

                instance.spawnerList.addCubeSpawner(instance
                        .getNextSpawnerData(b.getLocation()));
                return true;
            }
        return false;
    }

    public static boolean handleSpawnerDestroying(Block b) {
        return instance.spawnerList.removeCubeSpawner(b.getLocation());
    }

    public static void handleSpawnerMoving(Block b) {
        instance.spawnerList.checkNearSpawner(b.getLocation());
    }

    public static void handleSpawnerMoving(Location l) {
        instance.spawnerList.checkNearSpawner(l);
    }

    public static boolean handleSpawnerEdit(Block b) {
        if (instance.nextAction == NextActionType.EDITING) {
            instance.nextAction = NextActionType.NONE;
            if (instance.spawnerList.removeCubeSpawner(b.getLocation())) {
                instance.spawnerList.addCubeSpawner(instance
                        .getNextSpawnerData(b.getLocation()));
                return true;
            }
        }
        return false;
    }

    public static boolean canPlace(Material m) {
        return m.isBlock() && m.isSolid() && m != Material.AIR;
    }

    public static void delaySpawn(final CubeSpawnerBlock cs) {
        Bukkit.getScheduler().runTaskLater(instance, new Runnable() {

            @Override
            public void run() {
                cs.doSpawn();
            }

        }, cs.getDelay());
    }

}
