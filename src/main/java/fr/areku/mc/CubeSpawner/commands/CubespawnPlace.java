package fr.areku.mc.CubeSpawner.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.areku.mc.CubeSpawner.CubeSpawner;
import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnDirection;
import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnMode;
import fr.areku.mc.CubeSpawner.PlayersideCommand;

public class CubespawnPlace extends PlayersideCommand {

	@Override
	public boolean doCommand(Player p, String[] args) {
		if (args.length == 4) {
			// /cubespawn B SAND 0 0
			SpawnDirection di = SpawnDirection.matchDirection(args[0]);
			Material ma;
			try {
				ma = Material.getMaterial(Integer.parseInt(args[1]));
			} catch (Exception e) {
				ma = Material.matchMaterial(args[1]);
			}
			if (!ma.isBlock()) {
				sendMessage(ChatColor.RED + "" + ChatColor.BOLD + ma.toString()
						+ ChatColor.RESET + ChatColor.RED
						+ " is not a valid block");
				return true;
			}
			byte da = Byte.parseByte(args[2]);
			int d = Integer.parseInt(args[3]);

			CubeSpawner.setPlacingSpawner(ma, da, di, SpawnMode.FREE_SPACE, d, false);
			sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Okay !"
					+ ChatColor.RESET + ChatColor.BLUE
					+ " Now place a block to be the spawner");
			return true;
		}/* else {
			sendMessage(ChatColor.YELLOW
					+ "Usage: /cubespawn-place <direction> <block> <data> <delay>"
					+ ChatColor.RESET);
			sendMessage(ChatColor.YELLOW
					+ "Alias: /csp <direction> <block> <data> <delay>"
					+ ChatColor.RESET);
		}*/
		return false;
	}

}
