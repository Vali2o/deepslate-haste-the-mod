package validev.deepslatehaste;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import validev.deepslatehaste.network.TogglePayload;
import validev.deepslatehaste.screen.DeepslateHasteScreen;

@Environment(EnvType.CLIENT)
public class DeepslateHasteClient implements ClientModInitializer {

    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(
                            DeepslateHaste.MOD_ID,
                            "main"
                    )
            );

    public static KeyMapping TOGGLE_KEY;
    public static KeyMapping OPEN_MENU_KEY;

    public static boolean enabled = false;
    public static DeepslateHasteConfig config;

    @Override
    public void onInitializeClient() {

        // Load configuration
        config = DeepslateHasteConfig.load();
        enabled = config.enabled;

        // Toggle Haste - default V
        TOGGLE_KEY = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.deepslate-haste.toggle",
                        InputConstants.Type.KEYBOARD,
                        InputConstants.KEY_V,
                        CATEGORY
                )
        );

        // Open menu - default B
        OPEN_MENU_KEY = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.deepslate-haste.open_menu",
                        InputConstants.Type.KEYBOARD,
                        InputConstants.KEY_B,
                        CATEGORY
                )
        );

        // Handle key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (TOGGLE_KEY.consumeClick()) {
                toggle();
            }

            while (OPEN_MENU_KEY.consumeClick()) {
                client.gui.setScreen(
                        new DeepslateHasteScreen()
                );
            }
        });
    }

    public static void toggle() {

        // Toggle state
        enabled = !enabled;

        // Save configuration
        config.enabled = enabled;
        config.save();

        // Send state to server
        ClientPlayNetworking.send(
                new TogglePayload(enabled)
        );

        // Display Action Bar message
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level != null) {

            Component message = Component.literal("Deepslate Haste: ")
                    .append(
                            Component.literal(enabled ? "ON" : "OFF")
                                    .setStyle(
                                            Style.EMPTY.withColor(
                                                    enabled
                                                            ? 0x55FF55
                                                            : 0xFF5555
                                            )
                                    )
                    );

            minecraft.gui.hud.setOverlayMessage(
                    message,
                    false
            );
        }
    }
}