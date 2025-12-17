package app.goldbach.hartzAttackUtil.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public final class SpawnConfig {

    private static final String PATH_SPAWN = "spawn";
    private static final String PATH_WORLD = PATH_SPAWN + ".world";
    private static final String PATH_START = PATH_SPAWN + ".start";
    private static final String PATH_END = PATH_SPAWN + ".end";
    private static final String PATH_ELYTRA_HEIGHT = PATH_SPAWN + ".elytra-height";

    private static final String PATH_X = "x";
    private static final String PATH_Y = "y";
    private static final String PATH_Z = "z";

    private final JavaPlugin plugin;

    private String worldName;
    private BlockPoint start;
    private BlockPoint end;
    private int elytraHeight;

    public SpawnConfig(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        loadFromBukkitConfig();
    }

    public void setSpawn(String worldName, BlockPoint start, BlockPoint end) {
        Objects.requireNonNull(worldName, "worldName");
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");

        plugin.getConfig().set(PATH_WORLD, worldName);

        writeBlockPoint(PATH_START, start);
        writeBlockPoint(PATH_END, end);

        plugin.saveConfig();
        plugin.reloadConfig();
        loadFromBukkitConfig();
    }

    public void setSpawnFromLocations(Location start, Location end) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (start.getWorld() == null || end.getWorld() == null) {
            throw new IllegalArgumentException("Start/End Location muss eine World haben.");
        }
        if (!start.getWorld().equals(end.getWorld())) {
            throw new IllegalArgumentException("Start und End müssen in derselben World sein.");
        }

        setSpawn(
            start.getWorld().getName(),
            new BlockPoint(start.getBlockX(), start.getBlockY(), start.getBlockZ()),
            new BlockPoint(end.getBlockX(), end.getBlockY(), end.getBlockZ())
        );
    }

    /** Block-mittig (0.5) ist für Teleports oft angenehmer als exakt auf Blockkante. */
    public Location teleportLocationCentered() {
        World world = resolveWorld();

        int minX = Math.min(start.x(), end.x());
        int maxX = Math.max(start.x(), end.x());
        int minY = Math.min(start.y(), end.y());
        int maxY = Math.max(start.y(), end.y());
        int minZ = Math.min(start.z(), end.z());
        int maxZ = Math.max(start.z(), end.z());

        double centerX = (minX + maxX + 1) / 2.0;
        double centerY = (minY + maxY + 1) / 2.0;
        double centerZ = (minZ + maxZ + 1) / 2.0;

        return new Location(world, centerX, centerY, centerZ);
    }

    private void loadFromBukkitConfig() {
        ConfigurationSection spawn = plugin.getConfig().getConfigurationSection(PATH_SPAWN);
        if (spawn == null) {
            throw new IllegalStateException("config.yml: Abschnitt '" + PATH_SPAWN + "' fehlt");
        }

        this.worldName = plugin.getConfig().getString(PATH_WORLD, "world");
        this.start = readBlockPointRequired(spawn, "start");
        this.end = readBlockPointRequired(spawn, "end");
        this.elytraHeight = plugin.getConfig().getInt(PATH_ELYTRA_HEIGHT, 200);
    }

    private void writeBlockPoint(String basePath, BlockPoint point) {
        plugin.getConfig().set(basePath + "." + PATH_X, point.x());
        plugin.getConfig().set(basePath + "." + PATH_Y, point.y());
        plugin.getConfig().set(basePath + "." + PATH_Z, point.z());
    }

    private World resolveWorld() {
        World world = Bukkit.getWorld(worldName);
        if (world != null) return world;

        World fallback = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().getFirst();
        if (fallback == null) {
            throw new IllegalStateException("Keine World geladen; kann Spawn-World '" + worldName + "' nicht auflösen.");
        }

        plugin.getLogger().warning("World '" + worldName + "' nicht gefunden, nutze Fallback-World '" + fallback.getName() + "'.");
        return fallback;
    }

    private static BlockPoint readBlockPointRequired(ConfigurationSection parent, String path) {
        ConfigurationSection section = parent.getConfigurationSection(path);
        if (section == null) {
            throw new IllegalStateException("config.yml: Abschnitt 'spawn." + path + "' fehlt");
        }

        int x = section.getInt("x");
        int y = section.getInt("y");
        int z = section.getInt("z");
        return new BlockPoint(x, y, z);
    }

    public record BlockPoint(int x, int y, int z) {}
}