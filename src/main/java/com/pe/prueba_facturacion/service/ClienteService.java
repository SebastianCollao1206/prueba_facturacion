package com.pe.prueba_facturacion.service;

import com.pe.prueba_facturacion.dto.cliente.CrearCliente;
import com.pe.prueba_facturacion.model.Cliente;
import com.pe.prueba_facturacion.model.TipoDocumento;
import com.pe.prueba_facturacion.repository.ClienteRepository;
import com.pe.prueba_facturacion.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public Cliente obtenerOCrearCliente(CrearCliente dto) {

        String[] docInfo = resolverDocumento(dto);
        String codigoTipoDoc = docInfo[0];
        String numeroDocumento = docInfo[1];

        if (numeroDocumento != null) {
            Optional<Cliente> clienteExistente = clienteRepository.findByNumeroDocumento(numeroDocumento);
            if (clienteExistente.isPresent()) {
                return clienteExistente.get();
            }
        }

        TipoDocumento tipoDocumento = tipoDocumentoRepository.findByCodigo(codigoTipoDoc)
                .orElseThrow(() -> new RuntimeException(
                        "Tipo de documento no encontrado con código: " + codigoTipoDoc
                ));

        Cliente nuevoCliente = new Cliente(
                tipoDocumento,
                dto.getRazonSocial(),
                numeroDocumento,
                dto.getDireccionFiscal(),
                dto.getCodigoPais() != null ? dto.getCodigoPais() : "PE"
        );

        return clienteRepository.save(nuevoCliente);
    }

    private String[] resolverDocumento(CrearCliente dto) {
        if (dto.getRuc() != null && dto.getRuc().length() == 11) {
            return new String[]{"06", dto.getRuc()};
        }
        if (dto.getDni() != null && dto.getDni().length() == 8) {
            return new String[]{"01", dto.getDni()};
        }
        if (dto.getCarneExtranjeria() != null) {
            return new String[]{"04", dto.getCarneExtranjeria()};
        }
        if (dto.getPasaporte() != null) {
            return new String[]{"07", dto.getPasaporte()};
        }
        return new String[]{"00", null};
    }

}
