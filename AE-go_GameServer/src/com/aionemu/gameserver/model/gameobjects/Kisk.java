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
package com.aionemu.gameserver.model.gameobjects;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.KiskStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_KISK_UPDATE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Sarynth
 *
 */
public class Kisk extends Npc
{
	/**
	 * Creator of this kisk.
	 *  - Avoid Use. Owner may log out.
	 */
	private Player		creator;
	
	private String		ownerName;
	private Legion		ownerLegion;
	private Race		ownerRace;
	
	private KiskStatsTemplate kiskStatsTemplate;

	private int			remainingResurrections;
	private long		kiskSpawnTime;
	
	private final List<Player> kiskMembers = new ArrayList<Player>();

	
	/**
	 * 
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 */
	public Kisk(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate npcTemplate)
	{
		super(objId, controller, spawnTemplate, npcTemplate);
		
		this.kiskStatsTemplate = npcTemplate.getKiskStatsTemplate();
		if (this.kiskStatsTemplate == null)
			this.kiskStatsTemplate = new KiskStatsTemplate();
		
		remainingResurrections = this.kiskStatsTemplate.getMaxResurrects();
		kiskSpawnTime = System.currentTimeMillis() / 1000;
	}
	
	/**
	 * Required so that the enemy race can attack the Kisk!
	 */
	@Override
	public boolean isAggressiveTo(Creature creature)
	{
		if (creature instanceof Player)
		{
			Player player = (Player)creature;
			if (player.getCommonData().getRace() != this.ownerRace)
				return true;
		}
		return false;
	}

	/**
	 * @return the creator
	 */
	public Player getCreator()
	{
		return creator;
	}

	/**
	 * @param creator the player
	 */
	public void setCreator(Player creator)
	{
		this.creator = creator;
		this.ownerName = creator.getName();
		this.ownerLegion = creator.getLegion();
		this.ownerRace = creator.getCommonData().getRace();
	}
	
	@Override
	protected boolean isEnemyNpc(Npc visibleObject)
	{
		return this.creator.isEnemyNpc(visibleObject);
	}

	@Override
	protected boolean isEnemyPlayer(Player visibleObject)
	{
		return this.creator.isEnemyPlayer(visibleObject);
	}
	
	/**
	 * @return NpcObjectType.NORMAL 
	 */
	@Override
	public NpcObjectType getNpcObjectType()
	{
		return NpcObjectType.NORMAL;
	}

	@Override
	public Creature getActingCreature()
	{
		return this.creator;
	}
	
	@Override
	public Creature getMaster()
	{
		return this.creator;
	}
	
	/**
	 * 1 ~ race
	 * 2 ~ legion
	 * 3 ~ solo
	 * 4 ~ group
	 * 5 ~ alliance
	 * @return useMask
	 */
	public int getUseMask()
	{
		return this.kiskStatsTemplate.getUseMask();
	}

	public List<Player> getCurrentMemberList()
	{
		return this.kiskMembers;
	}
	
	/**
	 * @return
	 */
	public int getCurrentMemberCount()
	{
		return this.kiskMembers.size();
	}

	/**
	 * @return
	 */
	public int getMaxMembers()
	{
		return this.kiskStatsTemplate.getMaxMembers();
	}

	/**
	 * @return
	 */
	public int getRemainingResurrects()
	{
		return this.remainingResurrections;
	}
	
	/**
	 * @return
	 */
	public int getMaxRessurects()
	{
		return this.kiskStatsTemplate.getMaxResurrects();
	}

	/**
	 * @return
	 */
	public int getRemainingLifetime()
	{
		long timeElapsed = (System.currentTimeMillis() / 1000) - kiskSpawnTime;
		int timeRemaining = (int)(this.kiskStatsTemplate.getMaxLifetime() - timeElapsed);
		return (timeRemaining > 0 ? timeRemaining : 0);
	}

	/**
	 * @return
	 */
	public int getMaxLifetime()
	{
		return this.kiskStatsTemplate.getMaxLifetime();
	}
	
	/**
	 * @param player
	 * @return
	 */
	public boolean canBind(Player player)
	{
		String playerName = player.getName();
		
		if (playerName != this.ownerName)
		{
			// Check if they fit the usemask
			switch (this.getUseMask())
			{
				case 1: // Race
					if (this.ownerRace == player.getCommonData().getRace())
						return false;
					break;
					
				case 2: // Legion
					if (ownerLegion == null)
						return false;
					if (!ownerLegion.isMember(player.getObjectId()))
						return false;
					break;
					
				case 3: // Solo
					return false; // Already Checked Name
					
				case 5: // Alliance - Only checks current group.
				case 4: // Group
					if (player.getPlayerGroup() == null)
						return false;
					
					boolean isMember = false;
					for(Player member : player.getPlayerGroup().getMembers())
					{
						if (member.getName() == this.ownerName) {
							isMember = true;
						}
					}
					if (isMember == false)
						return false;
					break;
				default:
					return false;
			}
		}
		
		if (this.kiskMembers.size() >= getMaxMembers())
			return false;
		
		return true;
	}
	
	/**
	 * @param player
	 * @return true if player was added
	 */
	public boolean addPlayer(Player player)
	{
		if(kiskMembers.contains(player))
			return false;
		
		if (player.getKisk() != null)
			player.getKisk().removePlayer(player);

		kiskMembers.add(player);
		
		player.setKisk(this);
		this.broadcastKiskUpdate();
		return true;
	}

	/**
	 * @param player
	 * @return true if member was removed
	 */
	public boolean removePlayer(Player player)
	{
		if(!kiskMembers.contains(player))
			return false;
		
		kiskMembers.remove(player);
		player.setKisk(null);
		this.broadcastKiskUpdate();
		return true;
	}
	
	/**
	 * Sends SM_KISK_UPDATE to each member
	 */
	private void broadcastKiskUpdate()
	{
		// Logic to prevent enemy race from knowing kisk information.
		for(Player member : this.kiskMembers)
		{
			if (!this.getKnownList().knowns(member))
				PacketSendUtility.sendPacket(member, new SM_KISK_UPDATE(this));
		}
		for(VisibleObject obj : this.getKnownList())
		{
			if(obj instanceof Player)
			{
				Player target = (Player) obj;
				if(target.getCommonData().getRace() == this.ownerRace)
					PacketSendUtility.sendPacket(target, new SM_KISK_UPDATE(this));
			}
		}
	}

	/**
	 * Probably not needed, but better to clear them anyways.
	 */
	public void clearOnDeath()
	{
		this.kiskMembers.clear();
	}

	/**
	 * @param player
	 */
	public void resurrectionUsed(Player player)
	{
		remainingResurrections--;
		if (remainingResurrections <= 0)
		{
			player.getKisk().getController().onDespawn(true);
		}
		else
		{
			broadcastKiskUpdate();
		}
	}

	/**
	 * @return
	 */
	public Race getOwnerRace()
	{
		return this.ownerRace;
	}

	/**
	 * @return
	 */
	public String getOwnerName()
	{
		return this.ownerName;
	}

}
