# 🚀 Guide de Déploiement - KOTIGHI AI

Ce guide détaille les différentes options de déploiement pour KOTIGHI AI.

---

## 📋 Table des Matières

- [Prérequis](#prérequis)
- [Déploiement Backend](#déploiement-backend)
  - [Render](#render)
  - [Heroku](#heroku)
  - [VPS/Cloud](#vpscloud)
  - [Docker](#docker)
- [Déploiement Android](#déploiement-android)
  - [Google Play Store](#google-play-store)
  - [Distribution Directe](#distribution-directe)
- [Configuration Production](#configuration-production)

---

## ✅ Prérequis

### Backend
- Python 3.9+
- PostgreSQL 13+ (recommandé pour production)
- Compte sur une plateforme cloud (Render, Heroku, AWS, etc.)

### Android
- Android Studio Arctic Fox+
- JDK 11+
- Compte Google Play Developer (pour Play Store)
- Certificat de signature (keystore)

---

## 🐍 Déploiement Backend

### Option 1: Render (Recommandé - Gratuit)

1. **Créer un compte sur [Render](https://render.com)**

2. **Créer une base de données PostgreSQL**
   - Dashboard → New → PostgreSQL
   - Nom : `kotighi-db`
   - Copier l'URL de connexion (Internal Database URL)

3. **Créer un Web Service**
   - Dashboard → New → Web Service
   - Connecter votre repo GitHub
   - Configuration :
     ```yaml
     Name: kotighi-api
     Environment: Python 3
     Build Command: cd Kotighi-AI && pip install -r requirements.txt && python train_models.py
     Start Command: cd Kotighi-AI && uvicorn api_kotighi:app --host 0.0.0.0 --port $PORT
     ```

4. **Variables d'environnement**
   ```
   DATABASE_URL=<votre_url_postgresql>
   SECRET_KEY=<générer_avec_openssl_rand_hex_32>
   API_ENV=production
   ```

5. **Déployer**
   - Render déploie automatiquement à chaque push sur `main`

### Option 2: Heroku

```bash
# Installer Heroku CLI
curl https://cli-assets.heroku.com/install.sh | sh

# Login
heroku login

# Créer l'app
cd Kotighi-AI
heroku create kotighi-api

# Ajouter PostgreSQL
heroku addons:create heroku-postgresql:mini

# Configurer les variables
heroku config:set SECRET_KEY=$(openssl rand -hex 32)
heroku config:set API_ENV=production

# Créer Procfile
echo "web: uvicorn api_kotighi:app --host 0.0.0.0 --port \$PORT" > Procfile

# Déployer
git push heroku main

# Entraîner les modèles
heroku run python train_models.py
```

### Option 3: VPS/Cloud (AWS, DigitalOcean, etc.)

```bash
# Se connecter au serveur
ssh user@votre-serveur.com

# Installer les dépendances
sudo apt update
sudo apt install python3.9 python3-pip postgresql nginx

# Cloner le projet
git clone https://github.com/votre-repo/kotighi-ai.git
cd kotighi-ai/Kotighi-AI

# Créer environnement virtuel
python3.9 -m venv venv
source venv/bin/activate

# Installer dépendances
pip install -r requirements.txt
pip install gunicorn

# Configurer PostgreSQL
sudo -u postgres psql
CREATE DATABASE kotighi_db;
CREATE USER kotighi WITH PASSWORD 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON DATABASE kotighi_db TO kotighi;
\q

# Créer .env
cat > .env << EOF
DATABASE_URL=postgresql://kotighi:votre_mot_de_passe@localhost:5432/kotighi_db
SECRET_KEY=$(openssl rand -hex 32)
API_ENV=production
EOF

# Entraîner les modèles
python train_models.py

# Créer service systemd
sudo nano /etc/systemd/system/kotighi-api.service
```

Contenu du service :
```ini
[Unit]
Description=KOTIGHI AI API
After=network.target

[Service]
User=votre-user
WorkingDirectory=/home/votre-user/kotighi-ai/Kotighi-AI
Environment="PATH=/home/votre-user/kotighi-ai/Kotighi-AI/venv/bin"
EnvironmentFile=/home/votre-user/kotighi-ai/Kotighi-AI/.env
ExecStart=/home/votre-user/kotighi-ai/Kotighi-AI/venv/bin/gunicorn -w 4 -k uvicorn.workers.UvicornWorker api_kotighi:app --bind 0.0.0.0:8000

[Install]
WantedBy=multi-user.target
```

```bash
# Démarrer le service
sudo systemctl daemon-reload
sudo systemctl start kotighi-api
sudo systemctl enable kotighi-api

# Configurer Nginx
sudo nano /etc/nginx/sites-available/kotighi-api
```

Configuration Nginx :
```nginx
server {
    listen 80;
    server_name votre-domaine.com;

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Activer le site
sudo ln -s /etc/nginx/sites-available/kotighi-api /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx

# Installer SSL avec Let's Encrypt
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d votre-domaine.com
```

### Option 4: Docker

```bash
# Build et lancer avec Docker Compose
docker-compose up -d

# Vérifier les logs
docker-compose logs -f api

# Arrêter
docker-compose down
```

---

## 📱 Déploiement Android

### Créer un Keystore

```bash
# Générer un keystore pour signer l'APK
keytool -genkey -v -keystore kotighi-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias kotighi-key

# Sauvegarder le mot de passe et l'alias en lieu sûr !
```

### Configurer la Signature

Créer `app/keystore.properties` :
```properties
storePassword=votre_store_password
keyPassword=votre_key_password
keyAlias=kotighi-key
storeFile=../kotighi-release-key.jks
```

Modifier `app/build.gradle` :
```groovy
android {
    signingConfigs {
        release {
            def keystorePropertiesFile = rootProject.file("app/keystore.properties")
            def keystoreProperties = new Properties()
            keystoreProperties.load(new FileInputStream(keystorePropertiesFile))

            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile file(keystoreProperties['storeFile'])
            storePassword keystoreProperties['storePassword']
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            
            // URL de production
            buildConfigField "String", "API_BASE_URL", "\"https://votre-api.com\""
        }
    }
}
```

### Build Release

```bash
# Nettoyer et builder
./gradlew clean
./gradlew assembleRelease

# L'APK signé sera dans :
# app/build/outputs/apk/release/app-release.apk
```

### Google Play Store

1. **Créer un compte développeur** ($25 unique)
   - [Google Play Console](https://play.google.com/console)

2. **Créer une application**
   - Nom : KOTIGHI AI
   - Catégorie : Outils / Santé et Fitness

3. **Préparer les assets**
   - Icône : 512x512 PNG
   - Screenshots : 2-8 images par type d'appareil
   - Feature Graphic : 1024x500
   - Description courte (80 caractères max)
   - Description complète (4000 caractères max)

4. **Configurer la fiche**
   - Politique de confidentialité (obligatoire)
   - Classification du contenu
   - Prix et distribution

5. **Upload l'APK/AAB**
   ```bash
   # Générer un Android App Bundle (recommandé)
   ./gradlew bundleRelease
   
   # Le fichier sera dans :
   # app/build/outputs/bundle/release/app-release.aab
   ```

6. **Tests internes/fermés/ouverts**
   - Commencer par des tests internes
   - Puis tests fermés avec un groupe restreint
   - Enfin tests ouverts avant production

7. **Publication**
   - Soumettre pour révision
   - Attendre validation (1-7 jours)
   - Publication automatique après approbation

### Distribution Directe (APK)

```bash
# Héberger l'APK sur votre serveur
scp app/build/outputs/apk/release/app-release.apk user@serveur:/var/www/downloads/

# Créer une page de téléchargement
# https://votre-site.com/download
```

⚠️ **Avertissement** : Les utilisateurs devront activer "Sources inconnues" dans les paramètres Android.

---

## ⚙️ Configuration Production

### Backend

1. **Sécurité**
   ```bash
   # Générer une clé secrète forte
   openssl rand -hex 32
   
   # Configurer HTTPS (obligatoire)
   # Utiliser Let's Encrypt ou un certificat SSL
   ```

2. **Base de données**
   - Utiliser PostgreSQL (pas SQLite)
   - Activer les backups automatiques
   - Configurer les connexions pooling

3. **Monitoring**
   - Configurer des logs structurés
   - Utiliser Sentry pour le tracking d'erreurs
   - Mettre en place des alertes

4. **Performance**
   - Activer le cache Redis (optionnel)
   - Configurer un CDN pour les assets statiques
   - Optimiser les requêtes DB

### Android

1. **Sécurité**
   - Désactiver `usesCleartextTraffic` en production
   - Utiliser uniquement HTTPS
   - Obfusquer le code avec ProGuard
   - Valider toutes les entrées utilisateur

2. **Performance**
   - Optimiser les images (WebP)
   - Minimiser les requêtes réseau
   - Implémenter un cache local

3. **Analytics** (optionnel)
   - Google Analytics
   - Firebase Analytics
   - Crashlytics pour les rapports de crash

---

## 🔄 Mises à Jour

### Backend

```bash
# Pull les dernières modifications
git pull origin main

# Redémarrer le service
sudo systemctl restart kotighi-api

# Ou avec Docker
docker-compose pull
docker-compose up -d
```

### Android

1. Incrémenter `versionCode` et `versionName` dans `build.gradle`
2. Builder un nouveau APK/AAB
3. Upload sur Play Store
4. Les utilisateurs recevront la mise à jour automatiquement

---

## 📊 Checklist de Déploiement

### Backend
- [ ] Variables d'environnement configurées
- [ ] Base de données PostgreSQL en production
- [ ] HTTPS activé
- [ ] Backups automatiques configurés
- [ ] Monitoring en place
- [ ] Tests passent en CI/CD
- [ ] Documentation API à jour

### Android
- [ ] Keystore créé et sauvegardé
- [ ] URL API de production configurée
- [ ] ProGuard activé
- [ ] Tests passent
- [ ] Screenshots et assets préparés
- [ ] Politique de confidentialité publiée
- [ ] APK/AAB signé et testé

---

## 🆘 Dépannage

### Backend ne démarre pas
```bash
# Vérifier les logs
journalctl -u kotighi-api -f

# Vérifier la connexion DB
psql $DATABASE_URL

# Tester manuellement
cd Kotighi-AI
source venv/bin/activate
uvicorn api_kotighi:app --reload
```

### Android ne se connecte pas à l'API
- Vérifier l'URL dans `BuildConfig.API_BASE_URL`
- Vérifier que l'API est accessible (curl/Postman)
- Vérifier les permissions réseau dans le manifest
- Vérifier les logs Android : `adb logcat | grep Kotighi`

---

## 📞 Support

- 📧 Email : support@vulsoft.com
- 🐛 Issues : [GitHub Issues](https://github.com/votre-repo/kotighi-ai/issues)
- 📖 Documentation : [Wiki](https://github.com/votre-repo/kotighi-ai/wiki)

---

**Bon déploiement ! 🚀**
