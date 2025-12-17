package app.goldbach.hartzAttackUtil.command;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import app.goldbach.hartzAttackUtil.config.SpawnConfig;
import app.goldbach.hartzAttackUtil.out.Colors;
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
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class Spawn {

    private final HartzAttackUtil plugin;

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(HartzAttackUtil plugin) {
        Spawn handler = new Spawn(plugin);

        return Commands.literal("hspawn")
            .requires(src -> src.getSender().hasPermission("hartzattack.hspawn"))
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
            )
            .then(Commands.literal("get")
                .requires(src -> src.getSender().hasPermission("hartzattack.spawn.get"))
                .executes(handler::runSpawnGet)
            );
    }

    private int runSpawnGet(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        CommandSender sender = source.getSender();

        SpawnConfig.BlockPoint startPoint = plugin.pluginConfig().spawn().getStart();
        SpawnConfig.BlockPoint endPoint = plugin.pluginConfig().spawn().getEnd();

        plugin.sender().sendMessage(sender, String.format("Spawn ist bei Start: %s, %s, %s; Ende: %s, %s, %s",
            startPoint.x(), startPoint.y(), startPoint.z(), endPoint.x(), endPoint.y(), endPoint.z()));

        return Command.SINGLE_SUCCESS;
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

        SpawnConfig.BlockPoint startPoint = new SpawnConfig.BlockPoint(start.blockX(), start.blockY(), start.blockZ());
        SpawnConfig.BlockPoint endPoint = new SpawnConfig.BlockPoint(end.blockX(), end.blockY(), end.blockZ());

        plugin.pluginConfig().spawn().setSpawn(worldName, startPoint, endPoint);

        plugin.sender().sendMessage(sender,
            "Spawn gesetzt: start=(" + startPoint.x() + "," + startPoint.y() + "," + startPoint.z() + ") "
                + "end=(" + endPoint.x() + "," + endPoint.y() + "," + endPoint.z() + ") world=" + worldName);


        return Command.SINGLE_SUCCESS;
    }

    private int runSpawnTp(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            plugin.sender().sendMessage(sender, Component.text("Nur Spieler können zum Spawn teleportiert werden.").color(Colors.RED));
        } else {
            org.bukkit.Location location = plugin.pluginConfig().spawn().teleportLocationCentered();
            player.teleport(location);
            plugin.sender().sendMessage(sender, "Erfolgreich teleportiert!");
        }

        return Command.SINGLE_SUCCESS;
    }
}
