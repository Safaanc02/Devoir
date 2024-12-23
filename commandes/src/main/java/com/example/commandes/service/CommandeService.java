package com.example.commandes.service;

import com.example.commandes.entity.Commande;
import com.example.commandes.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;

    @Autowired
    public CommandeService(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    public List<Commande> findAll() {
        return commandeRepository.findAll();
    }

    public boolean hasCommandes() {
        return !commandeRepository.findAll().isEmpty();
    }

    public List<Commande> findLastCommandes(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return commandeRepository.findAll(pageable).getContent();
    }

    public Commande save(Commande commande) {
        return commandeRepository.save(commande);
    }

    public Commande update(Long id, Commande commandeDetails) {
        return commandeRepository.findById(id).map(commande -> {
            commande.setDescription(commandeDetails.getDescription());
            commande.setQuantite(commandeDetails.getQuantite());
            commande.setDate(commandeDetails.getDate());
            commande.setMontant(commandeDetails.getMontant());
            return commandeRepository.save(commande);
        }).orElseThrow(() -> new RuntimeException("Commande avec ID " + id + " non trouvée"));
    }

    public void delete(Long id) {
        if (commandeRepository.existsById(id)) {
            commandeRepository.deleteById(id);
        } else {
            throw new RuntimeException("Commande avec ID " + id + " non trouvée");
        }
    }
}
