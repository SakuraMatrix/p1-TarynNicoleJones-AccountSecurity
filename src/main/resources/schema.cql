CREATE KEYSPACE IF NOT EXISTS pokemart WITH replication ={'class': 'SimpleStrategy', 'replication_factor': 1};

USE pokemart;

CREATE TABLE IF NOT EXISTS item{
    item_id int,
    name text,
    price double,
    PRIMARY KEY (item_id, name)

};