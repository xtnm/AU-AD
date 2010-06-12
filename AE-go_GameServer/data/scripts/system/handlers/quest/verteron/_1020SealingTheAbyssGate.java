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
package quest.verteron;

import java.util.Collections;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ZoneService;
import com.aionemu.gameserver.services.InstanceService;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.google.inject.Inject;

/**
 * @author Angelis
 * 
 */
public class _1020SealingTheAbyssGate extends QuestHandler
{
	@Inject
	ItemService itemService;
	@Inject
	ZoneService zoneService;
	@Inject
	TeleportService teleportService;
	@Inject
	InstanceService instanceService;
	
	private final static int	questId	= 1020;
	
	public _1020SealingTheAbyssGate()
	{
		super( questId );
	}

	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.addOnEnterWorld(questId);
		qe.setNpcQuestData(203098).addOnTalkEvent(questId);
		qe.setNpcQuestData(700141).addOnTalkEvent(questId);
		qe.setNpcQuestData(210753).addOnKillEvent(questId);		
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVarById( 0 );
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if( qs.getStatus() == QuestStatus.REWARD )
		{
			if( targetId == 203098 )
					return defaultQuestEndDialog( env );
		}
		else if( qs.getStatus() != QuestStatus.START )
			return false;
		
		switch( targetId )
		{
			case 203098:
				switch( env.getDialogId() )
				{
					case 25:
						if(var == 0)
							return sendQuestDialog( player, env.getVisibleObject().getObjectId(), 1011 );

					case 10000:
						if(var == 0)
						{
							qs.setQuestVarById( 0, var + 1 );
							updateQuestStatus( player, qs );
							return true;
						}

					default:
						return false;
				}

			case 700141:
				if( var == 1 )
				{
					WorldMapInstance newInstance = instanceService.getNextAvailableInstance(310010000);
					teleportService.teleportTo(player, 310030000, newInstance.getInstanceId(), 52, 174, 229, 0);
					qs.setQuestVarById( 0, var + 1 );
					updateQuestStatus( player, qs );
					return true;
				}

			default:
				return false;
		}
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState( questId );
		if( qs == null || qs.getStatus() != QuestStatus.START )
			return false;

		int var = qs.getQuestVarById( 0 );
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch(targetId)
		{
			case 210753:
				if( var >= 2 && var < 3 )
				{
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(player, qs);
					return true;
				}
			break;
		}
		return false;
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED)
			return false;
		int[] quests = {1130, 1011, 1012, 1013, 1014, 1015, 1016, 1017, 1018, 1019, 1021, 1022, 1023};
		for (int id : quests)
		{
			QuestState qs2 = player.getQuestStateList().getQuestState(id);
			if (qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE)
				return false;
		}

		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}
	
	@Override
	public boolean onEnterWorldEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED)
			return false;
		int[] quests = {1130, 1011, 1012, 1013, 1014, 1015, 1016, 1017, 1018, 1019, 1021, 1022, 1023};
		for (int id : quests)
		{
			QuestState qs2 = player.getQuestStateList().getQuestState(id);
			if (qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE)
				return false;
		}

		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}
}