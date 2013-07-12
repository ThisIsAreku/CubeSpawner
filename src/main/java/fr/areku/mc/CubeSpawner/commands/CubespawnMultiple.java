package fr.areku.mc.CubeSpawner.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.areku.mc.CubeSpawner.CubeSpawner;
import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnDirection;
import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnMode;
import fr.areku.mc.CubeSpawner.PlayersideCommand;

public class CubespawnMultiple extends PlayersideCommand {

	@Override
	public boolean doCommand(Player p, String[] args) {
		/*if (args.length < 1) {
			displayHelp();
			return false;
		}*/
		if (args.length == 1 && "stop".equalsIgnoreCase(args[0])) {
			CubeSpawner.setStopMultiple();
			sendMessage(ChatColor.BLUE + "Stopped cube spawner placing");
			return true;
		}
		if (args.length == 4) {

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

			CubeSpawner.setPlacingSpawner(ma, da, di, SpawnMode.FREE_SPACE, d, true);
			sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Okay !"
					+ ChatColor.RESET + ChatColor.BLUE
					+ " use /cubespawn-multiple stop to stop placing spawners");
			return true;
		}
		return false;
	}

	/*private void displayHelp() {
		sendMessage(ChatColor.YELLOW
				+ "Usage: /cubespawn-multiple <direction> <material> <data> <delay>"
				+ ChatColor.RESET);
		sendMessage(ChatColor.YELLOW
				+ "Alias: /csm <direction> <material> <data> <delay>"
				+ ChatColor.RESET);
		sendMessage(ChatColor.YELLOW + "Usage: /cubespawn-multiple stop"
				+ ChatColor.RESET);
		sendMessage(ChatColor.YELLOW + "Alias: /csm stop"
				+ ChatColor.RESET);

	}*/
}
