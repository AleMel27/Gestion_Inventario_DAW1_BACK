package com.gestionInventario.exception;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.gestionInventario.dtos.request.CompraDTO;
import com.gestionInventario.dtos.request.ProductoCreateDTO;

import jakarta.validation.Valid;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void resourceNotFoundExceptionDevuelve404() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("El producto no existe"))
                .andExpect(jsonPath("$.path").value("/test/not-found"));
    }

    @Test
    void noResourceFoundExceptionDevuelve404DeRutaInexistente() throws Exception {
        mockMvc.perform(get("/test/no-resource"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("La ruta solicitada no existe"))
                .andExpect(jsonPath("$.path").value("/test/no-resource"));
    }

    @Test
    void dtoInvalidoDevuelve400ConErroresPorCampo() throws Exception {
        String body = """
                {
                  "codigo": "",
                  "nombre": "",
                  "precioVenta": -10,
                  "stockMinimo": -1,
                  "idCategoria": 0
                }
                """;

        mockMvc.perform(post("/test/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Datos de entrada inválidos"))
                .andExpect(jsonPath("$.errors", hasKey("codigo")))
                .andExpect(jsonPath("$.errors", hasKey("nombre")))
                .andExpect(jsonPath("$.errors", hasKey("precioVenta")))
                .andExpect(jsonPath("$.errors", hasKey("stockMinimo")))
                .andExpect(jsonPath("$.errors", hasKey("idCategoria")));
    }

    @Test
    void dtoAnidadoInvalidoDevuelve400AntesDeLlegarAServicio() throws Exception {
        String body = """
                {
                  "idProveedor": 1,
                  "idUsuario": 1,
                  "idAlmacen": 1,
                  "idTipoComprobante": 1,
                  "numeroComprobante": "F001-1",
                  "detalles": [
                    {
                      "idProducto": 1,
                      "cantidad": -5,
                      "costoUnitario": 10
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/test/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors", hasKey("detalles[0].cantidad")));
    }

    @Test
    void businessRuleExceptionDevuelve400() throws Exception {
        mockMvc.perform(post("/test/business"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Stock insuficiente"));
    }

    @Test
    void duplicadoConConstraintConocidaDevuelve409() throws Exception {
        mockMvc.perform(post("/test/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Ya existe una compra con ese comprobante"));
    }

    @Test
    void excepcionInesperadaDevuelve500SinMensajeInterno() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Ocurrió un error interno"))
                .andExpect(jsonPath("$", not(hasKey("errors"))));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        void notFound() {
            throw new ResourceNotFoundException("El producto no existe");
        }

        @GetMapping("/test/no-resource")
        void noResource() throws NoResourceFoundException {
            throw new NoResourceFoundException(
                    HttpMethod.GET,
                    "/test/no-resource",
                    "/test/no-resource");
        }

        @PostMapping("/test/productos")
        void crearProducto(@Valid @RequestBody ProductoCreateDTO dto) {
        }

        @PostMapping("/test/compras")
        void crearCompra(@Valid @RequestBody CompraDTO dto) {
        }

        @PostMapping("/test/business")
        void business() {
            throw new BusinessRuleException("Stock insuficiente");
        }

        @PostMapping("/test/duplicate")
        void duplicate() {
            throw new DataIntegrityViolationException(
                    "Duplicate entry '1-1-F001-1' for key 'uq_compras_comprobante'");
        }

        @GetMapping("/test/unexpected")
        void unexpected() {
            throw new NullPointerException("detalle interno sensible");
        }
    }
}
