package fr.areku.mc.CubeSpawner;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayersideCommand implements CommandExecutor {
	private CommandSender cs;

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage("This command is player-only");
			return true;
		}
		cs = arg0;
		return doCommand((Player) arg0, arg3);
	}

	public abstract boolean doCommand(Player p, String[] args);

	public void sendMessage(String message) {
		cs.sendMessage(message);
	}
}
