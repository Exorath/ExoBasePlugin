package com.exorath.plugin.base.serverId;

import java.util.UUID;

/**
 * Created by toonsev on 2/4/2017.
 */
public class ServerUUIDProvider implements ServerIdProvider {
    private String id;

    public ServerUUIDProvider() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String getServerId() {
        return id;
    }
}
