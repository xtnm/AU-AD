package quest.pandaemonium;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

public class _2900NoEscapingDestiny extends QuestHandler {
	
	private final static int questId = 2900;
	
	public _2900NoEscapingDestiny()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.setNpcQuestData(204182).addOnQuestStart(questId);
		qe.setNpcQuestData(204182).addOnTalkEvent(questId);
		qe.setNpcQuestData(203550).addOnTalkEvent(questId);
		qe.setNpcQuestData(790003).addOnTalkEvent(questId);
		qe.setNpcQuestData(790002).addOnTalkEvent(questId);
		qe.setNpcQuestData(204264).addOnTalkEvent(questId);
		qe.setNpcQuestData(203550).addOnTalkEvent(questId);
		qe.setNpcQuestData(204263).addOnKillEvent(questId);
		qe.setNpcQuestData(204061).addOnTalkEvent(questId);
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
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				return defaultQuestStartDialog(env);
			}
		}
		else if(targetId == 204061)
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
