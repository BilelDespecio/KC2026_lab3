# Présenter le Lab 3

## 1 Montrer les deux interfaces

Ouvre AuctionService.java puis AuctionManagement.java. Montre les trois opérations conservées et les cinq ajoutées.

«В третьей лабораторной работе я расширил систему аукционов. Исходный интерфейс AuctionService сохранился. Новый интерфейс AuctionManagement добавляет список открытых аукционов, историю ставок, завершение, отмену и получение результата.»

## 2 Montrer le serveur

Dans AuctionServiceBean.java, montre @Remote avec les deux interfaces, puis closeAuction, cancelAuction et getAuctionResult. Les trois actions qui modifient un lot — offre, clôture et annulation — utilisent un verrou d’écriture sur la même ligne.

«Сервер предоставляет две удалённые бизнес-вью. Поэтому старые клиенты продолжают использовать прежний контракт. Новые операции также выполняются в транзакциях. Блокировка строки координирует ставки, завершение и отмену одного аукциона.»

## 3 Montrer les deux menus

Ouvre JavaMenu.java puis GroovyMenu.groovy. Les deux implémentent un vrai menu dans leur langage. Le helper de connexion reste Java dans le module Groovy.

«Я добавил интерактивные меню на Java и Groovy. Пользователь вводит название лота, сумму и идентификатор. Бизнес-правила находятся на сервере, поэтому оба клиента получают одинаковые проверки.»

## 4 Démonstration complète

1. Java : choix 1, créer Casque à 1000 RUB pour 3600 secondes. Noter l’ID renvoyé.
2. Groovy : choix 2, retrouver le lot ; choix 4, proposer Anna à 1800 sur ce même ID.
3. Java : choix 5, montrer l’historique. Choix 4, proposer Ivan à 1700 : refus.
4. Groovy : choix 7, tenter l’annulation de ce lot, confirmer ДА : refus car une offre existe.
5. Java : choix 6, clôturer le lot, confirmer ДА.
6. Groovy : choix 8, afficher ПРОДАНО, Anna, 1800 RUB. Choix 4 avec une somme supérieure : refus après clôture.
7. Java : créer un autre lot sans offre, l’annuler avec le choix 7 ; Groovy : choix 8, afficher ОТМЕНЁН.

«Java создаёт аукцион, Groovy добавляет ставку. История доступна из обоих клиентов. Более низкую ставку сервер отклоняет. Аукцион с принятой ставкой нельзя отменить. После завершения победителем становится Анна с ценой тысяча восемьсот рублей. Новые ставки после завершения запрещены.»

## 5 Compatibilité et conclusion

«Я проверил исходные клиенты без перекомпиляции с новым сервером. Их три операции продолжают работать. Таким образом, приложение использует два языка и сохраняет обратную совместимость с прототипом.»

Cette phrase sur la vérification correspond aux tests fournis dans Verification_Lab3.md. Pour montrer toi-même la compatibilité sur Windows, conserve les anciens dossiers target des deux clients de ta sauvegarde et lance-les face au serveur mis à jour, avec leur ancienne API. N’utilise pas les nouvelles classes pour affirmer avoir démontré la compatibilité binaire.

Si on demande les limites : «Это учебная система без реальных платежей. Используется один технический аккаунт. H2 хранит данные в памяти. Автоматическое истечение срока определяется при обращении к серверу, отдельного таймера нет.»
