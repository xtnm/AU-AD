/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class CustomConfig
{
	/**
	 * Factions speaking mode
	 */
	@Property(key = "gameserver.factions.speaking.mode", defaultValue = "0")
	public static int		FACTIONS_SPEAKING_MODE;

	/**
	 * Skill autolearn
	 */
	@Property(key = "gameserver.skill.autolearn", defaultValue = "false")
	public static boolean	SKILL_AUTOLEARN;

	/**
	 * Stigma autolearn
	 */
	@Property(key = "gameserver.stigma.autolearn", defaultValue = "false")
	public static boolean	STIGMA_AUTOLEARN;

	/**
	 * Disable monsters aggressive behave
	 */
	@Property(key = "gameserver.disable.mob.aggro", defaultValue = "false")
	public static boolean	DISABLE_MOB_AGGRO;

	/**
	 * Enable 2nd class change simple mode
	 */
	@Property(key = "gameserver.enable.simple.2ndclass", defaultValue = "false")
	public static boolean	ENABLE_SIMPLE_2NDCLASS;

	/**
	 * Unstuck delay
	 */
	@Property(key = "gameserver.unstuck.delay", defaultValue = "3600")
	public static int		UNSTUCK_DELAY;

	/**
	 * Enable instances
	 */
	@Property(key = "gameserver.instances.enable", defaultValue = "true")
	public static boolean	ENABLE_INSTANCES;
	
	/**
	 * Base Fly Time
	 */
	@Property(key = "gameserver.base.flytime", defaultValue = "60")
	public static int		BASE_FLYTIME;
	/**
	 * ManaStone Rates
	 */
	@Property(key = "gameserver.manastone.percent", defaultValue = "57")
	public static int		MSPERCENT;
	@Property(key = "gameserver.manastone.percent1", defaultValue = "43")
	public static int		MSPERCENT1;	
	@Property(key = "gameserver.manastone.percent2", defaultValue = "33")
	public static int		MSPERCENT2;	
	@Property(key = "gameserver.manastone.percent3", defaultValue = "25")
	public static int		MSPERCENT3;	
	@Property(key = "gameserver.manastone.percent4", defaultValue = "19")
	public static int		MSPERCENT4;	
	@Property(key = "gameserver.manastone.percent5", defaultValue = "2")
	public static int		MSPERCENT5;	
}
