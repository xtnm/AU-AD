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
package com.aionemu.gameserver.utils;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AutoAnnounceDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author root
 *
 */
public class AutoAnnounce extends Thread
{
	
	private static final Logger log = Logger.getLogger(AutoAnnounce.class);
	private Iterator<String> announces = null;
	private boolean status = false;
	
	@Inject
	private World world;
	
	public AutoAnnounce()
	{
		log.info("AutoAnnounce loading from database ...");
		announces = DAOManager.getDAO(AutoAnnounceDAO.class).loadAnnounces().iterator();
	}
	
	public void run()
	{
		while(announces != null)
		{
			while(announces.hasNext())
			{
				String announce = announces.next();
				Iterator<Player> players = world.getPlayersIterator();
				log.info("AutoAnnounce:: " + announce);
				while(players.hasNext())
				{
					PacketSendUtility.sendMessage(players.next(), "Annonce automatique : " + announce);
				}
				
				try
				{
					Thread.sleep(120000);
				}
				catch(InterruptedException ie)
				{
					
				}
			}
			
		}
		announces = null;
		log.info("AutoAnnounce reloading from database");
		announces = DAOManager.getDAO(AutoAnnounceDAO.class).loadAnnounces().iterator();
		run();
	}
	
}
