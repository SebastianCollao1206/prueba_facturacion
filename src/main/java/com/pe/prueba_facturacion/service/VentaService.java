package com.pe.prueba_facturacion.service;

import com.pe.prueba_facturacion.dto.venta.CrearVentaDTO;
import com.pe.prueba_facturacion.dto.venta.DetalleVentaDTO;
import com.pe.prueba_facturacion.dto.venta.VentaResponseDTO;
import com.pe.prueba_facturacion.model.*;
import com.pe.prueba_facturacion.repository.ComprobanteElectronicoRepository;
import com.pe.prueba_facturacion.repository.DetalleVentaRepository;
import com.pe.prueba_facturacion.repository.ProductoRepository;
import com.pe.prueba_facturacion.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final ComprobanteElectronicoRepository comprobanteRepository;
    private final ClienteService clienteService;

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");
    private static final BigDecimal DIVISOR_IGV = new BigDecimal("1.18");

    @Transactional
    public VentaResponseDTO crearVenta(CrearVentaDTO dto) {

        Cliente cliente = clienteService.obtenerOCrearCliente(dto.getCliente());

        BigDecimal mtoOpGravadas = BigDecimal.ZERO;
        BigDecimal mtoIgv        = BigDecimal.ZERO;
        BigDecimal mtoTotal      = BigDecimal.ZERO;

        List<DetalleVentaItem> items = new ArrayList<>();

        for (DetalleVentaDTO detalleDTO : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado: " + detalleDTO.getProductoId()));

            if (!producto.getEstado())
                throw new IllegalArgumentException("Producto inactivo: " + producto.getDescripcion());

            Integer cantidad = detalleDTO.getCantidad();

            if (producto.getStock() < cantidad.intValue())
                throw new IllegalArgumentException(
                        "Stock insuficiente para: " + producto.getDescripcion() +
                                " (disponible: " + producto.getStock() + ")");

            BigDecimal mtoPrecioUnitario = producto.getPrecio()
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal mtoValorUnitario = mtoPrecioUnitario
                    .divide(DIVISOR_IGV, 4, RoundingMode.HALF_UP);

            BigDecimal mtoBaseIgv = mtoValorUnitario
                    .multiply(BigDecimal.valueOf(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal igvItem = mtoBaseIgv
                    .multiply(IGV_RATE)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal subtotal = mtoPrecioUnitario
                    .multiply(BigDecimal.valueOf(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);

            mtoOpGravadas = mtoOpGravadas.add(mtoBaseIgv);
            mtoIgv        = mtoIgv.add(igvItem);
            mtoTotal      = mtoTotal.add(subtotal);

            items.add(new DetalleVentaItem(producto, cantidad, mtoValorUnitario,
                    mtoPrecioUnitario, mtoBaseIgv, igvItem, subtotal));
        }

        Venta venta = new Venta(
                cliente,
                dto.getTipoPago(),
                mtoOpGravadas.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                mtoIgv.setScale(2, RoundingMode.HALF_UP),
                mtoTotal.setScale(2, RoundingMode.HALF_UP)
        );

        Venta ventaGuardada = ventaRepository.save(venta);

        for (DetalleVentaItem item : items) {
            detalleVentaRepository.save(new DetalleVenta(
                    ventaGuardada,
                    item.producto,
                    item.cantidad,
                    item.mtoValorUnitario,
                    item.mtoPrecioUnitario,
                    item.mtoBaseIgv,
                    item.igv,
                    item.subtotal
            ));

            item.producto.setStock(item.producto.getStock() - item.cantidad.intValue());
            productoRepository.save(item.producto);
            log.info("Stock descontado — producto {}: -{}", item.producto.getId(), item.cantidad);
        }

        ComprobanteElectronico comprobante = new ComprobanteElectronico();
        comprobante.setVenta(ventaGuardada);

        ComprobanteElectronico comprobanteGuardado = comprobanteRepository.save(comprobante);
        log.info("Venta {} creada. Comprobante {} — estado: {}",
                ventaGuardada.getId(), comprobanteGuardado.getId(), comprobanteGuardado.getEstadoSunat());

        return new VentaResponseDTO(ventaGuardada, comprobanteGuardado);
    }

    private record DetalleVentaItem(
            Producto producto,
            Integer cantidad,
            BigDecimal mtoValorUnitario,
            BigDecimal mtoPrecioUnitario,
            BigDecimal mtoBaseIgv,
            BigDecimal igv,
            BigDecimal subtotal) {}

}
