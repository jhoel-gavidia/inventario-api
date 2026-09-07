package com.motorepuestos.inventario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "auditorias",
        indexes = @Index(name = "idx_auditoria_entidad", columnList = "entidad, entidad_id"))
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auditoria_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_ant", columnDefinition = "jsonb")
    private String datosAnt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_new", columnDefinition = "jsonb")
    private String datosNew;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
