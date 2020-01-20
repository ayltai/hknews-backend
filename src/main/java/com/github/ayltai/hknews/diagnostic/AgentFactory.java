package com.github.ayltai.hknews.diagnostic;

import org.springframework.lang.Nullable;

import com.instrumentalapp.Agent;

public interface AgentFactory {
    @Nullable
    Agent create();
}
