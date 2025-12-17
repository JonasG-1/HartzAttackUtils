package app.goldbach.hartzAttackUtil.command;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Spawn {

    private final HartzAttackUtil pluginConfig;

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(HartzAttackUtil plugin) {
        Spawn handler = new Spawn(plugin);

        return Commands.literal("hspawn")
            .then(Commands.literal("tp")
                .requires(src -> src.getSender().hasPermission("hartzattack.spawn.tp"))
                .executes(handler::runSpawnTp)
            )
            .then(Commands.literal("set")
                .requires(src -> src.getSender().hasPermission("hartzattack.spawn.set"))
                .then(Commands.argument("start", ArgumentTypes.blockPosition())
                    .then(Commands.argument("end", ArgumentTypes.blockPosition())
                        .executes(handler::runSetSpawnSet)
                    )
                )
            );
    }

    private int runSetSpawnSet(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {

        return Command.SINGLE_SUCCESS;
    }

    private int runSpawnTp(CommandContext<CommandSourceStack> ctx) {


        return Command.SINGLE_SUCCESS;
    }
}
