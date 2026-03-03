# ⚡ Démarrage Rapide - KOTIGHI AI

Guide pour démarrer en 5 minutes.

---

## 🚀 Installation Automatique (Recommandé)

```bash
# Cloner le projet
git clone https://github.com/votre-repo/kotighi-ai.git
cd kotighi-ai

# Lancer le script de setup
./scripts/setup.sh

# Ou avec Make
make setup
```

Le script configure automatiquement :
- ✅ Environnement virtuel Python
- ✅ Dépendances backend et Android
- ✅ Modèles ML
- ✅ Fichiers de configuration
- ✅ Tests

---

## 🔧 Installation Manuelle

### 1. Backend (5 min)

```bash
cd Kotighi-AI

# Créer environnement virtuel
python3 -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# Installer dépendances
pip install -r requirements.txt

# Configurer
cp .env.example .env
# Éditer .env avec vos valeurs

# Entraîner les modèles
python train_models.py

# Lancer l'API
uvicorn api_kotighi:app --reload
```

✅ API disponible sur http://localhost:8000  
✅ Documentation sur http://localhost:8000/docs

### 2. Android (3 min)

```bash
# Rendre gradlew exécutable
chmod +x gradlew

# Synchroniser Gradle
./gradlew build

# Ouvrir dans Android Studio
# File → Open → Sélectionner le dossier du projet

# Lancer l'app
# Run → Run 'app'
```

---

## 🐳 Avec Docker (Le plus rapide)

```bash
# Démarrer tout avec Docker Compose
docker-compose up -d

# Vérifier les logs
docker-compose logs -f
```

✅ API : http://localhost:8000  
✅ Streamlit : http://localhost:8501  
✅ PostgreSQL : localhost:5432

---

## 🧪 Tester l'Installation

### Backend

```bash
# Tests
cd Kotighi-AI
pytest tests/ -v

# Tester l'API manuellement
curl http://localhost:8000/
# Devrait retourner : {"status":"online","message":"API KOTIGHI AI Sécurisée"}
```

### Android

```bash
# Tests unitaires
./gradlew test

# Lint
./gradlew lint
```

---

## 📱 Premiers Pas

### 1. Créer un Compte

**Via l'API** :
```bash
curl -X POST http://localhost:8000/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "Test123!",
    "full_name": "Test User",
    "role": "Utilisateur"
  }'
```

**Via l'App Android** :
1. Lancer l'app
2. Cliquer sur "Inscription"
3. Remplir le formulaire
4. Se connecter

### 2. Tester une Analyse Cyber

```bash
# Obtenir un token
TOKEN=$(curl -X POST http://localhost:8000/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test_user","password":"Test123!"}' \
  | jq -r '.access_token')

# Analyser du trafic
curl -X POST http://localhost:8000/predict/cyber \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "requetes_min": 1000,
    "duree": 2,
    "octets": 100,
    "ports_scanes": 50,
    "taux_erreur": 0.8,
    "flag_suspect": 1
  }'
```

### 3. Tester un Diagnostic Santé

```bash
curl -X POST http://localhost:8000/predict/sante \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fievre": 1,
    "toux": 1,
    "fatigue": 1,
    "maux_tete": 0,
    "douleur_gorge": 0,
    "nausees": 0,
    "douleur_thorax": 0,
    "essoufflement": 0,
    "diarrhee": 0,
    "frissons": 0,
    "perte_odorat": 1,
    "douleurs_musculaires": 1,
    "palpitations": 0,
    "vertiges": 0
  }'
```

---

## 🔑 Comptes de Démonstration

L'API inclut des comptes pré-configurés :

| Username | Password | Rôle |
|----------|----------|------|
| admin | kotighi2024 | Administrateur |
| analyste | analyse123 | Analyste Cyber |
| medecin | sante456 | Médecin |

---

## 🛠️ Commandes Utiles

```bash
# Afficher toutes les commandes
make help

# Lancer l'API
make run-api

# Lancer Streamlit
make run-streamlit

# Tests complets
make test

# Build APK debug
make build-debug

# Build APK release
make build-release

# Docker
make docker-up
make docker-down
make docker-logs

# Nettoyage
make clean
```

---

## 🐛 Dépannage

### L'API ne démarre pas

```bash
# Vérifier Python
python3 --version  # Doit être 3.9+

# Vérifier les dépendances
cd Kotighi-AI
source venv/bin/activate
pip list

# Vérifier les modèles
ls -la *.pkl

# Relancer l'entraînement
python train_models.py
```

### L'app Android ne compile pas

```bash
# Nettoyer Gradle
./gradlew clean

# Vérifier Java
java -version  # Doit être 11+

# Synchroniser Gradle
./gradlew build --refresh-dependencies

# Vérifier local.properties
cat local.properties
```

### L'app ne se connecte pas à l'API

1. Vérifier que l'API tourne : `curl http://localhost:8000/`
2. Vérifier l'URL dans `app/build.gradle` :
   ```groovy
   buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:8000\""
   ```
3. Pour émulateur : utiliser `10.0.2.2`
4. Pour appareil physique : utiliser l'IP locale (ex: `192.168.1.100`)

---

## 📚 Ressources

- 📖 [README complet](README.md)
- 🚀 [Guide de déploiement](DEPLOYMENT.md)
- 🤝 [Guide de contribution](CONTRIBUTING.md)
- 🔒 [Politique de sécurité](SECURITY.md)
- 📝 [Changelog](CHANGELOG.md)

---

## 💬 Support

- 🐛 [Signaler un bug](https://github.com/votre-repo/kotighi-ai/issues)
- 💡 [Proposer une fonctionnalité](https://github.com/votre-repo/kotighi-ai/issues)
- 📧 Email : support@vulsoft.com

---

**Prêt à coder ! 🚀**
