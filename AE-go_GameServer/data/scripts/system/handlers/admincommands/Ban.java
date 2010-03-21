/**
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
import com.aionemu.gameserver.controllers.AccountTimeController;
import com.aionemu.gameserver.controllers.BannedChatController;
import com.aionemu.gameserver.controllers.BannedIpController;
import com.aionemu.gameserver.model.account.AccountTime;
import com.aionemu.gameserver.model.ban.BannedChat;
import com.aionemu.gameserver.model.ban.BannedIP;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

import com.google.inject.Inject;

import java.sql.Timestamp;

/**
 * @author Divinity
 * 
 */
public class Ban extends AdminCommand
{
   @Inject
   private World   world;
   
    public Ban()
   {
        super("ban");
    }

    @Override
    public void executeCommand(Player admin, String[] params)
   {
      String syntaxCommand = "Syntax: //ban <account | chat | ip> <character name> <time in minutes | forever>";
        if (admin.getAccessLevel() < AdminConfig.COMMAND_BAN)
        {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
        }

      if (params.length < 3)
      {
         PacketSendUtility.sendMessage(admin, syntaxCommand);
            return;
      }
      
      Player    target         = world.findPlayer(Util.convertName(params[1]));
      long   timeToBan;
      
      if (target == null)
      {
         PacketSendUtility.sendMessage(admin, "WARNING: The specified player is not online.");
         return;
      }
      
      try
      {
         timeToBan = Long.parseLong(params[2]);
      }
      catch (NumberFormatException e)
      {
         if (params[2].equals("forever"))
            timeToBan = 5184000; // 10 years ;o
         else
         {
            PacketSendUtility.sendMessage(admin, syntaxCommand);
            return;
         }
      }            
      
      if (params[0].equals("account"))
      {
         final   AccountTime      newBanAccount   = new AccountTime();
               Timestamp      time         = new Timestamp(System.currentTimeMillis() + (timeToBan*60*1000));
         
         
         
         // Kicking Player
         target.getClientConnection().close(new SM_QUIT_RESPONSE(), true);
         
         // Update into account_time
         // Need to launch this function with a delay because when a player is kicked, this information in the database is updated
         ThreadPoolManager.getInstance().schedule(new Runnable()
         {
            @Override
            public void run()
            {
               AccountTimeController.update(newBanAccount);   
            }      
         }, 2000);
         
         // Done
         PacketSendUtility.sendMessage(admin, "INFO: The player (account) " + params[1] + " has been banned with successful.");
      }
      else if (params[0].equals("chat"))
      {
         BannedChat   newBanChat   = new BannedChat();
         Timestamp   time      = new Timestamp(System.currentTimeMillis() + (timeToBan*60*1000));
         
         // Setting Player Name
         newBanChat.setName(target.getName());
         
         // Setting Time to Ban
         newBanChat.setTimeEnd(time);
         
         // Insert or update to banned_chat
         BannedChatController.addOrUpdate(newBanChat);
         
         // Reload all banned player
         BannedChatController.reload();
         
         // Done
         PacketSendUtility.sendMessage(admin, "INFO: The player (Chat) " + params[1] + " has been banned with successful.");
         
         // Warning the player
         PacketSendUtility.sendMessage(target, "You are currently banned from the chat by the admin.");
      }
      else if (params[0].equals("ip"))
      {
         BannedIP   newBanIP   = new BannedIP();
         Timestamp   time      = new Timestamp(System.currentTimeMillis() + (timeToBan*60*1000));
         
         // Setting IP
         newBanIP.setMask(BannedIpController.getIP(target.getName()));
         
         // Setting Time to Ban
         newBanIP.setTimeEnd(time);
         
         // Insert or update to banned_ip
         BannedIpController.addOrUpdateBan(newBanIP);
         
         // Kicking Player
         target.getClientConnection().close(new SM_QUIT_RESPONSE(), true);
         
         // Done
         PacketSendUtility.sendMessage(admin, "INFO: The player (IP) " + params[1] + " has been banned with successful.");
      }
      else
      {
         PacketSendUtility.sendMessage(admin, syntaxCommand);
            return;
      }
    }
}