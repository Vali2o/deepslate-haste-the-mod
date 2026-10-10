
package validev.deepslatehaste;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class DeepslateHasteScreen extends Screen {

        public DeepslateHasteScreen() {
                super(Component.literal("Deepslate Haste"));
        }

        @Override
        protected void init() {
                int centerX = this.width / 2;
                int centerY = this.height / 2;

                Component title = Component.literal("Deepslate Haste");
                int titleWidth = this.font.width(title);

                this.addRenderableWidget(
                                new StringWidget(
                                                centerX - titleWidth / 2,
                                                centerY - 60,
                                                titleWidth,
                                                20,
                                                title,
                                                this.font));

                Component subtitle = Component.literal("By ValiDev ")
                                .setStyle(Style.EMPTY.withColor(0xFFFFFF))
                                .append(
                                                Component.literal("❤")
                                                                .setStyle(Style.EMPTY.withColor(0xFF5555)));

                int subtitleWidth = this.font.width(subtitle);

                this.addRenderableWidget(
                                new StringWidget(
                                                centerX - subtitleWidth / 2,
                                                centerY - 40,
                                                subtitleWidth,
                                                20,
                                                subtitle,
                                                this.font));

                this.addRenderableWidget(
                                Button.builder(getToggleText(), button -> {
                                        DeepslateHasteClient.toggle();
                                        button.setMessage(getToggleText());
                                }).bounds(centerX - 100, centerY - 15, 200, 20).build());

                this.addRenderableWidget(
                                Button.builder(Component.literal("Done"), button -> this.onClose())
                                                .bounds(centerX - 100, centerY + 10, 200, 20)
                                                .build());
        }

        private Component getToggleText() {
                return Component.literal("Haste Status: ")
                                .append(
                                                Component.literal(
                                                                DeepslateHasteClient.isEnabled() ? "ON" : "OFF")
                                                                .setStyle(
                                                                                Style.EMPTY.withColor(
                                                                                                DeepslateHasteClient
                                                                                                                .isEnabled()
                                                                                                                                ? 0x55FF55
                                                                                                                                : 0xFF5555)));
        }
}