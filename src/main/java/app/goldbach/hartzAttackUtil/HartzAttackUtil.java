package app.goldbach.hartzAttackUtil;

import app.goldbach.hartzAttackUtil.command.Elytra;
import app.goldbach.hartzAttackUtil.command.Spawn;
import app.goldbach.hartzAttackUtil.config.PluginConfig;
import app.goldbach.hartzAttackUtil.out.Sender;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class HartzAttackUtil extends JavaPlugin {

    private PluginConfig pluginConfig;
    private Sender sender;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        this.pluginConfig = new PluginConfig(this);
        this.sender = new Sender(this);

        registerCommands();
    }

    public PluginConfig pluginConfig() {
        return pluginConfig;
    }

    public Sender sender() {
        return sender;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.pluginConfig.reload();
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(Spawn.createCommand(this).build());
            commands.registrar().register(Elytra.createCommand(this).build());
        });
    }

    @Override
    public void onDisable() {

    }
}
