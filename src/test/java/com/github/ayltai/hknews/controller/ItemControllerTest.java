package com.github.ayltai.hknews.controller;

import java.util.Arrays;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.github.ayltai.hknews.UnitTest;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.service.ItemService;

@WebMvcTest(ItemController.class)
public final class ItemControllerTest extends UnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    public void when_getItem_then_returnItem() throws Exception {
        Mockito.when(this.itemService
            .getItem(ArgumentMatchers.any(ObjectId.class)))
            .thenReturn(ItemControllerTest.getItem());

        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/item/507f1f77bcf86cd799439011"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    public void when_getItems_then_returnItems() throws Exception {
        Mockito.when(this.itemService
            .getItems(ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Pageable.class)))
            .thenReturn(ItemControllerTest.getItems());

        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/items/蘋果日報,東方日報/港聞,國際/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"));
    }

    private static Item getItem() {
        final Item item = new Item();
        item.set_id(new ObjectId("507f1f77bcf86cd799439011"));

        return item;
    }

    private static Page<Item> getItems() {
        return new PageImpl<>(Arrays.asList(ItemControllerTest.getItem(), ItemControllerTest.getItem()));
    }
}
