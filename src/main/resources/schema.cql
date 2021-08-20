CREATE KEYSPACE IF NOT EXISTS AccountSecurity WITH replication ={'class': 'SimpleStrategy', 'replication_factor': 1};

USE AccountSecurity;

CREATE TABLE IF NOT EXISTS items {
    account int,
    password text,
    time double,
    PRIMARY KEY (account, password)

};