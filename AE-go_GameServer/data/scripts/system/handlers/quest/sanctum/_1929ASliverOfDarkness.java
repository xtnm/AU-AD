package quest.sanctum;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

public class _1929ASliverOfDarkness extends QuestHandler {
	
	private final static int questId = 1929;
	
	public _1929ASliverOfDarkness()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.setNpcQuestData(203752).addOnTalkEvent(questId);
		qe.setQuestEnterZone(ZoneName.VERTERON_CITADEL).add(questId);
	}
	
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zone)
	{
		if(zone != ZoneName.VERTERON_CITADEL)
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
		if(targetId == 203752)
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
