# Vérification du Lab 3

Tests du 6 novembre 2026, sous Linux avec Java 17, Maven 3.9.9, WildFly 32.0.1.Final et Groovy 4.0.24. Compilation des quatre modules et déploiement du vrai WAR réussis. Appels distants EJB authentifiés, sans mock.

## Résultats

42 contrôles métier du Lab 3 et 14 contrôles du prototype réussis. Les anciens clients Java et Groovy et les anciens contrôles ont utilisé leurs classes et leur API compilées avant le Lab 3, sans recompilation, face au nouveau serveur. Les sources AuctionService, AuctionView et AuctionException ont été comparées octet par octet : identiques.

## Essais des menus

- Un lot créé par l’ancien client Java est lu dans le menu Groovy ; une offre russe Анна de 1800 RUB est acceptée et apparaît dans l’historique.
- Le menu Java consulte ce lot, le clôture avec confirmation ДА et affiche ПРОДАНО / Анна / 1800 RUB.
- L’ancien client Java consulte ensuite le même lot et le voit fermé avec son ancien DTO.
- Dans l’autre sens, le menu Java crée un lot et le menu Groovy ajoute une offre, clôture et lit le résultat Иван / 300 RUB.
- Le menu Groovy crée et annule un autre lot ; résultat ОТМЕНЁН / aucun gagnant / 0 RUB.
- Le menu Java lit l’historique vide du lot annulé et affiche le refus d’une nouvelle offre.
- Le menu Groovy récupère correctement après un choix inconnu et une saisie numérique invalide. Les deux menus sortent sur 0.

Les parcours console ont été pilotés avec des entrées UTF-8 et leurs sorties ont fait l’objet d’assertions. Les scripts CMD et PowerShell sont préparés pour le PC Windows de l’étudiant, mais n’ont pas été exécutés sur Windows dans cet environnement. Il reste à vérifier la mise à jour et les menus sur ce PC. Aucun test de cluster, de paiement ou de rôle individuel n’est revendiqué.

## legacy-checks.log

```
PASS 1 new lot has no bids
PASS 2 empty title
PASS 3 negative price
PASS 4 invalid duration
PASS 5 missing lot
PASS 6 empty bidder
PASS 7 equal price
PASS 8 valid bid
PASS 9 lower bid
PASS 10 rejection preserves price, leader and history
PASS 11 20 concurrent bids preserve maximum
PASS 12 history contains exactly committed bids
PASS 13 late bid refused
PASS 14 expired lot is reported closed
ALL 14 CHECKS PASSED
```

## checks3.log

```
PASS 1 new lot is open
PASS 2 empty history
PASS 3 open lot has no terminal result
PASS 4 two history rows
PASS 5 history order
PASS 6 history lot identity
PASS 7 equal bid refused
PASS 8 lower bid refused
PASS 9 lot with accepted bids cannot be cancelled
PASS 10 failed cancellation leaves lot open
PASS 11 close produces winning result
PASS 12 closed lot excluded from list
PASS 13 legacy view sees manual close
PASS 14 closed lot rejects bids
PASS 15 repeated close idempotent
PASS 16 history preserved after close
PASS 17 cannot cancel closed lot
PASS 18 no bid means no sale
PASS 19 cancellation result
PASS 20 cancelled lot legacy closed and not listed
PASS 21 cancel idempotent
PASS 22 cannot bid on cancelled lot
PASS 23 cannot close cancelled lot
PASS 24 cancelled result readable
PASS 25 deadline result without manual close
PASS 26 expired lot not listed
PASS 27 expired lot cannot be cancelled
PASS 28 deadline rejects late bid
PASS 29 missing history lot
PASS 30 missing close lot
PASS 31 missing cancel lot
PASS 32 missing result lot
PASS 33 duration bound
PASS 34 bid/cancel race has one winning action 0
PASS 35 race history consistent 0
PASS 36 race state consistent 0
PASS 37 bid/cancel race has one winning action 1
PASS 38 race history consistent 1
PASS 39 race state consistent 1
PASS 40 bid/cancel race has one winning action 2
PASS 41 race history consistent 2
PASS 42 race state consistent 2
ALL 42 LAB3 CHECKS PASSED
```