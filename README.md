# SAE MESSAGERIE

Voici les participants :
    
    BRAS Enzo
    CORRET Bryan

## Server

Pour lancer le serveur vous pouvez faire :

```
java Server
```
Cela va créer un serveur sur le port 1500 et en localhost

```
java Server [port] [adresse]
```
Cela va créer un serveur sur le port et l'adresse désiré

## Client

Pour créer un client vous devez :

```
java Client
```
Cela va créer un client sur le port 1500 en localhost

```
java Client [pseudo]
```
Cela va créer un client sur le port 1500 en localhost avec le pseudo voulu

```
java Client [pseudo] [port] [adresse]
```
Cela va créer un client sur le port 1500 en localhost avec le pseudo, le port et l'adresse voulu


## Quelques fonctionalités
### Voici les commandes disopnibles

```WHO_HERE```
Permet de savoir quelle personnes sont dans le même salon que vous ou un salon différents, ou une la liste des personnes connectées

```@pseudo``` Permet d'envoyer un message privé a une personne 

```LOGOUT``` Permet de se déconnecter

```CREATEROOM``` Permet de créer un salon

```DELETEROOM``` Permet de suprimer un salon

```JOINROOM``` Permet de rejoindre le salon 

```DISPLAYROOMS``` Permet de voir la liste de salon disponibles

### Le Parent et l'Interface
#### Parent : 
Pour la class Parent on appelle la class Thread.

Cela va nous permettre faire plusieurs chose en même temps

#### L'interface : 
Nous utilisons la class seriziable pour faire transiter la class commande dans notre réseau
