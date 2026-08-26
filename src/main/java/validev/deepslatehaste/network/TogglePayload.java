package validev.deepslatehaste.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import validev.deepslatehaste.DeepslateHaste;

public record TogglePayload(boolean enabled) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(
            DeepslateHaste.MOD_ID,
            "toggle"
    );

    public static final CustomPacketPayload.Type<TogglePayload> TYPE =
            new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<ByteBuf, TogglePayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    TogglePayload::enabled,
                    TogglePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}