package dev.pedidos.services;

import dev.rest.funkos.models.Funko;
import dev.rest.funkos.repositories.FunkoRepository;
import dev.rest.pedido.exceptions.*;
import dev.rest.pedido.models.LineaPedido;
import dev.rest.pedido.models.Pedido;
import dev.rest.pedido.repositories.PedidoRepository;
import dev.rest.pedido.services.PedidoServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidosRepository;
    @Mock
    private FunkoRepository funkoRepository;

    @InjectMocks
    private PedidoServiceImpl pedidosService;

    @Test
    void findAll() {
        List<Pedido> pedidos = List.of(new Pedido(), new Pedido());
        Page<Pedido> expectedPage = new PageImpl<>(pedidos);
        Pageable pageable = PageRequest.of(0, 10);
        when(pedidosRepository.findAll(pageable)).thenReturn(expectedPage);
        Page<Pedido> result = pedidosService.findAll(pageable);

        assertAll(
                () -> assertEquals(expectedPage, result),
                () -> assertEquals(expectedPage.getContent(), result.getContent()),
                () -> assertEquals(expectedPage.getTotalElements(), result.getTotalElements()));

        verify(pedidosRepository, times(1)).findAll(pageable);
    }

    @Test
    void findById() {
        ObjectId idPedido = new ObjectId();
        Pedido pedidoEsperado = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoEsperado));
        Pedido resultPedido = pedidosService.findById(idPedido);
        assertEquals(pedidoEsperado, resultPedido);
        verify(pedidosRepository).findById(idPedido);
    }

    @Test
    void findByIdNotFound() {
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFound.class, () -> pedidosService.findById(idPedido));
        verify(pedidosRepository).findById(idPedido);
    }

    @Test
    void findByIdUsuario() {
        Long idUsuario = 1L;
        Pageable pageable = mock(Pageable.class);
        @SuppressWarnings("unchecked") Page<Pedido> expectedPage = mock(Page.class);
        when(pedidosRepository.findByIdUsuario(idUsuario, pageable)).thenReturn(expectedPage);
        Page<Pedido> resultPage = pedidosService.findByIdUsuario(idUsuario, pageable);
        assertEquals(expectedPage, resultPage);
        verify(pedidosRepository).findByIdUsuario(idUsuario, pageable);
    }

    @Test
    void save() {
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(10.0)
                .cantidad(5)
                .rutaImagen("ruta1")
                .categoria(null)
                .build();

        Pedido pedido = new Pedido();
        LineaPedido lineaPedido = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(2)
                .precioFunko(10.0)
                .total(20.0)
                .build();

        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToSave = new Pedido();
        pedidoToSave.setLineasPedido(List.of(lineaPedido));

        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoToSave);
        when(funkoRepository.findById(anyLong())).thenReturn(Optional.of(funko));
        Pedido pedidoGuardado = pedidosService.save(pedido);
        assertAll(
                () -> assertEquals(pedidoToSave, pedidoGuardado),
                () -> assertEquals(pedidoToSave.getLineasPedido(), pedidoGuardado.getLineasPedido()),
                () -> assertEquals(pedidoToSave.getLineasPedido().size(), pedidoGuardado.getLineasPedido().size()));

        verify(pedidosRepository).save(any(Pedido.class));
        verify(funkoRepository, times(2)).findById(anyLong());
    }

    @Test
    void saveNotFound() {
        Pedido pedido = new Pedido();
        assertThrows(PedidoNotItems.class, () -> pedidosService.save(pedido));
        verify(pedidosRepository, never()).save(any(Pedido.class));
        verify(funkoRepository, never()).findById(anyLong());
    }

    @Test
    void delete() {
        ObjectId idPedido = new ObjectId();
        Pedido pedidoToDelete = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToDelete));
        pedidosService.delete(idPedido);
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository).delete(pedidoToDelete);
    }

    @Test
    void deleteNotFound() {
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFound.class, () -> pedidosService.delete(idPedido));
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository, never()).deleteById(idPedido);
    }

    @Test
    void update() {
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(10.0)
                .cantidad(5)
                .rutaImagen("ruta1")
                .categoria(null)
                .build();


        LineaPedido lineaPedido = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(2)
                .precioFunko(10.0)
                .total(20.0)
                .build();

        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido updatePedido = new Pedido();
        updatePedido.setLineasPedido(List.of(lineaPedido));

        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(updatePedido));
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(updatePedido);
        when(funkoRepository.findById(anyLong())).thenReturn(Optional.of(funko));
        Pedido pedidoEsperado = pedidosService.update(idPedido, pedido);
        assertAll(
                () -> assertEquals(updatePedido, pedidoEsperado),
                () -> assertEquals(updatePedido.getLineasPedido(), pedidoEsperado.getLineasPedido()),
                () -> assertEquals(updatePedido.getLineasPedido().size(), pedidoEsperado.getLineasPedido().size()));
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository).save(any(Pedido.class));
        verify(funkoRepository, times(3)).findById(anyLong());
    }

    @Test
    void updateNotFound() {
        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFound.class, () -> pedidosService.update(idPedido, pedido));
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository, never()).save(any(Pedido.class));
        verify(funkoRepository, never()).findById(anyLong());
    }

    @Test
    void reserveStockPedidos() throws PedidoNotFound {
        Pedido pedido = new Pedido();
        List<LineaPedido> lineaPedidos = new ArrayList<>();
        LineaPedido lineaPedido = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(2)
                .precioFunko(10.0)
                .total(20.0)
                .build();

        lineaPedidos.add(lineaPedido);

        pedido.setLineasPedido(lineaPedidos);

        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(10.0)
                .cantidad(5)
                .rutaImagen("ruta1")
                .categoria(null)
                .build();

        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funko));


        Pedido result = pedidosService.reserveStockPedidos(pedido);


        assertAll(() -> assertEquals(3, funko.getCantidad()),
                () -> assertEquals(20.0, lineaPedido.getTotal()),
                () -> assertEquals(20.0, result.getTotal()),
                () -> assertEquals(2, result.getTotalItems())
        );

        verify(funkoRepository, times(1)).findById(1L);
        verify(funkoRepository, times(1)).save(funko);
    }

    @Test
    void returnStockPedidoWithUpdatedStock() {

        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(2)
                .precioFunko(10.0)
                .total(20.0)
                .build();

        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);

        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(10.0)
                .cantidad(5)
                .rutaImagen("ruta1")
                .categoria(null)
                .build();

        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funko));
        when(funkoRepository.save(funko)).thenReturn(funko);

        Pedido result = pedidosService.returnStockPedidos(pedido);

        assertEquals(7, funko.getCantidad());
        assertEquals(pedido, result);


        verify(funkoRepository, times(1)).findById(1L);
        verify(funkoRepository, times(1)).save(funko);
    }

    @Test
    void checkPedido() {

        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(2)
                .precioFunko(10.0)
                .total(20.0)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(10.0)
                .cantidad(5)
                .rutaImagen("ruta1")
                .categoria(null)
                .build();

        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funko));
        assertDoesNotThrow(() -> pedidosService.checkPedido(pedido));
        verify(funkoRepository, times(1)).findById(1L);
    }

    @Test
    void checkPedido_FunkoNoExiste_DebeLanzarFunkoNotFound() {

        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(2)
                .precioFunko(10.0)
                .total(20.0)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(FunkoNotFound.class, () -> pedidosService.checkPedido(pedido));
        verify(funkoRepository, times(1)).findById(1L);
    }

    @Test
    void checkPedidoFunkoNotStock() {

        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(10)
                .precioFunko(10.0)
                .total(100.0)
                .build();
        lineaPedido1.setIdFunko(1L);
        lineaPedido1.setCantidad(10);
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(10.0)
                .cantidad(5)
                .rutaImagen("ruta1")
                .categoria(null)
                .build();
        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funko));
        assertThrows(FunkoNotStock.class, () -> pedidosService.checkPedido(pedido));
        verify(funkoRepository, times(1)).findById(1L);
    }

    @Test
    void checkPedidoFunkoBadPrice() {

        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idFunko(1L)
                .cantidad(2)
                .precioFunko(10.0)
                .total(20.0)
                .build();
        lineaPedido1.setIdFunko(1L);
        lineaPedido1.setCantidad(2);
        lineaPedido1.setPrecioFunko(20.0);
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);

        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(10.0)
                .cantidad(5)
                .rutaImagen("ruta1")
                .categoria(null)
                .build();
        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funko));
        assertThrows(FunkoBadPrice.class, () -> pedidosService.checkPedido(pedido));
        verify(funkoRepository, times(1)).findById(1L);
    }

}
