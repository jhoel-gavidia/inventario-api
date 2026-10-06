package com.motorepuestos.inventario.security;

import com.motorepuestos.inventario.common.security.TokenBlacklistService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenBlacklistServiceTest {

    private final TokenBlacklistService blacklist = new TokenBlacklistService();

    @Test
    void revoke_debeMarcarComoRevocado() {

        long futureExp = System.currentTimeMillis() + 60_000;

        blacklist.revoke("jti-1", futureExp);

        assertThat(blacklist.isRevoked("jti-1")).isTrue();
    }

    @Test
    void jtiNuncaRevocado_noDebeEstarEnBlacklist() {

        assertThat(blacklist.isRevoked("jti-inexistente")).isFalse();
    }

    @Test
    void jtiNulo_noDebeEstarEnBlacklist() {

        assertThat(blacklist.isRevoked(null)).isFalse();
    }

    @Test
    void revokeConNulo_noDebeFallarNiMarcar() {

        blacklist.revoke(null, System.currentTimeMillis() + 60_000);

        assertThat(blacklist.isRevoked(null)).isFalse();
    }

    @Test
    void tokenExpirado_noDebeEstarEnBlacklist() {

        long alreadyExpired = System.currentTimeMillis() - 1_000;

        blacklist.revoke("jti-expirado", alreadyExpired);

        assertThat(blacklist.isRevoked("jti-expirado")).isFalse();
    }

    @Test
    void purgeExpired_debeRemoverEntradasExpiradas() {

        blacklist.revoke("expirado", System.currentTimeMillis() - 1_000);
        blacklist.revoke("vigente", System.currentTimeMillis() + 60_000);

        blacklist.purgeExpired();

        assertThat(blacklist.isRevoked("expirado")).isFalse();
        assertThat(blacklist.isRevoked("vigente")).isTrue();
    }
}