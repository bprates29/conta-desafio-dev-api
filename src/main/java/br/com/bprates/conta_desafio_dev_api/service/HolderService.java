package br.com.bprates.conta_desafio_dev_api.service;

import br.com.bprates.conta_desafio_dev_api.domain.Holder;
import br.com.bprates.conta_desafio_dev_api.repository.HolderRepository;
import br.com.bprates.conta_desafio_dev_api.utils.CpfUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class HolderService {
    private final HolderRepository holderRepository;

    public HolderService(HolderRepository holderRepository) {
        this.holderRepository = holderRepository;
    }

    @Transactional
    public Holder createHolder(String fullName, String cpf){
        if (isValidateCpf(cpf)) {
            throw new RuntimeException("CPF is invalid or already exists");
        }

        Holder holder = new Holder(fullName, cpf);
        return holderRepository.save(holder);
    }

    // TODO: TESTAR essa lógica unitariamente daqui a pouco
    private boolean isValidateCpf(String cpf) {
        return holderRepository.existsByCpf(cpf) || !CpfUtils.isValidCPF(cpf);
    }

    @Transactional
    public void deleteHolder(Long holderId) {
        // TODO: posso verificar se o holder tem conta ativa antes de remover
        holderRepository.deleteById(holderId);
    }

    public Holder findByCpf(String cpf) {
        return holderRepository.findByCpf(cpf);
    }
}
