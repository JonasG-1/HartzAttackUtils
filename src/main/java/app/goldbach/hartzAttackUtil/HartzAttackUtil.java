package app.goldbach.hartzAttackUtil;

import app.goldbach.hartzAttackUtil.command.Elytra;
import app.goldbach.hartzAttackUtil.command.Spawn;
import app.goldbach.hartzAttackUtil.command.Werbung;
import app.goldbach.hartzAttackUtil.config.PluginConfig;
import app.goldbach.hartzAttackUtil.event.ElytraEvent;
import app.goldbach.hartzAttackUtil.event.ElytraParticleEvent;
import app.goldbach.hartzAttackUtil.event.JoinEvent;
import app.goldbach.hartzAttackUtil.out.Sender;
import app.goldbach.hartzAttackUtil.service.AdService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class HartzAttackUtil extends JavaPlugin {

    private PluginConfig pluginConfig;
    private Sender sender;
    private AdService adService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        this.pluginConfig = new PluginConfig(this);
        this.sender = new Sender(this);
        this.adService = new AdService(this);

        registerCommands();
        registerEvents();
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
            commands.registrar().register(Werbung.createCommand(adService, this).build());
        });
    }

    private void registerEvents() {
        var manager = this.getServer().getPluginManager();
        manager.registerEvents(new JoinEvent(adService, this), this);
        manager.registerEvents(new ElytraEvent(this), this);
        manager.registerEvents(new ElytraParticleEvent(), this);
    }

    @Override
    public void onDisable() {

    }
}
