package org.jcdaviconde.springcloud.msvc.cursos.models.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cursos_usuarios", uniqueConstraints = {@UniqueConstraint(columnNames={"usuario_id","curso_id"})})
public class CursoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", unique = false)
    private Long usuarioId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CursoUsuario that = (CursoUsuario) o;
        return Objects.equals(usuarioId, that.usuarioId);
    }

}
