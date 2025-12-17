package app.goldbach.hartzAttackUtil.event;

import app.goldbach.hartzAttackUtil.service.AdService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class JoinEvent implements Listener {

    private final AdService adService;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        adService.showTo(event.getPlayer());
    }
}
