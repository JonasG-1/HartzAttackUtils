package app.goldbach.hartzAttackUtil.listener;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ElytraParticleEvent implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.isGliding()) {
            java.awt.Color color = getRainbow();
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), 1);
            p.getWorld().spawnParticle(Particle.DUST, p.getLocation(), 20, dustOptions);
        }
    }

    private java.awt.Color getRainbow() {
        long index = System.currentTimeMillis() % 50000L;
        int rainbowSpeed = 50;
        index = index * (long) rainbowSpeed / 2L % 50000L;
        float color = (float) index / 50000.0F;
        return java.awt.Color.getHSBColor(color, 0.8F, 0.8F);
    }
}
