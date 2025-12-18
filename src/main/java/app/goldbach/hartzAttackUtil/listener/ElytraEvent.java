package app.goldbach.hartzAttackUtil.listener;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import app.goldbach.hartzAttackUtil.config.SpawnConfig;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.ArrayList;

@RequiredArgsConstructor
public class ElytraEvent implements Listener {

    private final HartzAttackUtil plugin;

    ArrayList<Player> fly = new ArrayList<>();

    @EventHandler
    public void onDoubleJump(final PlayerToggleFlightEvent event) {
        final Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (!player.hasPermission("hartzattack.elytra.use")) {
            return;
        }

        if (!this.isFlying(player) && isInSpawnArea(player)) {
            plugin.getLogger().info(String.format("%s hat das Gleiten ausgeloest.", player.getName()));
            event.setCancelled(true);
            activateFlight(player);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!isInSpawnArea(player)) {
            if (player.getAllowFlight() && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                enableFlightMode(player, false);
            }
            return;
        }

        // Creative/Spectator nicht anfassen
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        // Wenn gerade "Elytra-Mode" aktiv ist: allowFlight nicht umschalten
        if (isFlying(player)) {
            return;
        }

        boolean eligible = player.hasPermission("hartzattack.elytra.use");

        // allowFlight nur bei Bedarf setzen, damit wir nichts “spammen”
        if (eligible && !player.getAllowFlight()) {
            enableFlightMode(player, true);
        } else if (!eligible && player.getAllowFlight()) {
            enableFlightMode(player, false);
        }
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (fly.contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRocketOrTridentUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!fly.contains(player)) return;

        ItemStack item = event.getItem();
        if (item == null || (item.getType() != Material.FIREWORK_ROCKET && item.getType() != Material.TRIDENT)) return;

        EquipmentSlot hand = event.getHand();
        if (hand == null) return;

        event.setCancelled(true);
    }

    private boolean isFlying(Player player) {
        return fly.contains(player);
    }

    private boolean isInSpawnArea(Player player) {
        Location loc = player.getLocation();
        if (loc.getWorld() == null) return false;

        SpawnConfig spawn = plugin.pluginConfig().spawn();

        // World prüfen
        if (!loc.getWorld().getName().equalsIgnoreCase(spawn.getWorldName())) {
            return false;
        }

        // Höhe prüfen (nur über der konfigurierten Elytra-Höhe aktivieren)
        if (loc.getBlockY() < spawn.getElytraHeight()) {
            return false;
        }

        // Cuboid (Start/End) prüfen
        SpawnConfig.BlockPoint start = spawn.getStart();
        SpawnConfig.BlockPoint end = spawn.getEnd();

        int minX = Math.min(start.x(), end.x());
        int maxX = Math.max(start.x(), end.x());
        int minY = Math.min(start.y(), end.y());
        int maxY = Math.max(start.y(), end.y());
        int minZ = Math.min(start.z(), end.z());
        int maxZ = Math.max(start.z(), end.z());

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return x >= minX && x <= maxX
            && y >= minY && y <= maxY
            && z >= minZ && z <= maxZ;
    }

    private void activateFlight(Player player) {
        player.setGliding(true);
        player.setVelocity(player.getLocation().getDirection().setY(1));
        fly.add(player);
        enableFlightMode(player, false);

        Bukkit.getScheduler().runTaskTimer(plugin, bukkitTask -> {
            if (isGrounded(player)) {
                player.setGliding(false);
                enableFlightMode(player, player.hasPermission("spawnelytra.use") && isInSpawnArea(player));
                fly.remove(player);
                plugin.getLogger().info(String.format("Spieler %s ist gelandet. Der Flugmodus wurde beendet.", player.getName()));
                bukkitTask.cancel();
            }
        }, 0, 2);
    }

    private void enableFlightMode(Player player, boolean enable) {
        plugin.getLogger().info(String.format("Flugmodus fuer %s umgestellt: %s", player.getName(), enable));
        player.setAllowFlight(enable);
        if (enable) {
            Title title = Title.title(
                Component.text("Flug bereit!", NamedTextColor.AQUA),
                Component.text("Doppelsprung in der Luft zum Gleiten", NamedTextColor.WHITE),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(4), Duration.ofMillis(400))
            );
            player.showTitle(title);
        }
    }

    private boolean isGrounded(Player player) {
        return player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid();
    }

}
