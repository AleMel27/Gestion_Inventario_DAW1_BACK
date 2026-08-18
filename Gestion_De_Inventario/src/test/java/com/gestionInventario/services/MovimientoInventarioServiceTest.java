package com.gestionInventario.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.Lock;

import com.gestionInventario.exception.BusinessRuleException;
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
                compraRepo);

        producto = new Producto();
        producto.setIdProducto(1L);

        almacen = new Almacen();
        almacen.setIdAlmacen(1L);

        usuario = new Usuario();
        usuario.setIdUsuario(1L);

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
}
