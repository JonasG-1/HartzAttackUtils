package app.goldbach.hartzAttackUtil.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CommandsConfig {

    private final String prefix;

    public CommandsConfig(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("commands");
        if (section == null) {
            throw new IllegalStateException("config.yml: Abschnitt 'commands' fehlt");
        }

        this.prefix = section.getString("prefix", "§7>>§r ");
    }

    public String prefix() {
        return prefix;
    }
}