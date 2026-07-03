package com.wjbaker.ccm;

import com.wjbaker.ccm.config.ConfigManager;
import com.wjbaker.ccm.config.GlobalProperties;
import com.wjbaker.ccm.events.KeyBindings;
import com.wjbaker.ccm.gui.screen.screens.editCrosshair.EditCrosshairGuiScreen;
import com.wjbaker.ccm.helpers.Helper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.Executors;

import static org.apache.logging.log4j.LogManager.getLogger;

@Mod("custom_crosshair_mod")
@Mod.EventBusSubscriber(Dist.CLIENT)
public final class CustomCrosshairMod {

    public static CustomCrosshairMod INSTANCE;

    public static final String TITLE = "Custom Crosshair Mod";
    public static final String VERSION = "1.6.6-forge";
    public static final String MC_VERSION = "26.2-forge";
    public static final String CURSEFORGE_PAGE = "https://www.curseforge.com/projects/242995/";
    public static final String MC_FORUMS_PAGE = "https://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2637819/";
    public static final String PATREON_PAGE = "https://www.patreon.com/bePatron?u=66431720";
    public static final String PAYPAL_PAGE = "https://www.paypal.com/donate/?hosted_button_id=KSDHLTS64X5G2";

    private final Logger logger;
    private final GlobalProperties properties;

    private ConfigManager configManager;

    public CustomCrosshairMod(final FMLJavaModLoadingContext context) {
        this.logger = getLogger(CustomCrosshairMod.class);
        this.properties = new GlobalProperties();

        INSTANCE = this;

        this.loadConfig();
        this.checkVersionAsync();

        this.properties.getCustomCrosshairDrawer().loadImage();

        RegisterKeyMappingsEvent.BUS.addListener(event -> event.register(KeyBindings.EDIT_CROSSHAIR.get()));
    }

    private void loadConfig() {
        this.configManager = new ConfigManager(
            "crosshair_config.ccmcfg",
            this.properties.getCrosshair(),
            this.properties.getIsModEnabled());

        if (!this.configManager.read()) {
            if (!this.configManager.write()) {
                this.error("Config Manager (Load)", "Unable to load or write config.");
            }
        }
    }

    private void checkVersionAsync() {
        Executors.newSingleThreadExecutor().submit(this::checkVersion);
    }

    private void checkVersion() {
        try (final BufferedReader reader = Helper.getUrl("https://pastebin.com/raw/B2sL8QCh")) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] lineSplit = currentLine.split(" ");

                if (lineSplit.length != 2)
                    continue;

                String mcVersion = lineSplit[0];
                String expectedModVersion = lineSplit[1];

                if (mcVersion.equals(MC_VERSION) && !expectedModVersion.equals(VERSION)) {
                    this.log("Version Checker", "Not using latest version of Customer Crosshair Mod.");
                    this.properties.setLatestVersion(false);
                }
            }
        }
        catch (final IOException e) {
            this.error("Version Checker", "Unable to check the version.");
        }
    }

    @SubscribeEvent
    public static void onClientTickEvent(final TickEvent.ClientTickEvent.Post event) {
        if (Minecraft.getInstance().gui.screen() == null && KeyBindings.EDIT_CROSSHAIR.get().isDown())
            Minecraft.getInstance().setScreenAndShow(new EditCrosshairGuiScreen(CustomCrosshairMod.INSTANCE.properties.getCrosshair()));
    }

    public GlobalProperties properties() {
        return this.properties;
    }

    public ConfigManager configManager() {
        return this.configManager;
    }

    public void log(final String action, final String message, final Object... values) {
        this.logger.info(String.format("[%s] %s", action, message), values);
    }

    public void error(final String action, final String message, final Object... values) {
        this.logger.error(String.format("[%s] %s", action, message), values);
    }
}