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
package com.aionemu.gameserver.controllers;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.PortalData;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.portal.ExitPoint;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.services.InstanceService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.google.inject.Inject;

/**
 * @author ATracer
 * 
 */
public class PortalController extends NpcController
{
	private static final Logger	log						= Logger.getLogger(PortalController.class);

	@Inject
	private PortalData		portalData;
	@Inject
	private InstanceService	instanceService;

	private PortalTemplate	portalTemplate;

	@Override
	public void setOwner(Creature owner)
	{
		super.setOwner(owner);
		portalTemplate = portalData.getPortalTemplate(owner.getObjectTemplate().getTemplateId());
	}

	@Override
	public void onDialogRequest(final Player player)
	{
		if(portalTemplate == null)
			return;
			
		if(!CustomConfig.ENABLE_INSTANCES)
			return;

		final int defaultUseTime = 3000;
		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getOwner().getObjectId(),
			defaultUseTime, 1));
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0, getOwner().getObjectId()), true);

		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getOwner().getObjectId(),
					defaultUseTime, 0));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 38, 0, getOwner().getObjectId()), true);
				
				analyzePortation(player);
			}
			
			/**
			 * @param player
			 */
			private void analyzePortation(final Player player)
			{
				if(portalTemplate.getIdTitle() !=0 && player.getCommonData().getTitleId() != portalTemplate.getIdTitle())
					return;

				if(portalTemplate.getRace() != null && !portalTemplate.getRace().equals(player.getCommonData().getRace()))
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MOVE_PORTAL_ERROR_INVALID_RACE);
					return;
				}

				if((portalTemplate.getMaxLevel() != 0 && player.getLevel() > portalTemplate.getMaxLevel())
					|| player.getLevel() < portalTemplate.getMinLevel())
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
					return;
				}

				PlayerGroup group = player.getPlayerGroup();
				if(portalTemplate.isGroup() && group == null)
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ENTER_ONLY_PARTY_DON);
					return;
				}

				if(portalTemplate.isGroup() && group != null)
				{
					WorldMapInstance instance = instanceService.getRegisteredInstance(portalTemplate.getExitPoint()
						.getMapId(), group.getGroupId());
					// register if not yet created
					if(instance == null)
					{
						instance = registerGroup(group);
					}

					transfer(player, instance);
				}
				else if(!portalTemplate.isGroup())
				{
					WorldMapInstance instance = instanceService.getRegisteredInstance(portalTemplate.getExitPoint()
						.getMapId(), player.getObjectId());
					// if already registered - just teleport
					if(instance != null)
					{
						transfer(player, instance);
						return;
					}
					port(player);
				}
			}
		}, defaultUseTime);

	}

	/**
	 * @param player
	 */
	private void port(Player requester)
	{
		WorldMapInstance instance = null;
		int worldId = portalTemplate.getExitPoint().getMapId();
		if(portalTemplate.isInstance())
		{
			instance = instanceService.getNextAvailableInstance(worldId);
			instanceService.registerPlayerWithInstance(instance, requester);
			
		}
		else
		{
			WorldMap worldMap = sp.getWorld().getWorldMap(worldId);
			if(worldMap == null)
			{
				log.warn("There is no registered map with id " + worldId);
				return;
			}
			instance = worldMap.getWorldMapInstance();
		}
		
		transfer(requester, instance);
	}

	/**
	 * @param player
	 */
	private WorldMapInstance registerGroup(PlayerGroup group)
	{
		WorldMapInstance instance = instanceService.getNextAvailableInstance(portalTemplate.getExitPoint().getMapId());
		instanceService.registerGroupWithInstance(instance, group);
		return instance;
	}

	/**
	 * @param players
	 */
	private void transfer(Player player, WorldMapInstance instance)
	{
		ExitPoint exitPoint = portalTemplate.getExitPoint();
		sp.getTeleportService().teleportTo(player, exitPoint.getMapId(), instance.getInstanceId(),
			exitPoint.getX(), exitPoint.getY(), exitPoint.getZ(), 0);
	}

}
