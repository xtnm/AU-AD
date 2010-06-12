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
package quest.pandaemonium;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Nanou
 * 
 */
public class _4940DecorationsOfPandaemonium extends QuestHandler
{
	private final static int	questId	= 4940;

	public _4940DecorationsOfPandaemonium()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(204050).addOnQuestStart(questId);	//Tragi
		qe.setNpcQuestData(204050).addOnTalkEvent(questId);		//Tragi
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		// Instanceof
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		// ------------------------------------------------------------
		// NPC Quest :
		// Start to Tragi
		if(qs == null || qs.getStatus() == QuestStatus.NONE) 
		{
			if(targetId == 204050)
			{
				// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
				if(env.getDialogId() == 25)
					// Send select_none to eddit-HtmlPages.xml
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 4762);
				else
					return defaultQuestStartDialog(env);

			}
		}
		
		if(qs == null)
			return false;
		
		if(qs.getStatus() == QuestStatus.START)
		{
			
			switch(targetId)
			{
				// 1 - Collect the Deathmark in Brusthonin for Tragi
				case 204050 :
					switch(env.getDialogId())
					{
						// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
						case 25 :
							// Send select1 to eddit-HtmlPages.xml
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
						// Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
						case 10000:
							// Send select2 to eddit-HtmlPages.xml
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
						// Get HACTION_CHECK_USER_HAS_QUEST_ITEM in the eddit-HyperLinks.xml
						case 33 :
							// Collect Crown Ksellid's Sting (10)
							// Collect Rocky Back Ksellid's Shell (10)
							// Collect Refugee's Old Clothes (10)
							// Collect Lobnite's Stalk (10)
							if(	player.getInventory().getItemCountByItemId(182207117) >= 10 && 
								player.getInventory().getItemCountByItemId(182207118) >= 10 &&
								player.getInventory().getItemCountByItemId(182207119) >= 10 &&
								player.getInventory().getItemCountByItemId(182207120) >= 10 )
							{
								player.getInventory().removeFromBagByItemId(182207117, 10);
								player.getInventory().removeFromBagByItemId(182207118, 10);
								player.getInventory().removeFromBagByItemId(182207119, 10);
								player.getInventory().removeFromBagByItemId(182207120, 10);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(player, qs);
								// Send select_quest_reward1 to eddit-HtmlPages.xml
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
							}
							else
							{
								// Send check_user_item_fail to eddit-HtmlPages.xml
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 10001);	
							}
					}
					break;
				// No match 
				default : 
					return defaultQuestStartDialog(env);
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203710)
				return defaultQuestEndDialog(env);
		}
	return false;
	}
}