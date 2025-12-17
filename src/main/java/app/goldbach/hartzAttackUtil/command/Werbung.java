package app.goldbach.hartzAttackUtil.command;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import app.goldbach.hartzAttackUtil.out.Colors;
import app.goldbach.hartzAttackUtil.service.AdService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class Werbung {

    private static final String PERM_OTHERS = "hartzattack.werbung.others";
    private static final String PERM_ADMIN = "hartzattack.werbung.admin";

    private final AdService adService;
    private final HartzAttackUtil plugin;

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(AdService adService, HartzAttackUtil plugin) {
        Werbung handler = new Werbung(adService, plugin);

        return Commands.literal("werbung")
            // /werbung  => toggle für sich selbst
            .executes(handler::toggleSelf)

            // /werbung on [targets]
            .then(Commands.literal("on")
                .executes(ctx -> handler.setSelf(ctx, true))
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .requires(src -> src.getSender().hasPermission(PERM_OTHERS))
                    .executes(ctx -> handler.setTargets(ctx, true))
                )
            )

            // /werbung off [targets]
            .then(Commands.literal("off")
                .executes(ctx -> handler.setSelf(ctx, false))
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .requires(src -> src.getSender().hasPermission(PERM_OTHERS))
                    .executes(ctx -> handler.setTargets(ctx, false))
                )
            )

            // /werbung deactivate
            .then(Commands.literal("deactivate")
                .requires(src -> src.getSender().hasPermission(PERM_ADMIN))
                .executes(handler::deactivateServer)
            )

            // /werbung activate
            .then(Commands.literal("activate")
                .requires(src -> src.getSender().hasPermission(PERM_ADMIN))
                .executes(handler::activateServer)
            );
    }

    private int toggleSelf(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            plugin.sender().sendMessage(sender, Component.text("Nur Spieler können /werbung ohne Ziel ausführen.").color(Colors.RED));
            return Command.SINGLE_SUCCESS;
        }

        int result = adService.switchFor(player);
        switch (result) {
            case 0 -> plugin.sender().sendMessage(sender, "Die Werbung wurde für diese Session deaktiviert.");
            case 1 -> plugin.sender().sendMessage(sender, "Die Werbung wurde wieder aktiviert.");
            default ->
                plugin.sender().sendMessage(sender, Component.text("Die Werbung ist serverweit deaktiviert.").color(Colors.RED));
        }

        return Command.SINGLE_SUCCESS;
    }

    private int setSelf(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            plugin.sender().sendMessage(sender, Component.text("Nur Spieler können diesen Befehl ohne Ziel ausführen.").color(Colors.RED));
            return Command.SINGLE_SUCCESS;
        }

        if (!adService.isServerEnabled()) {
            plugin.sender().sendMessage(sender, Component.text("Die Werbung ist serverweit deaktiviert.").color(Colors.RED));
            return Command.SINGLE_SUCCESS;
        }

        adService.setEnabledFor(player, enabled);
        plugin.sender().sendMessage(sender, enabled ? "Werbung aktiviert." : "Werbung deaktiviert.");
        return Command.SINGLE_SUCCESS;
    }

    private int setTargets(CommandContext<CommandSourceStack> ctx, boolean enabled) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        CommandSender sender = source.getSender();

        if (!adService.isServerEnabled()) {
            plugin.sender().sendMessage(sender, Component.text("Die Werbung ist serverweit deaktiviert.").color(Colors.RED));
            return Command.SINGLE_SUCCESS;
        }

        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class);
        final List<Player> targets;

        try {
            targets = targetResolver.resolve(ctx.getSource());
        } catch (Exception ex) {
            plugin.sender().sendMessage(sender, Component.text("Konnte Zielauswahl nicht auflösen.").color(Colors.RED));
            return Command.SINGLE_SUCCESS;
        }

        if (targets.isEmpty()) {
            plugin.sender().sendMessage(sender, Component.text("Keine passenden (online) Spieler gefunden.").color(Colors.RED));
            return Command.SINGLE_SUCCESS;
        }

        adService.setEnabledFor(targets, enabled);
        plugin.sender().sendMessage(sender, (enabled ? "Werbung aktiviert für " : "Werbung deaktiviert für ") + targets.size() + " Spieler.");
        return Command.SINGLE_SUCCESS;
    }

    private int deactivateServer(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        adService.setServerEnabled(false);
        plugin.sender().sendMessage(sender, "Werbung wurde serverweit deaktiviert.");
        return Command.SINGLE_SUCCESS;
    }

    private int activateServer(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        adService.setServerEnabled(true);
        plugin.sender().sendMessage(sender, "Werbung wurde serverweit aktiviert.");
        return Command.SINGLE_SUCCESS;
    }
}