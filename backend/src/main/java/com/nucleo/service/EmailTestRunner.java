package com.nucleo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EmailTestRunner{

    private final EmailService emailService;

//    @Override
//    public void run(String... args) {
//        emailService.enviarEmail(
//                "joaogotado@gmail.com",
//                "Teste de alerta",
//                "Olá joao 🌸!\n\nSeu sistema de alertas está funcionando perfeitamente!"
//        );
//    }

}
