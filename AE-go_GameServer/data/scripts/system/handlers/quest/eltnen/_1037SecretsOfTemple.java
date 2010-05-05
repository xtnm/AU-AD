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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author Nephis
 * 
 */
public class _1037SecretsOfTemple extends QuestHandler
{

	private final static int	questId	= 1037;

	public _1037SecretsOfTemple()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203965).addOnTalkEvent(questId); //Castor
		qe.setNpcQuestData(203967).addOnTalkEvent(questId); //Exelion
		qe.setNpcQuestData(700151).addOnTalkEvent(questId); //Flower Wall
		qe.setNpcQuestData(700154).addOnTalkEvent(questId); //Lightning Wall
		qe.setNpcQuestData(700150).addOnTalkEvent(questId); //Wave Wall
		qe.setNpcQuestData(700153).addOnTalkEvent(questId); //Wind Wall
		qe.setNpcQuestData(700152).addOnTalkEvent(questId); //Fire Wall
		qe.addQuestLvlUp(questId);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || player.getCommonData().getLevel() < 24 || qs.getStatus() != QuestStatus.LOCKED)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
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
		if(targetId == 203965) //Castor
		{
			if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
				else if(env.getDialogId() == 10000)
					{
					qs.setQuestVar(1);
                    updateQuestStatus(player, qs);
					PacketSendUtility
						.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
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
		else if(targetId == 203967) //Exelion
		{
			if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
				else if(env.getDialogId() == 10001)
				{
					qs.setQuestVar(2);
					updateQuestStatus(player, qs);
					PacketSendUtility
						.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}
			
			if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
				else if(env.getDialogId() == 10002)
				{
					qs.setQuestVar(3);
					updateQuestStatus(player, qs);
					PacketSendUtility
						.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}	
		}

		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3)
		{
			switch(targetId)
			{
				case 700151: //Flower wall
				{
					if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
							@Override
							public void run()
							{
								final QuestState qs = player.getQuestStateList().getQuestState(questId);
								qs.setQuestVar(4);
								updateQuestStatus(player, qs);
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
		
		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4)
		{
			switch(targetId)
			{
				case 700154: //Lightning wall
				{
					if (qs.getQuestVarById(0) == 4)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
							@Override
							public void run()
							{
								final QuestState qs = player.getQuestStateList().getQuestState(questId);
								qs.setQuestVar(5);
								player.getInventory().removeFromBagByItemId(182201020, 1);
								updateQuestStatus(player, qs);
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
		
		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5)
		{
			switch(targetId)
			{
				case 700150: //Wave wall
				{
					if (qs.getQuestVarById(0) == 5)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
							@Override
							public void run()
							{	
							
								final QuestState qs = player.getQuestStateList().getQuestState(questId);
								qs.setQuestVar(6);
								updateQuestStatus(player, qs);
								player.getInventory().removeFromBagByItemId(182201021, 1);
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
		
		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 6)
		{
			switch(targetId)
			{
				case 700153: //Wind wall
				{
					if (qs.getQuestVarById(0) == 6)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
							@Override
							public void run()
							{
								final QuestState qs = player.getQuestStateList().getQuestState(questId);
								qs.setQuestVar(7);
								updateQuestStatus(player, qs);
								player.getInventory().removeFromBagByItemId(182201022, 1);								
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
		
		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7)
		{
			switch(targetId)
			{
				case 700152: //Fire wall
				{
					if (qs.getQuestVarById(0) == 7)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
							@Override
							public void run()
							{
								final QuestState qs = player.getQuestStateList().getQuestState(questId);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(player, qs);
								player.getInventory().removeFromBagByItemId(182201023, 1);								
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
