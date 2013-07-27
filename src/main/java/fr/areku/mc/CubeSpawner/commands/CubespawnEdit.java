package fr.areku.mc.CubeSpawner.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.areku.mc.CubeSpawner.CubeSpawner;
import fr.areku.mc.CubeSpawner.PlayersideCommand;
import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnDirection;
import fr.areku.mc.CubeSpawner.CubeSpawnerBlock.SpawnMode;

import java.util.ArrayList;
import java.util.List;

public class CubespawnEdit extends PlayersideCommand implements TabCompleter {

    @Override
    public boolean doCommand(Player p, String[] args) {
        if (args.length == 4 || args.length == 5) {
            // /cubespawn B SAND 0 0
            SpawnDirection di = SpawnDirection.matchDirection(args[0]);
            Material ma;
            try {
                ma = Material.getMaterial(Integer.parseInt(args[1]));
            } catch (Exception e) {
                ma = Material.matchMaterial(args[1]);
            }
            if (!CubeSpawner.canPlace(ma)) {
                sendMessage(ChatColor.RED + "You can't place " + ChatColor.BOLD + ma.toString()
                        + ChatColor.RESET + ChatColor.RED
                        + " !");
                return true;
            }
            byte da = Byte.parseByte(args[2]);
            int d = Integer.parseInt(args[3]);

            boolean red = false;
            if (args.length == 5) {
                red = (args[4].equalsIgnoreCase("true"));
            }

            CubeSpawner.setEditingSpawner(ma, da, di, SpawnMode.FREE_SPACE, d, red);
            sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Okay !"
                    + ChatColor.RESET + ChatColor.BLUE
                    + " Now right-click on the spawner !");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        String current;
        List<String> ret = null;
        if (strings.length == 0) {
            ret = new ArrayList<String>();
            for (SpawnDirection spd : SpawnDirection.values())
                ret.add(spd.toString());
        } else if (strings.length == 1) {
            current = strings[0];
            ret = new ArrayList<String>();
            for (SpawnDirection spd : SpawnDirection.values())
                if (spd.toString().toUpperCase().startsWith(current.toUpperCase()))
                    ret.add(spd.toString());

        } else if (strings.length == 2) {
            current = strings[1];
            ret = new ArrayList<String>();
            for (Material mt : Material.values())
                if (CubeSpawner.canPlace(mt) && mt.toString().toUpperCase().startsWith(current.toUpperCase()))
                    ret.add(mt.toString());

        }
        return ret;
    }
}
