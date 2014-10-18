# TP1.1 INF4410 1761581-

1. Compilez avec la commande `ant`
2. Démarrez le registre RMI avec la commande `rmiregistry` à partir du dossier bin de votre projet. Ajoutez un & pour le détacher de la console.
3. Démarrez le serveur avec le script server (`./server` ou bash server).
4. dans une nouvelle console, déplacez vous dans le répertoire `client1` et exécutez la commande `./client XX YY`voulue
5. réalisez de même que 4. dans le répertoire `client2` pour contrôller un deuxième client au serveur.

## Commandes disponibles :

* `./client list`
  * Permet d'afficher la liste des fichiers présents sur le serveur de fichier
* `./client create monSuperFichier`
  * Permet de créer le fichier monSuperFichier sur le serveur. Il sera récupéré dans la foulée sur le répertoire du client
* `./client get    monSuperFichier`
  * récupère le fichier monSuperFichier si présent sur le serveur
* `./client lock   monSuperFichier`
  * vérouille le fichier monSuperFichier à ce client pour qu'il puisse l'éditer et le mettre à jour sans crainte de voir sa MAJ écrasée.
* `./client push   monSuperFichier`
  * Envoie la mise à jour au serveur et libère le fichier

Pour plus de précisions, veuillez vous reporter au sujet de ce TP et au Compte Rendu fourni. Vous y retrouverez des screenshots de deux cas : un cas simple avec un seul client et un cas exposant les conflits entre deux clients connectés à la fois sur le serveur et le bon fonctionnement de celui-ci.