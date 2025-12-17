package app.goldbach.hartzAttackUtil.out;

import app.goldbach.hartzAttackUtil.HartzAttackUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class Sender {

    private final HartzAttackUtil plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public void sendMessage(CommandSender sender, String string) {
        sendMessage(sender, Component.text(string).color(Colors.GREEN));
    }


    public void sendMessage(CommandSender sender, Component component) {
        String prefix = plugin.pluginConfig().commands().prefix();
        Component prefixComponent = MM.deserialize(prefix);
        Component result = prefixComponent.append(component);
        sender.sendMessage(result);
    }
}
