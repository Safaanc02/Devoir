package com.example.commandes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mes-config-ms")
public class CommandesProperties {

    private int commandesLast;

    public int getCommandesLast() {
        return commandesLast;
    }

    public void setCommandesLast(int commandesLast) {
        this.commandesLast = commandesLast;
    }
}
