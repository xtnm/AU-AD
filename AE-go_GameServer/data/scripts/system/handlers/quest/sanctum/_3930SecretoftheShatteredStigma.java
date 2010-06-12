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
package quest.sanctum;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author Xitanium
 * 
 */
public class _3930SecretoftheShatteredStigma extends QuestHandler
{
	private final static int	questId	= 3930;
	
	@Inject
	private ItemService itemService;

	public _3930SecretoftheShatteredStigma()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203711).addOnQuestStart(questId); //miriya start
		qe.setNpcQuestData(203833).addOnTalkEvent(questId);	//Xenophon
		qe.setNpcQuestData(798321).addOnTalkEvent(questId);		//Koruchinerk
		qe.setQuestItemIds(182206075).add(questId);				//Drana crystal
		qe.setNpcQuestData(203711).addOnTalkEvent(questId);		// Miriya
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
		// 0 - Start to Xenophon
		if(qs == null || qs.getStatus() == QuestStatus.NONE) 
		{
			if(targetId == 203711)
			{
				// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
				if(env.getDialogId() == 25)
					// Send HTML_PAGE_SELECT_NONE to eddit-HtmlPages.xml
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 4762);
				else
					qs.setStatus(QuestStatus.START);
					updateQuestStatus(player, qs);
					return defaultQuestStartDialog(env);

			}
		}
		
		if(qs == null)
			return false;
		
		int var = qs.getQuestVarById(0);			

		if(qs.getStatus() == QuestStatus.START)
		{
			
			switch(targetId)
			{
				case 203833:
					if(var == 0)
					{
						switch(env.getDialogId())
						{
							// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
							case 25:
								// Send select1 to eddit-HtmlPages.xml
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
							// Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
							case 10000:
								qs.setQuestVarById(0, var++);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					}
				// 2 / 4- Talk ith Koruchinerk
				case 798321:
					if(var == 1)
					{
						switch(env.getDialogId())
						{
							// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
							case 25:
								// Send select1 to eddit-HtmlPages.xml
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
							// Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
							case 10000:
								player.getInventory().putToBag(itemService.newItem(182206076, 1));
								qs.setQuestVarById(0, var++);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					}
					else if(var == 2)
					{
						switch(env.getDialogId())
						{
							// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
							case 25:
								// Send select1 to eddit-HtmlPages.xml
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
							// Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
							case 10000:
								if(player.getInventory().getItemCountByItemId(182206075) < 1)
								{
									// player doesn't own required item
									return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 10001);
								}
								player.getInventory().removeFromBagByItemId(182206075, 1);	
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(player, qs);	
								// Send select_quest_reward1 to eddit-HtmlPages.xml									
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
						}
					}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203711)
				return defaultQuestEndDialog(env);
		}
	return false;
	}
}