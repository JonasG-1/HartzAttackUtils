package app.goldbach.hartzAttackUtil.command;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class Elytra {

    private final HartzAttackUtil plugin;

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(HartzAttackUtil plugin) {
        Elytra handler = new Elytra(plugin);

        return Commands.literal("helytra")
            .then(Commands.literal("height")
                .then(Commands.literal("set")
                    .then(Commands.argument("height", IntegerArgumentType.integer(-64, 320))
                        .requires(src -> src.getSender().hasPermission("hartzattack.elytra.set"))
                        .executes(handler::runElytraHeightSet)
                    )
                )
                .then(Commands.literal("get")
                    .requires(src -> src.getSender().hasPermission("hartzattack.elytra.get"))
                    .executes(handler::runElytraHeightGet)
                )
            )
            .then(Commands.literal("remove")
                .requires(src -> src.getSender().hasPermission("hartzattack.elytra.remove"))
                .then(Commands.argument("player", ArgumentTypes.players())
                    .executes(handler::runElytraRemove)
                )
            );
    }

    private int runElytraRemove(CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private int runElytraHeightGet(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        CommandSender sender = source.getSender();

        int height = plugin.pluginConfig().spawn().getElytraHeight();

        plugin.sender().sendMessage(sender,
            String.format("Die Höhe für das Vergeben von Elytren im Spawn Bereich liegt bei %s.", height));

        return Command.SINGLE_SUCCESS;
    }

    private int runElytraHeightSet(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        CommandSender sender = source.getSender();

        int height = ctx.getArgument("height", int.class);

        plugin.pluginConfig().spawn().setElytraHeight(height);

        plugin.sender().sendMessage(sender,
            String.format("Die Höhe für das Vergeben von Elytren im Spawn Bereich wurde erfolgreich auf %s gesetzt.",
                height)
        );

        return Command.SINGLE_SUCCESS;
    }
}
