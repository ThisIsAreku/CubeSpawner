package fr.areku.mc.CubeSpawner;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

	private Material spM;
	private byte spB;
	private SpawnDirection spD;
	private SpawnMode spMd;
	private int spDl;

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

		getCommand("cubespawner-place").setExecutor(new CubespawnPlace());
		getCommand("cubespawner-multiple").setExecutor(new CubespawnMultiple());
		getCommand("cubespawner-edit").setExecutor(new CubespawnEdit());

		spawnerList = new SpawnerList();
		int purged = spawnerList.loadFormFlatFile(new File(getDataFolder(),
				"spawners.txt"));
		getLogger().log(Level.INFO,
				"Loaded " + spawnerList.getSpawnerListSize() + " Cube spawner");
		if (purged > 0)
			getLogger().log(Level.INFO,
					"Removed " + purged + " non-existant Cube spawner");
	}

	public void setNextSpawnerData(Material m, byte data,
			SpawnDirection direction, SpawnMode mode, int delay) {
		spM = m;
		spB = data;
		spD = direction;
		spDl = delay;
		spMd = mode;
	}

	public CubeSpawnerBlock getNextSpawnerData(Location loc) {
		double multiplier = spDl;
		if (spDl == 0)
			multiplier = 0.5;
		return new CubeSpawnerBlock(spM, spB, loc, spD, spMd,
				(int) (multiplier * 20));
	}

	public static NextActionType getNextAction() {
		return instance.nextAction;
	}

	public static void setPlacingSpawner(Material m, byte data,
			SpawnDirection direction, SpawnMode mode, int delay,
			boolean multiple) {
		instance.nextAction = NextActionType.PLACING;
		if (multiple)
			instance.nextAction = NextActionType.PLACING_MULTIPLE;
		instance.setNextSpawnerData(m, data, direction, mode, delay);
	}

	public static void setStopMultiple() {
		instance.nextAction = NextActionType.NONE;
	}

	public static void setEditingSpawner(Material m, byte data,
			SpawnDirection direction, SpawnMode mode, int delay) {
		instance.nextAction = NextActionType.EDITING;
		instance.setNextSpawnerData(m, data, direction, mode, delay);
	}

	public static boolean handleSpawnerPlacing(Block b) {
		if (b.getType().isBlock())
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
			if (instance.spawnerList.removeCubeSpawner(instance
					.getNextSpawnerData(b.getLocation()))) {
				instance.spawnerList.addCubeSpawner(instance
						.getNextSpawnerData(b.getLocation()));
				return true;
			}
		}
		return false;
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
