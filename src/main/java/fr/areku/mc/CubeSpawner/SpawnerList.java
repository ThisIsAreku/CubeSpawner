package fr.areku.mc.CubeSpawner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnDirection;
import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnMode;

public class SpawnerList {
	private Map<String, CubeSpawnerBlock> spawnerList;

	SpawnerList() {
		spawnerList = Collections
				.synchronizedMap(new HashMap<String, CubeSpawnerBlock>());
	}

	/*
	 * private boolean checkSpawnerExists(CubeSpawnerBlock spawner) { return
	 * (spawner.getSpawnerLocation().getBlock() != null); }
	 */

	public int loadFormFlatFile(File f) {
		if (!f.exists())
			return -1;
		int purged = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			String strLine;
			// Read File Line By Line
			String data[];
			World w;
			int x, y, z;
			Material m;
			byte d;
			SpawnDirection sD;
			SpawnMode sM;
			int del;
			Location l;

			while ((strLine = br.readLine()) != null) {
				try {
					data = strLine.split(":");
					w = Bukkit.getWorld(UUID.fromString(data[0]));
					x = Integer.parseInt(data[1]);
					y = Integer.parseInt(data[2]);
					z = Integer.parseInt(data[3]);
					m = Material.getMaterial(data[4]);
					d = Byte.parseByte(data[5]);
					sD = SpawnDirection.valueOf(data[6]);
					sM = SpawnMode.valueOf(data[7]);
					del = Integer.parseInt(data[8]);

					if (w != null && m.isBlock()) {
						l = new Location(w, x, y, z);
						if (!l.getBlock().isEmpty()) {
							addCubeSpawner(new CubeSpawnerBlock(m, d, l, sD,
									sM, del));
						} else {
							purged++;
						}
					} else {
						purged++;
					}
				} catch (Exception e) {
					CubeSpawner.d("Cannot parse line " + strLine);
				}
			}
		} catch (IOException e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return purged;
	}

	public int saveToFlatFile(File f) {
		int purged = 0;
		String dataSep = ":";
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(f));
			synchronized (spawnerList) {
				for (CubeSpawnerBlock b : spawnerList.values()) {
					if (!b.getSpawnerLocation().getBlock().isEmpty()) {
						out.write(b.getSpawnerLocation().getWorld().getUID()
								.toString());
						out.write(dataSep);
						out.write(b.getSpawnerLocation().getBlockX() + "");
						out.write(dataSep);
						out.write(b.getSpawnerLocation().getBlockY() + "");
						out.write(dataSep);
						out.write(b.getSpawnerLocation().getBlockZ() + "");
						out.write(dataSep);
						out.write(b.getCubeMaterial().name());
						out.write(dataSep);
						out.write(b.getCubeData() + "");
						out.write(dataSep);
						out.write(b.getSpawnerDirection().name());
						out.write(dataSep);
						out.write(b.getSpawnMode().name());
						out.write(dataSep);
						out.write(b.getDelay() + "");
						out.newLine();
					} else {
						purged++;
					}
				}
			}
		} catch (IOException e) {

		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return purged;
	}

	public int getSpawnerListSize() {
		return spawnerList.size();
	}

	private String getKey(Location l) {
		return l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":"
				+ l.getWorld().getUID();
	}

	private boolean checkCubeSpawner(Location l) {
		synchronized (spawnerList) {
			String k = getKey(l);
			// System.out.println("Check for " + k);
			if (spawnerList.containsKey(k)) {
				// System.out.println("delaySpawn !");
				if (!spawnerList.get(k).exist()) {
					removeCubeSpawner(spawnerList.get(k));
					return false;
				} else {
					CubeSpawner.delaySpawn(spawnerList.get(k));
					return true;
				}
			}
			return false;
		}
	}

	public CubeSpawnerBlock getCubeSpawner(Location l) {
		synchronized (spawnerList) {
			String keyf = getKey(l);
			if (spawnerList.containsKey(keyf))
				return spawnerList.get(keyf);
		}
		return null;
	}

	public void addCubeSpawner(CubeSpawnerBlock cs) {
		synchronized (spawnerList) {
			// System.out.println("addCubeSpawner");
			spawnerList.put(getKey(cs.getSpawnerLocation()), cs);
			cs.doSpawn();
		}
	}

	public boolean removeCubeSpawner(CubeSpawnerBlock cs) {
		synchronized (spawnerList) {
			return removeCubeSpawner(cs.getSpawnerLocation());
		}
	}

	public boolean removeCubeSpawner(Location loc) {
		// CubeSpawnerBlock cs = getCubeSpawner(loc);
		if (spawnerList.containsKey(getKey(loc))) {
			// System.out.println("Removed CSBlock");
			spawnerList.remove(getKey(loc));
			return true;
		} else {
			// System.out.println("no CSBlock");
			return false;
		}

	}

	public void checkNearSpawner(Location loc) {
		// x

		checkCubeSpawner(loc.add(1, 0, 0));
		checkCubeSpawner(loc.subtract(2, 0, 0));
		// y
		checkCubeSpawner(loc.add(1, 1, 0));
		checkCubeSpawner(loc.subtract(0, 2, 0));
		// z
		checkCubeSpawner(loc.add(0, 1, 1));
		checkCubeSpawner(loc.subtract(0, 0, 1));
	}

}
