package org.jcdavidconde.springcloud.msvc.usuarios.msvcusuarios.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-cursos", url = "${msvc.cursos.url}")
public interface CursoClientRest {

    @DeleteMapping("elminiar-usuario-cursos/{usuarioId}")
    void eliminarUsuarioCursos(@PathVariable Long usuarioId);
}
