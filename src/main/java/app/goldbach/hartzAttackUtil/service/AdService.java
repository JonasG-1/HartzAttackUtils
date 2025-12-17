package app.goldbach.hartzAttackUtil.service;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final Set<Player> affectedPlayers;
    private final MiniMessage miniMessage;
    private @Nullable BossBar bossBar;
    private int colorIndex = 0;

    public AdService(HartzAttackUtil plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.affectedPlayers = new HashSet<>();
    }

    public void startBossbarAnimation() {
        String template = plugin.pluginConfig().hartzAttack().bossbarTemplate();

        this.bossBar = BossBar.bossBar(Component.empty(), 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (bossBar == null) return;

            colorIndex = (colorIndex + 1) % RAINBOW_COLORS.size();

            Component name = miniMessage.deserialize(template,
                Placeholder.styling("color-changer", RAINBOW_COLORS.get(colorIndex)));
            bossBar.name(name);
        }, 0L, 20L);
    }

    public void showTo(Player player) {
        if (bossBar == null) return;
        player.showBossBar(bossBar);
        affectedPlayers.add(player);
    }

    public void hideFrom(Player player) {
        if (bossBar == null) return;
        player.hideBossBar(bossBar);
        affectedPlayers.remove(player);
    }

    public void switchFor(Player player) {
        if (affectedPlayers.contains(player)) {
            hideFrom(player);
        } else {
            showTo(player);
        }
    }
}
