
package validev.deepslatehaste;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import validev.deepslatehaste.network.HasteNetwork;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

@Mod(DeepslateHaste.MOD_ID)
public final class DeepslateHaste {

    public static final String MOD_ID = "deepslate_haste";

    private static final Set<UUID> ENABLED_PLAYERS = ConcurrentHashMap.newKeySet();

    public DeepslateHaste(FMLJavaModLoadingContext context) {
        loadEnabledPlayers();

        HasteNetwork.register();

        TickEvent.PlayerTickEvent.Post.BUS.addListener(
                DeepslateHaste::onPlayerTick);
    }

    public static void setEnabled(ServerPlayer player, boolean enabled) {
        UUID playerId = player.getUUID();

        if (enabled) {
            ENABLED_PLAYERS.add(playerId);
        } else {
            ENABLED_PLAYERS.remove(playerId);
        }

        saveEnabledPlayers();

    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        boolean enabled = ENABLED_PLAYERS.contains(player.getUUID());

        HasteNetwork.sendSync(player, enabled);
    }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path SAVE_FILE = FMLPaths.CONFIGDIR.get()
            .resolve("deepslate_haste_players.json");

    private static void loadEnabledPlayers() {
        if (!Files.exists(SAVE_FILE)) {
            return;
        }

        try {
            String json = Files.readString(SAVE_FILE);

            java.lang.reflect.Type type = new TypeToken<Set<String>>() {
            }.getType();

            Set<String> savedIds = GSON.fromJson(json, type);

            ENABLED_PLAYERS.clear();

            if (savedIds != null) {
                for (String id : savedIds) {
                    try {
                        ENABLED_PLAYERS.add(UUID.fromString(id));
                    } catch (IllegalArgumentException e) {
                        System.err.println(
                                "[Deepslate Haste] Invalid saved UUID: " + id);
                    }
                }
            }

            System.out.println(
                    "[Deepslate Haste] Loaded "
                            + ENABLED_PLAYERS.size()
                            + " saved player preferences.");
        } catch (IOException | RuntimeException e) {
            System.err.println(
                    "[Deepslate Haste] Could not load preferences: "
                            + e.getMessage());
        }
    }

    private static void saveEnabledPlayers() {
        try {
            Files.createDirectories(SAVE_FILE.getParent());

            Set<String> savedIds = new HashSet<>();

            for (UUID id : ENABLED_PLAYERS) {
                savedIds.add(id.toString());
            }

            Files.writeString(SAVE_FILE, GSON.toJson(savedIds));
        } catch (IOException e) {
            System.err.println(
                    "[Deepslate Haste] Could not save preferences: "
                            + e.getMessage());
        }
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        if (event.side() != LogicalSide.SERVER) {
            return;
        }

        Player player = event.player();

        if (!ENABLED_PLAYERS.contains(player.getUUID())) {
            return;
        }

        BlockPos below = player.blockPosition().below();

        if (player.level().getBlockState(below).is(Blocks.DEEPSLATE)) {
            player.addEffect(
                    new MobEffectInstance(
                            MobEffects.HASTE,
                            20,
                            7,
                            false,
                            false,
                            true));
        }
    }
}