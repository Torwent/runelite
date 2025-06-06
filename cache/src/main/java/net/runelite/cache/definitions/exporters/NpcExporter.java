/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
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
package net.runelite.cache.definitions.exporters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.runelite.cache.definitions.NpcDefinition;

public class NpcExporter
{
	private final NpcDefinition npc;
	private final Gson gson;

	public NpcExporter(NpcDefinition npc)
	{
		this.npc = npc;

		GsonBuilder builder = new GsonBuilder()
				.setPrettyPrinting();
		gson = builder.create();
	}

	public String export()
	{
		return gson.toJson(npc);
	}

	public void exportTo(File file) throws IOException
	{
		try (FileWriter fw = new FileWriter(file))
		{
			fw.write(export());
		}
	}

	public String simbaExport(int height, List<Integer> colors)
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("name", npc.name);
		obj.addProperty("level", npc.combatLevel);
		obj.addProperty("category", npc.category);
		obj.addProperty("minimapdot", npc.isMinimapVisible);
		JsonArray actions = new JsonArray();
		for (int i = 0; i < npc.actions.length; i++) {
			if (npc.actions[i] != null) actions.add(npc.actions[i]);
		}

		obj.add("actions", actions);

		JsonArray size = new JsonArray();
		size.add(npc.size);
		size.add(npc.size);
		size.add(height);
		obj.add("size", size);

		JsonArray jsonColors = new JsonArray();
		for (Integer color : colors) {
			jsonColors.add(color);
		}
		obj.add("colors", jsonColors);

		return obj.toString();
	}

	public void simbaExportTo(File file, int height, List<Integer> colors) throws IOException
	{
		try (FileWriter fw = new FileWriter(file))
		{
			fw.write(simbaExport(height, colors));
		}
	}
}
