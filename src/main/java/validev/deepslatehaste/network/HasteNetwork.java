
package validev.deepslatehaste.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import validev.deepslatehaste.DeepslateHaste;
import validev.deepslatehaste.DeepslateHasteClient;
import net.minecraft.server.level.ServerPlayer;

public final class HasteNetwork {

        private static final SimpleChannel CHANNEL = ChannelBuilder
                        .named("deepslate-haste:main")
                        .networkProtocolVersion(1)
                        .simpleChannel();

        private static final StreamCodec<RegistryFriendlyByteBuf, TogglePayload> TOGGLE_CODEC = StreamCodec.composite(
                        ByteBufCodecs.BOOL,
                        TogglePayload::enabled,
                        TogglePayload::new);

        private HasteNetwork() {
        }

        public static void register() {
                CHANNEL.play()
                                .flow(PacketFlow.SERVERBOUND)
                                .add(
                                                TogglePayload.class,
                                                TOGGLE_CODEC,
                                                (message, context) -> context.enqueueWork(() -> {
                                                        var player = context.getSender();

                                                        if (player != null) {
                                                                DeepslateHaste.setEnabled(player, message.enabled());
                                                        }
                                                }));

                CHANNEL.play()
                                .flow(PacketFlow.CLIENTBOUND)
                                .add(
                                                SyncPayload.class,
                                                SYNC_CODEC,
                                                (message, context) -> context.enqueueWork(() -> DeepslateHasteClient
                                                                .setEnabledFromServer(message.enabled())));

                CHANNEL.build();
        }

        public static void sendSync(ServerPlayer player, boolean enabled) {
                CHANNEL.send(
                                new SyncPayload(enabled),
                                PacketDistributor.PLAYER.with(player));
        }

        private static final StreamCodec<RegistryFriendlyByteBuf, SyncPayload> SYNC_CODEC = StreamCodec.composite(
                        ByteBufCodecs.BOOL,
                        SyncPayload::enabled,
                        SyncPayload::new);

        public static void sendToggle(boolean enabled) {
                CHANNEL.send(
                                new TogglePayload(enabled),
                                PacketDistributor.SERVER.noArg());
        }
}