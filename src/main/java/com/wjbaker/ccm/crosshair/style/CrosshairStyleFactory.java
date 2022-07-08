package com.wjbaker.ccm.crosshair.style;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.style.styles.*;

public final class CrosshairStyleFactory {

    public ICrosshairStyle from(final CrosshairStyle style, final CustomCrosshair crosshair) {
        return switch (style) {
            case DEFAULT -> new DefaultStyle(crosshair);
            case CIRCLE -> new CircleStyle(crosshair);
            case SQUARE -> new SquareStyle(crosshair);
            case TRIANGLE -> new TriangleStyle(crosshair);
            case ARROW -> new ArrowStyle(crosshair);
            case DEBUG -> new DebugStyle(crosshair);
            case DRAWN -> new DrawnStyle(crosshair);

            default -> new CrossStyle(crosshair);
        };
    }
}