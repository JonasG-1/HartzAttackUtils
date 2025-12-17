package app.goldbach.hartzAttackUtil.config;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {

    private final JavaPlugin plugin;

    private SpawnConfig spawn;
    private HartzAttackConfig hartzAttack;
    private CommandsConfig commands;

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        this.spawn = new SpawnConfig(plugin);
        this.hartzAttack = new HartzAttackConfig(plugin);
        this.commands = new CommandsConfig(plugin);
    }

    public SpawnConfig spawn() {
        return spawn;
    }

    public HartzAttackConfig hartzAttack() {
        return hartzAttack;
    }

    public CommandsConfig commands() {
        return commands;
    }
}