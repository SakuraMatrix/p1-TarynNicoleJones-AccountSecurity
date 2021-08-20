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
public class ItemRepository {
    // static List<Item> cache = Arrays.asList(new Item(1, "Potion", 20), new Item(2, "Pokeball", 40));

    private CqlSession session;

    public ItemRepository(CqlSession session) {
        this.session = session;
    }

    public Flux<Item> getAllItems() {
//        return cache;
        return Flux.from(session.executeReactive("SELECT * FROM AccountSecurity.items"))
                .map(row -> new Item(row.getInt("item_id"), row.getString("name"), row.getDouble("price")));
    }

    public Mono<Item> get(int id){
        /*int itemId = Integer.parseInt(id);
        return cache.get(itemId - 1);*/
        return Mono.from(session.executeReactive("SELECT * FROM AccountSecurity.items WHERE item_id = " + id))
                .map(row -> new Item(row.getInt("item_id"), row.getString("name"), row.getDouble("price")));
    }

    public Item create(Item item) {
        SimpleStatement statement = SimpleStatement.builder("INSERT INTO AccountSecurity.items (item_id, name, price) values (?, ?, ?)")
                .addPositionalValues(item.getId(), item.getName(), item.getPrice()).build();

        Flux.from(session.executeReactive(statement)).blockLast();

        return item;
    }
}
