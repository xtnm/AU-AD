/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import static org.apache.commons.io.filefilter.FileFilterUtils.andFileFilter;
import static org.apache.commons.io.filefilter.FileFilterUtils.makeSVNAware;
import static org.apache.commons.io.filefilter.FileFilterUtils.notFileFilter;
import static org.apache.commons.io.filefilter.FileFilterUtils.prefixFileFilter;
import static org.apache.commons.io.filefilter.FileFilterUtils.suffixFileFilter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.QuestScriptsData;
import com.aionemu.gameserver.dataholders.QuestsData;
import com.aionemu.gameserver.dataholders.StaticData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.unishell.AuthorizedKeys;
import com.aionemu.gameserver.unishell.CryptoHelper;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;

/**
 * @author MrPoke
 * 
 */
public class Unishell extends AdminCommand
{

	private static final Logger	log	= Logger.getLogger(Unishell.class);

	public Unishell()
	{
		super("unishell");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < 3)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if(params.length < 2)
		{
			PacketSendUtility.sendMessage(admin, "Syntax: //unishell <useradd|> <values...>");
			PacketSendUtility.sendMessage(admin, "//unishell useradd username password");
			return;
		}
		
		if(params[0].equals("adduser"))
		{
			if(params.length < 3)
			{
				PacketSendUtility.sendMessage(admin, "Syntax; //unishell useradd username password");
				return;
			}
			
			String username = params[1];
			String password = params[2];
			String hashedPassword = CryptoHelper.encodeSHA1(password);
			
			Map<String,String> actualAuthorizedKeys = AuthorizedKeys.loadAuthorizedKeys();
			Iterator<Entry<String,String>> actualAuthorizedEntries = actualAuthorizedKeys.entrySet().iterator();
			boolean checkResult = false;
			while(actualAuthorizedEntries.hasNext())
			{
				if(username.equals(actualAuthorizedEntries.next().getKey()))
				{
					checkResult = true;
				}
			}
			
			if(checkResult)
			{
				PacketSendUtility.sendMessage(admin, "Error: username already exists.");
				return;
			}
			
			try
			{
				FileOutputStream file = new FileOutputStream("./config/network/unishell.passwd",true);
				DataOutputStream out  = new DataOutputStream(file);
				out.writeBytes(username + ":" + hashedPassword);
				out.flush();
				out.close();
				PacketSendUtility.sendMessage(admin, "Unishell user '" + username + "' successfully added !");
				return;
			}
			catch(FileNotFoundException fnfe)
			{
				log.error("Cannot open unishell password file for writing at ./config/network/unishell.passwd",fnfe);
				PacketSendUtility.sendMessage(admin, "Error: cannot open password file.");
				return;
			}
			catch(IOException ioe)
			{
				log.error("Cannot write to unishell password file for writing at ./config/network/unishell.passwd", ioe);
				PacketSendUtility.sendMessage(admin, "Error: cannot write to password file.");
				return;
			}
			
		}

	}
}
