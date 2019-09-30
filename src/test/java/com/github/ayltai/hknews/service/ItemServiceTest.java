package com.github.ayltai.hknews.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.github.ayltai.hknews.UnitTest;
import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;

@SpringBootTest
public final class ItemServiceTest extends UnitTest {
    private static final String OBJECT_ID = "507f1f77bcf86cd799439011";

    @Autowired
    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        this.itemRepository.deleteAll();
    }

    @Test
    public void given_noItem_when_getItem_returnNull() {
        final Item item = new ItemService(this.itemRepository).getItem(new ObjectId(ItemServiceTest.OBJECT_ID));

        Assert.assertNull(item);
    }

    @Test
    public void given_noItem_when_getItems_returnNoItem() {
        final Page<Item> items = new ItemService(this.itemRepository).getItems(Arrays.asList("蘋果日報", "東方日報"), Arrays.asList("港聞", "國際"), 1, PageRequest.of(0, 20));

        Assert.assertNotNull(items);
        Assert.assertEquals(0, items.getTotalElements());
    }

    @Test
    public void given_dummyItem_when_getItem_returnDummyItem() {
        final Item dummyItem = new Item();
        dummyItem.set_id(new ObjectId(ItemServiceTest.OBJECT_ID));

        // Given
        this.itemRepository.save(dummyItem);

        // When
        final Item item = new ItemService(this.itemRepository).getItem(new ObjectId(ItemServiceTest.OBJECT_ID));

        // Then
        Assert.assertNotNull(item);
        Assert.assertEquals(dummyItem.get_id(), item.get_id());
    }

    @Test
    public void given_dummyItem_when_getItems_returnDummyItem() {
        final Category category = new Category(Collections.emptyList(), "港聞");

        final Source source = new Source();
        source.setName("蘋果日報");
        source.getCategories().add(category);

        final Item dummyItem = new Item();
        dummyItem.set_id(new ObjectId(ItemServiceTest.OBJECT_ID));
        dummyItem.setCategory(category);
        dummyItem.setSource(source);
        dummyItem.setPublishDate(new Date());

        // Given
        this.itemRepository.save(dummyItem);

        // When
        final Page<Item> items = new ItemService(this.itemRepository).getItems(Arrays.asList("蘋果日報", "東方日報"), Arrays.asList("港聞", "國際"), 1, PageRequest.of(0, 20));

        // Then
        Assert.assertNotNull(items);
        Assert.assertEquals(1, items.getTotalElements());
        Assert.assertEquals(dummyItem.get_id(), items.get().findFirst().get().get_id());
    }
}
