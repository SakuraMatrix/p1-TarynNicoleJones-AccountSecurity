package com.github.megrad79.AccountSecurity.service;

import com.github.megrad79.AccountSecurity.domain.Item;
import com.github.megrad79.AccountSecurity.repository.ItemRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public class ItemService {
    private ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Flux<Item> getAllItems(){
        return Flux.fromIterable(itemRepository.getAllItems());
    }
}
