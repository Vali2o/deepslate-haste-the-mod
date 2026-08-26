package validev.deepslatehaste;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import validev.deepslatehaste.network.TogglePayload;

public class DeepslateHaste implements ModInitializer {
    public static final String MOD_ID = "deepslate-haste";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(
                TogglePayload.TYPE,
                TogglePayload.CODEC
        );

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (var player : server.getPlayerList().getPlayers()) {

				if (!PlayerState.isEnabled(player.getUUID())) {
					continue;
				}

				if (player.level().getBlockState(player.blockPosition().below()).is(net.minecraft.world.level.block.Blocks.DEEPSLATE)) {

					player.addEffect(
						new net.minecraft.world.effect.MobEffectInstance(
							net.minecraft.world.effect.MobEffects.HASTE,
							20,
							7,
							false,
							false,
							true
						)
					);
				}
			}
		});

        ServerPlayNetworking.registerGlobalReceiver(
                TogglePayload.TYPE,
                (payload, context) -> {
					PlayerState.setEnabled(
						context.player().getUUID(),
						payload.enabled()
					);
                }
        );
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}