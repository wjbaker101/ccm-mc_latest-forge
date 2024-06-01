package com.wjbaker.ccm;

import com.wjbaker.ccm.config.ConfigManager;
import com.wjbaker.ccm.config.GlobalProperties;
import com.wjbaker.ccm.crosshair.rendering.CrosshairRenderManager;
import com.wjbaker.ccm.events.KeyBindings;
import com.wjbaker.ccm.events.ModEventBusRegistrar;
import com.wjbaker.ccm.gui.screen.screens.editCrosshair.EditCrosshairGuiScreen;
import com.wjbaker.ccm.helpers.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    public static final String VERSION = "1.6.0-forge";
    public static final String MC_VERSION = "1.20.4-forge";
    public static final String CURSEFORGE_PAGE = "https://www.curseforge.com/projects/242995/";
    public static final String MC_FORUMS_PAGE = "https://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2637819/";
    public static final String PATREON_PAGE = "https://www.patreon.com/bePatron?u=66431720";
    public static final String PAYPAL_PAGE = "https://www.paypal.com/cgi-bin/webscr?return=https://www.curseforge.com/projects/242995&cn=Add+special+instructions+to+the+addon+author()&business=sparkless101%40gmail.com&bn=PP-DonationsBF:btn_donateCC_LG.gif:NonHosted&cancel_return=https://www.curseforge.com/projects/242995&lc=US&item_name=Custom+Crosshair+Mod+(from+curseforge.com)&cmd=_donations&rm=1&no_shipping=1&currency_code=USD";

    private final Logger logger;
    private final GlobalProperties properties;
    private final CrosshairRenderManager crosshairRenderManager;

    private ConfigManager configManager;

    public CustomCrosshairMod() {
        this.logger = getLogger(CustomCrosshairMod.class);
        this.properties = new GlobalProperties();
        this.crosshairRenderManager = new CrosshairRenderManager(false);

        INSTANCE = this;

        this.loadConfig();
        this.checkVersionAsync();

        this.properties.getCustomCrosshairDrawer().loadImage();

        FMLJavaModLoadingContext.get().getModEventBus().register(new ModEventBusRegistrar());
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
    public static void onClientTickEvent(final TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().screen == null && KeyBindings.EDIT_CROSSHAIR.isDown())
            Minecraft.getInstance().setScreen(new EditCrosshairGuiScreen(CustomCrosshairMod.INSTANCE.properties.getCrosshair()));
    }

    @SubscribeEvent
    public static void onRenderTickEvent(final TickEvent.RenderTickEvent event) {
        if (!CustomCrosshairMod.INSTANCE.properties.getIsModEnabled().get())
            return;
        if (Minecraft.getInstance().screen != null && !(Minecraft.getInstance().screen instanceof ChatScreen))
            return;

        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int x = Math.round(width / 2.0F);
        int y = Math.round(height / 2.0F);

        CustomCrosshairMod.INSTANCE.crosshairRenderManager.draw(
            CustomCrosshairMod.INSTANCE.properties.getCrosshair(),
            x,
            y);
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