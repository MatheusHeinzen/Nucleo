package com.nucleo.utils;

import java.util.Objects;
import java.util.function.Consumer;

public class EntityUtils {

    public static <T> void atualizarSeDiferente(Consumer<T> setter, T novoValor, T valorAtual) {
        if (novoValor != null && !Objects.equals(novoValor, valorAtual)) {
            setter.accept(novoValor);
        }
    }
}
