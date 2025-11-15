package com.divas.cemii.api.controller;

import com.divas.cemii.domain.model.Idoso;
import com.divas.cemii.domain.model.Usuario;
import com.divas.cemii.domain.repository.IdosoRepository;
import com.divas.cemii.domain.repository.UsuarioRepository;
import com.divas.cemii.dto.LoginRequestDTO;
import com.divas.cemii.dto.RegisterRequestDTO;
import com.divas.cemii.dto.ResponseDTO;
import com.divas.cemii.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IdosoRepository idosoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        Usuario usuario = usuarioRepository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (passwordEncoder.matches(body.senha(), usuario.getSenha())) {
            String token = tokenService.generateToken(usuario);
            return ResponseEntity.ok(
                    new ResponseDTO(usuario.getId(), usuario.getNome(), token, usuario.getEmail(), usuario.getTipo())
            );
        }

        return ResponseEntity.badRequest().body("Senha incorreta");
    }

    // ♻️ REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(name = "Authorization") String bearerToken) {
        String subject = tokenService.validateToken(tokenService.recoverToken(bearerToken));

        Usuario usuario = usuarioRepository.findByEmail(subject)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = tokenService.generateToken(usuario);

        return ResponseEntity.ok(
                new ResponseDTO(usuario.getId(), usuario.getNome(), token, usuario.getEmail(), usuario.getTipo())
        );
    }

    // 🆕 REGISTRO
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(body.email());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(409).body("Email já cadastrado");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(body.name());
        novoUsuario.setEmail(body.email());
        novoUsuario.setSenha(passwordEncoder.encode(body.password()));
        novoUsuario.setTelefone(body.telefone());
        novoUsuario.setCpf(body.cpf());
        novoUsuario.setFoto(body.foto());
        novoUsuario.setNascimento(body.nascimento());
        novoUsuario.setParentesco(body.parentesco());
        novoUsuario.setProfissao(body.profissao());
        novoUsuario.setCoren(body.coren());
        novoUsuario.setCidade(body.cidade()); // importante: verificar se a entidade Cidade está correta

        if (body.tipo() == null || body.tipo().isBlank()) {
            return ResponseEntity.badRequest().body("Campo 'tipo' é obrigatório (RESPONSAVEL ou CUIDADOR).");
        }
        novoUsuario.setTipo(body.tipo().toUpperCase());

        usuarioRepository.save(novoUsuario);

        String token = tokenService.generateToken(novoUsuario);
        return ResponseEntity.ok(
                new ResponseDTO(novoUsuario.getId(), novoUsuario.getNome(), token, novoUsuario.getEmail(), novoUsuario.getTipo())
        );
    }


}
