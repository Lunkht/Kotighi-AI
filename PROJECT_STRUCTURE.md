# 📁 Structure du Projet - KOTIGHI AI

Documentation complète de l'architecture du projet.

---

## 🌳 Arborescence

```
kotighi-ai/
│
├── 📱 app/                              # Application Android
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/vulsoft/kotighiai/
│   │   │   │   ├── MainActivity.java           # Activité principale
│   │   │   │   ├── LoginActivity.java          # Authentification
│   │   │   │   ├── SignupActivity.java         # Inscription
│   │   │   │   ├── CyberActivity.java          # Module cybersécurité
│   │   │   │   ├── SanteActivity.java          # Module santé
│   │   │   │   ├── ChatActivity.java           # Chatbot
│   │   │   │   ├── HistoryActivity.java        # Historique
│   │   │   │   ├── ProfileActivity.java        # Profil utilisateur
│   │   │   │   ├── SettingsActivity.java       # Paramètres
│   │   │   │   ├── NotificationsActivity.java  # Notifications
│   │   │   │   ├── ApiClient.java              # Client API REST
│   │   │   │   ├── ChatAdapter.java            # Adapter chat
│   │   │   │   ├── HistoryAdapter.java         # Adapter historique
│   │   │   │   ├── NotificationAdapter.java    # Adapter notifications
│   │   │   │   ├── SurveillanceAdapter.java    # Adapter surveillance
│   │   │   │   ├── IpTargetAdapter.java        # Adapter IPs
│   │   │   │   ├── PhishingService.java        # Service anti-phishing
│   │   │   │   ├── NotificationUtil.java       # Utilitaires notifications
│   │   │   │   └── KotighiApp.java             # Application class
│   │   │   ├── res/
│   │   │   │   ├── drawable/                    # Icônes et images
│   │   │   │   ├── layout/                      # Layouts XML
│   │   │   │   ├── values/                      # Strings, colors, themes
│   │   │   │   ├── values-night/                # Thème sombre
│   │   │   │   ├── mipmap-*/                    # Icônes app
│   │   │   │   └── xml/
│   │   │   │       ├── network_security_config.xml  # Config réseau
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml              # Manifest Android
│   │   ├── test/                                # Tests unitaires
│   │   │   └── java/com/vulsoft/kotighiai/
│   │   │       ├── ValidationTest.java
│   │   │       └── ApiClientTest.java
│   │   └── androidTest/                         # Tests instrumentés
│   │       └── java/com/vulsoft/kotighiai/
│   │           └── ExampleInstrumentedTest.java
│   ├── build.gradle                             # Configuration Gradle app
│   └── proguard-rules.pro                       # Règles ProGuard
│
├── 🐍 Kotighi-AI/                       # Backend Python
│   ├── api_kotighi.py                           # API FastAPI principale
│   ├── app_kotighi.py                           # Interface Streamlit
│   ├── train_models.py                          # Entraînement modèles ML
│   ├── kotighi_ai.py                            # Modèles IA de base
│   ├── cyber_nslkdd.py                          # Module cyber avancé
│   ├── rapport_pdf.py                           # Génération PDF
│   ├── requirements.txt                         # Dépendances Python
│   ├── .env.example                             # Template variables env
│   ├── .env                                     # Variables env (gitignored)
│   ├── Dockerfile                               # Image Docker
│   ├── .dockerignore                            # Exclusions Docker
│   ├── pytest.ini                               # Config pytest
│   ├── .coveragerc                              # Config coverage
│   ├── render.yaml                              # Config Render
│   ├── config.toml                              # Config Streamlit
│   ├── kotighi.db                               # Base SQLite (dev)
│   ├── *.pkl                                    # Modèles ML entraînés
│   │   ├── model_cyber.pkl
│   │   ├── scaler_cyber.pkl
│   │   ├── model_sante.pkl
│   │   ├── labels_sante.pkl
│   │   └── conseils_sante.pkl
│   └── tests/                                   # Tests backend
│       ├── __init__.py
│       └── test_api.py
│
├── 🐳 Docker/                           # Configuration Docker
│   ├── docker-compose.yml                       # Orchestration services
│   └── (Dockerfile dans Kotighi-AI/)
│
├── 🔧 gradle/                           # Configuration Gradle
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml                       # Catalogue versions
│
├── 🤖 .github/                          # CI/CD GitHub Actions
│   └── workflows/
│       ├── python-ci.yml                        # CI Backend
│       └── android-ci.yml                       # CI Android
│
├── 📜 scripts/                          # Scripts utilitaires
│   ├── setup.sh                                 # Setup automatique
│   └── health-check.sh                          # Vérification santé
│
├── 📚 Documentation/
│   ├── README.md                                # Documentation principale
│   ├── QUICKSTART.md                            # Démarrage rapide
│   ├── DEPLOYMENT.md                            # Guide déploiement
│   ├── CONTRIBUTING.md                          # Guide contribution
│   ├── SECURITY.md                              # Politique sécurité
│   ├── CHANGELOG.md                             # Historique versions
│   ├── LICENSE                                  # Licence MIT
│   └── PROJECT_STRUCTURE.md                     # Ce fichier
│
├── ⚙️ Configuration/
│   ├── .gitignore                               # Exclusions Git
│   ├── .editorconfig                            # Config éditeurs
│   ├── build.gradle                             # Gradle racine
│   ├── settings.gradle                          # Settings Gradle
│   ├── gradle.properties                        # Propriétés Gradle
│   ├── local.properties                         # Config locale (gitignored)
│   ├── gradlew                                  # Wrapper Gradle (Unix)
│   ├── gradlew.bat                              # Wrapper Gradle (Windows)
│   └── Makefile                                 # Commandes Make
│
└── 🗑️ Fichiers temporaires (gitignored)
    ├── .gradle/
    ├── .idea/
    ├── build/
    ├── __pycache__/
    ├── venv/
    └── *.db
```

---

## 🏗️ Architecture Technique

### Backend (Python/FastAPI)

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT (Android/Web)                  │
└────────────────────┬────────────────────────────────────┘
                     │ HTTPS/REST
                     ▼
┌─────────────────────────────────────────────────────────┐
│                   FastAPI Application                    │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Authentication (JWT)                             │  │
│  │  - /signup, /login, /token                        │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Prediction Endpoints                             │  │
│  │  - /predict/cyber (ML Cybersécurité)             │  │
│  │  - /predict/sante (ML Santé)                     │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Data Endpoints                                   │  │
│  │  - /historique (Historique utilisateur)          │  │
│  │  - /chat (Chatbot IA)                            │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Machine Learning Models                     │
│  ┌──────────────────┐  ┌──────────────────────────┐    │
│  │ Random Forest    │  │ Random Forest            │    │
│  │ (Cyber)          │  │ (Santé)                  │    │
│  │ - 6 features     │  │ - 14 symptômes           │    │
│  │ - 2 classes      │  │ - 8 diagnostics          │    │
│  └──────────────────┘  └──────────────────────────┘    │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                Database (PostgreSQL/SQLite)              │
│  ┌──────────────────┐  ┌──────────────────────────┐    │
│  │ users            │  │ historique               │    │
│  │ - id             │  │ - id                     │    │
│  │ - username       │  │ - user_id                │    │
│  │ - hashed_password│  │ - type_analyse           │    │
│  │ - full_name      │  │ - resultat               │    │
│  │ - role           │  │ - details                │    │
│  │ - created_at     │  │ - created_at             │    │
│  └──────────────────┘  └──────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### Frontend (Android)

```
┌─────────────────────────────────────────────────────────┐
│                    User Interface                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Activities                                       │  │
│  │  - LoginActivity (Authentification)              │  │
│  │  - MainActivity (Dashboard)                      │  │
│  │  - CyberActivity (Analyse réseau)                │  │
│  │  - SanteActivity (Diagnostic médical)            │  │
│  │  - ChatActivity (Chatbot)                        │  │
│  │  - HistoryActivity (Historique)                  │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                  Business Logic                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │  ApiClient (Singleton)                            │  │
│  │  - HTTP requests (OkHttp)                        │  │
│  │  - JSON parsing (Gson)                           │  │
│  │  - Token management                              │  │
│  │  - Async callbacks                               │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Adapters (RecyclerView)                         │  │
│  │  - ChatAdapter                                   │  │
│  │  - HistoryAdapter                                │  │
│  │  - NotificationAdapter                           │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Services                                        │  │
│  │  - PhishingService (Background)                  │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                    API Backend                           │
│                (FastAPI - voir ci-dessus)                │
└─────────────────────────────────────────────────────────┘
```

---

## 🔄 Flux de Données

### Authentification

```
Android App → POST /signup → FastAPI
                              ↓
                         Hash password (bcrypt)
                              ↓
                         Save to DB
                              ↓
                         Generate JWT token
                              ↓
Android App ← Token ← FastAPI
```

### Analyse Cybersécurité

```
Android App → POST /predict/cyber + JWT → FastAPI
                                            ↓
                                       Validate JWT
                                            ↓
                                       Extract features
                                            ↓
                                       Scale features
                                            ↓
                                       ML Prediction
                                            ↓
                                       Save to historique
                                            ↓
Android App ← Result (verdict, confiance) ← FastAPI
```

### Diagnostic Santé

```
Android App → POST /predict/sante + JWT → FastAPI
                                            ↓
                                       Validate JWT
                                            ↓
                                       Extract symptoms
                                            ↓
                                       ML Prediction
                                            ↓
                                       Get conseils
                                            ↓
                                       Check urgence
                                            ↓
                                       Save to historique
                                            ↓
Android App ← Result (diagnostic, conseils) ← FastAPI
```

---

## 🗄️ Schéma de Base de Données

```sql
-- Table users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table historique
CREATE TABLE historique (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    type_analyse VARCHAR(20) NOT NULL,  -- 'Cyber' ou 'Sante'
    resultat VARCHAR(100),
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pour performance
CREATE INDEX idx_historique_user_id ON historique(user_id);
CREATE INDEX idx_historique_created_at ON historique(created_at DESC);
```

---

## 🔐 Sécurité

### Couches de Sécurité

1. **Transport** : HTTPS obligatoire en production
2. **Authentification** : JWT avec expiration
3. **Autorisation** : Vérification du token sur chaque endpoint protégé
4. **Données** : Hashage bcrypt des mots de passe
5. **Validation** : Pydantic pour valider toutes les entrées
6. **Network** : Network Security Config sur Android
7. **Code** : ProGuard pour obfusquer l'APK

---

## 📦 Dépendances Principales

### Backend (Python)

| Package | Version | Usage |
|---------|---------|-------|
| fastapi | latest | Framework API REST |
| uvicorn | latest | Serveur ASGI |
| sqlalchemy | latest | ORM base de données |
| scikit-learn | ≥1.3.0 | Machine Learning |
| pandas | ≥2.0.0 | Manipulation données |
| python-jose | latest | JWT tokens |
| passlib | latest | Hashage mots de passe |
| pytest | latest | Tests |

### Android (Java)

| Library | Version | Usage |
|---------|---------|-------|
| OkHttp | 4.12.0 | Client HTTP |
| Gson | 2.10.1 | Parsing JSON |
| Material | 1.13.0 | Material Design |
| AndroidX | latest | Composants Android |

---

## 🚀 Déploiement

### Environnements

| Environnement | Backend | Android | Base de Données |
|---------------|---------|---------|-----------------|
| Développement | localhost:8000 | Émulateur | SQLite |
| Staging | staging.kotighi.com | TestFlight/Beta | PostgreSQL |
| Production | api.kotighi.com | Play Store | PostgreSQL |

---

## 📊 Métriques & Monitoring

### Backend

- Logs structurés (JSON)
- Métriques Prometheus (optionnel)
- Sentry pour erreurs (optionnel)
- Health check endpoint : `/`

### Android

- Crashlytics (optionnel)
- Analytics (optionnel)
- Logs locaux avec filtrage

---

## 🔧 Configuration

### Variables d'Environnement (Backend)

```bash
DATABASE_URL=postgresql://user:pass@host:5432/db
SECRET_KEY=votre_cle_secrete_32_caracteres
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=1440
API_ENV=production
```

### Build Config (Android)

```groovy
buildConfigField "String", "API_BASE_URL", "\"https://api.kotighi.com\""
```

---

## 📞 Support

- 📧 Email : support@vulsoft.com
- 🐛 Issues : [GitHub Issues](https://github.com/votre-repo/kotighi-ai/issues)
- 📖 Wiki : [Documentation complète](https://github.com/votre-repo/kotighi-ai/wiki)

---

**Documentation maintenue par l'équipe VULSOFT** 🛡️
