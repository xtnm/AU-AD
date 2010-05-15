/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package admincommands;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.dao.NpcSpawnDAO;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;
import com.sun.tools.javac.util.Log;

/**
 * NPC ingame manager
 *
 * @author xitanium
 * 
 */

public class Npc extends AdminCommand
{
	
	@Inject
	private SpawnEngine spawnEngine;
	
	private static final Logger log = Logger.getLogger(Npc.class);
	
	public Npc()
	{
		super("npc");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_SPAWNNPC)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //npc add <npcid> <[temp]>");
			PacketSendUtility.sendMessage(admin, "syntax //npc move <[temp]> (need to select target npc first)");
			PacketSendUtility.sendMessage(admin, "syntax //npc del <[temp]> (need to select target first)");
			PacketSendUtility.sendMessage(admin, "[temp] : add 'temp' keyword at end of command to make your change discarded on next reboot.");
			return;
		}
		
		if(params[0].equals("add"))
		{
			if(params.length < 2)
			{
				PacketSendUtility.sendMessage(admin, "syntax //npc add <npcid> <[temp]>");
				return;
			}
			
			int npcId = 0;
			
			try
			{
				npcId = Integer.parseInt(params[1]);
			}
			catch(NumberFormatException nfe)
			{
				PacketSendUtility.sendMessage(admin, "invalid npc id");
				return;
			}
			
			boolean temp = false;
			
			if(params.length > 2)
			{
				if(params[2].equals("temp"))
				{
					temp = true;
				}
			}
			
			SpawnTemplate spawn = spawnEngine.addNewSpawn(admin.getWorldId(), 1, npcId, admin.getX(), admin.getY(), admin.getZ(), admin.getHeading(), 0, 0, false, true);
			
			if(spawn == null)
			{
				PacketSendUtility.sendMessage(admin, "no npc with such ID");
				return;
			}
			
			VisibleObject obj = spawnEngine.spawnObject(spawn, 1);
			
			if(!temp)
			{
				int newTemplateId = DAOManager.getDAO(NpcSpawnDAO.class).insertTemplate(npcId, admin.getPosition());
			
				log.info("Admin added new npc spawn template : " + newTemplateId);
			
				DAOManager.getDAO(NpcSpawnDAO.class).insertCache(newTemplateId, obj.getObjectId());
			}
			
			PacketSendUtility.sendMessage(admin, "Spawned : " + obj.getObjectTemplate().getName());
			
		}
		else if(params[0].equals("move"))
		{
			PacketSendUtility.sendMessage(admin, "not yet implemented");
		}
		else if(params[0].equals("del"))
		{
			boolean temp = false;
			if(params.length == 2)
			{
				if(params[1].equals("temp"))
				{
					temp = true;
				}
			}
			if(admin.getTarget() == null || !(admin.getTarget() instanceof com.aionemu.gameserver.model.gameobjects.Npc))
			{
				PacketSendUtility.sendMessage(admin, "Invalid target");
				return;
			}
			com.aionemu.gameserver.model.gameobjects.Npc target = (com.aionemu.gameserver.model.gameobjects.Npc)admin.getTarget();
			int relatedTemplateId = DAOManager.getDAO(NpcSpawnDAO.class).getCacheEntryRelatedTemplate(target.getObjectId());
			if(relatedTemplateId == 0)
			{
				PacketSendUtility.sendMessage(admin, "This NPC is xml-defined. It will be despawned but will re-appear on next reboot.");
			}
			else
			{
				DAOManager.getDAO(NpcSpawnDAO.class).deleteFromCache(relatedTemplateId);
				if(!temp)
				{
					DAOManager.getDAO(NpcSpawnDAO.class).deleteTemplate(relatedTemplateId);
				}
			}
			target.getController().delete();
			PacketSendUtility.sendMessage(admin, "NPC deleted");
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "syntax //npc add <npcid> <[temp]>");
			PacketSendUtility.sendMessage(admin, "syntax //npc move <[temp]> (need to select target npc first)");
			PacketSendUtility.sendMessage(admin, "syntax //npc del <[temp]> (need to select target first)");
			PacketSendUtility.sendMessage(admin, "[temp] : add 'temp' keyword at end of command to make your change discarded on next reboot.");
			return;
		}

	}
}