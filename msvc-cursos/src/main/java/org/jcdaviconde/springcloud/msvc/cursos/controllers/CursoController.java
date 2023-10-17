package org.jcdaviconde.springcloud.msvc.cursos.controllers;

import feign.FeignException;
import org.jcdaviconde.springcloud.msvc.cursos.models.Usuario;
import org.jcdaviconde.springcloud.msvc.cursos.models.entity.Curso;
import org.jcdaviconde.springcloud.msvc.cursos.services.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CursoController {

    @Autowired
    CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok().body(cursoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<Curso> o = cursoService.porId(id);
        return o.isPresent() ?
                ResponseEntity.ok(o.get()):
                ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@Valid @RequestBody Curso curso, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return validar(bindingResult);
        Curso cursodb = cursoService.guardar(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursodb);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult bindingResult, @PathVariable Long id) {
        if (bindingResult.hasErrors()) return validar(bindingResult);
        Optional<Curso> o = cursoService.porId(id);
        if (o.isPresent()) {
            Curso cursoDb = o.get();
            cursoDb.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(cursoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Curso> o = cursoService.porId(id);
        if (o.isPresent()) {
            cursoService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<Map<String,String>> validar(BindingResult bindingResult) {
        Map<String,String> errores = new HashMap<>();
        bindingResult.getFieldErrors().forEach(err -> {
            errores.put(err.getField(),"El campo "+err.getField()+" "+err.getDefaultMessage());
        });
        return  ResponseEntity.badRequest().body(errores);
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        return operacionUsuario(usuario,cursoId,OperacionUsuario.ASIGNAR);
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        return operacionUsuario(usuario,cursoId,OperacionUsuario.CREAR);
    }

    @PostMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        return operacionUsuario(usuario,cursoId,OperacionUsuario.ELIMINAR);
    }

    private enum OperacionUsuario {
        CREAR,
        ASIGNAR,
        ELIMINAR;
    }

    private ResponseEntity<?> operacionUsuario(Usuario usuario, Long cursoId, OperacionUsuario operacionUsuario) {
        Optional<Usuario> o;
        HttpStatus status;
        try {
            status = switch (operacionUsuario) {
                case CREAR -> {
                    o = cursoService.crearUsuario(usuario, cursoId);
                    yield HttpStatus.CREATED;
                }
                case ASIGNAR -> {
                    o = cursoService.asignarUsuario(usuario, cursoId);
                    yield HttpStatus.CREATED;
                }
                case ELIMINAR -> {
                    o = cursoService.eliminarUsuario(usuario, cursoId);
                    yield HttpStatus.NO_CONTENT;
                }
                default -> {
                    o = Optional.empty();
                    yield HttpStatus.BAD_REQUEST;
                }
            };
            ;
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return o.isPresent() ?
                ResponseEntity.status(status).body(o.get()) :
                ResponseEntity.notFound().build() ;
    }

    @DeleteMapping("elminiar-usuario-cursos/{usuarioId}")
    public ResponseEntity<?> eliminarUsuarioCursos(@PathVariable Long usuarioId) {
        cursoService.eliminarCursoUsuarioPorUsuarioId(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
