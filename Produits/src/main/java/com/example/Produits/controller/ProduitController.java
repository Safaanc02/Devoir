package com.example.Produits.controller;

import com.example.Produits.model.Produit;
import com.example.Produits.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    @Autowired
    private ProduitRepository produitRepository;

    // Récupérer tous les produits
    @GetMapping
    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    // Récupérer un produit par ID
    @GetMapping("/{id}")
    public Produit getProduitById(@PathVariable Long id) throws InterruptedException {

        Thread.sleep(5000); // Délai de 5 secondes

        return produitRepository.findById(id).orElse(null);
    }

    // Créer un nouveau produit
    @PostMapping
    public Produit createProduit(@RequestBody Produit produit) {
        return produitRepository.save(produit);
    }

    // Mettre à jour un produit
    @PutMapping("/{id}")
    public Produit updateProduit(@PathVariable Long id, @RequestBody Produit produitDetails) {
        Produit produit = produitRepository.findById(id).orElseThrow();
        produit.setNom(produitDetails.getNom());
        produit.setPrix(produitDetails.getPrix());
        return produitRepository.save(produit);
    }

    // Supprimer un produit
    @DeleteMapping("/{id}")
    public String deleteProduit(@PathVariable Long id) {
        produitRepository.deleteById(id);
        return "Produit supprimé avec succès.";
    }
}
