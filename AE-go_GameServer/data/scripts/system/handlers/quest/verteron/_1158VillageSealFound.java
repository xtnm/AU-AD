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
package quest.veteron;

import java.util.Collections;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author Rhys2002
 * 
 */
public class _1158VillageSealFound extends QuestHandler
{
	@Inject
	ItemService itemService;

	private final static int questId = 1158;

	public _1158VillageSealFound()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(798003).addOnQuestStart(questId);
		qe.setNpcQuestData(798003).addOnTalkEvent(questId);
		qe.setNpcQuestData(700003).addOnTalkEvent(questId);	
		qe.setNpcQuestData(203128).addOnTalkEvent(questId);		
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		
		if(qs == null || qs.getStatus() == QuestStatus.NONE)
		{
			if(targetId == 798003)		
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
						else
					return defaultQuestStartDialog(env);
			}
		}
		else if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 700003 && qs.getQuestVarById(0) == 0)
			{	
				final int targetObjectId = env.getVisibleObject().getObjectId();
				if(env.getDialogId() == 25)
				{
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0, targetObjectId), true);
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
				}
				else if(env.getDialogId() == 1353)
				{
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 38, 0, targetObjectId), true);				
					qs.setQuestVarById(0, 1);					
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(player, qs);
					itemService.addItems(player, Collections.singletonList(new QuestItems(182200502, 1)));
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 0);
				}	
			}
				return false;
		}				
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203128)
				return defaultQuestEndDialog(env);
		}
		return false;	
	}
}
