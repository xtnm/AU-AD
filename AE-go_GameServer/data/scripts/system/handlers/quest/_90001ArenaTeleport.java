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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * 
 * 
 */
public class _90001ArenaTeleport extends QuestHandler
{

	private final static int	questId	= 90001;

	public _90001ArenaTeleport()
	{
		super(questId); 
	}

	@Override
	public void register()
	{	
		//Pandemonium
		qe.setNpcQuestData(204089).addOnTalkEvent(questId);
		qe.setNpcQuestData(204087).addOnTalkEvent(questId);
		
		//Sanctum
		qe.setNpcQuestData(203875).addOnTalkEvent(questId);
		qe.setNpcQuestData(203764).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		
			switch(targetId){
				
				//Pandemonium
					case 204089://Outside Arena -> Inside Pandemonium
						if(env.getDialogId()==30){
							teleport(player, 120010000,(float) 984.0828,(float) 1543.0769,(float) 222.13889);
							return true;
						}
						break;
						
					case 204087://Inside Arena -> Outside Pandemonium
						if(env.getDialogId()==31){
							teleport(player, 120010000,(float) 1005.12646,(float) 1528.9696,(float) 222.19403);
							return true;
						}
						break;
					
					
				//Sanctum
					case 203875://Inside -> Outside
						if(env.getDialogId()==31){
							teleport(player, 110010000,(float) 1462.9454,(float) 1339.3353,(float) 566.41583);
							return true;
						}
						break;
					case 203764://Outside -> Inside
						if(env.getDialogId()==30){
							teleport(player, 110010000,(float) 1462.7565,(float) 1324.7617,(float) 564.1405);
							return true;
						}
						break;

					
				default:
						PacketSendUtility.sendMessage(player, "ERROR: Please tell administrator, 90001 quest problem.");
					break;
			}
		
		
			return false;
	}
	
	public void teleport(Player player, final int map, final float x, final float y, final float z){
		World world = player.getActiveRegion().getWorld();
		world.despawn(player);
		world.setPosition(player, map, x, y, z, player.getHeading());
		player.getController().startProtectionActiveTask();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
	}
	
}




