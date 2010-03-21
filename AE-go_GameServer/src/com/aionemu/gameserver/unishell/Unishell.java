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

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * @author root
 *
 */
public class Unishell implements Runnable
{
	
	private static final Logger log = Logger.getLogger(Unishell.class);
	private int port;
	
	public Unishell(int port)
	{
		this.port = port;
	}
	
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(this.port);
			log.info("Unishell listening on port " + this.port);
			Socket client;
			while(true)
			{
				client = serverSocket.accept();
				//UnishellClientHandler handler
				
			}
		}
		catch(Exception e)
		{
			log.error("Cannot start Unishell", e);
		}
		
	}

}
