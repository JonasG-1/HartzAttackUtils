package app.goldbach.hartzAttackUtil.event;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import app.goldbach.hartzAttackUtil.service.AdService;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;

@RequiredArgsConstructor
public class JoinEvent implements Listener {

    private final AdService adService;
    private final HartzAttackUtil plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Werbung (Bossbar) beim Join anzeigen (sofern serverweit aktiv und Spieler nicht opted-out)
        adService.showTo(player);

        // Title 1: sofort
        Title title = Title.title(
            Component.text("Willkommen bei HartzAttack!", NamedTextColor.DARK_GREEN),
            Component.empty(),
            Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO)
        );
        player.showTitle(title);

        // Actionbar: sofort
        player.sendActionBar(Component.text("...", NamedTextColor.DARK_GREEN));

        // Title 2 + Sound: nach 30 Ticks
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Title title1 = Title.title(
                Component.text(player.getName(), NamedTextColor.GOLD),
                Component.text("Willkommen bei HartzAttack!", NamedTextColor.DARK_GREEN),
                Title.Times.times(Duration.ofSeconds(2), Duration.ofSeconds(5), Duration.ofSeconds(2))
            );
            player.showTitle(title1);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }, 30L);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.sendActionBar(
            Component.text("Du kannst die obere Leiste mit \"/werbung\" entfernen & anzeigen.", NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
        ), 20L * 5L);

        // Actionbar Hinweis 2: nach 8 Sekunden
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.sendActionBar(
            Component.text("Viel Spaß!", NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
        ), 20L * 8L);
    }
}
