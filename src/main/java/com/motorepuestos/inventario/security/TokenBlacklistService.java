package com.motorepuestos.inventario.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Long> revokedJtis = new ConcurrentHashMap<>();

    public void revoke(String jti, long expiresAtMs) {

        if (jti == null) {
            return;
        }

        revokedJtis.put(jti, expiresAtMs);
    }

    public boolean isRevoked(String jti) {

        if (jti == null) {
            return false;
        }

        Long expiresAtMs = revokedJtis.get(jti);

        if (expiresAtMs == null) {
            return false;
        }

        return expiresAtMs > System.currentTimeMillis();
    }

    @Scheduled(fixedDelayString = "${app.token.blacklist.purge-ms:3600000}")
    public void purgeExpired() {

        long now = System.currentTimeMillis();
        revokedJtis.entrySet().removeIf(entry ->
                entry.getValue() <= now
        );
    }
}