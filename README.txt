README for Aion Dream Additions

== Launching the emulator ==

Use the following lines to put server up :

LS : java -Xms8m -Xmx128m -ea -Xbootclasspath/p:./libs/jsr166.jar -javaagent:libs/ae_commons.jar -cp ./libs/*:ae_login.jar com.aionemu.loginserver.LoginServer /path/to/config/file.config > /dev/null &
GS : java -Xms128m -Xmx3500m -ea -Xbootclasspath/p:./libs/jsr166.jar -javaagent:libs/ae_commons.jar -cp ./libs/*:ae_gameserver.jar com.aionemu.gameserver.GameServer /path/to/config/file.config > /dev/null &