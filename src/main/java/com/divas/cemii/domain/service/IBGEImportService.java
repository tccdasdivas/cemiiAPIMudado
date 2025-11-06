package com.divas.cemii.domain.service;

import com.divas.cemii.domain.model.Cidade;
import com.divas.cemii.domain.model.Estado;
import com.divas.cemii.domain.repository.CidadeRepository;
import com.divas.cemii.domain.repository.EstadoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class IBGEImportService {

    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;

    public IBGEImportService(EstadoRepository estadoRepository, CidadeRepository cidadeRepository) {
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
    }

    public Cidade importarCidade(String nomeCidade, String estadoInput) {
        if (estadoInput == null || estadoInput.isBlank()) {
            throw new IllegalArgumentException("O campo 'estado' é obrigatório.");
        }

        try {
            RestTemplate rest = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            Long estadoId;
            String estadoNome;
            String estadoSigla;

            // 🔹 Caso o parâmetro seja numérico (ID)
            if (estadoInput.matches("\\d+")) {
                String estadoJson = rest.getForObject(
                        "https://servicodados.ibge.gov.br/api/v1/localidades/estados/" + estadoInput,
                        String.class
                );
                JsonNode estadoNode = mapper.readTree(estadoJson);
                estadoId = estadoNode.get("id").asLong();
                estadoNome = estadoNode.get("nome").asText();
                estadoSigla = estadoNode.get("sigla").asText();

            } else {
                // 🔹 Caso seja sigla (ex: SP)
                String estadosJson = rest.getForObject(
                        "https://servicodados.ibge.gov.br/api/v1/localidades/estados",
                        String.class
                );
                JsonNode estadosNode = mapper.readTree(estadosJson);

                JsonNode estadoNodeEncontrado = null;
                for (JsonNode e : estadosNode) {
                    if (e.get("sigla").asText().equalsIgnoreCase(estadoInput)) {
                        estadoNodeEncontrado = e;
                        break;
                    }
                }

                if (estadoNodeEncontrado == null) {
                    throw new IllegalArgumentException("Estado " + estadoInput + " não encontrado no IBGE.");
                }

                estadoId = estadoNodeEncontrado.get("id").asLong();
                estadoNome = estadoNodeEncontrado.get("nome").asText();
                estadoSigla = estadoNodeEncontrado.get("sigla").asText();
            }

            // 🔹 Recupera ou salva estado
            Estado estado = estadoRepository.findById(estadoId).orElseGet(() -> {
                Estado novo = new Estado();
                novo.setId(estadoId);
                novo.setNome(estadoNome);
                novo.setSigla(estadoSigla);
                return estadoRepository.save(novo);
            });

            // 🔹 Buscar cidades do estado
            String cidadesJson = rest.getForObject(
                    "https://servicodados.ibge.gov.br/api/v1/localidades/estados/" + estadoId + "/municipios",
                    String.class
            );
            JsonNode cidadesNode = mapper.readTree(cidadesJson);

            JsonNode cidadeNodeEncontrada = null;
            for (JsonNode c : cidadesNode) {
                if (c.get("nome").asText().equalsIgnoreCase(nomeCidade)) {
                    cidadeNodeEncontrada = c;
                    break;
                }
            }

            if (cidadeNodeEncontrada == null) {
                throw new IllegalArgumentException("Cidade " + nomeCidade + " não encontrada no IBGE.");
            }

            Long cidadeId = cidadeNodeEncontrada.get("id").asLong();
            String cidadeNome = cidadeNodeEncontrada.get("nome").asText();

            // 🔹 Salvar ou retornar existente
            Optional<Cidade> cidadeExistente = cidadeRepository.findByNomeAndEstado(cidadeNome, estado);
            if (cidadeExistente.isPresent()) {
                return cidadeExistente.get();
            }

            Cidade nova = new Cidade();
            nova.setId(cidadeId);
            nova.setNome(cidadeNome);
            nova.setEstado(estado);
            return cidadeRepository.save(nova);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar cidade do IBGE: " + e.getMessage(), e);
        }
    }
}
