package net.runelite.client.plugins.grid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class TileGridOverlay extends Overlay
{
	private final Client client;
	private final TileGridConfig config;

	@Inject
	private TileGridOverlay(Client client, TileGridConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(PRIORITY_LOW);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Player player = client.getLocalPlayer();
		WorldView wv = client.getTopLevelWorldView();
		if (player == null || wv == null)
		{
			return null;
		}

		LocalPoint playerLocation = player.getLocalLocation();
		if (playerLocation == null)
		{
			return null;
		}

		Color gridColor = config.gridColor();
		Color fillColor = config.fillColor();
		Stroke stroke = new BasicStroke(config.lineThickness());
		int range = config.gridDistance();

		// Same as the dev tools line of sight overlay, but every tile in range is drawn
		int playerX = playerLocation.getSceneX();
		int playerY = playerLocation.getSceneY();
		for (int x = playerX - range; x <= playerX + range; x++)
		{
			for (int y = playerY - range; y <= playerY + range; y++)
			{
				if (x < 0 || y < 0 || x >= wv.getSizeX() || y >= wv.getSizeY())
				{
					continue;
				}

				LocalPoint lp = LocalPoint.fromScene(x, y, wv);
				Polygon poly = Perspective.getCanvasTilePoly(client, lp);
				if (poly == null)
				{
					continue;
				}

				OverlayUtil.renderPolygon(graphics, poly, gridColor, fillColor, stroke);
			}
		}

		return null;
	}
}
