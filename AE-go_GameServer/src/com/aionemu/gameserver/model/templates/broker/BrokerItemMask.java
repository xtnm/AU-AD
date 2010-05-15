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
package com.aionemu.gameserver.model.templates.broker;


/**
 * @author kosyachok
 * @author Simple
 */
public enum BrokerItemMask
{
	/**
	 * Weapon Section + sub categories
	 */
	WEAPON(9010, 98304),
	//WEAPON_SWORD(1000),
	//WEAPON_MACE(1001),
	//WEAPON_DAGGER(1002),
	//WEAPON_ORB(1005),
	//WEAPON_SPELLBOOK(1006),
	//WEAPON_GREATSWORD(1009),
	//WEAPON_POLEARM(1013),
	//WEAPON_STAFF(1015),
	//WEAPON_BOW(1017),
	
	/**
	 * Armor Section + sub categories
	 */
	ARMOR(9020, 106496),
	ARMOR_CLOTHING(8010, 106752),
	//ARMOR_CLOTHING_JACKET(1100),
	//ARMOR_CLOTHING_GLOVES(1110),
	//ARMOR_CLOTHING_PAULDRONS(1120),
	//ARMOR_CLOTHING_PANTS(1130),
	//ARMOR_CLOTHING_SHOES(1140),
	ARMOR_CLOTH(8020, 106500),
	//ARMOR_CLOTH_JACKET(1101),
	//ARMOR_CLOTH_GLOVES(1111),
	//ARMOR_CLOTH_PAULDRONS(1121),
	//ARMOR_CLOTH_PANTS(1131),
	//ARMOR_CLOTH_SHOES(1141),
	ARMOR_LEATHER(8030, 107012),
	//ARMOR_LEATHER_JACKET(1103),
	//ARMOR_LEATHER_GLOVES(1113),
	//ARMOR_LEATHER_PAULDRONS(1123),
	//ARMOR_LEATHER_PANTS(1133),
	//ARMOR_LEATHER_SHOES(1143),
	ARMOR_CHAIN(8040, 107268),
	//ARMOR_CHAIN_JACKET(1105),
	//ARMOR_CHAIN_GLOVES(1115),
	//ARMOR_CHAIN_PAULDRONS(1125),
	//ARMOR_CHAIN_PANTS(1135),
	//ARMOR_CHAIN_SHOES(1145),
	ARMOR_PLATE(8050, 110592),
	//ARMOR_PLATE_JACKET(1106),
	//ARMOR_PLATE_GLOVES(1116),
	//ARMOR_PLATE_PAULDRONS(1126),
	//ARMOR_PLATE_PANTS(1136),
	//ARMOR_PLATE_SHOES(1146),
	//ARMOR_SHIELD(1150),

	/**
	 * Accessory Section + sub categories
	 */
	ACCESSORY(9030, 114688),
	//ACCESSORY_EARRINGS(1200),
	//ACCESSORY_NECKLACE(1210),
	//ACCESSORY_RING(1220),
	//ACCESSORY_BELT(1230),
	ACCESSORY_HEADGEAR(7030, 125000),
	/**
	 * Skill related Section + sub categories
	 */
	SKILL_RELATED(9040, 131584),
	//SKILL_RELATED_STIGMA(1400),
	SKILL_RELATED_STIGMA_GLADIATOR(6010, 0),
	SKILL_RELATED_STIGMA_TEMPLAR(6011, 0),
	SKILL_RELATED_STIGMA_ASSASSIN(6012, 0),
	SKILL_RELATED_STIGMA_RANGER(6013, 0),
	SKILL_RELATED_STIGMA_SORCERER(6014, 0),
	SKILL_RELATED_STIGMA_SPIRITMASTER(6015, 0),
	SKILL_RELATED_STIGMA_CLERIC(6016, 0),
	SKILL_RELATED_STIGMA_CHANTER(6017, 0),
	//SKILL_RELATED_SKILL_MANUAL(1695),
	SKILL_RELATED_SKILL_MANUAL_GLADIATOR(6020, 0),
	SKILL_RELATED_SKILL_MANUAL_TEMPLAR(6021, 0),
	SKILL_RELATED_SKILL_MANUAL_ASSASSIN(6022, 0),
	SKILL_RELATED_SKILL_MANUAL_RANGER(6023, 0),
	SKILL_RELATED_SKILL_MANUAL_SORCERER(6024, 0),
	SKILL_RELATED_SKILL_MANUAL_SPIRITMASTER(6025, 0),
	SKILL_RELATED_SKILL_MANUAL_CLERIC(6026, 0),
	SKILL_RELATED_SKILL_MANUAL_CHANTER(6027, 0),
	
	/**
	 * Craft Section + sub categories
	 */
	CRAFT(9050, 151680),
	//CRAFT_MATERIALS(1520),
	CRAFT_MATERIALS_COLLECTION(6030, 152000),
	CRAFT_MATERIALS_GAIN(6031, 152010),
	CRAFT_MATERIALS_PARTS(6032, 152020),
	//CRAFT_DESIGN(1522),
	CRAFT_DESIGN_WEAPONSMITHING(6040, 0),
	CRAFT_DESIGN_ARMORSMITHING(6041, 0),
	CRAFT_DESIGN_TAILORING(6042, 0),
	CRAFT_DESIGN_HANDICRAFTING(6043, 0),
	CRAFT_DESIGN_ALCHEMY(6044, 0),
	CRAFT_DESIGN_COOKING(6045, 0),
	
	/**
	 * Consumables Section + sub categories
	 */
	CONSUMABLES(9060, 131072),
	//CONSUMABLES_FOOD(1600),
	//CONSUMABLES_POTION(1620),
	CONSUMABLES_SCROLL(7060, 164000),
	CONSUMABLES_MODIFY(8060, 163904),
	//CONSUMABLES_MODIFY_ENCHANTMENT_STONE(1660),
	//CONSUMABLES_MODIFY_MANASTONE(1670),
	//CONSUMABLES_MODIFY_GODSTONE(1680),
	CONSUMABLES_MODIFY_DYE(7061, 169200),
	CONSUMABLES_OTHER(7062, 132096),
	
	/**
	 * Other Section
	 */
	OTHER(7070, 164352),
	
	UNKNOWN(1, 0);
	
	private int typeId;
	
	private int mask;
	
	BrokerItemMask(int typeId, int mask)
	{
		this.typeId = typeId;
		this.mask = mask;
	}

	/**
	 * @return the typeId
	 */
	public int getId()
	{
		return typeId;
	}
	
	public int getMask()
	{
		return mask;
	}

	/**
	 * Return BrokerListType by id.
	 * @param id
	 * @return BrokerListType
	 */
	public static BrokerItemMask getBrokerMaskById(int id)
	{
		for(BrokerItemMask mt : values())
		{
			if(mt.typeId == id)
				return mt;
		}
		return UNKNOWN;
	}
}
