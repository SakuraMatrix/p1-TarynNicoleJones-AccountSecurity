package com.github.megrad79.AccountSecurity.service;

import com.github.megrad79.AccountSecurity.domain.Item;
import com.github.megrad79.AccountSecurity.repository.ItemRepository;

import java.util.List;

public class ItemService {
    private ItemRepository itemRepository;

    public void setItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems(){
        return itemRepository.getAllItems();
    }
}
