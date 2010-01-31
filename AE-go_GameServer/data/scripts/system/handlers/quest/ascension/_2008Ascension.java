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
package quest.ascension;

import java.util.ArrayList;
import java.util.List;


import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.dataholders.SpawnsData;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author MrPoke
 *
 */
public class _2008Ascension extends QuestHandler
{

	private final static int	questId			= 2008;
	private int					activePlayerId	= 0;
	private List<Npc>			mobs			= new ArrayList<Npc>();

	public _2008Ascension(SpawnsData spawnsData)
	{
		super(questId);
		if(Config.ENABLE_SIMPLE_2NDCLASS)
			return;
		QuestEngine.getInstance().addQuestLvlUp(questId);
		QuestEngine.getInstance().setNpcQuestData(203550).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(790003).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(790002).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(203546).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(205020).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(205040).addOnKillEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(205041).addOnAttackEvent(questId);
		QuestEngine.getInstance().setQuestMovieEndIds(152).add(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int var = qs.getQuestVars().getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(targetId == 205040)
		{
			if(var >= 51 && var <= 53)
			{
				qs.getQuestVars().setQuestVar(qs.getQuestVars().getQuestVars() + 1);
				updateQuestStatus(player, qs);
				if(mobs.contains((Monster)env.getVisibleObject()))
					mobs.remove((Monster)env.getVisibleObject());
				return true;
			}
			else if(var == 54)
			{
				qs.getQuestVars().setQuestVar(5);
				updateQuestStatus(player, qs);
				Monster mob = (Monster) QuestEngine.getInstance().addNewSpawn(320010000, 205041, 301f,
					259f, 205.5f, (byte) 0, false);
				((Monster) mob).getAggroList().addDamageHate(player, 1000, 0);
				mob.getAi().handleEvent(Event.ATTACKED);
				mobs.add(mob);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onAttackEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 5)
			return false;
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if(targetId != 205041)
			return false;
		Monster monster = (Monster) env.getVisibleObject();
		if(monster.getLifeStats().getCurrentHp() < monster.getLifeStats().getMaxHp() / 2)
		{
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 152));
			monster.getController().onDie();
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVars();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 203550)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 0)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
						else if(var == 4)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
						else if(var == 6)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
					case 2376:
						if(var == 4)
						{
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 57));
							player.getInventory().removeFromBagByItemId(182203009, 1);
							player.getInventory().removeFromBagByItemId(182203010, 1);
							player.getInventory().removeFromBagByItemId(182203011, 1);
							return false;
						}
					case 10000:
						if(var == 0)
						{
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
					case 10004:
						if(var == 4)
						{
							qs.getQuestVars().setQuestVar(99);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 0));
							player.getController().teleportTo(320010000, 457.65f, 426.8f, 230.4f, 0);
							return true;
						}
					case 10005:
						if(var == 6)
						{
							PlayerClass playerClass = player.getCommonData().getPlayerClass();
							if(playerClass == PlayerClass.WARRIOR)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 3057);
							else if(playerClass == PlayerClass.SCOUT)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 3398);
							else if(playerClass == PlayerClass.MAGE)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 3739);
							else if(playerClass == PlayerClass.PRIEST)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 4080);
						}
					case 10006:
						if(var == 6)
							return setPlayerClass(env, qs, PlayerClass.GLADIATOR);
					case 10007:
						if(var == 6)
							return setPlayerClass(env, qs, PlayerClass.TEMPLAR);
					case 10008:
						if(var == 6)
							return setPlayerClass(env, qs, PlayerClass.ASSASSIN);
					case 10009:
						if(var == 6)
							return setPlayerClass(env, qs, PlayerClass.RANGER);
					case 10010:
						if(var == 6)
							return setPlayerClass(env, qs, PlayerClass.SORCERER);
					case 10011:
						if(var == 6)
							return setPlayerClass(env, qs, PlayerClass.SPIRIT_MASTER);
					case 10012:
						if(var == 6)
							return setPlayerClass(env, qs, PlayerClass.CLERIC);
					case 10013:
						if(var == 6)
							return setPlayerClass(env, qs, PlayerClass.CHANTER);
				}
			}
			else if(targetId == 790003)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 1)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
					case 10001:
						if(var == 1)
						{
							if(player.getInventory().getItemCountByItemId(182203009) == 0)
								QuestEngine.getInstance().addItem(player, 182203009, 1);
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 790002)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 2)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
					case 10002:
						if(var == 2)
						{
							if(player.getInventory().getItemCountByItemId(182203010) == 0)
								QuestEngine.getInstance().addItem(player, 182203010, 1);
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 203546)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 3)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
					case 10003:
						if(var == 3)
						{
							if(player.getInventory().getItemCountByItemId(182203011) == 0)
								QuestEngine.getInstance().addItem(player, 182203011, 1);
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 205020)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 99)
						{
							if(activePlayerId != 0 && activePlayerId != player.getObjectId())
							{
								World world = player.getActiveRegion().getWorld();
								Player activePlayer = world.findPlayer(activePlayerId);
								if(!(activePlayer == null || !activePlayer.isOnline() || activePlayer.getWorldId() != 310010000))
									return false;
							}
							activePlayerId = player.getObjectId();
							for(Npc mob : mobs)
								mob.getController().onDie();
							mobs.clear();
							PacketSendUtility.sendPacket(player, new SM_EMOTION(player, 6, 3001, 0));
							qs.getQuestVars().setQuestVar(50);
							updateQuestStatus(player, qs);
							ThreadPoolManager.getInstance().schedule(new Runnable(){
								@Override
								public void run()
								{
									qs.getQuestVars().setQuestVar(51);
									updateQuestStatus(player, qs);
									mobs.add((Monster) QuestEngine.getInstance().addNewSpawn(320010000, 205040,
										294f, 277f, 207f, (byte) 0, false));
									mobs.add((Monster) QuestEngine.getInstance().addNewSpawn(320010000, 205040,
										305f, 279f, 206.5f, (byte) 0, false));
									mobs.add((Monster) QuestEngine.getInstance().addNewSpawn(320010000, 205040,
										298f, 253f, 205.7f, (byte) 0, false));
									mobs.add((Monster) QuestEngine.getInstance().addNewSpawn(320010000, 205040,
										306f, 251f, 206f, (byte) 0, false));
									for(Npc mob : mobs)
									{
										((Monster) mob).getAggroList().addDamageHate(player, 1000, 0);
										mob.getAi().handleEvent(Event.ATTACKED);
									}
								}
							}, 43000);
							return true;
						}
						return false;
					default:
						return false;
				}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203550)
			{
				return defaultQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs != null)
		{
			if(qs.getStatus() != QuestStatus.START)
				return false;
			int var = qs.getQuestVars().getQuestVars();
			if(var == 5 || (var >= 50 && var <= 55))
			{
				if(activePlayerId == player.getObjectId() && player.getWorldId() == 320010000)
					return false;
				else
				{
					if(player.getWorldId() == 320010000)
					{
						player.getController().moveToBindLocation(false);
					}
					qs.getQuestVars().setQuestVar(4);
					updateQuestStatus(player, qs);
				}
			}
		}
		else
		{
			if(player.getCommonData().getLevel() < 9)
				return false;
			env.setQuestId(questId);
			QuestEngine.getInstance().getQuest(env).startQuest(QuestStatus.START);
			return true;
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId)
	{
		if(movieId != 152)
			return false;
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 5)
			return false;
		mobs.add((Npc) QuestEngine.getInstance().addNewSpawn(320010000, 203550, 301.92999f, 274.26001f, 205.7f, (byte) 0, false));
		qs.getQuestVars().setQuestVar(6);
		updateQuestStatus(player, qs);
		return true;
	}

	private boolean setPlayerClass(QuestEnv env, QuestState qs, PlayerClass playerClass)
	{
		Player player = env.getPlayer();
		player.getCommonData().setPlayerClass(playerClass);
		player.getCommonData().upgradePlayer();
		qs.setStatus(QuestStatus.REWARD);
		updateQuestStatus(player, qs);
		sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
		return true;
	}
}
