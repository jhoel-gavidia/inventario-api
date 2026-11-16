package com.motorepuestos.inventario.movimiento.application.pipeline;

import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MovimientoRegistroPipelineTest {

    @Test
    void ejecutar_debeEjecutarLosPasosEnOrdenYRetornarLaRespuesta() {
        List<String> pasosEjecutados = new ArrayList<>();
        MovimientoResponse response = MovimientoResponse.builder().id(1L).build();

        MovimientoRegistroPipeline pipeline = new MovimientoRegistroPipeline(List.of(
                context -> pasosEjecutados.add("primero"),
                context -> {
                    pasosEjecutados.add("segundo");
                    context.setResponse(response);
                }
        ));

        MovimientoResponse resultado = pipeline.ejecutar(
                new RegistrarMovimientoCommand(Tipo.ENTRADA, List.of())
        );

        assertThat(pasosEjecutados).containsExactly("primero", "segundo");
        assertThat(resultado).isSameAs(response);
    }
}
