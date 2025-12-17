package app.goldbach.hartzAttackUtil;

import app.goldbach.hartzAttackUtil.command.Elytra;
import app.goldbach.hartzAttackUtil.command.Reload;
import app.goldbach.hartzAttackUtil.command.Spawn;
import app.goldbach.hartzAttackUtil.command.Werbung;
import app.goldbach.hartzAttackUtil.config.PluginConfig;
import app.goldbach.hartzAttackUtil.listener.ElytraEvent;
import app.goldbach.hartzAttackUtil.listener.ElytraParticleEvent;
import app.goldbach.hartzAttackUtil.listener.JoinEvent;
import app.goldbach.hartzAttackUtil.out.Responder;
import app.goldbach.hartzAttackUtil.service.AdService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class HartzAttackUtil extends JavaPlugin {

    private PluginConfig pluginConfig;
    private Responder responder;
    private AdService adService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        this.pluginConfig = new PluginConfig(this);
        this.responder = new Responder(this);
        this.adService = new AdService(this);

        registerCommands();
        registerEvents();
    }

    public PluginConfig pluginConfig() {
        return pluginConfig;
    }

    public Responder sender() {
        return responder;
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
            commands.registrar().register(Reload.createCommand(responder, pluginConfig, adService).build());
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
