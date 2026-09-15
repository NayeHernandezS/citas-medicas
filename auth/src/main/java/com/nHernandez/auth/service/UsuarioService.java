package com.nHernandez.auth.service;

import com.nHernandez.auth.dto.UsuarioRequest;
import com.nHernandez.auth.dto.UsuarioResponse;

import java.util.Set;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse eliminar(String username);
}
