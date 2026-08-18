package com.gestionInventario.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SecurityAuthorizationRulesTest {

    private static String securityConfig;

    @BeforeAll
    static void setUp() throws Exception {
        securityConfig = Files.readString(Path.of(
                "src/main/java/com/gestionInventario/security/SecurityConfig.java"));
    }

    @Test
    void loginPublicoYRestoUsuariosSoloAdministrador() {
        assertThat(securityConfig)
                .contains(".requestMatchers(HttpMethod.POST, \"/api/auth/login\").permitAll()")
                .contains(".requestMatchers(\"/api/usuarios/**\").hasRole(ADMINISTRADOR)")
                .contains(".requestMatchers(\"/api/roles/**\").hasRole(ADMINISTRADOR)");
    }

    @Test
    void productosPermiteCrudNormalAmbosPeroEstadoSoloAdmin() {
        assertThat(securityConfig)
                .contains(".requestMatchers(HttpMethod.DELETE, \"/api/producto/**\").hasRole(ADMINISTRADOR)")
                .contains(".requestMatchers(HttpMethod.PATCH, \"/api/producto/**\").hasRole(ADMINISTRADOR)")
                .contains(".requestMatchers(HttpMethod.GET, \"/api/producto/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)")
                .contains(".requestMatchers(HttpMethod.POST, \"/api/producto/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)")
                .contains(".requestMatchers(HttpMethod.PUT, \"/api/producto/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)");
    }

    @Test
    void proveedorBloqueaDesactivacionYPermiteCrudNormalAmbos() {
        assertThat(securityConfig)
                .contains(".requestMatchers(HttpMethod.DELETE, \"/api/proveedor/**\").denyAll()")
                .contains(".requestMatchers(HttpMethod.PATCH, \"/api/proveedor/**\").denyAll()")
                .contains(".requestMatchers(HttpMethod.GET, \"/api/proveedor/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)")
                .contains(".requestMatchers(HttpMethod.POST, \"/api/proveedor/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)")
                .contains(".requestMatchers(HttpMethod.PUT, \"/api/proveedor/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)");
    }

    @Test
    void inventarioBloqueaModificacionDirectaYPermiteConsultas() {
        assertThat(securityConfig)
                .contains(".requestMatchers(HttpMethod.POST, \"/api/inventario/**\").denyAll()")
                .contains(".requestMatchers(HttpMethod.PUT, \"/api/inventario/**\").denyAll()")
                .contains(".requestMatchers(HttpMethod.PATCH, \"/api/inventario/**\").denyAll()")
                .contains(".requestMatchers(HttpMethod.DELETE, \"/api/inventario/**\").denyAll()")
                .contains(".requestMatchers(HttpMethod.GET, \"/api/inventario/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)");
    }

    @Test
    void categoriasAlmacenesComprasCatalogosYFallbackEstanClasificados() {
        assertThat(securityConfig)
                .contains(".requestMatchers(HttpMethod.GET, \"/api/categorias/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)")
                .contains(".requestMatchers(\"/api/categorias/**\").hasRole(ADMINISTRADOR)")
                .contains(".requestMatchers(HttpMethod.GET, \"/api/almacen/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)")
                .contains(".requestMatchers(\"/api/almacen/**\").hasRole(ADMINISTRADOR)")
                .contains(".requestMatchers(\"/api/compras/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)")
                .contains(".requestMatchers(HttpMethod.GET, \"/api/comprobante/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)")
                .contains(".requestMatchers(HttpMethod.GET, \"/api/movimiento/**\").hasAnyRole(ADMINISTRADOR, ALMACENERO)")
                .contains(".requestMatchers(\"/api/**\").authenticated()");
    }
}
