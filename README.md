# AirFrance Project - Avis de vol

## Architecture du Projet

Ce projet implémente un système de gestion des avis de vols avec une architecture microservices composée de :

- Frontend : Application Angular (port 4200)
- Review API : Service public pour la soumission d'avis (port 8081)
- Backoffice API : Service privé pour la gestion des avis par les compagnies (port 8082)
- PostgreSQL : Base de données principale (port 5432)
- RabbitMQ : Message broker pour la communication asynchrone (ports 5672, 15672)

## Choix Architecturaux

### 1. PostgreSQL comme Base de Données

Raisons:

- ACID Compliance
- Support des Types Avancés : UUID natif pour les identifiants, types temporels (timestamptz) pour les dates
- Performance : Index optimisés pour les requêtes fréquentes (par état, numéro de vol, date)


Alternatives
- MongoDB : Rejeté car :
  - Relations complexes entre entités (Company ↔ Review ↔ Flight)
  - Besoin de transactions ACID pour la cohérence des données
  - Requêtes relationnelles fréquentes (JOINs entre tables)
  - Structure de données tabulaire plus adaptée qu'un document store
- MySQL : PostgreSQL offre de meilleures performances pour les requêtes complexes

### 2. Architecture Microservices avec Queue

- Séparation des Responsabilités :
  - `review-api` : Gestion publique des avis (soumission, consultation)
  - `backoffice-api` : Gestion privée (authentification, réponses aux avis)

- Sécurité : Isolation des données sensibles

- Scalabilité : Chaque service peut être déployé et mis à l'échelle indépendamment

- Résilience : Si un service tombe, l'autre continue de fonctionner (ex: surcharge sur les avis publics n'affecte pas le backoffice qui les consomme au fil de l'eau)
 

RabbitMQ:

- Découplage Asynchrone : Les services communiquent sans dépendance directe
- Fiabilité : Garantit la livraison des messages même en cas de panne temporaire
- Traçabilité : Permet de suivre le cycle de vie des avis (SUBMITTED → TREATED)
- Performance : Traitement asynchrone évite les blocages

**Flux de Communication :**
```
Client → review-api → RabbitMQ → backoffice-api
```

### 3. Modélisation des Données

Structure des Entités :

```sql
-- Compagnies aériennes
company (id, name, email, password_hash, created_at)

-- Vols
flight (id, flight_number, company_name, origin, destination, flight_date)

-- Avis
review (id, customer_name, customer_email, flight_number, company_name, 
        rating, description, submitted_at, state, response_text, response_at)
```

Choix de Modélisation :

- UUID comme Clés Primaires : Évite les problèmes de concurrence et facilite la distribution
- État des Avis :  (SUBMITTED → TREATED → PUBLISHED / REJECTED) pour le suivi
- Index : Optimisation des requêtes par état, vol et date

### 4. Séparation des Responsabilités

Pattern MVC + Repository :

#### Contrôleurs (Controllers)
- Rôle : Point d'entrée HTTP, validation des requêtes
- Responsabilités :
  - Validation des paramètres d'entrée
  - Gestion des codes de statut HTTP
  - Sérialisation/désérialisation JSON

#### Services (Services)
- Rôle : Logique métier
- Responsabilités :
  - Implémentation des règles métiers
  - Coordination entre repositories
  - Communication avec RabbitMQ

#### Repositories
- Rôle : Accès aux données
- Responsabilités :
  - Requêtes SQL via JPA/Hibernate
  - Mapping objet-relationnel

**Exemple de Flux :**
```
Controller → Service → Repository → Database
     ↓         ↓
  Response  Business Logic
```

## Démarrage du Projet

```bash
# Démarrer postgres et rabbitmq
docker-compose up -d

# Démarrer les services backend
cd backofficeapi && mvn spring-boot:run
cd review-api && mvn spring-boot:run

# Démarrer le frontend
cd frontoffice && npm start
```

## Limitations et Améliorations Futures

### Limitations Actuelles

- Initialisation de la Queue : Il faut soumettre un avis avant de lancer le backoffice pour initialiser RabbitMQ
- Filtre par Date : Le filtrage sur les dates ne fonctionne pas
- Tests Backend : Tests générés par IA mais non finalisés
- Frontend : Écrans créés rapidement avec l'IA générative puis peaufinés toujours grâce à l'IA.
- Logs Centralisés : Absence de système de logging centralisé
- Robustesse : L'application manque de robustesse générale
- Gestion d'Erreurs : La gestion des exceptions n'est pas au niveau souhaité

### Améliorations Possibles

1. Système de Logging : Implémentation de logs centralisés
2. Tests Complets : Finalisation des tests unitaires et d'intégration back et front
3. Validation : Renforcement de la validation des données
4. Sécurité : Amélioration de la sécurité (rate limiting, validation JWT)

## Technologies Utilisées

- Backend : Spring Boot 3.2, Java 21, JPA/Hibernate
- Frontend : Angular, TypeScript
- Base de Données : PostgreSQL 15
- Message Broker : RabbitMQ 3.11
- Migration : Flyway
- Sécurité : JWT, BCrypt
