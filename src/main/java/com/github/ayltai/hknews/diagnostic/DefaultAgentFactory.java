package com.github.ayltai.hknews.diagnostic;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.instrumentalapp.Agent;
import com.instrumentalapp.AgentOptions;

@Component
public final class DefaultAgentFactory implements AgentFactory {
    private static Agent agent;

    @NonNull
    @Override
    public Agent create() {
        if (DefaultAgentFactory.agent == null) DefaultAgentFactory.agent = new Agent(new AgentOptions().setApiKey(System.getProperty("instrumental.api-key")));

        return DefaultAgentFactory.agent;
    }
}
