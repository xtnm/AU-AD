CREATE TABLE IF NOT EXISTS `legion_members` (
  `legion_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `nickname` varchar(16) NOT NULL default '',  
  `rank` enum( 'BRIGADE_GENERAL', 'CENTURION', 'LEGIONARY' ) NOT NULL DEFAULT 'LEGIONARY',
  `selfintro` varchar(25) default '',
  PRIMARY KEY  (`player_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;