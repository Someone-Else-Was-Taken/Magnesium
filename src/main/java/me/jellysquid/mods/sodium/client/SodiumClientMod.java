package me.jellysquid.mods.sodium.client;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;

@Mod("magnesium")
public class SodiumClientMod {

    public SodiumClientMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private static SodiumGameOptions CONFIG;
    private static Logger LOGGER;

    private static String MOD_VERSION;
    public static boolean hasFlywheel;

    public void setup(final FMLClientSetupEvent event) {
        MOD_VERSION = ModList.get().getModContainerById("magnesium").get().getModInfo().getVersion().toString();

        hasFlywheel = ModList.get().isLoaded("flywheel");
        LOGGER = LogManager.getLogger("Magnesium");

    }

    public static SodiumGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    public static Logger logger() {
        if (LOGGER == null) {
            throw new IllegalStateException("Logger not yet available");
        }

        return LOGGER;
    }

    private static SodiumGameOptions loadConfig() {
        SodiumGameOptions config = SodiumGameOptions.load(Paths.get("config", "sodium-options.json"));

        return config;
    }

    public static void restoreDefaultOptions() {
        CONFIG = SodiumGameOptions.defaults();

        try {
            CONFIG.writeChanges();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write config file", e);
        }
    }

    public static String getVersion() {
        if (MOD_VERSION == null) {
            throw new NullPointerException("Mod version hasn't been populated yet");
        }

        return MOD_VERSION;
    }


    public static boolean isDirectMemoryAccessEnabled() {
        return options().advanced.allowDirectMemoryAccess;
    }
}
