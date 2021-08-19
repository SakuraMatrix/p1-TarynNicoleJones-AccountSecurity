package com.github.megrad79.AccountSecurity.repository;

import com.github.megrad79.AccountSecurity.domain.Item;

import java.util.Arrays;
import java.util.List;

public class ItemRepository {
    public List<Item> getAllItems() {
        return Arrays.asList(new Item(1, "Potion", 20), new Item(2, "Pokeball", 40));
    }
}
