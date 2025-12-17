package app.goldbach.hartzAttackUtil.command;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class Spawn {

    private final HartzAttackUtil plugin;

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
                        .executes(handler::runSpawnSet)
                    )
                )
            );
    }

    private int runSpawnSet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        CommandSender sender = source.getSender();

        BlockPositionResolver startResolver = ctx.getArgument("start", BlockPositionResolver.class);
        BlockPositionResolver endResolver = ctx.getArgument("end", BlockPositionResolver.class);

        BlockPosition start = startResolver.resolve(source);
        BlockPosition end = endResolver.resolve(source);

        String worldName;
        if (sender instanceof Player player) {
            worldName = player.getWorld().getName();
        } else {
            worldName = plugin.pluginConfig().spawn().getWorldName();
        }

        plugin.getConfig().set("spawn.world", worldName);

        plugin.getConfig().set("spawn.start.x", start.x());
        plugin.getConfig().set("spawn.start.y", start.y());
        plugin.getConfig().set("spawn.start.z", start.z());

        plugin.getConfig().set("spawn.end.x", end.x());
        plugin.getConfig().set("spawn.end.y", end.y());
        plugin.getConfig().set("spawn.end.z", end.z());

        plugin.saveConfig();
        plugin.reloadPluginConfig();

        sender.sendMessage(plugin.pluginConfig().commands().prefix()
            + "Spawn gesetzt: start=(" + start.x() + "," + start.y() + "," + start.z() + ") "
            + "end=(" + end.x() + "," + end.y() + "," + end.z() + ") world=" + worldName);

        return Command.SINGLE_SUCCESS;
    }

    private int runSpawnTp(CommandContext<CommandSourceStack> ctx) {


        return Command.SINGLE_SUCCESS;
    }
}
