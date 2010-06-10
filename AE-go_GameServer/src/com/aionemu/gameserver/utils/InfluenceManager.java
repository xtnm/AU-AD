package com.aionemu.gameserver.utils;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.FortressDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INFLUENCE_RATIO;
import com.aionemu.gameserver.services.FortressService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import com.sun.tools.javac.util.Log;

public class InfluenceManager 
{
	
	private static final Logger log = Logger.getLogger(InfluenceManager.class);
	
	private static int elyosRatio;
	private static int asmodiansRatio;
	private static int balaursRatio;
	
	private static int fortressCount = 1;
	
	@Inject
	private static World world;
	
	public static void initialize()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				recalculateInfluenceRatio();
			}
		}, 0, 300000);
	}
	
	public static void recalculateInfluenceRatio()
	{
		log.info("Starting influence ratios recalculation");
		ArrayList<Race> currentFortressHolders = new ArrayList<Race>();
		for(int i=0; i < fortressCount; i++)
		{
			currentFortressHolders.add(DAOManager.getDAO(FortressDAO.class).getCurrentFortressOwnerFaction(i+1));
		}
		int elyosCount = 0;
		int asmodiansCount = 0;
		int balaursCount = 0;
		for(Race r : currentFortressHolders)
		{
			if(r == Race.ELYOS)
			{
				elyosCount++;
			}
			else if(r == Race.ASMODIANS)
			{
				asmodiansCount++;
			}
			else
			{
				balaursCount++;
			}
		}
		elyosRatio = Math.round((elyosCount / fortressCount) * 100);
		asmodiansRatio = Math.round((asmodiansCount / fortressCount) * 100);
		balaursRatio = Math.round((balaursCount / fortressCount) * 100);
		log.info("Influence ratios were recalculated :: ELYOS " + elyosRatio + "% - ASMODIANS " + asmodiansRatio + "% - BALAURS " + balaursRatio + "%");
		if(world instanceof World)
		{
			Iterator<Player> players = world.getPlayersIterator();
			while(players.hasNext())
			{
				Player p = players.next();
				PacketSendUtility.sendPacket(p, new SM_INFLUENCE_RATIO());
			}
		}
	}
	
	public static int getElyosRatio()
	{
		return elyosRatio;
	}
	
	public static int getAsmodiansRatio()
	{
		return asmodiansRatio;
	}
	
	public static int getBalaursRatio()
	{
		return balaursRatio;
	}
	
}
