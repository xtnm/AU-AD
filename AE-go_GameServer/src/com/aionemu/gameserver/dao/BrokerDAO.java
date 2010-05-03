package com.aionemu.gameserver.dao;

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.BrokerItem;

public abstract class BrokerDAO implements IDFactoryAwareDAO
{
	public abstract List<BrokerItem> loadBroker();
	
	public abstract boolean storeBroker(List<BrokerItem> brokerItems);
	
	@Override
	public final String getClassName()
	{
		return BrokerDAO.class.getName();
	}
}
