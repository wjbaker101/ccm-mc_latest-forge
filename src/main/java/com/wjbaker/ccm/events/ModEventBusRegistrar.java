package com.wjbaker.ccm.events;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ModEventBusRegistrar {

    @SubscribeEvent
    public void onRegisterKeyBindings(final RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.EDIT_CROSSHAIR);
    }
}