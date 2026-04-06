package com.pe.prueba_facturacion.service;

import com.pe.prueba_facturacion.dto.sunat.MiapiResponseDTO;
import com.pe.prueba_facturacion.model.*;
import com.pe.prueba_facturacion.model.enums.EstadoSunat;
import com.pe.prueba_facturacion.repository.ComprobanteElectronicoRepository;
import com.pe.prueba_facturacion.repository.FacturacionConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SunatService {

    private final ComprobanteElectronicoRepository comprobanteRepository;
    private final FacturacionConfiguracionRepository configuracionRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public MiapiResponseDTO enviarASunat(Long comprobanteId) throws Exception {

        ComprobanteElectronico comp = comprobanteRepository.findById(comprobanteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Comprobante no encontrado: " + comprobanteId));

        if (comp.getEstadoSunat() == EstadoSunat.ACEPTADO) {
            throw new IllegalStateException("El comprobante ya fue aceptado por SUNAT.");
        }
        if (comp.getEstadoSunat() == EstadoSunat.SIN_EMITIR) {
            throw new IllegalStateException(
                    "El comprobante aún no fue emitido. Llama primero a /api/comprobantes/emitir");
        }

        FacturacionConfiguracion config = configuracionRepository
                .findByEstadoTrue()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay configuración de facturación activa."));

        // ── Construir payload ──
        Map<String, Object> comprobanteMap = new LinkedHashMap<>();
        comprobanteMap.put("tipoDoc", comp.getTipoComprobante().getCodigo());
        comprobanteMap.put("serie", comp.getSerie());
        comprobanteMap.put("correlativo", String.valueOf(Integer.parseInt(comp.getNumero())));

        Map<String, Object> payloadSunat = new LinkedHashMap<>();
        payloadSunat.put("claveSecreta", config.getClaveSecreta());
        payloadSunat.put("comprobante", comprobanteMap);

        String jsonSunat = objectMapper.writeValueAsString(payloadSunat);
        log.info("Payload /invoice/send:\n{}", jsonSunat);

        // ── Llamada HTTP ──
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getApiUrl() + "/apifact/invoice/send"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getApiToken())
                .POST(HttpRequest.BodyPublishers.ofString(jsonSunat))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());

        log.info("Respuesta /invoice/send [{}]: {}", response.statusCode(), response.body());

        // ── Parsear respuesta ──
        MiapiResponseDTO sunatResp = objectMapper.readValue(response.body(), MiapiResponseDTO.class);
        MiapiResponseDTO sunatData = sunatResp.unwrap();

        boolean aceptado = response.statusCode() == 200
                && sunatData != null
                && Boolean.TRUE.equals(sunatData.getSuccess());

        comp.setEstadoSunat(aceptado ? EstadoSunat.ACEPTADO : EstadoSunat.PENDIENTE);

        if (sunatData != null) {
            if (sunatData.getHashCpe() != null) {
                comp.setHashCpe(sunatData.getHashCpe());
            }
            if (sunatData.getCdrUrl() != null) {
                comp.setCdrUrl(sunatData.getCdrUrl());
            }
        }

        comprobanteRepository.save(comp);

        return sunatData;
    }

}
