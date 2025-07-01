package ua.torque.nexus.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public final class LocationUriBuilder {

    private LocationUriBuilder() {}

    public static URI buildLocationUriById(Object resourceId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resourceId)
                .toUri();
    }
}