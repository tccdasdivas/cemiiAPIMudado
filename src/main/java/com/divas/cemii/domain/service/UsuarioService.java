package com.divas.cemii.domain.service;

import com.divas.cemii.domain.exception.EntidadeEmUsoException;
import com.divas.cemii.domain.exception.EntidadeNaoEncontradaException;
import com.divas.cemii.domain.model.Cidade;
import com.divas.cemii.domain.model.Usuario;
import com.divas.cemii.domain.repository.CidadeRepository;
import com.divas.cemii.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    @Autowired
    private IBGEImportService ibgeImportService;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        try {

            // Se for UPDATE (ID existe), busca o usuário atual para recuperar a cidade
            if (usuario.getId() != null) {
                Usuario usuarioAtual = usuarioRepository.findById(usuario.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para atualização."));

                // Se a cidade NÃO foi enviada no PUT, mantém a cidade atual
                if (usuario.getCidade() == null) {
                    usuario.setCidade(usuarioAtual.getCidade());
                }
            }

            // Se a cidade ainda estiver vazia → é um cadastro novo → cidade é obrigatória
            if (usuario.getCidade() == null) {
                throw new IllegalArgumentException("Cidade é obrigatória para o cadastro do usuário.");
            }

            Cidade cidadeFinal;

            // Caso venha o ID da cidade
            if (usuario.getCidade().getId() != null) {
                cidadeFinal = cidadeRepository.findById(usuario.getCidade().getId())
                        .orElseThrow(() -> new EntidadeNaoEncontradaException(
                                "Cidade com ID " + usuario.getCidade().getId() + " não encontrada."
                        ));
            }
            // Caso venha nome + estado
            else if (usuario.getCidade().getNome() != null && usuario.getCidade().getEstado() != null) {
                String nomeCidade = usuario.getCidade().getNome();
                String siglaEstado = usuario.getCidade().getEstado().getSigla();

                cidadeFinal = cidadeRepository
                        .findByNomeAndEstado_SiglaIgnoreCase(nomeCidade, siglaEstado)
                        .orElseGet(() -> ibgeImportService.importarCidade(nomeCidade, siglaEstado));
            }
            // Nenhuma info válida
            else {
                throw new IllegalArgumentException("Dados de cidade inválidos ou incompletos.");
            }

            usuario.setCidade(cidadeFinal);
            return usuarioRepository.save(usuario);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    public Usuario verificar(LocalDate nascimento) {
        LocalDate dataAtual = LocalDate.now();
        int idade = Period.between(nascimento, dataAtual).getYears();

        if (idade >= 18) {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNascimento(nascimento);
            return usuarioRepository.save(novoUsuario);
        } else {
            throw new IllegalArgumentException("Cadastro não permitido: menor de idade.");
        }
    }

    public void excluir(Long id) {
        try {
            usuarioRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new EntidadeEmUsoException(
                    String.format("Usuário de código %d não pode ser removido, pois está em uso.", id)
            );
        } catch (EmptyResultDataAccessException e) {
            throw new EntidadeNaoEncontradaException(
                    String.format("Não existe cadastro de usuário com o código %d.", id)
            );
        }
    }
}
