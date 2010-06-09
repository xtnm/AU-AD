package com.aionemu.gameserver.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dao.LocaleDAO;

public class LocaleManager 
{
	
	private static final Logger log = Logger.getLogger(LocaleManager.class);
	
	private static Map<Integer, String> registeredLocales;
	
	private static boolean isInitialized = false;
	
	public static void initialize()
	{
		if(CustomConfig.LOCALE.equals(""))
		{
			log.error("Cannot initialize LocaleManager. Please add the gameserver.locale directive to your custom.config file");
			return;
		}
		registeredLocales = DAOManager.getDAO(LocaleDAO.class).loadLocalesByLang(CustomConfig.LOCALE);
		log.info("Successfully loaded " + registeredLocales.size() + " database locales strings");
		isInitialized = true;
	}
	
	public static boolean reloadData()
	{
		if(registeredLocales != null)
		{
			registeredLocales.clear();
			registeredLocales = null;
			registeredLocales = DAOManager.getDAO(LocaleDAO.class).loadLocalesByLang(CustomConfig.LOCALE);
			log.info("Successfully reloaded " + registeredLocales.size() + "database locales strings");
			return true;
		}
		else
		{
			return false;
		}
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
