/*
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
package com.aionemu.gameserver.controllers;

import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.DuelResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.templates.BindPointTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANNEL_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHERABLE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NEARBY_QUESTS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRIVATE_STORE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.AbyssService;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.HopType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.Skill.SkillType;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneManager;

/**
 * This class is for controlling players.
 * 
 * @author -Nemesiss-, ATracer (2009-09-29), xavier
 * 
 */
public class PlayerController extends CreatureController<Player>
{
	private static Logger	log	= Logger.getLogger(PlayerController.class);

	// TEMP till player AI introduced
	private Creature		lastAttacker;

	private boolean			isInShutdownProgress;

	/**
	 * @return the lastAttacker
	 */
	public Creature getLastAttacker()
	{
		return lastAttacker;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void see(VisibleObject object)
	{
		super.see(object);
		if(object instanceof Player)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO((Player) object, isEnemy((Player) object)));
			getOwner().getEffectController().sendEffectIconsTo((Player) object);
		}
		else if(object instanceof Npc)
		{
			boolean update = false;
			Npc npc = ((Npc) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			for(int questId : QuestEngine.getInstance().getNpcQuestData(npc.getNpcId()).getOnQuestStart())
			{
				if(QuestEngine.getInstance().getQuest(new QuestEnv(object, getOwner(), questId, 0))
					.checkStartCondition())
				{
					if(!getOwner().getNearbyQuests().contains(questId))
					{
						update = true;
						getOwner().getNearbyQuests().add(questId);
					}
				}
			}
			if(update)
				updateNearbyQuestList();
		}
		else if(object instanceof Gatherable)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO((Gatherable) object));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notSee(VisibleObject object)
	{
		super.notSee(object);
		if(object instanceof Npc)
		{
			boolean update = false;
			for(int questId : QuestEngine.getInstance().getNpcQuestData(((Npc) object).getNpcId()).getOnQuestStart())
			{
				QuestEnv env = new QuestEnv(object, getOwner(), questId, 0);
				if(QuestEngine.getInstance().getQuest(env).checkStartCondition())
				{
					if(getOwner().getNearbyQuests().contains(questId))
					{
						update = true;
						getOwner().getNearbyQuests().remove(getOwner().getNearbyQuests().indexOf(questId));
					}
				}
			}
			if(update)
				updateNearbyQuestList();
		}

		PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object));
	}

	/**
	 * Will be called by ZoneManager when player enters specific zone
	 * 
	 * @param zoneInstance
	 */
	public void onEnterZone(ZoneInstance zoneInstance)
	{
		QuestEngine.getInstance().onEnterZone(new QuestEnv(null, this.getOwner(), 0, 0),
			zoneInstance.getTemplate().getName());
	}

	/**
	 * Will be called by ZoneManager when player leaves specific zone
	 * 
	 * @param zoneInstance
	 */
	public void onLeaveZone(ZoneInstance zoneInstance)
	{

	}

	/**
	 * {@inheritDoc}
	 * 
	 * Shoul only be triggered from one place (life stats)
	 */
	@Override
	public void onDie()
	{
		super.onDie();
		// TODO probably introduce variable - last attack creature in player AI
		Player player = this.getOwner();
		// TODO move to DuelController
		if(lastAttacker instanceof Player && !isEnemy((Player) lastAttacker))
		{
			this.lostDuelWith((Player) lastAttacker);
			((Player) lastAttacker).getController().wonDuelWith(player);
		}
		else
		{
			getOwner().setState(CreatureState.DEAD);
			if(player.getLevel() > 4)
				player.getCommonData().calculateExpLoss();

			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 13, 0, lastAttacker.getObjectId()), true);
			PacketSendUtility.sendPacket(player, new SM_DIE());
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
			QuestEngine.getInstance().onDie(new QuestEnv(null, player, 0, 0));
		}
		if(lastAttacker instanceof Player && isEnemy((Player) lastAttacker))
		{
			AbyssService.doReward(getOwner(), (Player) lastAttacker);
		}
	}

	@Override
	public void attackTarget(int targetObjectId)
	{
		super.attackTarget(targetObjectId);

		Player player = getOwner();

		if(!player.canAttack())
			return;

		PlayerGameStats gameStats = player.getGameStats();		

		World world = player.getActiveRegion().getWorld();
		Creature target = (Creature) world.findAionObject(targetObjectId);

		if(!RestrictionsManager.canAttack(player, target))
			return;

		List<AttackResult> attackResult = AttackUtil.calculateAttackResult(player, target);

		int damage = 0;
		for(AttackResult result : attackResult)
		{
			damage += result.getDamage();
		}
		
		long time = System.currentTimeMillis();
		int attackType = 0; // TODO investigate attack types
		PacketSendUtility.broadcastPacket(player, new SM_ATTACK(player.getObjectId(), target.getObjectId(), gameStats
			.getAttackCounter(), (int) time, attackType, attackResult), true);

		target.getController().onAttack(player, damage);

		gameStats.increaseAttackCounter();
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage)
	{
		if(getOwner().getLifeStats().isAlreadyDead())
			return;

		super.onAttack(creature, skillId, type, damage);
		lastAttacker = creature;
		getOwner().getLifeStats().reduceHp(damage);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), type, skillId, damage), true);
	}

	public void useSkill(int skillId)
	{
		Player player = getOwner();

		if(!player.canAttack())
			return;

		// check if is casting to avoid multicast exploit
		// TODO cancel skill if other is used
		if(this.getOwner().isCasting())
			return;

		// later differentiate between skills
		notifyAttackObservers();

		Skill skill = SkillEngine.getInstance().getSkillFor(getOwner(), skillId);
		if(skill != null)
		{
			skill.useSkill(SkillType.CAST);
		}
	}

	@Override
	public void doDrop(Player player)
	{
		super.doDrop(player);
	}

	@Override
	public void onMove()
	{
		super.onMove();
		PlayerGameStats pgs = getOwner().getGameStats();
		pgs.increaseMoveCounter();
		if(pgs.getMoveCounter() % 5 == 0)
		{
			ZoneManager.getInstance().checkZone(getOwner());
		}
	}

	@Override
	public void onStopMove()
	{
		super.onStopMove();
	}

	@Override
	public void onStartMove()
	{
		if(this.getOwner().isCasting())
		{
			this.getOwner().setCasting(null);
			PacketSendUtility.sendPacket(this.getOwner(), new SM_SKILL_CANCEL(this.getOwner()));
			PacketSendUtility.sendPacket(this.getOwner(), SM_SYSTEM_MESSAGE.STR_SKILL_CANCELED());
		}
		super.onStartMove();
	}

	@Override
	public boolean teleportTo(final int worldId, final int instanceId, final float x, final float y, final float z,
		final byte heading, final int delay)
	{
		final Player player = getOwner();

		if(delay == 0)
		{
			changePosition(worldId, instanceId, x, y, z, heading);
			return true;
		}

		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, 0, delay, 0, 0));
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				if(player.getLifeStats().isAlreadyDead())
					return;
				
				if(delay != 0)
				{
					PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(0, 0, 0, 0, 1, 0));
				}
				changePosition(worldId, instanceId, x, y, z, heading);
			}
		}, delay);

		return true;
	}

	/**
	 * 
	 * @param worldId
	 * @param instanceId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 */
	private void changePosition(int worldId, int instanceId, float x, float y, float z, byte heading)
	{
		Player player = getOwner();
		World world = player.getActiveRegion().getWorld();
		if(player.getInstanceId() != instanceId || player.getWorldId() != worldId)
		{
			world.getWorldMap(player.getWorldId()).getWorldMapInstanceById(player.getInstanceId()).removePlayer(
				player.getObjectId());
			world.getWorldMap(worldId).getWorldMapInstanceById(instanceId).addPlayer(player.getObjectId());
		}
		world.despawn(player);
		world.setPosition(player, worldId, instanceId, x, y, z, heading);
		player.setProtectionActive(true);
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
	}

	/**
	 * Send the duel request to the owner
	 * 
	 * @param requester
	 *            the player who requested the duel
	 */
	public void onDuelRequest(Player requester)
	{
		log.debug("[Duel] Player " + this.getOwner().getName() + " has been requested for a duel by "
			+ requester.getName());
		RequestResponseHandler rrh = new RequestResponseHandler(requester){
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				responder.getController().rejectDuelRequest((Player) requester);
			}

			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				responder.getController().startDuelWith((Player) requester);
				((Player) requester).getController().startDuelWith(responder);
			}
		};
		this.getOwner().getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, rrh);
		PacketSendUtility.sendPacket(this.getOwner(), new SM_QUESTION_WINDOW(
			SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, requester.getObjectId(), requester.getName()));
		PacketSendUtility.sendPacket(this.getOwner(), SM_SYSTEM_MESSAGE.DUEL_ASKED_BY(requester.getName()));
	}

	/**
	 * Asks confirmation for the duel request
	 * 
	 * @param target
	 *            the player whose the duel was requested
	 */
	public void confirmDuelWith(Player target)
	{
		log.debug("[Duel] Player " + this.getOwner().getName() + " has to confirm his duel with " + target.getName());
		RequestResponseHandler rrh = new RequestResponseHandler(target){
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				log.debug("[Duel] Player " + responder.getName() + " confirmed his duel with " + requester.getName());
			}

			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				responder.getController().cancelDuelRequest((Player) requester);
			}
		};
		this.getOwner().getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL, rrh);
		PacketSendUtility.sendPacket(this.getOwner(), new SM_QUESTION_WINDOW(
			SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL, target.getObjectId(), target.getName()));
		PacketSendUtility.sendPacket(this.getOwner(), SM_SYSTEM_MESSAGE.DUEL_ASKED_TO(target.getName()));
	}

	/**
	 * Rejects the duel request
	 * 
	 * @param requester
	 *            the duel requester
	 */
	public void rejectDuelRequest(Player requester)
	{
		log.debug("[Duel] Player " + this.getOwner().getName() + " rejected duel request from " + requester.getName());
		requester.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECTED_BY(this.getOwner().getName()));
		this.getOwner().getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECT_DUEL_OF(requester.getName()));
	}

	/**
	 * Cancels the duel request
	 * 
	 * @param target
	 *            the duel target
	 */
	public void cancelDuelRequest(Player target)
	{
		log
			.debug("[Duel] Player " + this.getOwner().getName() + " cancelled his duel request with "
				+ target.getName());
		target.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_BY(this.getOwner().getName()));
		this.getOwner().getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_WITH(target.getName()));
	}

	/**
	 * Starts the duel
	 * 
	 * @param player
	 *            the player to start duel with
	 */
	public void startDuelWith(Player player)
	{
		log.debug("[Duel] Player " + this.getOwner().getName() + " start duel with " + player.getName());
		PacketSendUtility.sendPacket(getOwner(), SM_DUEL.SM_DUEL_STARTED(player.getObjectId()));
		lastAttacker = player;
	}

	/**
	 * Won the duel
	 * 
	 * @param attacker
	 *            the other player
	 */
	public void wonDuelWith(Player attacker)
	{
		log.debug("[Duel] Player " + attacker.getName() + " won duel against " + this.getOwner().getName());
		PacketSendUtility.sendPacket(getOwner(), SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_WON, attacker.getName()));
	}

	/**
	 * Lost the duel
	 * 
	 * @param attacker
	 *            the other player
	 */
	public void lostDuelWith(Player attacker)
	{
		log.debug("[Duel] Player " + attacker.getName() + " lost duel against " + this.getOwner().getName());
		PacketSendUtility.sendPacket(getOwner(), SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_LOST, attacker.getName()));
		PlayerLifeStats pls = getOwner().getLifeStats();
		getOwner().setLifeStats(new PlayerLifeStats(getOwner(), 1, pls.getCurrentMp()));
		getOwner().getLifeStats().triggerRestoreTask();
	}

	/**
	 * 
	 */
	public void updatePassiveStats()
	{
		for(SkillListEntry skillEntry : getOwner().getSkillList().getAllSkills())
		{
			Skill skill = SkillEngine.getInstance().getSkillFor(getOwner(), skillEntry.getSkillId());
			if(skill != null && skill.isPassive())
			{
				skill.useSkill(SkillType.PASSIVE);
			}
		}
	}

	@Override
	public Player getOwner()
	{
		return (Player) super.getOwner();
	}

	/**
	 * @param channel
	 */
	public void changeChannel(int channel)
	{
		Player player = getOwner();
		World world = player.getActiveRegion().getWorld();
		world.despawn(player);
		world.setPosition(player, player.getWorldId(), channel + 1, player.getX(), player.getY(), player.getZ(), player
			.getHeading());
		player.setProtectionActive(true);
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
	}

	@Override
	public void onRestore(HopType hopType, int value)
	{
		super.onRestore(hopType, value);
		switch(hopType)
		{
			case DP:
				getOwner().getCommonData().addDp(value);
				break;
		}
	}

	public void moveToBindLocation(boolean useTeleport)
	{
		this.moveToBindLocation(useTeleport, 0);
	}

	/**
	 * @param useTeleport
	 */
	public void moveToBindLocation(boolean useTeleport, int delay)
	{
		float x, y, z;
		int worldId;
		BindPointTemplate bplist;
		Player player = getOwner();
		World world = player.getActiveRegion().getWorld();

		LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getCommonData().getRace());

		int bindPointId = player.getCommonData().getBindPoint();
		if(bindPointId != 0)
		{
			bplist = DataManager.BIND_POINT_DATA.getBindPointTemplate2(bindPointId);
			worldId = bplist.getZoneId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
		}
		else
		{
			locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getCommonData().getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}

		if(useTeleport)
		{
			teleportTo(worldId, x, y, z, delay);
		}
		else
		{
			if(player.getInstanceId() != 1 || player.getWorldId() != worldId)
			{
				world.getWorldMap(player.getWorldId()).getWorldMapInstanceById(player.getInstanceId()).removePlayer(
					player.getObjectId());
				world.getWorldMap(worldId).getWorldMapInstanceById(1).addPlayer(player.getObjectId());
			}
			world.setPosition(player, worldId, x, y, z, player.getHeading());
		}
	}

	public boolean isEnemy(Player player)
	{
		return player.getCommonData().getRace() != getOwner().getCommonData().getRace();
	}

	public void updateNearbyQuestList()
	{
		getOwner().addPacketBroadcastMask(BroadcastMode.UPDATE_NEARBY_QUEST_LIST);
	}

	public void updateNearbyQuestListImpl()
	{
		PacketSendUtility.sendPacket(getOwner(), new SM_NEARBY_QUESTS(getOwner().getNearbyQuests()));
	}

	public boolean isInShutdownProgress()
	{
		return isInShutdownProgress;
	}

	public void setInShutdownProgress(boolean isInShutdownProgress)
	{
		this.isInShutdownProgress = isInShutdownProgress;
	}

	/**
	 * Handle dialog
	 * 
	 * @return true if target is player
	 */
	public void onDialogSelect(int dialogId, Player player, int questId, LegionService legionService, CubeExpandService cubeExpandService)
	{
		switch(dialogId)
		{
			case 2:
				PacketSendUtility.sendPacket(player, new SM_PRIVATE_STORE(getOwner().getStore()));
				break;
		}
	}
}