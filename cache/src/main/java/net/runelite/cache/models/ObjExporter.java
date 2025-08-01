/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.cache.models;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.runelite.cache.TextureManager;
import net.runelite.cache.definitions.ModelDefinition;
import net.runelite.cache.definitions.TextureDefinition;

public class ObjExporter
{
	private static final double BRIGHTNESS = JagexColor.BRIGHTNESS_MAX;

	private final TextureManager textureManager;
	private final ModelDefinition model;

	public ObjExporter(TextureManager textureManager, ModelDefinition model)
	{
		this.textureManager = textureManager;
		this.model = model;
	}

	public void export(PrintWriter objWriter, PrintWriter mtlWriter)
	{
		model.computeNormals();
		model.computeTextureUVCoordinates();

		objWriter.println("mtllib " + model.id + ".mtl");

		objWriter.println("o runescapemodel");

		for (int i = 0; i < model.vertexCount; ++i)
		{
			objWriter.println("v " + model.vertexX[i] + " "
					+ model.vertexY[i] * -1 + " "
					+ model.vertexZ[i] * -1);
		}

		if (model.faceTextures != null)
		{
			float[][] u = model.faceTextureUCoordinates;
			float[][] v = model.faceTextureVCoordinates;

			for (int i = 0; i < model.faceCount; ++i)
			{
				objWriter.println("vt " + u[i][0] + " " + v[i][0]);
				objWriter.println("vt " + u[i][1] + " " + v[i][1]);
				objWriter.println("vt " + u[i][2] + " " + v[i][2]);
			}
		}

		for (VertexNormal normal : model.vertexNormals)
		{
			objWriter.println("vn " + normal.x + " " + normal.y + " " + normal.z);
		}

		for (int i = 0; i < model.faceCount; ++i)
		{
			int x = model.faceIndices1[i] + 1;
			int y = model.faceIndices2[i] + 1;
			int z = model.faceIndices3[i] + 1;

			objWriter.println("usemtl m" + i);
			if (model.faceTextures != null)
			{
				objWriter.println("f "
						+ x + "/" + (i * 3 + 1) + " "
						+ y + "/" + (i * 3 + 2) + " "
						+ z + "/" + (i * 3 + 3));

			}
			else
			{
				objWriter.println("f " + x + " " + y + " " + z);
			}
			objWriter.println("");
		}

		// Write material
		for (int i = 0; i < model.faceCount; ++i)
		{
			short textureId = -1;

			if (model.faceTextures != null)
			{
				textureId = model.faceTextures[i];
			}

			mtlWriter.println("newmtl m" + i);

			if (textureId == -1)
			{
				int rgb = JagexColor.HSLtoRGB(model.faceColors[i], BRIGHTNESS);
				double r = ((rgb >> 16) & 0xff) / 255.0;
				double g = ((rgb >> 8) & 0xff) / 255.0;
				double b = (rgb & 0xff) / 255.0;

				mtlWriter.println("Kd " + r + " " + g + " " + b);
			}
			else
			{
				TextureDefinition texture = textureManager.findTexture(textureId);
				assert texture != null;

				mtlWriter.println("map_Kd sprite/" + texture.getFileIds()[0] + "-0.png");
			}

			int alpha = 0;

			if (model.faceTransparencies != null)
			{
				alpha = model.faceTransparencies[i] & 0xFF;
			}

			if (alpha != 0)
			{
				mtlWriter.println("d " + (alpha / 255.0));
			}
		}
	}

	public int getSimbaHeight()
	{
		int height = 0;
		for (int i = 0; i < model.getVertexY().length; i++) {
			int current = model.getVertexY()[i] * -1;
			if (current > height) height = current;
		}
		return height;
	}

	public List<Integer> getSimbaColors(){
		List<Integer> colors = new ArrayList<>();
		List<Integer> knownTextures = new ArrayList<>();
		List<Short> knownHSL = new ArrayList<>();
		for (int i = 0; i < model.getFaceCount(); ++i)
		{
			// determine face color (textured or colored?)
			int textureId = -1;
			if (model.getFaceTextures() != null) textureId = model.getFaceTextures()[i];

			int rgbColor;
			if (textureId != -1)
			{
				if (knownTextures.contains(textureId)) continue;
				TextureDefinition texture = textureManager.findTexture((textureId));
				rgbColor = JagexColor.adjustForBrightness(texture.getAverageRGB(), JagexColor.BRIGHTNESS_MAX);
				knownTextures.add(textureId);
			}
			else
			{
				if (knownHSL.contains(model.faceColors[i])) continue;
				rgbColor = JagexColor.HSLtoRGB(model.faceColors[i], JagexColor.BRIGHTNESS_MAX);
				knownHSL.add(model.faceColors[i]);
			}

			int bgr = JagexColor.RGBtoBGR(rgbColor);
			if (!colors.contains(bgr)) colors.add(bgr);
		}
		return colors;
	}
}
