package com.github.megrad79.AccountSecurity.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncCqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.github.megrad79.AccountSecurity.domain.Item;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Repository
public class ItemRepository{
    // static List<Item> cache = Arrays.asList(new Item(1, "Potion", 20), new Item(2, "Pokeball", 40));

    private CqlSession session;

    public ItemRepository(CqlSession session) {
        this.session = session;
    }

    public Flux<Item> getAllItems() {
//        return cache;
        return Flux.from(session.executeReactive("SELECT * FROM AccountSecurity.item"))
                .map(row -> new Item(row.getInt("account"), row.getString("password"), row.getDouble("time")));
    }

    public Mono<Item> get(int account){
        /*int itemId = Integer.parseInt(id);
        return cache.get(itemId - 1);*/
        return Mono.from(session.executeReactive("SELECT * FROM AccountSecurity.item WHERE account = " + account))
                .map(row -> new Item(row.getInt("account"), row.getString("password"), row.getDouble("time")));
    }

    public Item create(Item item) {
        SimpleStatement statement = SimpleStatement.builder("INSERT INTO AccountSecurity.item (account, password, time) values (?, ?, ?)")
                .addPositionalValues(item.getAccount(), item.getPassword(), item.getTime()).build();

        Flux.from(session.executeReactive(statement)).subscribe();

        return item;
    }
}
