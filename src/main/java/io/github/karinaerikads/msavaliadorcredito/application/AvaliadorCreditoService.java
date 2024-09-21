package io.github.karinaerikads.msavaliadorcredito.application;

import feign.FeignException;
import io.github.karinaerikads.msavaliadorcredito.application.exception.DadosClienteNotFoundException;
import io.github.karinaerikads.msavaliadorcredito.application.exception.ErroComunicacaoMicroserviceException;
import io.github.karinaerikads.msavaliadorcredito.domain.model.*;
import io.github.karinaerikads.msavaliadorcredito.infra.clients.CartoesResourceClient;
import io.github.karinaerikads.msavaliadorcredito.infra.clients.ClienteResourceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClienteNotFoundException, ErroComunicacaoMicroserviceException{
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteResourceClient.dadosClientes(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesResourceClient.getCartoesRendaAte(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            var listCartoesAprovados = cartoes.stream().map(cartao -> {
                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idade = BigDecimal.valueOf(dadosCliente.getIdade());
                var fator = idade.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado cartaoAprovado = new CartaoAprovado();
                cartaoAprovado.setCartao(cartao.getNome());
                cartaoAprovado.setBandeira(cartao.getBandeira());
                cartaoAprovado.setLimiteAprovado(limiteAprovado);

                return cartaoAprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listCartoesAprovados);

        }catch (FeignException.FeignClientException e){
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroserviceException(e.getMessage(), status);
        }
    }
}
