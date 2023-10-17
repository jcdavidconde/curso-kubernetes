package org.jcdavidconde.springcloud.msvc.usuarios.msvcusuarios.controllers;

import org.jcdavidconde.springcloud.msvc.usuarios.msvcusuarios.models.entity.Usuario;
import org.jcdavidconde.springcloud.msvc.usuarios.msvcusuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@SuppressWarnings("unused")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<Usuario> usuario = service.porId(id);
        return usuario.isPresent() ?
                ResponseEntity.ok(usuario.get()):
                ResponseEntity.notFound().build();
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> detalle(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(service.porIds(ids));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return validar(bindingResult);
        if(service.porEmail(usuario.getEmail()).isPresent()) {
            bindingResult.addError(new ObjectError("email", " ya existente"));
            return validar(bindingResult);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult bindingResult, @PathVariable Long id) {
        if (bindingResult.hasErrors()) return validar(bindingResult);

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuarioDb = o.get();
            if(!usuarioDb.getEmail().equals(usuario.getEmail()) && service.porEmail(usuario.getEmail()).isPresent()) {
                bindingResult.addError(new ObjectError("email", " ya existe"));
                return validar(bindingResult);
            }
            usuarioDb.setNombre(usuario.getNombre());
            usuarioDb.setEmail(usuario.getEmail());
            usuarioDb.setPassword(usuario.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<Map<String,String>> validar(BindingResult bindingResult) {
        Map<String,String> errores = new HashMap<>();
        bindingResult.getFieldErrors().forEach(err -> {
            errores.put(err.getField(),"El campo "+err.getField()+" "+err.getDefaultMessage());
        });
        bindingResult.getAllErrors().forEach(err -> {
            if (!"usuario".equals(err.getObjectName())) errores.put(err.getObjectName(),"El campo "+err.getObjectName()+" "+err.getDefaultMessage());
        });
        return  ResponseEntity.badRequest().body(errores);
    }
}
