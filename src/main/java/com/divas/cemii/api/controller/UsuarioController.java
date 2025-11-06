package com.divas.cemii.api.controller;

import com.divas.cemii.domain.exception.EntidadeEmUsoException;
import com.divas.cemii.domain.model.Usuario;
import com.divas.cemii.domain.repository.UsuarioRepository;
import com.divas.cemii.domain.service.UsuarioService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    // ✅ LISTAR TODOS OS USUÁRIOS
    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // ✅ BUSCAR USUÁRIO POR ID
    @GetMapping("/{usuarioId}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ CADASTRAR NOVO USUÁRIO
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario adicionar(@RequestBody Usuario usuario) {
        return usuarioService.salvar(usuario);
    }

    // ✅ ATUALIZAR USUÁRIO
    @PutMapping("/{usuarioId}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long usuarioId, @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioAtual = usuarioRepository.findById(usuarioId);

        if (usuarioAtual.isPresent()) {
            BeanUtils.copyProperties(usuario, usuarioAtual.get(), "id");

            Usuario usuarioSalvo = usuarioService.salvar(usuarioAtual.get());
            return ResponseEntity.ok(usuarioSalvo);
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ EXCLUIR USUÁRIO
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> remover(@PathVariable Long usuarioId) {
        try {
            usuarioService.excluir(usuarioId);
            return ResponseEntity.noContent().build();
        } catch (EnumConstantNotPresentException e) {
            return ResponseEntity.notFound().build();
        } catch (EntidadeEmUsoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // ✅ (OPCIONAL) VERIFICAR USUÁRIO POR DATA DE NASCIMENTO
    // Caso queira manter a lógica similar à de "ResponsavelController"
    @PostMapping("/verificar")
    public ResponseEntity<Usuario> verificar(@RequestBody Usuario usuario) {
        Usuario salvo = usuarioService.verificar(usuario.getNascimento());
        return ResponseEntity.ok(salvo);
    }
}
