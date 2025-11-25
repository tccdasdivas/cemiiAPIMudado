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

@CrossOrigin(origins = "*")
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
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Evita duplicação: cuidadores não enviam idosos
        usuarios.forEach(u -> {
            if (!"responsavel".equalsIgnoreCase(u.getTipo())) {
                u.setIdosos(null); // cuidadores não enviam lista de idosos
            }
        });

        return usuarios;
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
            Usuario u = usuarioAtual.get();

            // Atualiza apenas os campos que podem ser modificados
            u.setNome(usuario.getNome());
            u.setEmail(usuario.getEmail());
            u.setTelefone(usuario.getTelefone());
            u.setProfissao(usuario.getProfissao());
            u.setCidade(usuario.getCidade());
            u.setNascimento(usuario.getNascimento());
            u.setTipo(usuario.getTipo());

            // Só atualiza a senha se tiver sido enviada
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                u.setSenha(usuario.getSenha());
            }

            Usuario usuarioSalvo = usuarioService.salvar(u);
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
