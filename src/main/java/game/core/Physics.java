// Fichier : src/main/java/game/core/Physics.java
package game.core;

/**
 * A6 + A7 - Physique et Collisions
 * Classe pour gérer la gravité et les collisions
 */
public class Physics {

    /**
     * A7 - Détecte et gère la collision avec le sol
     * @param player Le joueur
     * @param ground Le sol
     */
    public static void checkGroundCollision(Player player, Ground ground) {

        // Vérifier si le joueur intersecte le sol
        if (player.getRectangle().getBoundsInParent()
                .intersects(ground.getRectangle().getBoundsInParent())) {

            // A7 - Snap le joueur sur le sol
            double groundTop = ground.getY();
            player.setPlayerY(groundTop - player.getHeight());

            // A7 - Reset la vélocité verticale
            player.setVelocityY(0);

            // A7 - Le joueur est sur le sol
            player.setOnGround(true);
        } else {
            // Le joueur n'est pas sur le sol
            player.setOnGround(false);
        }
    }
}