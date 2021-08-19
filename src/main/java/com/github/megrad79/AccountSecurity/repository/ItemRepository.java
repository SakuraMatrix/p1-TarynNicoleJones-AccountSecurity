package com.github.megrad79.AccountSecurity.repository;

import com.github.megrad79.AccountSecurity.domain.Item;

import java.util.Arrays;
import java.util.List;

public class ItemRepository {
    static List<Item> cache = Arrays.asList(new Item(1, "Potion", 20), new Item(2, "Pokeball", 40));

    public List<Item> getAllItems() {
        return cache;
    }

    public Item get(String id){
        int itemId = Integer.parseInt(id);
        return cache.get(itemId - 1);
    }


}
