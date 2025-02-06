package br.com.bprates.conta_desafio_dev_api.controller;

import br.com.bprates.conta_desafio_dev_api.domain.Account;
import br.com.bprates.conta_desafio_dev_api.domain.Holder;
import br.com.bprates.conta_desafio_dev_api.dtos.CreateHolderRequest;
import br.com.bprates.conta_desafio_dev_api.service.HolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/holders")
public class HolderController {

    private final HolderService holderService;

    public HolderController(HolderService holderService) {
        this.holderService = holderService;
    }

    @GetMapping
    public ResponseEntity<List<Holder>> getAllHolders() {
        List<Holder> holders = holderService.getAllHolders();
        return ResponseEntity.ok(holders);
    }

    @PostMapping
    public ResponseEntity<Holder> createHolder(@RequestBody CreateHolderRequest request) {
        Holder holder = holderService.createHolder(request.fullName(), request.cpf());
        return ResponseEntity.ok(holder);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteHolder(@PathVariable String cpf) {
        holderService.deleteHolder(cpf);
        return ResponseEntity.noContent().build();
    }

}
