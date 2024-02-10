package io.github.karinaerikads.msavaliadorcredito.application;

import feign.FeignException;
import io.github.karinaerikads.msavaliadorcredito.application.exception.DadosClienteNotFoundException;
import io.github.karinaerikads.msavaliadorcredito.application.exception.ErroComunicacaoMicroserviceException;
import io.github.karinaerikads.msavaliadorcredito.domain.model.CartaoCliente;
import io.github.karinaerikads.msavaliadorcredito.domain.model.DadosCliente;
import io.github.karinaerikads.msavaliadorcredito.domain.model.SituacaoCliente;
import io.github.karinaerikads.msavaliadorcredito.infra.clients.CartoesResourceClient;
import io.github.karinaerikads.msavaliadorcredito.infra.clients.ClienteResourceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clienteResourceClient;
    private final CartoesResourceClient cartoesResourceClient;
    public SituacaoCliente obterSituacaoCliente(String cpf)
            throws DadosClienteNotFoundException, ErroComunicacaoMicroserviceException{
        try {


            ResponseEntity<DadosCliente> dadosClientesResponse = clienteResourceClient.dadosClientes(cpf);
            ResponseEntity<List<CartaoCliente>> cartaoResponse = cartoesResourceClient.getCartoesByCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .dadosCliente(dadosClientesResponse.getBody())
                    .cartoes(cartaoResponse.getBody())
                    .build();
        }catch (FeignException.FeignClientException e){
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroserviceException(e.getMessage(), status);
        }
    }
}
