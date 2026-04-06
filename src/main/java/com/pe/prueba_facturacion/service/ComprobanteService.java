package com.pe.prueba_facturacion.service;

import com.pe.prueba_facturacion.dto.facturacion.ComprobanteResponseDTO;
import com.pe.prueba_facturacion.dto.facturacion.EmitirComprobanteDTO;
import com.pe.prueba_facturacion.dto.sunat.DocumentosGeneradosDTO;
import com.pe.prueba_facturacion.model.*;
import com.pe.prueba_facturacion.model.enums.EstadoSunat;
import com.pe.prueba_facturacion.repository.ComprobanteElectronicoRepository;
import com.pe.prueba_facturacion.repository.FacturacionConfiguracionRepository;
import com.pe.prueba_facturacion.repository.TipoComprobanteRepository;
import com.pe.prueba_facturacion.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprobanteService {

    private final ComprobanteElectronicoRepository comprobanteRepository;
    private final VentaRepository ventaRepository;
    private final TipoComprobanteRepository tipoComprobanteRepository;
    private final FacturacionConfiguracionRepository configuracionRepository;
    private final ObjectMapper objectMapper;

    private static final String SERIE_FACTURA = "F001";
    private static final String SERIE_BOLETA  = "B001";

    @Transactional
    public ComprobanteResponseDTO emitirComprobante(EmitirComprobanteDTO dto) {

        Venta venta = ventaRepository.findById(dto.getVentaId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Venta no encontrada: " + dto.getVentaId()));

        ComprobanteElectronico comprobante =
                comprobanteRepository.findByVenta(venta)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No existe comprobante para la venta: " + dto.getVentaId()));

        if (comprobante.getEstadoSunat() != EstadoSunat.SIN_EMITIR) {
            throw new IllegalStateException(
                    "El comprobante ya fue emitido. Estado actual: " +
                            comprobante.getEstadoSunat());
        }

        String codigoTipoDoc = venta.getCliente().getTipoDocumento().getCodigo();
        validarTipoComprobante(codigoTipoDoc, dto.getTipoComprobante());

        TipoComprobante tipoComprobante = tipoComprobanteRepository
                .findByCodigo(dto.getTipoComprobante())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de comprobante no encontrado: " + dto.getTipoComprobante()));

        String serie = dto.getTipoComprobante().equals("01") ? SERIE_FACTURA : SERIE_BOLETA;
        String numero = generarCorrelativo(serie);

        comprobante.setTipoComprobante(tipoComprobante);
        comprobante.setSerie(serie);
        comprobante.setNumero(numero);
        comprobante.setNumeroCompleto(serie + "-" + numero);

        FacturacionConfiguracion config = configuracionRepository
                .findByEstadoTrue()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay configuración de facturación activa."));

        try {
            generarDocumentosMiapi(comprobante, config);
            comprobante.setEstadoSunat(EstadoSunat.SIN_ENVIAR);
            log.info("Documentos generados para comprobante: {}", comprobante.getNumeroCompleto());
        } catch (Exception e) {
            comprobante.setEstadoSunat(EstadoSunat.SIN_ENVIAR);
            log.warn("No se pudo generar documentos en miapi.cloud: {}", e.getMessage());
        }

        ComprobanteElectronico guardado = comprobanteRepository.save(comprobante);
        log.info("Comprobante emitido: {} — Venta {}",
                guardado.getNumeroCompleto(), venta.getId());

        return new ComprobanteResponseDTO(guardado);
    }

    private void generarDocumentosMiapi(
            ComprobanteElectronico comprobante,
            FacturacionConfiguracion config) throws Exception {

        Map<String, Object> payload = construirPayload(comprobante, config);
        String jsonPayload = objectMapper.writeValueAsString(payload);
        log.info("Payload /invoice/create:\n{}", jsonPayload);

        HttpClient httpClient = HttpClient.newHttpClient();
        String endpoint = config.getApiUrl() + "/apifact/invoice/create";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getApiToken())
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());

        log.info("Respuesta /invoice/create [{}]: {}",
                response.statusCode(), response.body());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new IllegalStateException(
                    "Error miapi.cloud HTTP " + response.statusCode());
        }

        Map<String, Object> raw = objectMapper.readValue(response.body(), Map.class);

        if (raw.containsKey("respuesta")) {
            raw = (Map<String, Object>) raw.get("respuesta");
        }

        DocumentosGeneradosDTO docs = new DocumentosGeneradosDTO();
        docs.setXmlSinFirmar((String) raw.get("xml-sin-firmar"));
        docs.setXmlFirmado((String) raw.get("xml-firmado"));
        docs.setPdfA4Url((String) raw.get("pdf-a4"));
        docs.setPdfTicketUrl((String) raw.get("pdf-ticket"));

        comprobante.setXmlSinFirmar(docs.getXmlSinFirmar());
        comprobante.setXmlFirmado(docs.getXmlFirmado());
        comprobante.setPdfA4Url(docs.getPdfA4Url());
        comprobante.setPdfTicketUrl(docs.getPdfTicketUrl());
    }

    private Map<String, Object> construirPayload(
            ComprobanteElectronico comprobante,
            FacturacionConfiguracion config) {

        Venta venta = comprobante.getVenta();
        Cliente cliente = venta.getCliente();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("claveSecreta", config.getClaveSecreta());

        Map<String, Object> comp = new LinkedHashMap<>();
        comp.put("tipoOperacion", venta.getTipoOperacion());
        comp.put("tipoDoc", comprobante.getTipoComprobante().getCodigo());
        comp.put("serie", comprobante.getSerie());
        comp.put("correlativo", String.valueOf(Integer.parseInt(comprobante.getNumero())));
        comp.put("observacion", "");
        comp.put("fechaEmision", venta.getFechaEmision().toString());
        comp.put("horaEmision", venta.getHoraEmision()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        comp.put("tipoMoneda", venta.getTipoMoneda());
        comp.put("tipoPago", venta.getTipoPago());
        comp.put("total", venta.getMtoTotal());
        comp.put("mtoIGV", venta.getMtoIgv());
        comp.put("igvOp", 0);
        comp.put("mtoOperGravadas", venta.getMtoOpGravadas());
        comp.put("totalTexto", generarTotalTexto(venta.getMtoTotal()));
        payload.put("comprobante", comp);

        String codDoc = cliente.getTipoDocumento().getCodigo();
        String tipoDocCliente = String.valueOf(Integer.parseInt(codDoc));

        Map<String, Object> cli = new LinkedHashMap<>();
        cli.put("codigoPais", cliente.getCodigoPais() != null ? cliente.getCodigoPais() : "PE");
        cli.put("tipoDoc", tipoDocCliente);
        cli.put("numDoc", cliente.getNumeroDocumento());
        cli.put("rznSocial", cliente.getRazonSocial());
        cli.put("direccion", cliente.getDireccionFiscal() != null
                ? cliente.getDireccionFiscal() : "----");
        payload.put("cliente", cli);

        List<Map<String, Object>> items = new ArrayList<>();

        for (DetalleVenta d : venta.getDetalles()) {

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("codProducto", "PR" + String.format("%03d", d.getProducto().getId()));
            item.put("descripcion", d.getProducto().getDescripcion());
            item.put("unidad", "NIU");
            item.put("tipoPrecio", "01");
            item.put("cantidad", d.getCantidad());
            item.put("mtoBaseIgv", d.getMtoBaseIgv());
            item.put("mtoValorUnitario", d.getMtoValorUnitario());
            item.put("mtoPrecioUnitario", d.getMtoPrecioUnitario());
            item.put("codeAfectAlt", 10);
            item.put("codeAfect", 1000);
            item.put("nameAfect", "IGV");
            item.put("tipoAfect", "VAT");
            item.put("igvPorcent", 18);
            item.put("igv", d.getIgv());
            item.put("igvOpi", d.getIgv());
            items.add(item);
        }
        payload.put("items", items);

        return payload;
    }

    //METODOS DE VALIDACION
    private void validarTipoComprobante(String codigoTipoDoc, String tipoSolicitado) {
        boolean esRuc     = codigoTipoDoc.equals("06");
        boolean esBoleta  = tipoSolicitado.equals("03");
        boolean esFactura = tipoSolicitado.equals("01");

        if (!esBoleta && !esFactura) {
            throw new IllegalArgumentException(
                    "Tipo inválido: " + tipoSolicitado + ". Use '01' o '03'.");
        }
        if (!esRuc && esFactura) {
            throw new IllegalArgumentException(
                    "Solo se puede emitir Factura a clientes con RUC.");
        }
    }

    private String generarCorrelativo(String serie) {
        return comprobanteRepository
                .findTopBySerieOrderByNumeroDesc(serie)
                .map(ultimo -> {
                    int sig = Integer.parseInt(ultimo.getNumero()) + 1;
                    return String.format("%08d", sig);
                })
                .orElse("00000001");
    }

    private String generarTotalTexto(BigDecimal monto) {
        long entero = monto.longValue();
        int centavos = monto.remainder(BigDecimal.ONE)
                .multiply(new BigDecimal("100"))
                .abs()
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
        return "SON " + entero + " CON " + String.format("%02d", centavos) + "/100 SOLES";
    }

}
