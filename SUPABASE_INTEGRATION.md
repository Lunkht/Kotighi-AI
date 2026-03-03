# 🚀 Intégration Supabase - KOTIGHI AI

## ✅ Ce qui a été fait

### 1. Configuration Supabase

✅ **Fichiers créés** :
- `Kotighi-AI/supabase_config.py` - Configuration et utilitaires Supabase
- `Kotighi-AI/api_kotighi_supabase.py` - API FastAPI avec Supabase
- `SUPABASE_SETUP.md` - Guide complet de configuration
- `scripts/switch-to-supabase.sh` - Script de configuration automatique

✅ **Dépendances ajoutées** :
- `supabase>=2.0.0` - Client Python Supabase
- `postgrest-py` - Client PostgreSQL REST

✅ **Configuration** :
- `.env.example` mis à jour avec variables Supabase
- `Makefile` avec commandes Supabase

### 2. Schéma de Base de Données

✅ **Tables créées** :

1. **users** - Utilisateurs
   - id, username, hashed_password, full_name, role, email, phone
   - created_at, updated_at

2. **historique** - Analyses
   - id, user_id, type_analyse, resultat, details, confiance, urgent
   - created_at

3. **profils** - Profils utilisateurs
   - id, user_id, avatar_url, bio, specialite, organisation, preferences
   - created_at, updated_at

4. **notifications** - Notifications
   - id, user_id, titre, message, type, lu
   - created_at

✅ **Index de performance** :
- idx_historique_user_id
- idx_historique_created_at
- idx_historique_type
- idx_notifications_user_id
- idx_notifications_lu

✅ **Triggers** :
- Auto-update de updated_at sur users et profils

### 3. Nouveaux Endpoints API

✅ **Authentification** :
- `POST /signup` - Inscription avec email
- `POST /login` - Connexion
- `POST /token` - OAuth2 standard

✅ **Profil** :
- `GET /me` - Informations utilisateur + stats
- `PUT /me` - Mettre à jour le profil

✅ **Analyses** :
- `POST /predict/cyber` - Analyse cybersécurité (avec notifications)
- `POST /predict/sante` - Diagnostic médical (avec notifications)
- `GET /historique` - Historique avec filtres

✅ **Notifications** :
- `GET /notifications` - Liste des notifications
- `PUT /notifications/{id}/lire` - Marquer comme lu

✅ **Statistiques** :
- `GET /stats` - Stats utilisateur complètes

✅ **Chat** :
- `POST /chat` - Chatbot IA

### 4. Fonctionnalités Ajoutées

✅ **Notifications automatiques** :
- Notification de bienvenue à l'inscription
- Alerte si attaque détectée (cyber)
- Alerte si diagnostic urgent (santé)

✅ **Profils enrichis** :
- Avatar, bio, spécialité, organisation
- Préférences JSON personnalisables

✅ **Statistiques avancées** :
- Total analyses par type
- Analyses urgentes
- Date d'inscription

✅ **Sécurité** :
- Mots de passe hashés (bcrypt)
- JWT avec expiration
- Variables d'environnement
- Row Level Security (optionnel)

---

## 🚀 Démarrage Rapide

### Option 1 : Script Automatique

```bash
# Configuration interactive
./scripts/switch-to-supabase.sh
```

Le script va :
1. Installer les dépendances
2. Configurer .env
3. Afficher le SQL à exécuter
4. Tester la connexion
5. Lancer l'API

### Option 2 : Manuel

```bash
# 1. Installer dépendances
cd Kotighi-AI
source venv/bin/activate
pip install supabase postgrest-py

# 2. Configurer .env
cp .env.example .env
# Éditer .env avec vos clés Supabase

# 3. Créer les tables dans Supabase
# Copier le SQL de supabase_config.py
# Exécuter dans Supabase SQL Editor

# 4. Lancer l'API
uvicorn api_kotighi_supabase:app --reload
```

### Option 3 : Makefile

```bash
# Configuration Supabase
make setup-supabase

# Lancer l'API Supabase
make run-api-supabase
```

---

## 📋 Configuration Requise

### 1. Compte Supabase

Créer un compte gratuit sur https://supabase.com

### 2. Récupérer les Clés

Dashboard → Settings → API :
- **Project URL** : `https://izeukppgxagzvwfhiqra.supabase.co`
- **anon public** : Clé publique
- **service_role** : Clé admin (ne pas commiter !)

### 3. Mot de Passe PostgreSQL

Dashboard → Settings → Database :
- Copier ou réinitialiser le mot de passe

### 4. Fichier .env

```env
DATABASE_URL=postgresql://postgres:[PASSWORD]@db.izeukppgxagzvwfhiqra.supabase.co:5432/postgres
SUPABASE_URL=https://izeukppgxagzvwfhiqra.supabase.co
SUPABASE_KEY=votre_anon_key
SUPABASE_SERVICE_KEY=votre_service_role_key
SECRET_KEY=votre_cle_secrete_jwt
```

---

## 🧪 Tests

### Créer un Compte

```bash
curl -X POST http://localhost:8000/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "Test123!",
    "full_name": "Test User",
    "email": "test@example.com"
  }'
```

### Récupérer le Profil

```bash
TOKEN="votre_token"

curl -X GET http://localhost:8000/me \
  -H "Authorization: Bearer $TOKEN"
```

### Faire une Analyse

```bash
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

### Voir les Notifications

```bash
curl -X GET http://localhost:8000/notifications \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📊 Comparaison SQLite vs Supabase

| Fonctionnalité | SQLite (Local) | Supabase (Cloud) |
|----------------|----------------|------------------|
| Base de données | Fichier local | PostgreSQL cloud |
| Backups | Manuel | Automatique |
| Scaling | Limité | Automatique |
| Collaboration | Non | Oui |
| Dashboard | Non | Oui (web) |
| API REST | Non | Oui (auto) |
| Realtime | Non | Oui |
| Storage | Non | Oui |
| Auth | Manuel | Intégré |
| Prix | Gratuit | Gratuit (500MB) |

---

## 🔄 Migration SQLite → Supabase

Si vous avez des données dans SQLite :

```python
# Script de migration (à créer)
import sqlite3
from supabase import create_client

# Connexion SQLite
sqlite_conn = sqlite3.connect('kotighi.db')
cursor = sqlite_conn.cursor()

# Connexion Supabase
supabase = create_client(SUPABASE_URL, SUPABASE_KEY)

# Migrer les utilisateurs
cursor.execute("SELECT * FROM users")
users = cursor.fetchall()

for user in users:
    supabase.table("users").insert({
        "username": user[1],
        "hashed_password": user[2],
        "full_name": user[3],
        "role": user[4]
    }).execute()

print("Migration terminée !")
```

---

## 🎯 Avantages Supabase

### Pour le Développement

✅ Dashboard visuel pour voir les données
✅ SQL Editor intégré
✅ Logs en temps réel
✅ API REST auto-générée
✅ Pas besoin de gérer un serveur PostgreSQL

### Pour la Production

✅ Backups automatiques quotidiens
✅ Scaling automatique
✅ SSL/TLS par défaut
✅ Monitoring intégré
✅ 99.9% uptime SLA

### Pour l'Équipe

✅ Collaboration facile
✅ Environnements multiples (dev/staging/prod)
✅ Gestion des accès (RLS)
✅ Audit logs

---

## 📱 Configuration Android

L'app Android fonctionne de la même manière avec Supabase !

Aucune modification nécessaire côté Android, l'API reste compatible.

---

## 🔐 Sécurité

### Bonnes Pratiques

✅ Ne jamais commiter `SUPABASE_SERVICE_KEY`
✅ Utiliser des variables d'environnement
✅ Activer RLS en production
✅ Limiter les permissions
✅ Monitorer les logs

### Row Level Security (RLS)

Pour activer RLS (recommandé en production) :

```sql
-- Activer RLS
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- Policy : les utilisateurs voient leur propre profil
CREATE POLICY "Users can view own profile" ON users
    FOR SELECT USING (auth.uid()::text = id::text);
```

---

## 📚 Documentation

- [Guide complet](SUPABASE_SETUP.md) - Configuration détaillée
- [Supabase Docs](https://supabase.com/docs) - Documentation officielle
- [Python Client](https://github.com/supabase-community/supabase-py) - Client Python

---

## 🆘 Support

### Problèmes Courants

**Erreur : "Could not connect"**
→ Vérifier DATABASE_URL et le mot de passe

**Erreur : "Table does not exist"**
→ Exécuter le SQL de création des tables

**Erreur : "SUPABASE_KEY non configurée"**
→ Vérifier le fichier .env

### Aide

- 📖 [SUPABASE_SETUP.md](SUPABASE_SETUP.md)
- 🐛 [GitHub Issues](https://github.com/votre-repo/kotighi-ai/issues)
- 📧 support@vulsoft.com

---

## ✅ Checklist

- [ ] Compte Supabase créé
- [ ] Projet Supabase configuré
- [ ] Clés API récupérées
- [ ] Tables créées (SQL exécuté)
- [ ] Fichier .env configuré
- [ ] Dépendances installées
- [ ] API testée
- [ ] Compte utilisateur créé
- [ ] Données visibles dans Supabase

---

**Votre projet KOTIGHI AI est maintenant connecté à Supabase ! 🎉**

Base de données cloud, backups automatiques, dashboard intuitif... Profitez de tous les avantages de Supabase !
