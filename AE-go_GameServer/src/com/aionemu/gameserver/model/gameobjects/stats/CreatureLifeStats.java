/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.model.gameobjects.stats;

import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_MP;
import com.aionemu.gameserver.services.LifeStatsRestoreService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public abstract class CreatureLifeStats<T extends Creature>
{
	private static final Logger log = Logger.getLogger(CreatureLifeStats.class);

	protected int currentHp;
	protected int currentMp;

	protected boolean alreadyDead = false;

	protected Creature owner;

	private final ReentrantLock hpLock = new ReentrantLock();
	private final ReentrantLock mpLock = new ReentrantLock();
	
	private int HP_RESTORE_TICK = 5;
	private int MP_RESTORE_TICK = 5;
	
	private Future<?> lifeRestoreTask;

	public CreatureLifeStats(Creature owner, int currentHp, int currentMp)
	{
		super();
		setOwner(owner);
		this.currentHp = currentHp;
		this.currentMp = currentMp;
	}

	/**
	 * @param owner
	 */
	public void setOwner(Creature owner) throws IllegalArgumentException
	{
		this.owner = owner;
		if (owner==null) {
			return;
		}
		if (owner.getGameStats()==null) {
			throw new IllegalArgumentException("cannot set owner because he have no gamestats");
		}
	}

	/**
	 * @return the owner
	 */
	public Creature getOwner()
	{
		return owner;
	}

	/**
	 * @return the currentHp
	 */
	public int getCurrentHp()
	{
		return currentHp;
	}

	/**
	 * @return the currentMp
	 */
	public int getCurrentMp()
	{
		return currentMp;
	}

	/**
	 * @return maxHp of creature according to stats
	 */
	public int getMaxHp () 
	{
		return this.getOwner().getGameStats().getCurrentStat(StatEnum.MAXHP);
	}
	
	/**
	 * @return maxMp of creature according to stats
	 */
	public int getMaxMp () 
	{
		return this.getOwner().getGameStats().getCurrentStat(StatEnum.MAXMP);
	}
	
	/**
	 * @return the alreadyDead
	 * There is no setter method cause life stats should be completely renewed on revive
	 */
	public boolean isAlreadyDead()
	{
		return alreadyDead;
	}

	/**
	 *  This method is called whenever caller wants to absorb creatures's HP
	 * @param value
	 * @return
	 */
	public int reduceHp(int value)
	{
		hpLock.lock();
		try
		{
			int newHp = this.currentHp - value;

			if(newHp < 0)
			{
				this.currentHp = 0;
				if(!alreadyDead)
				{
					alreadyDead = true;
				}			
			}
			this.currentHp = newHp;

			sendHpPacketUpdate();

			if(alreadyDead)
			{
				getOwner().getController().onDie();	
			}
		}
		finally
		{
			hpLock.unlock();
		}

		onReduceHp();

		return currentHp;
	}

	/**
	 *  This method is called whenever caller wants to absorb creatures's HP
	 * @param value
	 * @return
	 */
	public int reduceMp(int value)
	{
		mpLock.lock();
		try
		{
			int newMp = this.currentMp - value;

			if(newMp < 0)
				newMp = 0;

			this.currentMp = newMp;	
		}
		finally
		{
			mpLock.unlock();
		}
		
		onReduceMp();

		return currentMp;
	}
	

	/**
	 * Informs about HP change
	 */
	private void sendHpPacketUpdate()
	{
		if(owner == null)
		{
			return;
		}
		int hpPercentage = Math.round(100 *  currentHp / getMaxHp());
		PacketSendUtility.broadcastPacket(owner, new SM_ATTACK_STATUS(getOwner().getObjectId(), hpPercentage));
		if(owner instanceof Player)
		{
			PacketSendUtility.sendPacket((Player) owner, new SM_ATTACK_STATUS(getOwner().getObjectId(), hpPercentage));
		}
	}

	/**
	 *  This method is called whenever caller wants to restore creatures's HP
	 * @param value
	 * @return
	 */
	public int increaseHp(int value)
	{
		if(value == getMaxHp())
			return 0;
		
		hpLock.lock();
		try
		{
			if(isAlreadyDead())
			{
				return 0;
			}
			int newHp = this.currentHp + value;
			if(newHp > getMaxHp())
			{
				newHp = getMaxHp();
			}
			this.currentHp = newHp;		
		}
		finally
		{
			hpLock.unlock();
		}		

		sendHpPacketUpdate();

		return currentHp;
	}

	/**
	 * This method is called whenever caller wants to restore creatures's MP
	 * @param value
	 * @return
	 */
	public int increaseMp(int value)
	{
		if(value == getMaxMp())
			return 0;
		
		mpLock.lock();
		
		try
		{
			if(isAlreadyDead())
			{
				return 0;
			}
			int newMp = this.currentMp + value;
			if(newMp > getMaxMp())
			{
				newMp = getMaxMp();
			}
			this.currentMp = newMp;
		}	
		finally
		{
			mpLock.unlock();
		}

		onIncreaseMp();

		return currentHp;
	}
	
	/**
	 *  Restores HP with value set as HP_RESTORE_TICK
	 */
	public void restoreHp()
	{
		increaseHp(HP_RESTORE_TICK);
	}
	
	/**
	 *  Restores HP with value set as MP_RESTORE_TICK
	 */
	public void restoreMp()
	{
		increaseMp(MP_RESTORE_TICK);
	}

	/**
	 *  Will trigger restore task if not already
	 */
	public void triggerRestoreTask()
	{
		if(lifeRestoreTask == null && !alreadyDead)
		{
			this.lifeRestoreTask = LifeStatsRestoreService.getInstance().scheduleRestoreTask(this);
		}
	}

	/**
	 *  Cancel currently running restore task
	 */
	public void cancelRestoreTask()
	{
		if(lifeRestoreTask != null && !lifeRestoreTask.isCancelled())
		{
			lifeRestoreTask.cancel(false);
			this.lifeRestoreTask = null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isFullyRestored()
	{
		return getMaxHp() == currentHp && getMaxMp() == currentMp;
	}
	
	/**
	 * The purpose of this method is synchronize current HP and MP with updated MAXHP and MAXMP stats 
	 * This method should be called only on creature load to game
	 */
	public void synchronizeWithMaxStats()
	{
		if(getMaxHp() != currentHp)
			currentHp = getMaxHp();
		if(getMaxMp() != currentMp)
			currentMp = getMaxMp();
	}
	
	/**
	 * The purpose of this method is synchronize current HP and MP with MAXHP and MAXMP when max
	 * stats were decreased below current level
	 */
	public void updateCurrentStats()
	{
		if(getMaxHp() < currentHp)
			currentHp = getMaxHp();
		if(getMaxHp() < currentMp)
			currentMp = getMaxMp();
		
		if(!isFullyRestored())
			triggerRestoreTask();
	}
	
	protected abstract void onIncreaseMp();
	
	protected abstract void onReduceMp();
	
	protected abstract void onReduceHp();
}
