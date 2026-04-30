## Commandes disponibles

| Commande | Description |
|----------|-------------|
| `make build` | Compile tous les fichiers `.java` dans le dossier `out/` |
| `make cli` | Compile puis lance le jeu en mode **terminal** (texte uniquement) |
| `make gui` | Compile puis lance le jeu en mode **graphique** via le menu principal |
| `make random` | Compile puis lance directement une **partie aléatoire** en mode graphique |
| `make clean` | Supprime le dossier `out/` et le fichier `sources.txt` |
| `make rebuild` | Équivalent de `clean` + `build` (recompilation complète) |

## Utilisation rapide

```bash
# Lancer le jeu avec le menu graphique
make gui

# Lancer directement une partie
make random

# Lancer en mode terminal
make cli

# Recompiler de zéro
make rebuild
```
