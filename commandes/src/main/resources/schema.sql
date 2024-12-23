CREATE TABLE commande (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          description VARCHAR(255) NOT NULL,
                          quantite INT NOT NULL,
                          date TIMESTAMP NOT NULL,
                          montant DOUBLE NOT NULL,
                          id_produit BIGINT
);
