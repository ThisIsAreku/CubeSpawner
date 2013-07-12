package fr.areku.mc.CubeSpawner;

import org.bukkit.Location;
import org.bukkit.Material;

public class CubeSpawnerBlock {
	public enum SpawnDirection {
		TOP, BOTTOM, NORTH, SOUTH, EAST, WEST;
		public static SpawnDirection matchDirection(String dir) {
			String v = dir.toLowerCase();
			if ("t".equals(v) || "top".equals(v)) {
				return TOP;
			} else if ("b".equals(v) || "bottom".equals(v)) {
				return BOTTOM;
			} else if ("n".equals(v) || "north".equals(v)) {
				return NORTH;
			} else if ("s".equals(v) || "south".equals(v)) {
				return SOUTH;
			} else if ("e".equals(v) || "east".equals(v)) {
				return EAST;
			} else if ("w".equals(v) || "west".equals(v)) {
				return WEST;
			}
			return null;
		}
	}

	public enum SpawnMode {
		FREE_SPACE, TIMED
	}

	private Material cubeMaterial;
	private byte cubeData;

	private Location spawnerLocation;
	private SpawnDirection spawnerDirection;
	private SpawnMode spawnerMode;
	private int spawnerDelay;

	private Location shiftedLocation = null;
	private long lastSpawn = 0;

	CubeSpawnerBlock(Material material, byte data, Location loc,
			SpawnDirection direction, SpawnMode mode, int delay) {
		cubeMaterial = material;
		cubeData = data;
		spawnerLocation = loc;
		spawnerDirection = direction;
		spawnerMode = mode;
		spawnerDelay = delay;
	}

	public Material getCubeMaterial() {
		return cubeMaterial;
	}

	public byte getCubeData() {
		return cubeData;
	}

	public Location getSpawnerLocation() {
		return spawnerLocation;
	}

	public SpawnDirection getSpawnerDirection() {
		return spawnerDirection;
	}

	public SpawnMode getSpawnMode() {
		return spawnerMode;
	}

	public int getDelay() {
		return spawnerDelay;
	}

	public boolean exist() {
		return (!spawnerLocation.getBlock().isEmpty());
	}

	public boolean isNear(Location loc) {
		return ((Math.abs(spawnerLocation.getBlockX() - loc.getBlockX()) == 1)
				|| (Math.abs(spawnerLocation.getBlockY() - loc.getBlockY()) == 1) || (Math
					.abs(spawnerLocation.getBlockZ() - loc.getBlockZ()) == 1));
	}

	public boolean canSpawn() {
		return (shiftLocation().getBlock().isEmpty() && (spawnerLocation
				.getWorld().getFullTime() - lastSpawn > spawnerDelay));
	}

	public void doSpawn() {
		if (canSpawn()) {
			lastSpawn = spawnerLocation.getWorld().getFullTime();
			shiftLocation().getBlock().setTypeIdAndData(cubeMaterial.getId(),
					cubeData, true);
			CubeSpawner.d("doSpawn:" + this.toString());
		}
	}

	private Location shiftLocation() {
		if (shiftedLocation == null) {
			shiftedLocation = spawnerLocation.clone();
			switch (spawnerDirection) {
			case TOP:
				shiftedLocation.add(0, 1, 0);
				break;
			case BOTTOM:
				shiftedLocation.add(0, -1, 0);
				break;
			case EAST:
				shiftedLocation.add(-1, 0, 0);
				break;
			case NORTH:
				shiftedLocation.add(0, 0, -1);
				break;
			case SOUTH:
				shiftedLocation.add(0, 0, 1);
				break;
			case WEST:
				shiftedLocation.add(1, 0, 0);
				break;
			}
		}
		return shiftedLocation;
	}

	@Override
	public String toString() {
		return "{" + cubeMaterial + ":" + cubeData + ", "
				+ spawnerLocation.toString() + ", " + spawnerDirection + ", "
				+ spawnerMode + ", " + spawnerDelay + "}";
	}

}
