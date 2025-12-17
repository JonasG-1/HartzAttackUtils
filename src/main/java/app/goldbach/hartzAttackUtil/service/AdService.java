package app.goldbach.hartzAttackUtil.service;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AdService {

    private static final List<TextColor> RAINBOW_COLORS = List.of(
        TextColor.color(0xff5555),
        TextColor.color(0xffaa00),
        TextColor.color(0xffff55),
        TextColor.color(0x55ff55),
        TextColor.color(0x55ffff),
        TextColor.color(0x5555ff),
        TextColor.color(0xff55ff)
    );

    private final HartzAttackUtil plugin;
    private final MiniMessage miniMessage;

    /**
     * Spieler, die die Bossbar in ihrer Session ausgeschaltet haben.
     */
    private final Set<UUID> optedOut;

    private @Nullable BossBar bossBar;
    @Getter
    private boolean serverEnabled = true;
    private boolean animationStarted = false;
    private int colorIndex = 0;

    public AdService(HartzAttackUtil plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.optedOut = new HashSet<>();
    }

    public void setServerEnabled(boolean enabled) {
        this.serverEnabled = enabled;

        // Wenn serverweit deaktiviert: sofort bei allen ausblenden
        if (!enabled) {
            if (bossBar != null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.hideBossBar(bossBar);
                }
            }
            return;
        }

        // Wenn wieder aktiviert: allen anzeigen, die nicht opted-out sind
        ensureStarted();
        if (bossBar != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!optedOut.contains(p.getUniqueId())) {
                    p.showBossBar(bossBar);
                }
            }
        }
    }

    public void setEnabledFor(Player player, boolean enabled) {
        if (enabled) {
            optedOut.remove(player.getUniqueId());
            showTo(player);
        } else {
            optedOut.add(player.getUniqueId());
            hideFrom(player);
        }
    }

    public void setEnabledFor(Iterable<Player> players, boolean enabled) {
        for (Player p : players) {
            setEnabledFor(p, enabled);
        }
    }

    public int switchFor(Player player) {
        if (!serverEnabled) {
            return -1; // serverweit aus
        }
        if (optedOut.contains(player.getUniqueId())) {
            setEnabledFor(player, true);
            return 1; // aktiviert
        }
        setEnabledFor(player, false);
        return 0; // deaktiviert
    }

    private void ensureStarted() {
        if (animationStarted) return;
        startBossbarAnimation();
        animationStarted = true;
    }

    public void startBossbarAnimation() {
        String template = plugin.pluginConfig().hartzAttack().bossbarTemplate();

        this.bossBar = BossBar.bossBar(Component.empty(), 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (bossBar == null) return;
            if (!serverEnabled) return; // serverweit deaktiviert => nichts updaten

            colorIndex = (colorIndex + 1) % RAINBOW_COLORS.size();

            Component name = miniMessage.deserialize(
                template,
                Placeholder.styling("color-changer", RAINBOW_COLORS.get(colorIndex))
            );
            bossBar.name(name);
        }, 0L, 20L);
    }

    public void showTo(Player player) {
        if (!serverEnabled) return;
        ensureStarted();
        if (bossBar == null) return;
        if (optedOut.contains(player.getUniqueId())) return;

        player.showBossBar(bossBar);
    }

    public void hideFrom(Player player) {
        if (bossBar == null) return;
        player.hideBossBar(bossBar);
    }
}