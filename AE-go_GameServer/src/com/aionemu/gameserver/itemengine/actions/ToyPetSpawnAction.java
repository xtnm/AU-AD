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
package com.aionemu.gameserver.itemengine.actions;

import java.util.concurrent.Future;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.loadingutils.XmlServiceProxy;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Sarynth
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ToyPetSpawnAction")
public class ToyPetSpawnAction extends AbstractItemAction
{
	
	@XmlAttribute
	protected int npcid;
	
	@XmlAttribute
	protected int time;	
	
	/**
	 * 
	 * @return the Npc Id
	 */
	public int getNpcId()
	{
		return npcid;
	}
	
	public int getTime()
	{
		return time;
	}
	
	/**
	 * 
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal (Unmarshaller u, Object parent)
	{		
		xsp = u.getAdapter(XmlServiceProxy.class);
	}
	
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		SpawnEngine spawnEngine = xsp.getSpawnEngine();
		float x = player.getX();
		float y = player.getY();
		float z = player.getZ();
		byte heading = (byte) ((player.getHeading() + 60)%120);
		int worldId = player.getWorldId();
		int instanceId = player.getInstanceId();

		SpawnTemplate spawn = spawnEngine.addNewSpawn(worldId, 
			instanceId, npcid, x, y, z, heading, 0, 0, true, true);
		
        final Kisk kisk = spawnEngine.spawnKisk(spawn, instanceId, player);
        
        // Schedule Despawn Action
		Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable(){

			@Override
			public void run()
			{
				kisk.getController().onDespawn(true);
			}
		}, kisk.getMaxLifetime() * 1000);
		kisk.getController().addTask(TaskId.DESPAWN, task);
		
		//ShowAction
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
			parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId()), true);
			
		//RemoveKisk
		player.getInventory().removeFromBagByObjectId(parentItem.getObjectId(), 1);
	}
}
