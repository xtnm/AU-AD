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
package quest.eltnen;

import java.util.Collections;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author Nephis
 * 
 */
public class _1038ShadowsCommand extends QuestHandler
{	
	@Inject
	ItemService itemService;
	
	private final static int	questId	= 1038;

	public _1038ShadowsCommand()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.setNpcQuestData(700162).addOnTalkEvent(questId); //Underground Artifact
		qe.setNpcQuestData(203933).addOnTalkEvent(questId); //Actaeon
		qe.setNpcQuestData(700172).addOnTalkEvent(questId); //Philipemos's Corpse
		qe.setNpcQuestData(203991).addOnTalkEvent(questId); //Dionera
		qe.setNpcQuestData(203331).addOnKillEvent(questId); //Hippolytus
		qe.setNpcQuestData(203331).addOnAttackEvent(questId); //Hippolytus
		qe.setQuestMovieEndIds(35).add(questId);
		qe.addQuestLvlUp(questId);
	}
		
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || player.getCommonData().getLevel() < 29 || qs.getStatus() != QuestStatus.LOCKED)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}	
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId)
	{
		if(movieId != 35)
			return false;
		Player player = env.getPlayer();
		int instanceId = player.getInstanceId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 7)
			return false;
		Npc npc = (Npc)player.getTarget();
		questService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 203331, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true);
		return true;
	}
	
	@Override
	public boolean onAttackEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 7)
			return false;
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if(targetId != 203331)
			return false;
		Npc npc = (Npc) env.getVisibleObject();
		if(npc.getLifeStats().getCurrentHp() < npc.getLifeStats().getMaxHp() / 5)
		{
			final int instanceId = player.getInstanceId();
			npc = (Npc)player.getTarget();
			npc.getController().onDelete();
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(player, qs);
		}
		return false;
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
		return false;
		if(targetId == 203933) //Actaeon
		{
			if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
				else if(env.getDialogId() == 10001)
					{
					qs.setQuestVar(2);
                    updateQuestStatus(player, qs);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
					}
				else
					return defaultQuestStartDialog(env);
			}
			
			else if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1694);
				else if(env.getDialogId() == 10002)
				{
					qs.setQuestVar(4);
					player.getInventory().removeFromBagByItemId(182201007, 1); //report to acteon with diary
					updateQuestStatus(player, qs);
					PacketSendUtility
						.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}	
			else if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
				else if(env.getDialogId() == 33)
				{
					if(player.getInventory().getItemCountByItemId(182201015) > 0) //TODO: Check other item
					{
						qs.setQuestVar(6);
						updateQuestStatus(player, qs);
						player.getInventory().removeFromBagByItemId(182201015, 1);
						player.getInventory().removeFromBagByItemId(182201016, 1);
						player.getInventory().removeFromBagByItemId(182201017, 1);
						PacketSendUtility
						.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					else
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2120);
				}
				else
					return defaultQuestStartDialog(env);
			}	
		}
		else if(targetId == 203991) //Dionera
		{
			if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 6)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
				else if(env.getDialogId() == 10004)
				{
					qs.setQuestVar(7);
					updateQuestStatus(player, qs);
					PacketSendUtility
						.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 35));
					return true;
				}
				else
					return defaultQuestStartDialog(env);
					
			}

			else if(qs.getStatus() == QuestStatus.REWARD)
				{
					return defaultQuestEndDialog(env);
				}
		}
		
		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
		{
			switch(targetId)
			{
				case 700162: //Underground Artifact
				{
					if (qs.getQuestVarById(0) == 0 && env.getDialogId() == -1)
					{

						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
						final QuestState qs = player.getQuestStateList().getQuestState(questId);
							@Override
							public void run()
							{
								qs.setQuestVar(1);
								updateQuestStatus(player, qs);
								if(player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
									return;
								PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(),
									targetObjectId, 3000, 0));
								PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 38, 0,
									targetObjectId), true);
								PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 34));
							}
						}, 3000);
					}
				}

			}
		}	
		
		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2)
		{
			switch(targetId)
			{
				case 700172: //Philipemos's Corpse
				{
					if (qs.getQuestVarById(0) == 2 && env.getDialogId() == -1)
					{

						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
						final QuestState qs = player.getQuestStateList().getQuestState(questId);
							@Override
							public void run()
							{
								qs.setQuestVar(3);
								updateQuestStatus(player, qs);
								itemService.addItems(player, Collections.singletonList(new QuestItems(182201007, 1)));
								if(player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
									return;
								PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(),
									targetObjectId, 3000, 0));
								PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 38, 0,
									targetObjectId), true);
							}
						}, 3000);
					}
				}

			}
		}
				
		return false;
		}
	
}
