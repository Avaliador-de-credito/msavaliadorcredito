package io.github.karinaerikads.msavaliadorcredito.application;

import io.github.karinaerikads.msavaliadorcredito.domain.model.CartaoCliente;
import io.github.karinaerikads.msavaliadorcredito.domain.model.DadosCliente;
import io.github.karinaerikads.msavaliadorcredito.domain.model.SituacaoCliente;
import io.github.karinaerikads.msavaliadorcredito.infra.clients.CartoesResourceClient;
import io.github.karinaerikads.msavaliadorcredito.infra.clients.ClienteResourceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clienteResourceClient;
    private final CartoesResourceClient cartoesResourceClient;
    public SituacaoCliente obterSituacaoCliente(String cpf){
        ResponseEntity<DadosCliente> dadosClientesResponse = clienteResourceClient.dadosClientes(cpf);
        ResponseEntity<List<CartaoCliente>> cartaoResponse = cartoesResourceClient.getCartoesByCliente(cpf);
        return SituacaoCliente
                .builder()
                .dadosCliente(dadosClientesResponse.getBody())
                .cartoes(cartaoResponse.getBody())
                .build();
    }
}
