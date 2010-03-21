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
package com.aionemu.gameserver.unishell;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author xitanium
 *
 */
public class AuthorizedKeys
{
	
	private static final Logger log = Logger.getLogger(AuthorizedKeys.class);
	
	public static Map<String, String> loadAuthorizedKeys()
	{
		Map<String, String> result = new HashMap<String, String>();
		try
		{
			FileInputStream fis = new FileInputStream("config/network/unishell.passwd");
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String line;
			while((line = br.readLine()) != null)
			{
				String[] values = line.split(":");
				if(values[0] != null && values[0] != "" && values[1] != null && values[1] != "")
				{
					result.put(values[0], values[1]);
				}
			}
			dis.close();
		}
		catch(FileNotFoundException fnfe)
		{
			log.error("Cannot load authorized keys file unishell.passwd", fnfe);
		}
		catch(IOException e)
		{
			log.error("Cannot read authorized keys file unishell.passwd", e);
		}
		
		return result;
	}

}
