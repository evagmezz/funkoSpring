package dev.rest.pedido.services;

import dev.rest.funkos.models.Funko;
import dev.rest.funkos.repositories.FunkoRepository;
import dev.rest.pedido.exceptions.*;
import dev.rest.pedido.models.LineaPedido;
import dev.rest.pedido.models.Pedido;
import dev.rest.pedido.repositories.PedidoRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@CacheConfig(cacheNames = {"pedidos"})
public class PedidoServiceImpl implements PedidoService {
    private final PedidoRepository pedidoRepository;
    private final FunkoRepository funkoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository, FunkoRepository funkoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.funkoRepository = funkoRepository;
    }

    @Override
    public Page<Pedido> findAll(Pageable pageable) {
        log.info("Buscando todos los pedidos...");
        return pedidoRepository.findAll(pageable);
    }

    @Override
    @Cacheable(key = "#idPedido")
    public Pedido findById(ObjectId idPedido) {
        log.info("Buscando pedido por id: {}", idPedido);
        return pedidoRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toString()));
    }

    @Override
    public Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable) {
        log.info("Buscando pedidos por idUsuario: {}", idUsuario);
        return pedidoRepository.findByIdUsuario(idUsuario, pageable);
    }

    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public Pedido save(Pedido pedido) {
        log.info("Guardando pedido: {}", pedido);
        checkPedido(pedido);

        var pedidoToSave = reserveStockPedidos(pedido);

        pedidoToSave.setCreatedAt(LocalDateTime.now());
        pedidoToSave.setUpdatedAt(LocalDateTime.now());

        return pedidoRepository.save(pedidoToSave);
    }

    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public Pedido update(ObjectId idPedido, Pedido pedido) {
        log.info("Actualizando pedido por id: {}", idPedido);
        Pedido pedidoUpdate = pedidoRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toString()));
        returnStockPedidos(pedidoUpdate);
        checkPedido(pedido);
        var pedidoToSave = reserveStockPedidos(pedido);
        pedidoToSave.setUpdatedAt(LocalDateTime.now());
        return pedidoRepository.save(pedidoToSave);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#idPedido")
    public void delete(ObjectId idPedido) {
        log.info("Borrando pedido por id: {}", idPedido);
        var pedidoToDelete = pedidoRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toString()));
        returnStockPedidos(pedidoToDelete);
        pedidoRepository.deleteById(idPedido);
    }

    public Pedido returnStockPedidos(Pedido pedido) {
        log.info("Retornando stock del pedido: {}", pedido);
        if (pedido.getLineasPedido() != null) {
            pedido.getLineasPedido().forEach(lineaPedido -> {
                Funko funko = funkoRepository.findById(lineaPedido.getIdFunko()).get();

                funko.setCantidad(funko.getCantidad() + lineaPedido.getCantidad());

                funkoRepository.save(funko);
            });
        }
        return pedido;
    }

    public Pedido reserveStockPedidos(Pedido pedido) {
        log.info("Reservando stock del pedido: {}", pedido);

        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }
        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funko = funkoRepository.findById(lineaPedido.getIdFunko()).get();

            funko.setCantidad(funko.getCantidad() - lineaPedido.getCantidad());

            funkoRepository.save(funko);

            lineaPedido.setTotal(lineaPedido.getCantidad() * lineaPedido.getPrecioFunko());
        });
        var total = pedido.getLineasPedido().stream()
                .map(lineaPedido -> lineaPedido.getCantidad() * lineaPedido.getPrecioFunko())
                .reduce(0.0, Double::sum);

        var totalItems = pedido.getLineasPedido().stream()
                .map(LineaPedido::getCantidad)
                .reduce(0, Integer::sum);

        pedido.setTotal(total);
        pedido.setTotalItems(totalItems);

        return pedido;
    }


    public void checkPedido(Pedido pedido) {
        log.info("Comprobando pedido: {}", pedido);
        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }
        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funko = funkoRepository.findById(lineaPedido.getIdFunko())
                    .orElseThrow(() -> new FunkoNotFound(lineaPedido.getIdFunko()));

            if (funko.getCantidad() < lineaPedido.getCantidad() && lineaPedido.getCantidad() > 0) {
                throw new FunkoNotStock(lineaPedido.getIdFunko());
            }
            if (!funko.getPrecio().equals(lineaPedido.getPrecioFunko())) {
                throw new FunkoBadPrice(lineaPedido.getIdFunko());
            }
        });
    }
}
