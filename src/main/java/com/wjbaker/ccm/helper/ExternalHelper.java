package com.wjbaker.ccm.helper;

import net.minecraft.Util;

public final class ExternalHelper {

    public void openInBrowser(final String url) {
        try {
            Util.getPlatform().openUri(url);
        }
        catch (final Exception ignored) {}
    }
}
