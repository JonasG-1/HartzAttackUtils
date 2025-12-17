package app.goldbach.hartzAttackUtil.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class HartzAttackConfig {

    private final String bossbarTemplate;

    public HartzAttackConfig(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("hartzattack");
        if (section == null) {
            throw new IllegalStateException("config.yml: Abschnitt 'hartzattack' fehlt");
        }

        this.bossbarTemplate = section.getString("bossbar", "");
    }

    public String bossbarTemplate() {
        return bossbarTemplate;
    }
}