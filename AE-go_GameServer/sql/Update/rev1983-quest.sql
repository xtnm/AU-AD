ALTER TABLE `player_quests`
CHANGE COLUMN `complite_count` `complete_count`  int(3) UNSIGNED NOT NULL DEFAULT 0 AFTER `quest_vars`;

UPDATE player_quests SET status = REPLACE(status, 'COMPLITE','COMPLETE');