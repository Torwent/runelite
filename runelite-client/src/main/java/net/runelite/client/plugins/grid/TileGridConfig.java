package net.runelite.client.plugins.grid;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("tilegrid")
public interface TileGridConfig extends Config
{
	@Alpha
	@ConfigItem(
		keyName = "gridColor",
		name = "Grid Color",
		description = "The color of the tile outlines.",
		position = 0
	)
	default Color gridColor()
	{
		return new Color(204, 42, 219, 128);
	}

	@Alpha
	@ConfigItem(
		keyName = "fillColor",
		name = "Fill Color",
		description = "The color used to fill each tile.",
		position = 1
	)
	default Color fillColor()
	{
		return new Color(0, 0, 0, 0);
	}

	@Range(min = 1, max = 10)
	@ConfigItem(
		keyName = "lineThickness",
		name = "Line Thickness",
		description = "The width of the tile outlines in pixels.",
		position = 2
	)
	default int lineThickness()
	{
		return 1;
	}

	@Range(min = 1, max = 64)
	@ConfigItem(
		keyName = "gridDistance",
		name = "Draw Distance",
		description = "The radius in tiles around the player to draw the grid.",
		position = 3
	)
	default int gridDistance()
	{
		return 10;
	}
}
