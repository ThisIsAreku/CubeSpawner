package fr.areku.mc.CubeSpawner;

import org.bukkit.Location;
import org.bukkit.Material;

public class CubeSpawnerBlock {
    public enum SpawnDirection {
        TOP, BOTTOM, NORTH, SOUTH, EAST, WEST;

        public static SpawnDirection matchDirection(String dir) {
            if (dir == null || dir.length() == 0)
                return null;
            String dS;
            for (SpawnDirection d : SpawnDirection.values()) {
                dS = d.toString();
                if (dS.equalsIgnoreCase(dir) || dS.charAt(0) == dir.charAt(0))
                    return d;
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
    private boolean spawnerRedstoneEnabled;

    private Location shiftedLocation = null;
    private long lastSpawn = 0;

    CubeSpawnerBlock(Material material, byte data, Location loc,
                     SpawnDirection direction, SpawnMode mode, int delay, boolean redstoneEnabled) {
        cubeMaterial = material;
        cubeData = data;
        spawnerLocation = loc;
        spawnerDirection = direction;
        spawnerMode = mode;
        spawnerDelay = delay;
        spawnerRedstoneEnabled = redstoneEnabled;
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

    public boolean isRedstoneEnabled() {
        return spawnerRedstoneEnabled;
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
        boolean can = (shiftLocation().getBlock().isEmpty() && (spawnerLocation
                .getWorld().getFullTime() - lastSpawn > spawnerDelay));
        if (spawnerRedstoneEnabled)
            can = can && spawnerLocation.getBlock().isBlockPowered();
        return can;
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
