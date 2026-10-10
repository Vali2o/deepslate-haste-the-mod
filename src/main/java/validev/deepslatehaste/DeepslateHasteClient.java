package validev.deepslatehaste;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import validev.deepslatehaste.network.HasteNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeepslateHaste.MOD_ID, value = Dist.CLIENT)

public final class DeepslateHasteClient {

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(
                    DeepslateHaste.MOD_ID,
                    "main"));

    private static KeyMapping toggleKey;
    private static KeyMapping openMenuKey;
    private static boolean enabled = false;

    private DeepslateHasteClient() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabledFromServer(boolean serverEnabled) {
        enabled = serverEnabled;
    }

    @net.minecraftforge.eventbus.api.listener.SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping(
                "key.deepslate_haste.toggle",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_V,
                CATEGORY);

        openMenuKey = new KeyMapping(
                "key.deepslate_haste.open_menu",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_B,
                CATEGORY);

        event.register(toggleKey);
        event.register(openMenuKey);
    }

    static {
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> onClientTick());
    }

    private static void onClientTick() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        if (toggleKey != null) {
            while (toggleKey.consumeClick()) {
                toggle();
            }
        }

        if (openMenuKey != null) {
            while (openMenuKey.consumeClick()) {
                minecraft.setScreenAndShow(new DeepslateHasteScreen());
            }
        }
    }

    public static void toggle() {
        enabled = !enabled;
        HasteNetwork.sendToggle(enabled);

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.gui != null) {
            Component status = Component.literal("Deepslate Haste: ")
                    .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                    .append(
                            Component.literal(enabled ? "ON" : "OFF")
                                    .withStyle(
                                            Style.EMPTY.withColor(
                                                    enabled ? 0x55FF55 : 0xFF5555)));

            minecraft.gui.hud.setOverlayMessage(status, false);
        }
    }

}
