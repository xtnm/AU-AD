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
package com.aionemu.gameserver.skillengine.effect;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import javolution.util.FastMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.aionemu.gameserver.controllers.movement.AttackCalcObserver;
import com.aionemu.gameserver.controllers.movement.AttackShieldObserver;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShieldEffect")
public class ShieldEffect extends EffectTemplate
{

	@XmlAttribute
	protected int	hitdelta;
	@XmlAttribute
	protected int	hitvalue;
	@XmlAttribute
	protected boolean	percent;
	@XmlAttribute
	protected int	delta;
	@XmlAttribute
	protected int	value;

	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect)
	{
		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		int hitValueWithDelta = hitvalue + hitdelta * skillLvl;
		effect.setReserved2(valueWithDelta);
		effect.setReserved3(hitValueWithDelta);
		effect.increaseSuccessEffect();
	}

	@Override
	public void startEffect(final Effect effect)
	{
		AttackShieldObserver asObserver = new AttackShieldObserver(effect.getReserved3(),
			effect.getReserved2(), percent, effect);
		
		effect.getEffected().getObserveController().addAttackCalcObserver(asObserver);
		effect.setAttackShieldObserver(asObserver, position);
	}

	@Override
	public void endEffect(Effect effect)
	{
		AttackCalcObserver acObserver = effect.getAttackShieldObserver(position);
		effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
	}

	public static void main(String[] args)
	{
		FastMap<Integer, Integer> data = new FastMap<Integer, Integer>().shared();
		for (int i = 1 ; i < 30; i++)
			data.put(i, i);
		
		int f = 0;
		long startTime = System.nanoTime();
	     
	
		for(Integer i : data.values())
		{
			f += i;
		}
		Iterator<Integer> it = data.values().iterator();
		while (it.hasNext())
		{
			f +=it.next();
		}
		System.out.println("iterator: "+ (System.nanoTime() - startTime));
		
		f = 0;
		startTime = System.nanoTime();
		for (FastMap.Entry<Integer, Integer> e = data.head(), end = data.tail(); (e = e.getNext()) != end;)
	     {
	    	 Integer key = e.getKey(); // No typecast necessary.
	    	 f +=key; 
	     }
		System.out.println("FastMap.Entry: " + (System.nanoTime() - startTime));

		f = 0;
		startTime = System.nanoTime();
		
		Iterator<Integer> it2 = new FastMapValueIterator<Integer, Integer>(data);
		while (it2.hasNext())
		{
			f += it2.next();
		}
	    System.out.println("FastMapValueIterator: " + (System.nanoTime() - startTime));
		
		
	}
	
	static class FastMapValueIterator<E, K> implements Iterator<K>
	{
		private FastMap.Entry<E, K> head;
		private FastMap.Entry<E, K>	end;
		private boolean hasNext;
		private FastMap.Entry<E, K> next;
		
		private FastMapValueIterator(FastMap<E, K> coll)
		{
			head = coll.head();
			end = coll.tail();
			hasNext = (next = head.getNext()) != end;
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public K next()
		{
			FastMap.Entry<E, K> nextReturn = next;
			hasNext = (next = next.getNext()) != end;
			return  nextReturn.getValue();
		}

		@Override
		public void remove()
		{
			throw new NotImplementedException();
		}	
	}
}
