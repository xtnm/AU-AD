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
package quest;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * 
 * 
 */
public class _90000TeleportObjects extends QuestHandler
{
	
	private final static int	questId	= 90000;

	public _90000TeleportObjects()
	{
		super(questId);
		}

	@Override
	public void register()
	{
		qe.setNpcQuestData(700088).addOnTalkEvent(questId);
		qe.setNpcQuestData(700177).addOnTalkEvent(questId);
		qe.setNpcQuestData(700360).addOnTalkEvent(questId);
		qe.setNpcQuestData(700177).addOnTalkEvent(questId);
		qe.setNpcQuestData(730075).addOnTalkEvent(questId);
		qe.setNpcQuestData(730074).addOnTalkEvent(questId);
		qe.setNpcQuestData(730073).addOnTalkEvent(questId);
		qe.setNpcQuestData(700089).addOnTalkEvent(questId);
		qe.setNpcQuestData(730064).addOnTalkEvent(questId);
		qe.setNpcQuestData(700183).addOnTalkEvent(questId);
		qe.setNpcQuestData(730077).addOnTalkEvent(questId);
		qe.setNpcQuestData(730078).addOnTalkEvent(questId);
		qe.setNpcQuestData(730076).addOnTalkEvent(questId);
		qe.setNpcQuestData(730047).addOnTalkEvent(questId);
		qe.setNpcQuestData(730051).addOnTalkEvent(questId);
		qe.setNpcQuestData(730045).addOnTalkEvent(questId);
		qe.setNpcQuestData(700152).addOnTalkEvent(questId);
		qe.setNpcQuestData(730041).addOnTalkEvent(questId);
		qe.setNpcQuestData(700150).addOnTalkEvent(questId);
		qe.setNpcQuestData(730043).addOnTalkEvent(questId);
		qe.setNpcQuestData(700153).addOnTalkEvent(questId);
		qe.setNpcQuestData(203981).addOnTalkEvent(questId);
  	qe.setNpcQuestData(203982).addOnTalkEvent(questId);	
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		
			switch(targetId){
				//To Abyss (Elyos)
				case 700088:{// Verteron -> Abyss (Elyos)
					if(player.getLevel() < 25){
						PacketSendUtility.sendMessage(player, "You must be at least level 25 to enter the Abyss.");
					}
					else if(player.getCommonData().getRace() == Race.ELYOS)
					teleport(player, 400010000, 2867, 1034, 1528);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 700177:{//Eltnen -> Abyss(Elyos)
					if(player.getLevel() < 25){
						PacketSendUtility.sendMessage(player, "You must be at least level 25 to enter the Abyss.");
					}
					else if(player.getCommonData().getRace() == Race.ELYOS)
					teleport(player, 400010000, 2867, 1034, 1528);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 700360:{//Heiron -> Abyss(Elyos)
					if(player.getLevel() < 25){
						PacketSendUtility.sendMessage(player, "You must be at least level 25 to enter the Abyss.");
					}
					else if(player.getCommonData().getRace() == Race.ELYOS)
					teleport(player, 400010000, 2867, 1034, 1528);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				
				//From Abyss (Elyos)
				case 730075:{//Abyss -> Heiron
					if(player.getCommonData().getRace() == Race.ELYOS)
					teleport(player, 210040000, 2540, 343, 411);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 730074:{//Abyss -> Eltnen
					if(player.getCommonData().getRace() == Race.ELYOS)
					teleport(player, 210020000, 343, 2724, 264);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 730073:{//Abyss -> Verteron
					if(player.getCommonData().getRace() == Race.ELYOS)
					teleport(player, 210030000, 1643, 1500, 119);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				

				//To Abyss (Asmo)
				case 700089:{//Altgard -> Abyss
					if(player.getLevel() < 25){
						PacketSendUtility.sendMessage(player, "You must be at least level 25 to enter the Abyss.");
					}
					else if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 400010000, 1078, 2839, 1636);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 730064:{//Beluslan -> Abyss
					if(player.getLevel() < 25){
						PacketSendUtility.sendMessage(player, "You must be at least level 25 to enter the Abyss.");
					}
					else if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 400010000, 1078, 2839, 1636);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 700183:{//Morheim -> Abyss
					if(player.getLevel() < 25){
						PacketSendUtility.sendMessage(player, "You must be at least level 25 to enter the Abyss.");
					}
					else if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 400010000, 1078, 2839, 1636);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				
				//From Abyss (Asmo)
				case 730077:{//Abyss -> Morheim
					if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 220020000, 308, 2274, 449);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 730078:{//Abyss -> Beluslan
					if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 220040000, 325, 336, 229);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 730076:{//Abyss -> Altgard
					if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 220030000, 1748, 1807, 254);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				
				//Instance Fire Temple(Asmo & Elyos)
				case 730047:{//Morheim & Eltnen -> Fire Temple
					teleport(player, 320100000, 149, 466, 142);
					player.setTarget(null);
					break;
				}
				case 730051:{//Fire Temple Talking Mirror-> Morheim
					if(player.getCommonData().getRace() == Race.ASMODIANS){
					teleport(player, 220020000, 1594, 976, 141);
					}else if(player.getCommonData().getRace() == Race.ELYOS){
					teleport(player, 210020000, 1346, 351, 349);
					}else{
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					}
					break;
				}
				
				//Instance Sky Temple Interior(Asmo only)
				case 730045:{//Morheim -> Fire Room
					if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 320050000, 102, 24, 612);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 700152:{//Fire Room -> Morheim
					if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 220020000, 1682, 1150, 201);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 730041:{//Morheim -> Water Room
					if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 320050000, 475, 116, 215);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 700150:{//Water Room -> Morheim
					if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 220020000, 1682, 1150, 201);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 730043:{//Morheim -> Wind Room
					if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 320050000, 306, 435, 417);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 700153:{//Wind Room -> Morheim
					if(player.getCommonData().getRace() == Race.ASMODIANS)
					teleport(player, 220020000, 1682, 1150, 201);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				//Eltnen Eracus Temple(Elyos)
				case 203981:{// Entrance Meneus -> Ipetos
					if(player.getCommonData().getRace() == Race.ELYOS)
					teleport(player, 210020000, 440, 422, 275);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				case 203982:{//Exit Ipetos -> Meneus
					if(player.getCommonData().getRace() == Race.ELYOS)
					teleport(player, 210020000, 449, 430, 275);
					else
						PacketSendUtility.sendMessage(player, "You cannot use this portal.");
					
					player.setTarget(null);
					break;
				}
				default:{
					PacketSendUtility.sendMessage(player, "This Object has no handler. Tell an admin. TargetID: "+ targetId);
					
					player.setTarget(null);
					break;
				}
			}
		
		
			return false;
	}
	
	public void teleport(Player player, final int map, final float x, final float y, final float z){
		final World world = player.getActiveRegion().getWorld();
		RequestResponseHandler responseHandler = new RequestResponseHandler(player) 
		{				
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				if(responder.isSpawned())
					world.despawn(responder);
				
				world.setPosition(responder, map, x, y, z, (byte) 20);
				responder.getController().startProtectionActiveTask();
				PacketSendUtility.sendPacket(responder, new SM_PLAYER_SPAWN(responder));	
			}


			@Override
			public void denyRequest(Creature requester, Player responder)
			{				
			}
		};

		boolean result = player.getResponseRequester().putRequest(160019,responseHandler);
		if(result)
		{
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(160019, 0));
		}
	}
	
}
