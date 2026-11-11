# Lab 3 EJB — système d’enchères

Java 17, WildFly 32.0.1.Final, Groovy 4.0.24. Application console pédagogique, sans paiement réel. Le serveur et les menus couvrent la création, les offres, l’historique, la clôture et l’annulation. Les noms des participants sont des données de démonstration.

## Mise à jour de ton projet Windows

Tu as déjà le projet fonctionnel dans `D:\PROJET\ITMO\CompSys\auction-ejb`, le JDK 17 et le compte application Bilel.

1. Arrête les clients, puis WildFly avec Ctrl+C dans sa fenêtre. Ne laisse pas le serveur écrire pendant la mise à jour.
2. Fais une copie de sauvegarde de ton dossier actuel `auction-ejb` avant de remplacer les fichiers. Cela permet de retrouver les sources et les clients précédents pour la soutenance.
3. Extrais le ZIP. Ouvre le dossier extrait `auction-ejb-lab3`. Copie **son contenu** dans ton dossier existant `auction-ejb`, et accepte le remplacement des fichiers. Tu dois retrouver `pom.xml`, `scripts` et `lancer-java.cmd` directement dans `auction-ejb`, sans dossier imbriqué supplémentaire.
4. **Conserve ton dossier `.runtime`**. Il contient WildFly, Maven et ton compte Bilel. Le ZIP n’en contient pas et ne l’efface pas. Ne remplace aucun fichier application-users.properties.
5. Dans `auction-ejb`, lance `compiler.cmd`. Attends `BUILD SUCCESS` puis `BUILD OK`. Le clean supprime les anciennes dépendances compilées afin d’éviter le mélange d’API.
6. Lance `demarrer-serveur.cmd`, laisse la fenêtre ouverte et attends `WFLYSRV0025 ... started`.
7. Lance `deployer.cmd`. Le déploiement remplace auction-server.war sur le même WildFly. Attention : la base H2 de démonstration repart à zéro au redéploiement.
8. Lance `lancer-java.cmd`. Saisis Bilel et ton mot de passe local. Le menu russe apparaît.
9. Lance `lancer-groovy.cmd` dans une autre fenêtre et utilise le même compte.
10. Lance `verifier.cmd` si tu veux exécuter les contrôles du Lab 3. Ils créent des enchères de test ; fais-les avant de préparer tes lots de démonstration.

Ces fichiers CMD activent UTF-8 et utilisent le chemin Java 17 trouvé sur ton PC. Si ton JDK change de dossier, adapte JAVA_HOME dans les CMD. Utilise une police Unicode comme Consolas.

## Installation sur une autre machine

Installer JDK 17, puis exécuter dans le dossier :
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\setup.ps1
```
Le téléchargement initial nécessite Internet. Créer un compte **Application User**. Ensuite suivre les étapes de démarrage et déploiement. Le script setup peut rouvrir l’assistant de comptes, donc il n’est pas nécessaire pour la simple mise à jour du PC déjà configuré.

## Le menu russe

| Choix | Action |
|---|---|
| 1 | Créer un lot avec titre, prix initial et durée en secondes |
| 2 | Lister les enchères actuellement ouvertes |
| 3 | Consulter un lot par ID |
| 4 | Saisir une offre et un nom de participant |
| 5 | Lire l’historique des offres acceptées |
| 6 | Clôturer immédiatement, confirmation `ДА` |
| 7 | Annuler un lot ouvert sans offre acceptée, confirmation `ДА` |
| 8 | Consulter le résultat d’un lot terminé ou annulé |
| 0 | Quitter le client |

Les IDs viennent du serveur : note le véritable ID après création. Les prix sont des roubles entiers. Le délai autorisé va de 1 à 86400 secondes. Les dates ISO avec Z sont en UTC. Les choix 6 et 7 demandent les deux lettres cyrilliques `ДА`, et non les lettres latines DA.

## Règles métier

- Une offre doit être strictement supérieure au prix courant. Les offres égales, négatives ou trop faibles sont refusées.
- Le serveur refuse les offres après l’échéance, la clôture manuelle ou l’annulation.
- Seules les offres acceptées apparaissent dans l’historique, dans l’ordre des identifiants de la base.
- Une enchère avec au moins une offre ne peut pas être annulée. Elle peut être clôturée.
- Le gagnant est le dernier meilleur enchérisseur. Le prix final est sa dernière offre acceptée.
- Sans offre, le résultat est `БЕЗ СТАВОК`, sans gagnant et sans vente : prix final 0. Le prix initial reste visible dans la fiche du lot.
- Un lot annulé donne `ОТМЕНЁН`, sans gagnant et sans vente. Le résultat est accessible par le choix 8.
- Répéter une clôture conserve le résultat ; répéter une annulation conserve l’annulation. Clôturer un lot annulé est refusé.
- Le résultat n’est pas disponible tant que le lot reste ouvert.

## Compatibilité avec les labs 1 et 2

L’interface **AuctionService**, ses trois signatures, **AuctionView**, son serialVersionUID et **AuctionException** restent inchangés. Le même nom de déploiement et le même nom JNDI de base restent accessibles.

Le Lab 3 ajoute l’interface distante **AuctionManagement extends AuctionService** avec cinq opérations. Le bean expose explicitement les deux vues distantes. Les clients anciens peuvent continuer à utiliser la vue AuctionService sans connaître les nouvelles classes. Ils voient un lot annulé ou clôturé comme fermé ; le résultat détaillé n’existe que dans la nouvelle API.

Les commandes `java demo`, `groovy demo`, `create`, `bid` et `show` continuent d’exister. Le menu s’ajoute via `java menu` et `groovy menu`. Pour lancer depuis une session PowerShell UTF-8 :
```powershell
.\scripts\client.ps1 java menu
.\scripts\client.ps1 groovy menu
```

Compatibilité du contrat ne signifie pas conservation des données de l’ancienne base. Le prototype recrée son schéma au déploiement.

## Fichiers à comprendre

- `auction-api/.../AuctionService.java` : contrat initial conservé.
- `auction-api/.../AuctionManagement.java` : cinq méthodes ajoutées.
- `auction-api/.../BidView.java` et `AuctionResult.java` : nouveaux DTO sérialisables.
- `auction-server/.../AuctionServiceBean.java` : règles, transactions et verrous.
- `client-java/.../JavaMenu.java` : menu Java.
- `client-groovy/.../GroovyMenu.groovy` : menu Groovy.
- `client-java/.../Lab3Checks.java` : contrôles d’intégration exécutés contre WildFly.

## Limites du périmètre

Un seul compte technique de connexion et @PermitAll, pas de rôles vendeur/acheteur. Toute connexion autorisée peut clôturer un lot. Le nom de l’enchérisseur est saisi et ne constitue pas une identité vérifiée. Serveur lié à 127.0.0.1. Aucun paiement, livraison ou site public. H2 en mémoire ; données perdues au redémarrage et au redéploiement. Aucun timer ou notification en arrière-plan : l’échéance s’applique avec l’heure serveur lors des appels et dans le filtre des lots ouverts. Une liste est une vue à l’instant de la demande et peut changer avant l’action suivante. Des erreurs réseau nécessitent de rétablir le serveur puis relancer le client.

Ce périmètre est une application d’enchères pédagogique complète pour les opérations annoncées, à confronter aux attentes finales du professeur. Les rôles, paiements et durabilité sont des extensions, pas des fonctionnalités implicitement réalisées.

## Dépannage

Erreur de connexion : vérifier le serveur, le déploiement et les ports 8080/9990. Erreur de compte : ApplicationRealm, Bilel et son mot de passe. Anciennes classes ou mauvaise API : relancer compiler.cmd puis redeployer. PowerShell bloqué : les CMD lancent uniquement les scripts locaux avec ExecutionPolicy Bypass pour le processus. Pas de changement global de politique.

## Sources officielles

- Vues distantes multiples : https://jakarta.ee/specifications/enterprise-beans/4.0/apidocs/jakarta/ejb/remote
- Spécification EJB : https://jakarta.ee/specifications/enterprise-beans/4.0/jakarta-enterprise-beans-spec-core-4.0.html
- Voir docs/Verification_Lab3.md pour les tests réels et docs/Soutenance_Lab3_RU_FR.md pour la défense.
