package com.github.ayltai.hknews;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
public abstract class UnitTest {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
}
