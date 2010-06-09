package com.aionemu.gameserver.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.LocaleDAO;

public class LocaleManager 
{
	
	private static final Logger log = Logger.getLogger(LocaleManager.class);
	
	private static Map<Integer, String> registeredLocales;
	
	private static boolean isInitialized = false;
	
	public static void initialize(String configurationLocale)
	{
		registeredLocales = DAOManager.getDAO(LocaleDAO.class).loadLocalesByLang(configurationLocale);
		log.info("Successfully loaded " + registeredLocales.size() + " database locales strings");
		isInitialized = true;
	}
	
	public static String getString(int stringId)
	{
		if(isInitialized)
		{
			String result = registeredLocales.get(stringId);
			if(result == null || result.equals(""))
			{
				return "";
			}
			else
			{
				return result;
			}
		}
		else
		{
			log.error("LocaleManager not initialized !");
			return "";
		}
	}
	
}
