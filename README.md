# Monde de Dev (MDD)

Application web full-stack permettant aux développeurs de partager des articles et de suivre des thèmes liés à la programmation informatique.

## Technologies utilisées

### Backend
- **Java 17**
- **Spring Boot 3.5.9**
- **Spring Security** avec JWT
- **MySQL 8**
- **Maven**
- **Lombok**

### Frontend
- **Angular 14**
- **Angular Material**
- **TypeScript**
- **RxJS**

## Prérequis

- Java 17 ou supérieur
- Node.js 14 ou supérieur
- MySQL 8
- Maven 3.8+
- Angular CLI 14

## Installation

### 1. Cloner le projet

```bash
git clone https://github.com/sunny-biard/MondeDeDev.git
```

### 2. Configuration de la base de données

Créer une base de données MySQL :

```sql
CREATE DATABASE mondededev;
```

### 3. Configuration du backend

Modifier le fichier `back/src/main/resources/application.properties` avec vos identifiants MySQL :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mondededev?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=votre_username
spring.datasource.password=votre_password
```

### 4. Installation des dépendances backend

```bash
cd back
./mvnw clean install
```

### 5. Installation des dépendances frontend

```bash
cd front
npm install
```

## Démarrage de l'application

### Backend

```bash
cd back
./mvnw spring-boot:run
```

Le serveur démarrera sur `http://localhost:3001`

### Frontend

```bash
cd front
ng serve
```

L'application sera accessible sur `http://localhost:4200`

## Fonctionnalités

### Authentification
- Inscription avec email, nom d'utilisateur et mot de passe
- Connexion avec email ou nom d'utilisateur
- JWT pour la sécurisation des routes

### Gestion des thèmes
- Consultation de tous les thèmes disponibles
- Abonnement/désabonnement aux thèmes

### Gestion des articles
- Création d'articles liés à un thème
- Consultation des articles des thèmes auxquels on est abonné
- Tri des articles par date (croissant/décroissant)
- Détail d'un article avec commentaires

### Commentaires
- Ajout de commentaires sur les articles
- Affichage des commentaires avec auteur

### Profil utilisateur
- Modification du nom d'utilisateur
- Modification de l'email
- Modification du mot de passe
- Consultation des abonnements

## Structure du projet

```
mdd-api/
├── back/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/openclassrooms/mddapi/
│   │   │   │   ├── configuration/     # Configuration Spring Security & JWT
│   │   │   │   ├── controller/        # Controllers REST
│   │   │   │   ├── model/            # Entités, DTOs, Requests, Responses
│   │   │   │   ├── repository/       # Repositories JPA
│   │   │   │   └── service/          # Services métier
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
└── front/
    ├── src/
    │   ├── app/
    │   │   ├── features/
    │   │   │   └── auth/             # Module d'authentification
    │   │   ├── guards/               # Guards de navigation
    │   │   ├── interceptors/         # Intercepteur HTTP JWT
    │   │   ├── interfaces/           # Interfaces TypeScript
    │   │   ├── pages/                # Composants pages
    │   │   └── services/             # Services Angular
    │   ├── assets/
    │   └── environments/
    └── package.json
```

## API Endpoints

### Authentification
- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion
- `GET /api/auth/me` - Profil utilisateur
- `PUT /api/auth/me` - Mise à jour du profil

### Thèmes
- `GET /api/topics` - Liste des thèmes
- `GET /api/topics/{id}` - Détail d'un thème
- `POST /api/topics/{id}/subscribe` - S'abonner à un thème
- `DELETE /api/topics/{id}/subscribe` - Se désabonner d'un thème
- `GET /api/topics/subscriptions` - Mes abonnements

### Articles
- `GET /api/posts` - Liste des articles (avec tri)
- `GET /api/posts/{id}` - Détail d'un article
- `POST /api/posts` - Créer un article
- `GET /api/posts/topic/{topicId}` - Articles d'un thème

### Commentaires
- `GET /api/comments?postId={id}` - Commentaires d'un article
- `POST /api/comments` - Créer un commentaire

## Sécurité

- Les mots de passe sont hashés avec BCrypt
- Authentification par JWT (JSON Web Token)
- Durée de validité du token : 1 heure
- Les routes API sont protégées sauf `/auth/login` et `/auth/register`
