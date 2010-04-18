package quest.pandaemonium;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

public class _2900NoEscapingDestiny extends QuestHandler {
	
	private final static int questId = 2900;
	
	public _2900NoEscapingDestiny()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.setNpcQuestData(204182).addOnTalkEvent(questId);
		qe.setQuestEnterZone(ZoneName.ALTGARD_FORTRESS_220030000).add(questId);
	}
	
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zone)
	{
		if(zone != ZoneName.ALTGARD_FORTRESS_220030000)
			return false;
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs != null || player.getLevel() < 20)
			return false;
		env.setQuestId(questId);
		questService.startQuest(env, QuestStatus.START);
		return true;
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 204182)
		{
			if(qs != null)
			{
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.COMPLITE);
				updateQuestStatus(player, qs);
				return defaultQuestEndDialog(env);
			}
		}
		return false;
	}

}
