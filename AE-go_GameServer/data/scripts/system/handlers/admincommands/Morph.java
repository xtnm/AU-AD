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
package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;

/**
* @author Atomics
*
*/

public class Morph extends AdminCommand
{
   public Morph()
   {
      super("morph");
   }
   
   @Override
   public void executeCommand(Player admin, String[] params)
   {
      if(admin.getAccessLevel() < AdminConfig.COMMAND_MORPH)
      {
         PacketSendUtility.sendMessage(admin, "Commande restreinte");
         return;
      }
      
int param;
      if (params == null || params.length < 1)
      {
         PacketSendUtility.sendMessage(admin, "syntax //morph <ID du NPC>");
         return;
      }
      try{
    param = Integer.parseInt(params[0]);
      }      catch (NumberFormatException e)
      {
         PacketSendUtility.sendMessage(admin, "Parametre Incorrect.");
         return;
      }
      
Creature transfo = (Creature) admin.getTarget();
       transfo.setTransformedModelId(param);
         PacketSendUtility.broadcastPacket(transfo, new SM_TRANSFORM(transfo));
         if (admin.getTarget() instanceof Player){
            Player player = (Player) admin.getTarget();
            PacketSendUtility.broadcastPacket(player, new SM_TRANSFORM(player));
			PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player));
            PacketSendUtility.sendMessage(player, "Transformation Reussie...");
         }
         
         
   }

}