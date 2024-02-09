package io.github.karinaerikads.msavaliadorcredito.application;

import io.github.karinaerikads.msavaliadorcredito.domain.model.DadosCliente;
import io.github.karinaerikads.msavaliadorcredito.domain.model.SituacaoCliente;
import io.github.karinaerikads.msavaliadorcredito.infra.clients.ClienteResourceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clienteResourceClient;
    public SituacaoCliente obterSituacaoCliente(String cpf){
        ResponseEntity<DadosCliente> dadosClientesResponse = clienteResourceClient.dadosClientes(cpf);
        return SituacaoCliente
                .builder()
                .dadosCliente(dadosClientesResponse.getBody())
                .build();
    }
}
