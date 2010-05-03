@echo off
title Aion Unique Database Installer

REM --------------------------------------------------------------------------------------------
REM Change it to your database settings.
REM --------------------------------------------------------------------------------------------
REM GAMESERVER
set gsuser=root
set gspass=root
set gsdb=au_server_gs
set gshost=localhost
REM --------------------------------------------------------------------------------------------



REM --------------------------------------------------------------------------------------------
:installer
echo.
echo                       ** Aion Unique - DataBase Installer **
echo                                [AU - Game Server]
echo.
echo.
echo.
echo Please select one from these options.
echo.
echo OPTIONS:
echo		FULL INSTALL (f)     -   FULL INSTALL/REINSTALL OF DATABASE
echo		UPGRADE INSTALL (u)  -   UPGRADE INSTALL
echo		QUIT (q)             -   TO EXIT THIS PROGRAM
echo.
set installtype=x
set /p installtype=
if /i %installtype%==f goto full
if /i %installtype%==u goto upgrade
if /i %installtype%==q goto credit
goto installer
REM --------------------------------------------------------------------------------------------



REM --------------------------------------------------------------------------------------------
:full
cls
echo.
echo THIS IS A FULL INSTALL/REINSTALL SO DELETING OLD DATATABLES !
echo CLEANING                                                         PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables.sql
echo CLEANING                                                         PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < server_datatables.sql
goto upgrade
REM --------------------------------------------------------------------------------------------


REM --------------------------------------------------------------------------------------------
:upgrade
echo.
echo                       ** Aion Unique - DataBase Installer **
echo                                [AU - Game Server]
echo.
echo.
echo.
echo Please select one from these options.
echo.
echo OPTIONS:
echo		SERVER DATATABLES (s) - INSTALL SERVER DATATABLES
echo		PLAYER DATATABLES (p) - INSTALL PLAYER DATATABLES
echo		FINISH (f)            - FINISH THE INSTALL
echo.

set installtype=x
set /p installtype=
if /i %installtype%==s goto serverdatatables
if /i %installtype%==p goto playerdatatables
if /i %installtype%==f goto credits
REM --------------------------------------------------------------------------------------------



REM --------------------------------------------------------------------------------------------
:serverdatatables
echo.
echo                       ** Aion Unique - DataBase Installer **
echo                             [AU - SERVER DATATABLES ]
echo.
echo.
echo CLEANING                                                         PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < server_datatables.sql
echo DROPLIST                                                         PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < server_datatables/droplist.sql
echo DROP OF MONSTERS                                                 PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < drops/droplist.sql
echo SERVER VARIABLES                                                 PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < server_datatables/server_variables.sql
echo BROKER                                                 	      PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < server_datatables/broker.sql

goto upgrade
REM --------------------------------------------------------------------------------------------



REM --------------------------------------------------------------------------------------------
:playerdatatables
echo.
echo                       ** Aion Unique - DataBase Installer **
echo                             [AU - PLAYER DATATABLES ]
echo.
echo.

echo PLAYERS                                                          PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/players.sql
echo PLAYER APPEARANCE                                                PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/player_appearance.sql
echo PLAYER MACROSSES                                                 PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/player_macrosses.sql
echo PLAYER TITLES                                                    PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/player_titles.sql
echo PLAYER SETTINGS                                                  PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/player_settings.sql
echo PLAYER SKILLS                                                    PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/player_skills.sql
echo PLAYER EFFECTS                                                   PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/player_effects.sql
echo PLAYER QUESTS                                                    PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/player_quests.sql
echo PLAYER RECIPES                                                   PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/player_recipes.sql
echo PLAYER PUNISHMENTS                                               PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/player_punishments.sql
echo ABYSS RANK                                                       PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/abyss_rank.sql
echo LEGIONS                                                          PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/legions.sql
echo LEGION ANNOUNCEMENT LIST                                         PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/legion_announcement_list.sql
echo LEGION MEMBERS                                                   PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/legion_members.sql
echo LEGION EMBLEMS                                                   PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/legion_emblems.sql
echo LEGION HISTORY                                                   PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/legion_history.sql
echo INVENTORY                                                        PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/inventory.sql
echo ITEM STONES                                                      PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/item_stones.sql
echo FRIENDS                                                          PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/friends.sql
echo BLOCKS                                                           PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/blocks.sql
echo MAIL                                                             PROCESSING...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < player_datatables/mail.sql

goto upgrade
REM --------------------------------------------------------------------------------------------



REM --------------------------------------------------------------------------------------------
:credits
echo.
echo.
echo.
echo DATABASE INSTALL COMPLETED !
echo CONTACTS: http://aion-unique.org
echo.
echo HAVE A FUN!
echo.
echo POWERED BY AION UNIQUE TEAM
echo COPYRIGHT 2009-2010 - ALL RIGHTS RESERVED.
echo.
pause
REM --------------------------------------------------------------------------------------------