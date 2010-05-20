package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Nemiroff
 *         Date: 28.12.2009
 */
public class Gps extends AdminCommand
{

    public Gps()
	{
        super("gps");
    }

    @Override
    public void executeCommand(Player admin, String[] params)
	{
        PacketSendUtility.sendMessage(admin, "== Your current coordinates ==");
        PacketSendUtility.sendMessage(admin, "Map = " + admin.getWorldId());
        PacketSendUtility.sendMessage(admin, "X = " + admin.getX());
        PacketSendUtility.sendMessage(admin, "Y = " + admin.getY());
        PacketSendUtility.sendMessage(admin, "Z = " + admin.getZ());
        PacketSendUtility.sendMessage(admin, "H = " + admin.getHeading());
    }
}
