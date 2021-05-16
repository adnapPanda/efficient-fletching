package com.EfficientFletching;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EfficientFletchingPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(EfficientFletchingPlugin.class);
		RuneLite.main(args);
	}
}