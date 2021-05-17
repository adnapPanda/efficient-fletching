package com.EfficientFletching;

import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EfficientFletchingOverlay extends Counter {

    public EfficientFletchingOverlay(BufferedImage image, EfficientFletchingPlugin plugin, int setCount) {
        super(image, plugin, setCount);
    }

    public void addSets(int nrSets) {
        setCount(nrSets);
    }

    @Override
    public Color getTextColor()
    {
        int count = getCount();
        return count > 0 ? Color.WHITE : Color.RED;
    }
}
