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
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.services.ZoneService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.google.inject.Inject;

/**
 * @author Rhys2002
 * 
 */
public class _1019FlyingReconnaissance extends QuestHandler
{
	@Inject
	ItemService itemService;
	@Inject
	ZoneService zoneService;
	
	private final static int	questId	= 1019;
	private final static int[]	npc_ids	= { 203146, 203098, 203147, 700037 };
	private final static int[]  movie_ids= { 13, 22, 18, 174 };
	
	public _1019FlyingReconnaissance()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.setNpcQuestData(210697).addOnKillEvent(questId);
		qe.setNpcQuestData(210158).addOnAttackEvent(questId);		
		qe.setQuestItemIds(182200505).add(questId);
		qe.setQuestItemIds(182200023).add(questId);		
		for(int movie_id : movie_ids)
		qe.setQuestMovieEndIds(movie_id).add(questId);	
		for(int npc_id : npc_ids)
			qe.setNpcQuestData(npc_id).addOnTalkEvent(questId);	
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED || player.getCommonData().getLevel() < 14)
			return false;

		QuestState qs2 = player.getQuestStateList().getQuestState(1130);
		if(qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}

	@Override
	public boolean onAttackEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if(qs.getQuestVarById(0) != 4 && qs.getQuestVarById(0) != 11)
			return false;
		
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if(targetId != 210158)
			return false;
			
		final Npc npc = (Npc) env.getVisibleObject();

		if (MathUtil.getDistance(1548, 1166, 114,npc.getX(), npc.getY(), npc.getZ()) > 10)
			return false;
			
		if(npc.getLifeStats().getCurrentHp() < npc.getLifeStats().getMaxHp() / 4)
		{
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 22));
			npc.getController().onDespawn(true);
			npc.getController().scheduleRespawn();
		}
		else
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 13));
			npc.getController().onDespawn(true);
			npc.getController().scheduleRespawn();			
		return true;
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(targetId == 210697)
		{
			if(qs.getQuestVarById(0) == 10)
			{
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(player, qs);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203098)
					return defaultQuestEndDialog(env);
		}
		else if(qs.getStatus() != QuestStatus.START)
		{
			return false;
		}
		if(targetId == 203146)
		{
			switch(env.getDialogId())
			{
				case 25:
					if(var == 0)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
					return false;
				
				case 10000:
					if(var == 0)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						if(!itemService.addItems(player, Collections.singletonList(new QuestItems(182200505, 1))));						
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		else if(targetId == 203098)
		{
			switch(env.getDialogId())
			{
				case 25:
					if(var == 2)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
					return false;

				case 10001:
					if(var == 2)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		else if(targetId == 203147)
		{
			switch(env.getDialogId())
			{
				case 25:
					if(var == 3)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1438);
					else if(var == 5)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
					return false;

				case 10002:
					if(var == 3)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					
				case 10003:
					if(var == 5)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}				
					return false;
			}
		}
		else if(targetId == 700037)
		{
			if (qs.getQuestVarById(0) >= 5 && qs.getQuestVarById(0) <= 8 && env.getDialogId() == -1)
			if (env.getDialogId() == -1)
			{
				final int targetObjectId = env.getVisibleObject().getObjectId();
				final Npc npc = (Npc) env.getVisibleObject();

				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0, targetObjectId), true);
				ThreadPoolManager.getInstance().schedule(new Runnable(){
					@Override
					public void run()
					{
						if(player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
							return;
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 38, 0, targetObjectId), true);
						npc.getController().onDespawn(true);
						npc.getController().scheduleRespawn();	
						qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
						updateQuestStatus(player, qs);						
					}
				}, 3000);
			}
		}		
		return false;
	}
	
	@Override
	public boolean onItemUseEvent(QuestEnv env, Item item)
	{
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();		
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if(qs.getQuestVarById(0) == 1 && zoneService.isInsideZone(player, ZoneName.TURSIN_OUTPOST) && id == 182200505)
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 18));
		else if (qs.getQuestVarById(0) == 9 && zoneService.isInsideZone(player, ZoneName.TURSIN_TOTEM_POLE) && id == 182200023)
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 174));				
		return true;
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if(movieId == 18)
		{
			qs.setQuestVarById(0, 2);
			updateQuestStatus(player, qs);
			player.getInventory().removeFromBagByItemId(182200505, 1);
			return true;
		}
		else if(movieId == 174)
		{
			qs.setQuestVarById(0, 10);
			updateQuestStatus(player, qs);
			player.getInventory().removeFromBagByItemId(182200023, 1);
			return true;
		}
		else if(movieId == 13)
		{
			qs.setQuestVarById(0, 11);
			updateQuestStatus(player, qs);
			return true;
		}
		else if(movieId == 22)
		{
			qs.setQuestVarById(0, 5);
			updateQuestStatus(player, qs);
			return true;
		}		
		return false;
	}	
}
