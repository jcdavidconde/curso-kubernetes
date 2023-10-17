package org.jcdavidconde.springcloud.msvc.usuarios.msvcusuarios.repositories;

import org.jcdavidconde.springcloud.msvc.usuarios.msvcusuarios.models.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario,Long> {

    Optional<Usuario> findByEmail(String email);
}
