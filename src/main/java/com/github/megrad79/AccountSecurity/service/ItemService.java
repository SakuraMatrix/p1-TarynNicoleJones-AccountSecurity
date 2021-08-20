package com.github.megrad79.AccountSecurity.service;

import com.github.megrad79.AccountSecurity.domain.Item;
import com.github.megrad79.AccountSecurity.repository.ItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ItemService {
    private ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Mono<Item> get(String id){
        return itemRepository.get(Integer.parseInt(id));
    }

    public Flux<Item> getAllItems(){
        return itemRepository.getAllItems();
    }

    public Item create(Item item){
        return itemRepository.create(item);
    }
}
