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
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
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
public class _1036KaidanPrisoner extends QuestHandler
{

	private final static int	questId	= 1036;

	public _1036KaidanPrisoner()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203904).addOnTalkEvent(questId); //Malonimus
		qe.setNpcQuestData(204045).addOnTalkEvent(questId); //Taera
		qe.setNpcQuestData(204003).addOnTalkEvent(questId); //Hierni
		qe.setNpcQuestData(204004).addOnTalkEvent(questId); //Captured Gardugu
		qe.setNpcQuestData(204020).addOnTalkEvent(questId); //Mabangtah
		qe.setNpcQuestData(203901).addOnTalkEvent(questId); //Telemachus
		qe.addQuestLvlUp(questId);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || player.getCommonData().getLevel() < 23 || qs.getStatus() != QuestStatus.LOCKED)
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
		if(targetId == 203904) //Malonimus
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
	
		}
		else if(targetId == 204045) //Taera
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
					PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 32));
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}
			
		}
		
		else if(targetId == 204003) //Hierni
		{
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
			
			if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3) //TODO: Item Check
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
				else if(env.getDialogId() == 10003)
				{
					qs.setQuestVar(4);
					updateQuestStatus(player, qs);
					PacketSendUtility
						.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 50));
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}
			
		}
		
		else if(targetId == 204004) //Captured Gardugu
		{
			if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
				else if(env.getDialogId() == 10004)
				{
					qs.setQuestVar(5);
					updateQuestStatus(player, qs);
					PacketSendUtility
						.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}			
		}
	
		else if(targetId == 204020) //Mabangtah
		{
			if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
				else if(env.getDialogId() == 10005)
				{
					qs.setStatus(QuestStatus.REWARD);
					qs.setQuestVar(6);
					updateQuestStatus(player, qs);
					PacketSendUtility
						.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}			
		}	
		
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203901)
				return defaultQuestEndDialog(env);
		}
		return false;

	}
}
