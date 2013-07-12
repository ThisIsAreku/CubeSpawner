package fr.areku.mc.CubeSpawner.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.areku.mc.CubeSpawner.CubeSpawner;
import fr.areku.mc.CubeSpawner.CubeSpawner.NextActionType;

public class EventListener implements Listener {

	@SuppressWarnings("unused")
	private boolean allowPropagation(Material m) {
		return (m == Material.SAND || m == Material.GRAVEL);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	private void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().hasPermission("cubespawner.admin"))
			if (CubeSpawner.handleSpawnerDestroying(event.getBlock())) {
				event.getPlayer().sendMessage(
						ChatColor.GREEN + "You destroyed a Cube spawner !");
			}
		CubeSpawner.handleSpawnerMoving(event.getBlock());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	private void onBlockPhysics(BlockPhysicsEvent event) {
		CubeSpawner.handleSpawnerMoving(event.getBlock());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	private void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (!event.isSticky()) {
			CubeSpawner.handleSpawnerMoving(event.getRetractLocation());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	private void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().hasPermission("cubespawner.admin"))
			if (CubeSpawner.handleSpawnerPlacing(event.getBlockPlaced())) {
				event.getPlayer().sendMessage(
						ChatColor.GREEN + "You placed a Cube spawner !");
			}
		CubeSpawner.handleSpawnerMoving(event.getBlock());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	private void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getPlayer().hasPermission("cubespawner.admin"))
			if ((event.getAction() == Action.RIGHT_CLICK_BLOCK)
					&& (CubeSpawner.getNextAction() == NextActionType.EDITING)) {
				if (CubeSpawner.handleSpawnerEdit(event.getClickedBlock())) {
					event.getPlayer().sendMessage(
							ChatColor.GREEN + "You edited a Cube spawner !");
				} else {
					event.getPlayer().sendMessage(
							ChatColor.RED + "It was not a Cube spawner .. :/");
				}
			}
		CubeSpawner.handleSpawnerMoving(event.getClickedBlock());
	}
}
