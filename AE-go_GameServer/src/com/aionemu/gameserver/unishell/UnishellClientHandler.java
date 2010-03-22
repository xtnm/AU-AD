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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.unishell.commands.UnishellCommand;
import com.aionemu.gameserver.unishell.commands.UnishellCommandIndex;

/**
 * @author xitanium
 *
 */
public class UnishellClientHandler extends Thread
{
	
	private static final Logger log = Logger.getLogger(UnishellClientHandler.class);
	private Socket client;
	
	public UnishellClientHandler(Socket s)
	{
		this.client = s;
	}
	
	public void run()
	{
		BufferedReader reader = null;
		try
		{
			String currentIPAddress = NetworkConfig.GAME_BIND_ADDRESS;
			log.info("New Unishell client connection from: " + client.getInetAddress().toString());
			
			// send helo
			client.getOutputStream().write(("WEL unishell@" + currentIPAddress + "\n\r").getBytes());
			
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String clientUsername = reader.readLine();
			if(clientUsername.equals(""))
			{
				client.getOutputStream().write("500 Invalid authentication sequence. Closing connection.".getBytes());
				client.close();
				return;
			}
			
			String clientPassword = reader.readLine();
			if(clientPassword.equals(""))
			{
				client.getOutputStream().write("501 Invalid authentication sequence. Closing connection.".getBytes());
				client.close();
				return;
			}
			
			Map<String,String> authorizedKeys = AuthorizedKeys.loadAuthorizedKeys();
			Iterator<Entry<String, String>> authorizedEntries = authorizedKeys.entrySet().iterator();
			boolean hasValidCredentials = false;
			
			while(authorizedEntries.hasNext())
			{
				Entry<String, String> currentFileCredentials = authorizedEntries.next();
				if(clientUsername.equals(currentFileCredentials.getKey()))
				{
					String clientPasswordHash = CryptoHelper.encodeSHA1(clientPassword);
					if(currentFileCredentials.getValue().equals(clientPasswordHash))
					{
						hasValidCredentials = true;
						break;
					}
				}
			}
			
			if(!hasValidCredentials)
			{
				log.warn("Invalid credentials : " + clientUsername + "/" + clientPassword + " from " + client.getInetAddress().toString());
				client.getOutputStream().write("510 Invalid credentials. Closing connection".getBytes());
				client.close();
				return;
			}
			
			client.getOutputStream().write(("250 USER " + clientUsername + "\n\r").getBytes());
			
			log.info("Unishell user logged in: " + clientUsername);
			
			// Listen for command
			String inputCommand = reader.readLine();
			String[] commandAndParams = inputCommand.split(" ", 2);
			String commandName = commandAndParams[0];
			
			UnishellCommand command = UnishellCommandIndex.getCommand(commandName);
			
			if(command == null)
			{
				client.getOutputStream().write("520 No such command.".getBytes());
				client.close();
				return;
			}
			
			log.info("Unishell client " + clientUsername + " executed command: " + inputCommand);
			
			client.getOutputStream().write(("200 DATA \n\r").getBytes());
			
			client.getOutputStream().write(command.execute(commandAndParams[1].split(" ")).getBytes());
			
			client.getOutputStream().write("\n\r250 END".getBytes());
			
			return;
			
		}
		catch(Exception e)
		{
			log.error("Cannot handle shell client", e);
		}
		
	}

}
