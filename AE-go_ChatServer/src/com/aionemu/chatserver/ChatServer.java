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
package com.aionemu.chatserver;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import org.apache.log4j.Logger;

import com.aionemu.chatserver.configs.Config;
import com.aionemu.chatserver.utils.guice.ServiceInjectionModule;
import com.aionemu.commons.services.LoggingService;
import com.google.inject.Guice;

/**
 * @author ATracer
 */
public class ChatServer
{
    /**
     * Logger for this class.
     */
    private static final Logger	log = Logger.getLogger(ChatServer.class);

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        LoggingService.init();      
		Config.load();
        
        Guice.createInjector(new ServiceInjectionModule());		

        MemoryUsage	hm  = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage	nhm = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

        log.info("Heap Memory Usage: " + (hm.getUsed() / 1048576) + "/" + (hm.getMax() / 1048576) + " MB");
        log.info("NonHeap Memory Usage: " + (nhm.getUsed() / 1048576) + "/" + (nhm.getMax() / 1048576) + " MB");
    }
}
