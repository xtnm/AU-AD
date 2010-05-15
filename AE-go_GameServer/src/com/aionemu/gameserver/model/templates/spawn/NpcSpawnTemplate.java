package com.aionemu.gameserver.model.templates.spawn;

/*
 * @author xitanium
 */

public class NpcSpawnTemplate 
{
	
	private int spawnTemplateId;
	private int npcTemplateId;
	private int map;
	private float x;
	private float y;
	private float z;
	private byte heading;
	
	public NpcSpawnTemplate(int spawnTemplateId, int npcTemplateId, int map, float x, float y, float z, byte heading)
	{
		this.spawnTemplateId = spawnTemplateId;
		this.npcTemplateId = npcTemplateId;
		this.map = map;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
	}
	
	public int getSpawnTemplateId()
	{
		return spawnTemplateId;
	}
	
	public int getNpcTemplateId()
	{
		return npcTemplateId;
	}
	
	public int getMap()
	{
		return map;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getZ()
	{
		return z;
	}
	
	public byte getHeading()
	{
		return heading;
	}
	
}
