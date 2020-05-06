package com.github.ayltai.hknews.diagnostic;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import com.instrumentalapp.Agent;
import com.instrumentalapp.AgentOptions;

@Component
public final class DefaultAgentFactory implements AgentFactory {
    private static Agent agent;

    @Nullable
    @Override
    public Agent create() {
        if (DefaultAgentFactory.agent == null) DefaultAgentFactory.agent = new Agent(new AgentOptions().setApiKey(System.getenv("INSTRUMENTAL_API_KEY")));

        return DefaultAgentFactory.agent;
    }
}
