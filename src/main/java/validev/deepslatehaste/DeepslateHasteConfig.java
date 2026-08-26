package validev.deepslatehaste;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeepslateHasteConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_PATH = Path.of(
            "config",
            "deepslate-haste.json"
    );

    public boolean enabled = false;

    public static DeepslateHasteConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);

                DeepslateHasteConfig config =
                        GSON.fromJson(json, DeepslateHasteConfig.class);

                if (config != null) {
                    return config;
                }
            }
        } catch (IOException | RuntimeException e) {
            System.err.println(
                    "Failed to load Deepslate Haste config: "
                            + e.getMessage()
            );
        }

        return new DeepslateHasteConfig();
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            Files.writeString(
                    CONFIG_PATH,
                    GSON.toJson(this)
            );
        } catch (IOException e) {
            System.err.println(
                    "Failed to save Deepslate Haste config: "
                            + e.getMessage()
            );
        }
    }
}