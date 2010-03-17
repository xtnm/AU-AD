/**
* This file is part of aion-emu <aion-emu.com>.
*
*  aion-emu is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  aion-emu is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
*/

package admincommands;

import java.util.Iterator;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;


/**
* @author Ark
*/

public class MoveToMeAll extends AdminCommand
{
    @Inject
    private TeleportService teleportService;
    @Inject
    private World   world;

   /**
    * Constructor.
    */
    public MoveToMeAll()
    {
        super("bind");
    }

    public void executeCommand(Player admin, String[] params)
    {
        if (params == null || params.length < 1)
        {
            PacketSendUtility.sendMessage(admin, "syntax //bind < all | elyos | asmos >");
            return;
      }

        if (params[0].equals("all"))
        {
            if(admin.getAccessLevel() < AdminConfig.COMMAND_MOVETOMEALLALL)
            {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
            }

            World world = admin.getActiveRegion().getWorld();
            Iterator<Player> players = world.getPlayersIterator();
            while(players.hasNext())
            {
                Player p = players.next();
                if(!p.equals(admin))
                {
               teleportService.teleportTo(p, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading(),5);
                PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));

                PacketSendUtility.sendMessage(admin, "Player " + p.getName() + " teleported.");
                PacketSendUtility.sendMessage(p, "Vous avez ete teleporte par " + admin.getName() + ".");
                }
            }
        }

        if (params[0].equals("elyos"))
        {
            if(admin.getAccessLevel() < AdminConfig.COMMAND_MOVETOMEALLELYOS)
            {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
            }
         
            World world = admin.getActiveRegion().getWorld();
            Iterator<Player> players = world.getPlayersIterator();
            while(players.hasNext())
            {
                Player p = players.next();
                if(!p.equals(admin))
                {
                    if(p.getCommonData().getRace() == Race.ELYOS)
                    {
                        teleportService.teleportTo(p, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading(),5);
                        PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));
              
                        PacketSendUtility.sendMessage(admin, "Player " + p.getName() + " teleported.");
                        PacketSendUtility.sendMessage(p, "Vous avez ete teleporte par " + admin.getName() + ".");
                    }
                }
            }
        }

        if (params[0].equals("asmos"))
        {
            if(admin.getAccessLevel() < AdminConfig.COMMAND_MOVETOMEALLASMOS)
            {
                PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
                return;
            }
         
            World world = admin.getActiveRegion().getWorld();
            Iterator<Player> players = world.getPlayersIterator();
            while(players.hasNext())
            {
                Player p = players.next();
                if(!p.equals(admin))
                {
                    if(p.getCommonData().getRace() == Race.ASMODIANS)
                    {
                        teleportService.teleportTo(p, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading(),5);
                        PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));
              
                        PacketSendUtility.sendMessage(admin, "Player " + p.getName() + " teleported.");
                        PacketSendUtility.sendMessage(p, "Vous avez ete teleporte par " + admin.getName() + ".");
                    }
                }
            }
        }
    }
}