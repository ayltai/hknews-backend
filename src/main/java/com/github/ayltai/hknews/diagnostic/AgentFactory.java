package com.github.ayltai.hknews.diagnostic;

import org.springframework.lang.NonNull;

import com.instrumentalapp.Agent;

public interface AgentFactory {
    @NonNull
    Agent create();
}
