package com.github.ayltai.hknews;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.github.ayltai.hknews.controller.ItemController;
import com.github.ayltai.hknews.controller.SourceController;

@SpringBootTest
public final class MainApplicationTest extends UnitTest {
    @Autowired
    private SourceController sourceController;

    @Autowired
    private ItemController itemController;

    @Test
    public void contextLoads() {
        Assert.assertNotNull(this.sourceController);
        Assert.assertNotNull(this.itemController);
    }
}
