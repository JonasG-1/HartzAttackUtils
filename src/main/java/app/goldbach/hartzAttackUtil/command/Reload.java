package app.goldbach.hartzAttackUtil.command;

import app.goldbach.hartzAttackUtil.config.PluginConfig;
import app.goldbach.hartzAttackUtil.out.Responder;
import app.goldbach.hartzAttackUtil.service.AdService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class Reload {

    private final Responder responder;
    private final PluginConfig config;
    private final AdService adService;

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(Responder responder, PluginConfig pluginConfig, AdService adService) {
        Reload handler = new Reload(responder, pluginConfig, adService);

        return Commands.literal("hartzattack")
            .requires(src -> src.getSender().hasPermission("hartzattack.admin"))
            .then(Commands.literal("reload")
                .executes(handler::reload)
            );
    }

    private int reload(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        CommandSender sender = source.getSender();

        config.reload();
        adService.reload();

        responder.sendMessage(sender, "Erfolgreich neu geladen.");

        return Command.SINGLE_SUCCESS;
    }

}
