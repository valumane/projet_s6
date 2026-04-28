Dungeon Adventure – Installation process

Présentation
Dungeon Adventure est un jeu d’aventure textuel dans lequel le joueur explore un donjon, 
affronte des ennemis et tente de vaincre le boss final pour s’échapper. 

Le jeu se joue entièrement dans un terminal via des commandes textuelles.

Installation

Pré-requis
Pour jouer à Dungeon Adventure, vous devez disposer de :
Java 21 installé sur votre machine
Un terminal (Windows, macOS ou Linux)

Pour vérifier votre version de Java :
-java version


Installation du jeu :
Télécharger et extraire l’archive duhem_sibé_vadégrelet.zip
Ouvrir un terminal et se placer à la racine du projet :

methode make :
  Compiler le jeu : make compile
  Lancer le jeu : make run

methode jar : 
  Lancer le jeu via le jar : java -jar game.jar


Objectif du jeu
Le joueur incarne un aventurier piégé dans un donjon.
Le but : vaincre le boss final pour s’échapper.

Pour progresser, il faudra :

* Explorer les différentes salles
* Trouver des objets utiles
* Combattre ou éviter les ennemis
* Gérer les points de vie et l’inventaire


Fonctionnement général

Interaction console

Le jeu fonctionne exclusivement dans un terminal.

Le joueur tape des commandes sous la forme : COMMANDE [arguments]

Le jeu interprète chaque ligne, exécute l’action correspondante et affiche le résultat.


Commandes disponibles

LOOK : Affiche la description de la salle : objets, ennemis, sorties.
LOOK <objet> -> Affiche des informations détaillées sur un élément.

GO <destination> -> Permet de se déplacer vers une salle adjacente.

TAKE <objet> -> Ramasse un objet dans la salle et l’ajoute à l’inventaire.
TAKE all -> Ramasse tout les objets d'une room

DROP <objet> -> Retire l’objet de l’inventaire et le dépose dans la salle.

USE <objet> [cible]
  Exemples :
    USE potion -> Utilise un objet consommable.
    USE key south -> Ouvre une porte verrouillée.

ATTACK <cible> -> Lance un combat :
        - le héros inflige des dégâts,
        - l’ennemi riposte (s’il est encore en vie).

INVENTORY -> Affiche tout le contenu du sac à dos.
STATS -> Affiche les statistiques du joueur :
        - HP
        - Dégâts de base
        - Arme équipée
        - Dégâts totaux

HELP -> Affiche la liste des commandes disponibles.

SAVE <nom> -> Créer un fichier de sauvegarde du jeu.
        Elles enregistrent :
        - la position du joueur
        - l'état des salles
        - l'inventaire
        - les ennemis vivants / morts
LOAD <nom> -> Recharge une partie sauvegardée.
exemple : SAVE partie1 puis LOAD partie1 pour reprendre.

QUIT -> Quitte immédiatement le jeu.

Déplacements dans le donjon
Les salles sont reliées entre elles par des sorties.
Certaines sont :
    Ouvertes
    Verrouillées (nécessitent une clé)


⚔️ Système de combat

Lors d'un combat :
    1. Le héros attaque et inflige des dégâts
    2. L’ennemi contre-attaque (s’il est encore vivant)
    3. Les points de vie sont mis à jour

    Si les HP du héros atteignent 0 → Game Over


Conditions de victoire et de défaite
    Victoire
    Le joueur gagne la partie s’il atteint et vainc le boss final.

    Défaite
    Le jeu se termine si :
    - Les points de vie du héros tombent à 0
    - Le joueur utilise la commande QUIT

Un message de fin est toujours affiché.


Problemes courants : 
Java n’est pas reconnu ?  
 -> Vérifiez que Java 21 est installé et accessible dans votre PATH.

make: command not found ?  
 -> Installer make ou utiliser `java -jar game.jar`.