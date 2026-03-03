# 🚀 Configuration Supabase - KOTIGHI AI

Guide complet pour configurer Supabase avec KOTIGHI AI.

---

## 📋 Prérequis

- Compte Supabase (gratuit) : https://supabase.com
- Projet Supabase créé
- URL du projet : `https://izeukppgxagzvwfhiqra.supabase.co`

---

## 🔧 Étape 1 : Récupérer les Clés API

### 1.1 Accéder au Dashboard Supabase

1. Connectez-vous à https://supabase.com
2. Sélectionnez votre projet
3. Allez dans **Settings** → **API**

### 1.2 Copier les Clés

Vous aurez besoin de :

- **Project URL** : `https://izeukppgxagzvwfhiqra.supabase.co`
- **anon public** (clé publique) : `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
- **service_role** (clé admin) : `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

⚠️ **IMPORTANT** : Ne jamais commiter la clé `service_role` dans Git !

---

## 🗄️ Étape 2 : Créer les Tables

### 2.1 Ouvrir le SQL Editor

1. Dashboard Supabase → **SQL Editor**
2. Cliquer sur **New Query**

### 2.2 Exécuter le Script SQL

Copiez et exécutez ce SQL :

```sql
-- Table users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(50) DEFAULT 'Utilisateur',
    email VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table historique
CREATE TABLE IF NOT EXISTS historique (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    type_analyse VARCHAR(20) NOT NULL CHECK (type_analyse IN ('Cyber', 'Sante')),
    resultat VARCHAR(100),
    details TEXT,
    confiance FLOAT,
    urgent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table profils
CREATE TABLE IF NOT EXISTS profils (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    avatar_url TEXT,
    bio TEXT,
    specialite VARCHAR(100),
    organisation VARCHAR(100),
    preferences JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table notifications
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    titre VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'info',
    lu BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pour performance
CREATE INDEX IF NOT EXISTS idx_historique_user_id ON historique(user_id);
CREATE INDEX IF NOT EXISTS idx_historique_created_at ON historique(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_historique_type ON historique(type_analyse);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_lu ON notifications(lu);

-- Trigger pour updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_profils_updated_at BEFORE UPDATE ON profils
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

Cliquez sur **Run** pour exécuter.

✅ Vous devriez voir : "Success. No rows returned"

---

## 🔐 Étape 3 : Configurer Row Level Security (RLS)

### 3.1 Activer RLS

```sql
-- Activer RLS sur toutes les tables
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE historique ENABLE ROW LEVEL SECURITY;
ALTER TABLE profils ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;
```

### 3.2 Créer les Policies

⚠️ **Note** : Pour simplifier, nous allons désactiver RLS temporairement car nous gérons l'authentification via JWT côté API.

```sql
-- Désactiver RLS pour permettre l'accès via service_role
ALTER TABLE users DISABLE ROW LEVEL SECURITY;
ALTER TABLE historique DISABLE ROW LEVEL SECURITY;
ALTER TABLE profils DISABLE ROW LEVEL SECURITY;
ALTER TABLE notifications DISABLE ROW LEVEL SECURITY;
```

Ou si vous voulez garder RLS activé, créez ces policies :

```sql
-- Policy pour permettre l'accès via service_role
CREATE POLICY "Service role can do anything" ON users
    FOR ALL USING (true);

CREATE POLICY "Service role can do anything" ON historique
    FOR ALL USING (true);

CREATE POLICY "Service role can do anything" ON profils
    FOR ALL USING (true);

CREATE POLICY "Service role can do anything" ON notifications
    FOR ALL USING (true);
```

---

## ⚙️ Étape 4 : Configuration de l'Application

### 4.1 Créer le fichier .env

```bash
cd Kotighi-AI
cp .env.example .env
```

### 4.2 Éditer .env

Remplacez les valeurs par vos clés Supabase :

```env
# Base de données Supabase
DATABASE_URL=postgresql://postgres:[YOUR-PASSWORD]@db.izeukppgxagzvwfhiqra.supabase.co:5432/postgres

# Supabase Configuration
SUPABASE_URL=https://izeukppgxagzvwfhiqra.supabase.co
SUPABASE_KEY=votre_anon_key_ici
SUPABASE_SERVICE_KEY=votre_service_role_key_ici

# JWT
SECRET_KEY=votre_cle_secrete_generee
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=1440
```

Pour générer SECRET_KEY :
```bash
openssl rand -hex 32
```

### 4.3 Récupérer le Mot de Passe PostgreSQL

1. Dashboard Supabase → **Settings** → **Database**
2. Copier le **Password** (ou le réinitialiser si oublié)
3. Remplacer `[YOUR-PASSWORD]` dans DATABASE_URL

---

## 📦 Étape 5 : Installation des Dépendances

```bash
cd Kotighi-AI

# Activer l'environnement virtuel
source venv/bin/activate  # Windows: venv\Scripts\activate

# Installer les nouvelles dépendances
pip install supabase postgrest-py python-dotenv
```

---

## 🚀 Étape 6 : Lancer l'API Supabase

### 6.1 Tester la Configuration

```bash
# Tester la connexion Supabase
python supabase_config.py
```

### 6.2 Lancer l'API

```bash
# Lancer l'API avec Supabase
uvicorn api_kotighi_supabase:app --reload --host 0.0.0.0 --port 8000
```

### 6.3 Vérifier

Ouvrir http://localhost:8000/docs

Vous devriez voir la documentation Swagger avec tous les endpoints.

---

## 🧪 Étape 7 : Tester l'API

### 7.1 Créer un Compte

```bash
curl -X POST http://localhost:8000/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "Test123!",
    "full_name": "Test User",
    "role": "Utilisateur",
    "email": "test@example.com"
  }'
```

Réponse attendue :
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "user_id": 1,
  "username": "test_user",
  "full_name": "Test User",
  "role": "Utilisateur"
}
```

### 7.2 Vérifier dans Supabase

1. Dashboard → **Table Editor**
2. Sélectionner la table `users`
3. Vous devriez voir votre utilisateur !

### 7.3 Tester une Analyse

```bash
# Récupérer le token de la réponse précédente
TOKEN="votre_token_ici"

# Analyse cyber
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

### 7.4 Vérifier l'Historique

```bash
curl -X GET http://localhost:8000/historique \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📱 Étape 8 : Configurer l'App Android

### 8.1 Mettre à Jour l'URL de l'API

Éditer `app/build.gradle` :

```groovy
buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:8000\""
// Ou pour production :
// buildConfigField "String", "API_BASE_URL", "\"https://votre-api.com\""
```

### 8.2 Synchroniser et Tester

1. Ouvrir le projet dans Android Studio
2. Sync Gradle
3. Lancer l'app
4. Créer un compte
5. Tester les analyses

---

## 🔍 Étape 9 : Vérifier les Données

### 9.1 Via Dashboard Supabase

1. **Table Editor** → Voir toutes les tables
2. **users** → Voir les utilisateurs
3. **historique** → Voir les analyses
4. **notifications** → Voir les notifications

### 9.2 Via SQL Editor

```sql
-- Compter les utilisateurs
SELECT COUNT(*) FROM users;

-- Voir les dernières analyses
SELECT * FROM historique ORDER BY created_at DESC LIMIT 10;

-- Voir les notifications non lues
SELECT * FROM notifications WHERE lu = false;

-- Stats par utilisateur
SELECT 
    u.username,
    u.full_name,
    COUNT(h.id) as total_analyses,
    COUNT(CASE WHEN h.type_analyse = 'Cyber' THEN 1 END) as analyses_cyber,
    COUNT(CASE WHEN h.type_analyse = 'Sante' THEN 1 END) as analyses_sante
FROM users u
LEFT JOIN historique h ON u.id = h.user_id
GROUP BY u.id, u.username, u.full_name;
```

---

## 🎯 Fonctionnalités Supabase

### Avantages

✅ **Base de données PostgreSQL cloud**
✅ **Backups automatiques**
✅ **Scaling automatique**
✅ **Dashboard intuitif**
✅ **API REST auto-générée**
✅ **Authentification intégrée (optionnel)**
✅ **Storage pour fichiers (optionnel)**
✅ **Realtime subscriptions (optionnel)**

### Nouveaux Endpoints

- `GET /me` - Informations utilisateur
- `PUT /me` - Mettre à jour le profil
- `GET /notifications` - Liste des notifications
- `PUT /notifications/{id}/lire` - Marquer comme lu
- `GET /stats` - Statistiques utilisateur

---

## 🐛 Dépannage

### Erreur : "Could not connect to database"

```bash
# Vérifier la connexion
psql "postgresql://postgres:[PASSWORD]@db.izeukppgxagzvwfhiqra.supabase.co:5432/postgres"
```

### Erreur : "SUPABASE_KEY non configurée"

Vérifier que `.env` contient bien :
```env
SUPABASE_KEY=votre_cle_ici
```

### Erreur : "Table does not exist"

Exécuter à nouveau le script SQL de création des tables.

### Erreur : "Row Level Security"

Désactiver RLS temporairement :
```sql
ALTER TABLE users DISABLE ROW LEVEL SECURITY;
```

---

## 📊 Monitoring

### Dashboard Supabase

- **Database** → Voir les métriques
- **Logs** → Voir les requêtes
- **API** → Voir l'utilisation

### Limites du Plan Gratuit

- 500 MB de base de données
- 1 GB de bande passante
- 50 000 requêtes/mois
- 2 GB de stockage

Pour plus : https://supabase.com/pricing

---

## 🚀 Déploiement Production

### Option 1 : Render avec Supabase

```yaml
# render.yaml
services:
  - type: web
    name: kotighi-api
    env: python
    buildCommand: pip install -r requirements.txt && python train_models.py
    startCommand: uvicorn api_kotighi_supabase:app --host 0.0.0.0 --port $PORT
    envVars:
      - key: DATABASE_URL
        sync: false
      - key: SUPABASE_URL
        value: https://izeukppgxagzvwfhiqra.supabase.co
      - key: SUPABASE_KEY
        sync: false
      - key: SECRET_KEY
        generateValue: true
```

### Option 2 : Docker avec Supabase

```bash
# Construire l'image
docker build -t kotighi-api -f Kotighi-AI/Dockerfile .

# Lancer avec variables d'environnement
docker run -p 8000:8000 \
  -e DATABASE_URL="postgresql://..." \
  -e SUPABASE_URL="https://..." \
  -e SUPABASE_KEY="..." \
  kotighi-api
```

---

## 📚 Ressources

- [Documentation Supabase](https://supabase.com/docs)
- [Supabase Python Client](https://github.com/supabase-community/supabase-py)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

## ✅ Checklist

- [ ] Compte Supabase créé
- [ ] Tables créées via SQL Editor
- [ ] Clés API récupérées
- [ ] Fichier .env configuré
- [ ] Dépendances installées
- [ ] API testée localement
- [ ] Compte utilisateur créé
- [ ] Analyse testée
- [ ] Données visibles dans Supabase
- [ ] App Android configurée

---

**Félicitations ! Votre projet KOTIGHI AI est maintenant connecté à Supabase ! 🎉**
