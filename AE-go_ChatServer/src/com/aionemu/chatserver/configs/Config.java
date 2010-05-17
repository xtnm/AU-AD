/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.chatserver.configs;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.aionemu.commons.configuration.ConfigurableProcessor;
import com.aionemu.commons.configuration.Property;
import com.aionemu.commons.utils.PropertiesUtils;

/**
 * @author ATracer
 */
public class Config
{
	/**
	 * Logger for this class.
	 */
	protected static final Logger	log	= Logger.getLogger(Config.class);

	/**
	 * Chat Server port
	 */
	@Property(key = "chatserver.network.client.port", defaultValue = "10241")
	public static int				CHAT_PORT;

	/**
	 * Chat Server bind ip
	 */
	@Property(key = "chatserver.network.client.host", defaultValue = "*")
	public static String			CHAT_BIND_ADDRESS;

	/**
	 * Game Server port
	 */
	@Property(key = "chatserver.network.gameserver.port", defaultValue = "9021")
	public static int				GAME_PORT;

	/**
	 * Game Server bind ip
	 */
	@Property(key = "chatserver.network.gameserver.host", defaultValue = "*")
	public static String			GAME_BIND_ADDRESS;
	
	/**
	 * Game Server bind ip
	 */
	@Property(key = "chatserver.network.gameserver.password", defaultValue = "*")
	public static String			GAME_SERVER_PASSWORD;

	/**
	 * Load configs from files.
	 */
	public static void load()
	{
		try
		{
			Properties[] props = PropertiesUtils.loadAllFromDirectory("./config");
			ConfigurableProcessor.process(Config.class, props);
		}
		catch (Exception e)
		{
			log.fatal("Can't load chatserver configuration", e);
			throw new Error("Can't load chatserver configuration", e);
		}
	}
}
