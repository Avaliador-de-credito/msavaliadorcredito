package io.github.karinaerikads.msavaliadorcredito.application.exception;

public class DadosClienteNotFoundException extends Exception{
    public DadosClienteNotFoundException() {
        super("Dados do cliente não informado para o cpf informado.");
    }
}
