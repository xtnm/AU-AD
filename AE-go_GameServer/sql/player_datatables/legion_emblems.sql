CREATE TABLE IF NOT EXISTS `legion_emblems` (
  `legion_id` int(11) NOT NULL,
  `emblem_id` int(1) NOT NULL default '0',
  `color_r` int(3) NOT NULL default '0',  
  `color_g` int(3) NOT NULL default '0', 
  `color_b` int(3) NOT NULL default '0',
  PRIMARY KEY  (`legion_id`),
  FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;