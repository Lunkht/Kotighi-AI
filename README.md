# 🛡️ KOTIGHI AI - Cybersécurité & Santé

Application mobile Android combinant **détection d'intrusion réseau** et **analyse médicale de symptômes** avec intelligence artificielle.

![Android](https://img.shields.io/badge/Android-24%2B-green)
![Python](https://img.shields.io/badge/Python-3.9+-blue)
![FastAPI](https://img.shields.io/badge/FastAPI-Latest-teal)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 📱 Fonctionnalités

### 🔐 Module Cybersécurité
- Analyse du trafic réseau en temps réel
- Détection de 5 types d'attaques : DoS, DDoS, Probe, R2L, U2R
- Score de risque avec visualisation interactive
- Recommandations automatiques de sécurité
- Service anti-phishing en arrière-plan

### 🏥 Module Santé
- Analyse de 14 symptômes médicaux
- Prédiction de 8 pathologies avec probabilités
- Alerte urgence pour cas critiques
- Conseils de prévention personnalisés
- Historique des diagnostics

### 📊 Dashboard
- Statistiques d'activité réseau 24h
- Répartition des types d'attaques
- Historique des analyses
- Profil utilisateur avec rôles (Admin, Analyste, Médecin)

---

## 🏗️ Architecture

```
kotighi-ai/
├── app/                          # Application Android (Java)
│   ├── src/main/
│   │   ├── java/com/vulsoft/kotighiai/
│   │   └── res/                  # Ressources UI
│   └── build.gradle
├── api_kotighi.py                # API FastAPI (SQLite)
├── api_kotighi_supabase.py       # API FastAPI (Supabase)
├── app_kotighi.py                # Interface web (Streamlit)
├── train_models.py               # Entraînement des modèles
├── requirements.txt              # Dépendances Python
├── build.gradle                  # Root Gradle build
├── settings.gradle               # Root Gradle settings
├── README.md                     # Ce document
└── *.pkl                         # Modèles ML pré-entraînés
```

---

## 🚀 Installation & Déploiement

### Prérequis

- **Android Studio** Arctic Fox ou supérieur
- **JDK 11+**
- **Python 3.9+**
- **PostgreSQL** (optionnel, SQLite par défaut)

### 1️⃣ Configuration du Backend

```bash
cd Kotighi-AI

# Créer un environnement virtuel
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# Installer les dépendances
pip install -r requirements.txt

# Créer le fichier .env
cp .env.example .env
# Éditer .env avec vos configurations

# Entraîner les modèles (si nécessaire)
python train_models.py

# Lancer l'API
uvicorn api_kotighi:app --host 0.0.0.0 --port 8000 --reload
```

L'API sera accessible sur `http://localhost:8000`

### 2️⃣ Configuration de l'Application Android

1. Ouvrir le projet dans Android Studio
2. Créer `local.properties` si nécessaire :
   ```properties
   sdk.dir=/path/to/Android/sdk
   ```

3. Configurer l'URL de l'API dans `app/build.gradle` :
   ```groovy
   buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:8000\""  // Émulateur
   // ou
   buildConfigField "String", "API_BASE_URL", "\"https://votre-api.com\""  // Production
   ```

4. Synchroniser Gradle et compiler :
   ```bash
   ./gradlew clean build
   ```

5. Lancer l'application sur un émulateur ou appareil physique

### 3️⃣ Déploiement Production

#### Backend (Render / Heroku / VPS)

```bash
# Render (render.yaml déjà configuré)
git push origin main

# Docker
docker build -t kotighi-api -f Kotighi-AI/Dockerfile .
docker run -p 8000:8000 --env-file .env kotighi-api
```

#### Application Android

```bash
# Build APK de production
./gradlew assembleRelease

# APK généré dans : app/build/outputs/apk/release/
```

---

## 🔐 Sécurité

### Variables d'Environnement

Créer un fichier `.env` dans `Kotighi-AI/` :

```env
# Base de données
DATABASE_URL=postgresql://user:password@host:5432/kotighi_db

# JWT
SECRET_KEY=votre_cle_secrete_tres_longue_et_aleatoire
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=1440

# API
API_ENV=production
```

### Bonnes Pratiques

- ✅ Ne jamais commiter `.env` ou `local.properties`
- ✅ Utiliser HTTPS en production
- ✅ Changer les mots de passe par défaut
- ✅ Activer ProGuard pour l'APK de release
- ✅ Valider toutes les entrées utilisateur

---

## 🧪 Tests

### Backend

```bash
cd Kotighi-AI
pytest tests/ -v --cov=.
```

### Android

```bash
# Tests unitaires
./gradlew test

# Tests instrumentés
./gradlew connectedAndroidTest
```

---

## 📚 API Documentation

Une fois l'API lancée, accéder à :
- **Swagger UI** : http://localhost:8000/docs
- **ReDoc** : http://localhost:8000/redoc

### Endpoints Principaux

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/signup` | Créer un compte |
| POST | `/login` | Authentification |
| POST | `/predict/cyber` | Analyse cybersécurité |
| POST | `/predict/sante` | Diagnostic médical |
| GET | `/historique` | Historique utilisateur |
| POST | `/chat` | Chatbot IA |

---

## 🛠️ Technologies Utilisées

### Frontend (Android)
- Java 11
- Material Design 3
- OkHttp (requêtes HTTP)
- Gson (parsing JSON)

### Backend
- FastAPI (API REST)
- Streamlit (interface web)
- Scikit-learn (ML)
- SQLAlchemy (ORM)
- PostgreSQL / SQLite
- **Supabase** (base de données cloud) ⭐ NEW
- JWT (authentification)

### Machine Learning
- Random Forest Classifier
- Gradient Boosting
- StandardScaler
- Dataset NSL-KDD (cybersécurité)

---

## 📊 Modèles IA

### Cybersécurité
- **Entrées** : 6 features (requêtes/min, durée, octets, ports scannés, taux d'erreur, flags)
- **Sortie** : Normal (0) ou Attaque (1)
- **Précision** : ~99%

### Santé
- **Entrées** : 14 symptômes binaires
- **Sorties** : 8 diagnostics possibles
- **Précision** : ~95%

---

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/amelioration`)
3. Commit les changements (`git commit -m 'Ajout fonctionnalité'`)
4. Push vers la branche (`git push origin feature/amelioration`)
5. Ouvrir une Pull Request

---

## ⚠️ Avertissement

Ce projet est **éducatif et démonstratif**. Il ne remplace pas :
- Un expert en cybersécurité professionnel
- Un médecin ou professionnel de santé

Ne pas utiliser en environnement de production critique sans validation par des experts certifiés.

---

## 📄 License

MIT License - Voir [LICENSE](LICENSE) pour plus de détails

---

## 👤 Auteur

**VULSOFT**  
Plateforme d'Intelligence Analytique

---

## 📞 Support

- 📧 Email : support@vulsoft.com
- 🐛 Issues : [GitHub Issues](https://github.com/votre-repo/kotighi-ai/issues)
- 📖 Wiki : [Documentation complète](https://github.com/votre-repo/kotighi-ai/wiki)

---

Fait avec ❤️ et ☕ | Propulsé par FastAPI & Android
