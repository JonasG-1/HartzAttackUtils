package app.goldbach.hartzAttackUtil;

import app.goldbach.hartzAttackUtil.command.Spawn;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class HartzAttackUtil extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        this.pluginConfig = new PluginConfig(this);

        registerCommands();
    }

    public PluginConfig pluginConfig() {
        return pluginConfig;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.pluginConfig.reload();
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(Spawn.createCommand(this).build());
        });
    }

    @Override
    public void onDisable() {

    }
}
