package org.jcdavidconde.springcloud.msvc.usuarios.msvcusuarios.services;

import org.jcdavidconde.springcloud.msvc.usuarios.msvcusuarios.models.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> listar();
    Optional<Usuario> porId(Long id);
    Usuario guardar(Usuario usuario);
    void eliminar(Long id);
    Optional<Usuario> porEmail(String email);

    List<Usuario> porIds(Iterable<Long> ids);
}
