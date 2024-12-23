package com.example.commandes.controller;

import com.example.commandes.entity.Commande;
import com.example.commandes.service.CommandeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommandesController implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(CommandesController.class);
    private final CommandeService commandeService;
    private final RestTemplate restTemplate;

    @Value("${mes-config-ms.commandes-last}")
    private int commandesLast;

    public CommandesController(CommandeService commandeService, RestTemplate restTemplate) {
        this.commandeService = commandeService;
        this.restTemplate = restTemplate;
    }

    // Récupérer les dernières commandes
    @GetMapping("/commandes")
    public List<Commande> listeDesCommandes() {
        logger.info("Récupération des {} dernières commandes.", commandesLast);
        List<Commande> commandes = commandeService.findLastCommandes(commandesLast);
        if (commandes.isEmpty()) {
            logger.warn("Aucune commande trouvée dans la base de données.");
        }
        return commandes;
    }

    // Récupérer les détails d'un produit via un autre microservice
    @GetMapping("/commandes/{id}/produit")
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "produitService", fallbackMethod = "fallbackProduitDetails")
    public String obtenirProduitDetails(@PathVariable Long id) {
        logger.info("Appel au microservice produit pour la commande avec ID {}", id);
        String url = "http://localhost:8082/api/produits/" + id; // Remplacez par l'URL réelle du microservice produit
        return restTemplate.getForObject(url, String.class);
    }

    // Fallback en cas d'échec de l'appel au service produit
    public String fallbackProduitDetails(Long id, Throwable throwable) {
        logger.error("Échec de l'appel au service produit pour la commande avec ID {}. Cause : {}", id, throwable.getMessage());
        return "Les détails du produit avec ID " + id + " ne sont pas disponibles pour le moment.";
    }


    // Créer une nouvelle commande
    @PostMapping("/commandes")
    public ResponseEntity<Commande> creerCommande(@RequestBody Commande commande) {
        logger.info("Création d'une nouvelle commande : {}", commande);
        Commande nouvelleCommande = commandeService.save(commande);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleCommande);
    }

    // Mettre à jour une commande existante
    @PutMapping("/commandes/{id}")
    public ResponseEntity<Commande> modifierCommande(@PathVariable Long id, @RequestBody Commande commandeDetails) {
        logger.info("Modification de la commande avec ID {}", id);
        Commande commandeModifiee = commandeService.update(id, commandeDetails);
        return ResponseEntity.ok(commandeModifiee);
    }

    // Supprimer une commande
    @DeleteMapping("/commandes/{id}")
    public ResponseEntity<Void> supprimerCommande(@PathVariable Long id) {
        logger.info("Suppression de la commande avec ID {}", id);
        commandeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Indicateur de santé du microservice
    @Override
    public Health health() {
        if (!commandeService.hasCommandes()) {
            return Health.down().withDetail("message", "Aucune commande n'est disponible").build();
        }
        return Health.up().withDetail("nombreDeCommandes", commandeService.findAll().size()).build();
    }
}
