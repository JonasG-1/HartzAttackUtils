package app.goldbach.hartzAttackUtil.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SpawnConfig {

    private final JavaPlugin plugin;

    private final String worldName;
    private final BlockPoint start;
    private final BlockPoint end;
    private final int elytraHeight;

    public SpawnConfig(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");

        ConfigurationSection spawn = plugin.getConfig().getConfigurationSection("spawn");
        if (spawn == null) {
            throw new IllegalStateException("config.yml: Abschnitt 'spawn' fehlt");
        }

        this.worldName = spawn.getString("world", "world");

        this.start = readBlockPointRequired(spawn, "start");
        this.end = readBlockPointRequired(spawn, "end");

        this.elytraHeight = spawn.getInt("elytra-height", 200);
    }

    public String worldName() {
        return worldName;
    }

    public int elytraHeight() {
        return elytraHeight;
    }

    public BlockPoint startBlock() {
        return start;
    }

    public BlockPoint endBlock() {
        return end;
    }

    /** Block-mittig (0.5) ist für Teleports oft angenehmer als exakt auf Blockkante. */
    public Location startLocationCentered() {
        World world = resolveWorld();
        return new Location(world, start.x() + 0.5, start.y(), start.z() + 0.5);
    }

    public Location endLocationCentered() {
        World world = resolveWorld();
        return new Location(world, end.x() + 0.5, end.y(), end.z() + 0.5);
    }

    public Location startLocationBlockCorner() {
        World world = resolveWorld();
        return new Location(world, start.x(), start.y(), start.z());
    }

    public Location endLocationBlockCorner() {
        World world = resolveWorld();
        return new Location(world, end.x(), end.y(), end.z());
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