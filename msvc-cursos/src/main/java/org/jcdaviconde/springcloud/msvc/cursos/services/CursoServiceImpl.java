package org.jcdaviconde.springcloud.msvc.cursos.services;

import org.jcdaviconde.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.jcdaviconde.springcloud.msvc.cursos.models.Usuario;
import org.jcdaviconde.springcloud.msvc.cursos.models.entity.Curso;
import org.jcdaviconde.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.jcdaviconde.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements CursoService{

    @Autowired
    CursoRepository repository;

    @Autowired
    UsuarioClientRest client;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {

        return (List<Curso>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        Optional<Curso> o = repository.findById(id);
        o.ifPresent(this::buscarUsuariosPorCurso);
        return o;
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return repository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> o = repository.findById(cursoId);
        if (o.isPresent()) {
            Usuario usuariom = client.detalle(usuario.getId());
            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuariom.getId());
            curso.addCursoUsuario(cursoUsuario);
            repository.save(curso);
            return Optional.of(usuariom);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Usuario usuariom = client.crear(usuario);
        return asignarUsuario(usuariom,cursoId);
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> o = repository.findById(cursoId);
        if (o.isPresent()) {
            Usuario usuariom = client.detalle(usuario.getId());
            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuariom.getId());
            curso.removeCursoUsuario(cursoUsuario);
            repository.save(curso);
            return Optional.of(usuariom);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorUsuarioId(Long usuarioId) {
        repository.deleteCursoUsuarioByUsuarioId(usuarioId);
    }

    private void buscarUsuariosPorCurso(Curso curso) {
        if (!curso.getCursoUsuarios().isEmpty()) {
            List<Long> usuariosIds = curso.getCursoUsuarios()
                    .stream().map(CursoUsuario::getUsuarioId).toList();
            curso.setUsuarios(client.listarPorIds(usuariosIds));
        }
    }
}
