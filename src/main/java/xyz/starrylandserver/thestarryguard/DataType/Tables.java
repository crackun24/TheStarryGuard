package xyz.starrylandserver.thestarryguard.DataType;

public class Tables {
    public class Mysql {
        public static final String CREATE_TG_ACTION = """
                CREATE TABLE IF NOT EXISTS `tg_action` (
                  `player` int(10) NOT NULL,
                  `action` int(10) NOT NULL,
                  `target` int(10) NOT NULL,
                  `time` int(10) NOT NULL,
                  `data` varchar(255) DEFAULT NULL,
                  `x` int(10) NOT NULL,
                  `y` int(10) NOT NULL,
                  `z` int(10) NOT NULL,
                  `dimension` int(10) NOT NULL,
                  KEY `location` (`x`,`y`,`z`)
                ) ENGINE=InnoDB DEFAULT CHARSET=latin1;""";
        public static final String CREATE_TG_ACTION_MAP = """
                CREATE TABLE IF NOT EXISTS `tg_action_map` (
                  `action` varchar(255) NOT NULL,
                  `id` int(10) NOT NULL,
                  PRIMARY KEY (`action`)
                ) ENGINE=InnoDB DEFAULT CHARSET=latin1;""";
        public static final String CREATE_TG_ENTITY_MAP = """
                CREATE TABLE IF NOT EXISTS `tg_entity_map` (
                  `entity` varchar(255) NOT NULL,
                  `id` int(10) NOT NULL,
                  PRIMARY KEY (`entity`)
                ) ENGINE=InnoDB DEFAULT CHARSET=latin1;""";
        public static final String CREATE_TG_ITEM_MAP = """
                CREATE TABLE IF NOT EXISTS `tg_item_map` (
                  `item` varchar(255) NOT NULL,
                  `id` int(10) NOT NULL,
                  PRIMARY KEY (`item`)
                ) ENGINE=InnoDB DEFAULT CHARSET=latin1;""";
        public static final String CREATE_TG_PLAYER_MAP = """
                CREATE TABLE IF NOT EXISTS `tg_player_map` (
                  `uuid` varchar(255) NOT NULL,
                  `name` varchar(255) NOT NULL,
                  `id` int(10) NOT NULL,
                  PRIMARY KEY (`uuid`)
                ) ENGINE=InnoDB DEFAULT CHARSET=latin1;""";
        public static final String CREATE_TG_DIMENSION_MAP = """
                CREATE TABLE IF NOT EXISTS `tg_dimension_map` (
                  `dimension` varchar(255) NOT NULL,
                  `id` int(10) NOT NULL,
                  PRIMARY KEY (`dimension`)
                ) ENGINE=InnoDB DEFAULT CHARSET=latin1;""";


    }

    public static final String INSERT_ACTION_STR = "INSERT INTO tg_action (player, action, target, time, data, x, y, z, dimension) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";//玩家行为预处理语句
    public static final String INSERT_ACTION_MAP_STR = "INSERT INTO tg_action_map (action, id) VALUES (?, ?)";
    public static final String INSERT_DIMENSION_MAP_STR = "INSERT INTO tg_dimension_map (dimension, id) VALUES (?, ?)";
    public static final String INSERT_ENTITY_MAP_STR = "INSERT INTO tg_entity_map (entity, id) VALUES (?, ?)";
    public static final String INSERT_ITEM_MAP_STR = "INSERT INTO tg_item_map (item, id) VALUES (?, ?)";
    public static final String INSERT_PLAYER_MAP_STR = "INSERT INTO tg_player_map (uuid, name, id) VALUES (?, ?, ?)";
    public static final String QUERY_POINT_ACTION = "SELECT * FROM tg_action WHERE x = ? AND y = ? AND z = ? AND dimension = ? ORDER BY time DESC LIMIT ?, ?";
    public static final String QUERY_POINT_ACTION_COUNT = "SELECT COUNT(*) AS count FROM tg_action WHERE x = ? AND y = ? AND z = ? AND dimension = ?";
    public static final String QUERY_AREA_ACTION = "SELECT * FROM tg_action WHERE x BETWEEN ? AND ? AND y BETWEEN ? AND ? AND z BETWEEN ? AND ? AND dimension = ? ORDER BY time DESC LIMIT ?,?";
    public static final String QUERY_AREA_ACTION_COUNT = "SELECT COUNT(*) AS count FROM tg_action WHERE x BETWEEN ? AND ? AND y BETWEEN ? AND ? AND z BETWEEN ? AND ? AND dimension = ? ORDER BY time DESC";

    public class SqlLite {
        public static final String CREATE_TG_ACTION = """
                CREATE TABLE IF NOT EXISTS tg_action (
                    player INTEGER NOT NULL,
                    action INTEGER NOT NULL,
                    target INTEGER NOT NULL,
                    time INTEGER NOT NULL,
                    data TEXT DEFAULT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    z INTEGER NOT NULL,
                    dimension INTEGER NOT NULL
                );
                CREATE INDEX location_index ON tg_action (x, y, z);""";
        public static final String CREATE_TG_ACTION_MAP = """
                CREATE TABLE IF NOT EXISTS tg_action_map (
                    action TEXT NOT NULL,
                    id INTEGER NOT NULL,
                    PRIMARY KEY (action)
                );""";
        public static final String CREATE_TG_ENTITY_MAP = """
                CREATE TABLE IF NOT EXISTS tg_entity_map (
                    entity TEXT NOT NULL,
                    id INTEGER NOT NULL,
                    PRIMARY KEY (entity)
                );""";
        public static final String CREATE_TG_ITEM_MAP = """
                CREATE TABLE IF NOT EXISTS tg_item_map (
                    item TEXT NOT NULL,
                    id INTEGER NOT NULL,
                    PRIMARY KEY (item)
                );
                """;
        public static final String CREATE_TG_PLAYER_MAP = """
                CREATE TABLE IF NOT EXISTS tg_player_map (
                    uuid TEXT NOT NULL,
                    name TEXT NOT NULL,
                    id INTEGER NOT NULL,
                    PRIMARY KEY (uuid)
                );""";
        public static final String CREATE_TG_DIMENSION_MAP = """
                CREATE TABLE IF NOT EXISTS tg_dimension_map (
                    dimension TEXT NOT NULL,
                    id INTEGER NOT NULL,
                    PRIMARY KEY (dimension)
                );
                """;
    }
}
