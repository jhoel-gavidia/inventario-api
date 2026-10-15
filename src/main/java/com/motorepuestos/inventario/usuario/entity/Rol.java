package com.motorepuestos.inventario.usuario.entity;

public enum Rol {

    ADMIN(true, "administrador"),
    USER(false, "usuario");

    private final boolean exigePresencia;
    private final String descripcion;

    Rol(boolean exigePresencia, String descripcion) {
        this.exigePresencia = exigePresencia;
        this.descripcion = descripcion;
    }

    public boolean exigePresencia() {
        return exigePresencia;
    }

    public String descripcion() {
        return descripcion;
    }
}