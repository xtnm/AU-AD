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
package com.aionemu.gameserver.services;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GAME_TIME;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.DayTime;
import com.aionemu.gameserver.utils.gametime.GameTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.utils.gametime.listeners.DayTimeListener;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 * 
 */
public class GameTimeService
{
	private static Logger	log	= Logger.getLogger(GameTimeService.class);
	@Inject
	private World			world;
	
	private final static int GAMETIME_UPDATE = 3 * 60000;

	public GameTimeService()
	{
		/**
		 * Update players with current game time
		 */
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable(){

			@Override
			public void run()
			{
				log.info("Sending current game time to all players");
				Iterator<Player> iterator = world.getPlayersIterator();
				while(iterator.hasNext())
				{
					Player next = iterator.next();
					PacketSendUtility.sendPacket(next, new SM_GAME_TIME());
				}
			}
		}, GAMETIME_UPDATE, GAMETIME_UPDATE);
		
		/**
		 * Update npcs with time changes
		 */
		addGameTimeHook();
	}

	/**
	 * 
	 * @param dayTime
	 */
	private void sendDayTimeChangeEvents(DayTime dayTime)
	{
		Iterator<AionObject> it = world.getObjectsIterator();
		while(it.hasNext())
		{
			AionObject obj = it.next();
			if(obj instanceof Npc)
			{
				((Npc) obj).getAi().handleEvent(Event.DAYTIME_CHANGE);
			}
		}
	}

	/**
	 * Called only once when game server starts
	 */
	public void addGameTimeHook()
	{
		((EnhancedObject) GameTimeManager.getGameTime()).addCallback(new DayTimeListener(){
			@Override
			protected void onDayTimeChange(GameTime gameTime)
			{
				sendDayTimeChangeEvents(gameTime.getDayTime());
			}

		});
	}

}
