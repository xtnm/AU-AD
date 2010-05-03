CREATE TABLE `bookmark` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(50) default NULL,
  `char_id` int(11) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `z` float NOT NULL,
  `world_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;