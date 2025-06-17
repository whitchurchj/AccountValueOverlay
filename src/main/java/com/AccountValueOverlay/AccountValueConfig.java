package com.AccountValueOverlay;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AccountValue")
public interface AccountValueConfig extends Config
{
	@ConfigItem(
		keyName = "showPriceSuffix",
		name = "Show Suffix",
		description = "Adds a K/M/B suffix to Bank Value"
	)
	default boolean showPriceSuffix()
	{
		return true;
	}
	/*@ConfigItem(
			keyName = "ShowProjectedGe",
			name = "Use Ge Sale Price",
			description = "Shows the expected value from the Grand Exchange if all your items instead of the items estimated value."
	)
	default boolean ShowProjectedGe()
	{
		return false;
	}*/
}
