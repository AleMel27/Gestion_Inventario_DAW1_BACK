package com.gestionInventario.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gestionInventario.dtos.request.MovimientoInventarioCreateDTO;
import com.gestionInventario.dtos.response.MovimientoInventarioResDTO;
import com.gestionInventario.exception.BusinessRuleException;
import com.gestionInventario.mapper.MovimientoInventarioMapper;
import com.gestionInventario.model.Almacen;
import com.gestionInventario.model.Inventario;
import com.gestionInventario.model.MovimientoInventario;
import com.gestionInventario.model.Producto;
import com.gestionInventario.model.TipoMovimiento;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IAlmacenRepository;
import com.gestionInventario.repository.ICompraRepository;
import com.gestionInventario.repository.IInventarioRepository;
import com.gestionInventario.repository.IMovimientoInventarioRepository;
import com.gestionInventario.repository.IProductoRepository;
import com.gestionInventario.repository.ITipoMovimientoRepository;
import com.gestionInventario.repository.IUsuarioRepository;

import jakarta.persistence.LockModeType;

@ExtendWith(MockitoExtension.class)
class MovimientoInventarioServiceTest {

    @Mock
    private IMovimientoInventarioRepository movimientoRepo;
    @Mock
    private IInventarioRepository inventarioRepo;
    @Mock
    private IProductoRepository productoRepo;
    @Mock
    private IAlmacenRepository almacenRepo;
    @Mock
    private IUsuarioRepository usuarioRepo;
    @Mock
    private ITipoMovimientoRepository tipoMovimientoRepo;
    @Mock
    private ICompraRepository compraRepo;

    private MovimientoInventarioService service;
    private Producto producto;
    private Almacen almacen;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new MovimientoInventarioService(
                movimientoRepo,
                inventarioRepo,
                productoRepo,
                almacenRepo,
                usuarioRepo,
                tipoMovimientoRepo,
                compraRepo,
                new MovimientoInventarioMapper());

        producto = new Producto();
        producto.setIdProducto(1L);

        almacen = new Almacen();
        almacen.setIdAlmacen(1L);

        usuario = new Usuario();
        usuario.setIdUsuario(1L);

    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void consultaDeInventarioParaModificarUsaBloqueoPesimista() throws Exception {
        Method method = IInventarioRepository.class.getMethod(
                "findByProductoAndAlmacenForUpdate",
                Long.class,
                Long.class);

        Lock lock = method.getAnnotation(Lock.class);

        assertEquals(LockModeType.PESSIMISTIC_WRITE, lock.value());
    }

    @Test
    void salidaSinInventarioNoCreaFilaEnCero() {
        TipoMovimiento salida = tipoMovimiento(2, "SALIDA", (short) -1);
        stubEntidadesBase();
        when(tipoMovimientoRepo.findById(2)).thenReturn(Optional.of(salida));
        when(inventarioRepo.findByProductoAndAlmacenForUpdate(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class, () -> service.registrarMovimiento(
                1L,
                1L,
                1L,
                2,
                new BigDecimal("5"),
                "SALIDA",
                null,
                null));

        verify(inventarioRepo, never()).insertarSiNoExiste(1L, 1L);
        verify(inventarioRepo, never()).save(any(Inventario.class));
        verify(movimientoRepo, never()).save(any(MovimientoInventario.class));
    }

    @Test
    void salidaConcurrenteSecuenciadaValidaContraStockActualizado() {
        TipoMovimiento salida = tipoMovimiento(2, "SALIDA", (short) -1);
        Inventario inventario = inventario(new BigDecimal("10"));

        stubEntidadesBaseConMovimiento();
        when(tipoMovimientoRepo.findById(2)).thenReturn(Optional.of(salida));
        when(inventarioRepo.findByProductoAndAlmacenForUpdate(1L, 1L)).thenReturn(Optional.of(inventario));

        MovimientoInventario primero = service.registrarMovimiento(
                1L,
                1L,
                1L,
                2,
                new BigDecimal("7"),
                "SALIDA",
                null,
                null);

        assertEquals(new BigDecimal("10"), primero.getStockAnterior());
        assertEquals(new BigDecimal("3"), primero.getStockPosterior());
        assertEquals(new BigDecimal("3"), inventario.getStockActual());

        assertThrows(RuntimeException.class, () -> service.registrarMovimiento(
                1L,
                1L,
                1L,
                2,
                new BigDecimal("6"),
                "SALIDA",
                null,
                null));

        assertEquals(new BigDecimal("3"), inventario.getStockActual());
    }

    @Test
    void entradasSobreInventarioInicialAcumulanStockYMovimientosSecuenciales() {
        TipoMovimiento entrada = tipoMovimiento(1, "ENTRADA", (short) 1);
        Inventario inventario = inventario(BigDecimal.ZERO);

        stubEntidadesBaseConMovimiento();
        when(tipoMovimientoRepo.findById(1)).thenReturn(Optional.of(entrada));
        when(inventarioRepo.findByProductoAndAlmacenForUpdate(1L, 1L)).thenReturn(Optional.of(inventario));

        MovimientoInventario primero = service.registrarMovimiento(
                1L,
                1L,
                1L,
                1,
                new BigDecimal("10"),
                "ENTRADA",
                null,
                null);

        MovimientoInventario segundo = service.registrarMovimiento(
                1L,
                1L,
                1L,
                1,
                new BigDecimal("5"),
                "ENTRADA",
                null,
                null);

        assertEquals(BigDecimal.ZERO, primero.getStockAnterior());
        assertEquals(new BigDecimal("10"), primero.getStockPosterior());
        assertEquals(new BigDecimal("10"), segundo.getStockAnterior());
        assertEquals(new BigDecimal("15"), segundo.getStockPosterior());
        assertEquals(new BigDecimal("15"), inventario.getStockActual());

        verify(inventarioRepo, org.mockito.Mockito.times(2)).insertarSiNoExiste(1L, 1L);
    }

    @Test
    void entradasSimultaneasConceptualesSobreFilaInexistenteUsanInsercionIdempotenteYAcumulan() {
        TipoMovimiento entrada = tipoMovimiento(1, "ENTRADA", (short) 1);
        Inventario inventario = inventario(BigDecimal.ZERO);

        stubEntidadesBaseConMovimiento();
        when(tipoMovimientoRepo.findById(1)).thenReturn(Optional.of(entrada));
        when(inventarioRepo.findByProductoAndAlmacenForUpdate(1L, 1L)).thenReturn(Optional.of(inventario));

        service.registrarMovimiento(1L, 1L, 1L, 1, new BigDecimal("10"), "ENTRADA", null, null);
        service.registrarMovimiento(1L, 1L, 1L, 1, new BigDecimal("5"), "ENTRADA", null, null);

        assertEquals(new BigDecimal("15"), inventario.getStockActual());
        verify(inventarioRepo, org.mockito.Mockito.times(2)).insertarSiNoExiste(1L, 1L);

        ArgumentCaptor<MovimientoInventario> captor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepo, org.mockito.Mockito.times(2)).save(captor.capture());

        assertEquals(BigDecimal.ZERO, captor.getAllValues().get(0).getStockAnterior());
        assertEquals(new BigDecimal("10"), captor.getAllValues().get(0).getStockPosterior());
        assertEquals(new BigDecimal("10"), captor.getAllValues().get(1).getStockAnterior());
        assertEquals(new BigDecimal("15"), captor.getAllValues().get(1).getStockPosterior());
    }

    @Test
    void historialProductoAlmacenDevuelveMovimientosMapeados() {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setIdMovimiento(10L);
        movimiento.setProducto(producto);
        movimiento.setAlmacen(almacen);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipoMovimiento(1, "ENTRADA", (short) 1));
        movimiento.setCantidad(new BigDecimal("10"));
        movimiento.setStockAnterior(BigDecimal.ZERO);
        movimiento.setStockPosterior(new BigDecimal("10"));
        movimiento.setMotivo("Ingreso");

        when(movimientoRepo.findByProducto_IdProductoAndAlmacen_IdAlmacen(1L, 1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(movimiento)));

        List<MovimientoInventarioResDTO> items = service
                .listarHistorialProductoAlmacen(1L, 1L, PageRequest.of(0, 10))
                .getContent();

        assertEquals(1, items.size());
        assertEquals(10L, items.get(0).getIdMovimientoInventario());
        assertEquals("ENTRADA", items.get(0).getTipoMovimiento().getCodigo());
    }

    @Test
    void registrarManualTomaUsuarioAutenticado() {
        TipoMovimiento entrada = tipoMovimiento(1, "ENTRADA", (short) 1);
        Inventario inventario = inventario(BigDecimal.ZERO);
        usuario.setCorreo("admin@licores.com");
        autenticar("admin@licores.com", "ROLE_ADMINISTRADOR");

        when(usuarioRepo.findByCorreo("admin@licores.com")).thenReturn(Optional.of(usuario));
        when(tipoMovimientoRepo.findById(1)).thenReturn(Optional.of(entrada));
        when(productoRepo.findById(1L)).thenReturn(Optional.of(producto));
        when(almacenRepo.findById(1L)).thenReturn(Optional.of(almacen));
        when(inventarioRepo.findByProductoAndAlmacenForUpdate(1L, 1L)).thenReturn(Optional.of(inventario));
        when(movimientoRepo.save(any(MovimientoInventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MovimientoInventarioCreateDTO dto = movimientoManual(1);

        MovimientoInventarioResDTO response = service.registrarManual(dto);

        assertEquals(1L, response.getUsuario().getIdUsuario());
        assertEquals(new BigDecimal("5"), response.getCantidad());
        verify(usuarioRepo).findByCorreo("admin@licores.com");
    }

    @Test
    void almaceneroNoPuedeRegistrarAjusteEntradaManual() {
        TipoMovimiento ajuste = tipoMovimiento(3, "AJUSTE_ENTRADA", (short) 1);
        usuario.setCorreo("almacen@licores.com");
        autenticar("almacen@licores.com", "ROLE_ALMACENERO");

        when(usuarioRepo.findByCorreo("almacen@licores.com")).thenReturn(Optional.of(usuario));
        when(tipoMovimientoRepo.findById(3)).thenReturn(Optional.of(ajuste));

        assertThrows(AccessDeniedException.class, () -> service.registrarManual(movimientoManual(3)));
        verify(movimientoRepo, never()).save(any(MovimientoInventario.class));
    }

    @Test
    void administradorPuedeRegistrarAjusteEntradaManual() {
        TipoMovimiento ajuste = tipoMovimiento(3, "AJUSTE_ENTRADA", (short) 1);
        Inventario inventario = inventario(BigDecimal.ZERO);
        usuario.setCorreo("admin@licores.com");
        autenticar("admin@licores.com", "ROLE_ADMINISTRADOR");

        when(usuarioRepo.findByCorreo("admin@licores.com")).thenReturn(Optional.of(usuario));
        when(tipoMovimientoRepo.findById(3)).thenReturn(Optional.of(ajuste));
        when(productoRepo.findById(1L)).thenReturn(Optional.of(producto));
        when(almacenRepo.findById(1L)).thenReturn(Optional.of(almacen));
        when(inventarioRepo.findByProductoAndAlmacenForUpdate(1L, 1L)).thenReturn(Optional.of(inventario));
        when(movimientoRepo.save(any(MovimientoInventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MovimientoInventarioResDTO response = service.registrarManual(movimientoManual(3));

        assertEquals("AJUSTE_ENTRADA", response.getTipoMovimiento().getCodigo());
    }

    private void stubEntidadesBase() {
        when(productoRepo.findById(1L)).thenReturn(Optional.of(producto));
        when(almacenRepo.findById(1L)).thenReturn(Optional.of(almacen));
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
    }

    private void stubEntidadesBaseConMovimiento() {
        stubEntidadesBase();
        when(movimientoRepo.save(any(MovimientoInventario.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private TipoMovimiento tipoMovimiento(Integer id, String codigo, short signo) {
        TipoMovimiento tipo = new TipoMovimiento();
        tipo.setIdTipoMovimiento(id);
        tipo.setCodigo(codigo);
        tipo.setNombre(codigo);
        tipo.setSignoStock(signo);
        tipo.setEstado(true);
        return tipo;
    }

    private Inventario inventario(BigDecimal stock) {
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setAlmacen(almacen);
        inventario.setStockActual(stock);
        return inventario;
    }

    private MovimientoInventarioCreateDTO movimientoManual(Integer idTipoMovimiento) {
        MovimientoInventarioCreateDTO dto = new MovimientoInventarioCreateDTO();
        dto.setIdProducto(1L);
        dto.setIdAlmacen(1L);
        dto.setIdTipoMovimiento(idTipoMovimiento);
        dto.setCantidad(new BigDecimal("5"));
        dto.setMotivo("Movimiento manual");
        dto.setReferencia("MAN-001");
        return dto;
    }

    private void autenticar(String correo, String authority) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                correo,
                null,
                List.of(new SimpleGrantedAuthority(authority)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
