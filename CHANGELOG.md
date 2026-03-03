# 📝 Changelog - KOTIGHI AI

Tous les changements notables de ce projet seront documentés dans ce fichier.

Le format est basé sur [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/),
et ce projet adhère au [Semantic Versioning](https://semver.org/lang/fr/).

---

## [Unreleased]

### À venir
- Intégration Firebase Cloud Messaging pour notifications push
- Mode hors ligne avec synchronisation
- Export PDF des rapports d'analyse
- Support multilingue (Français, Anglais, Espagnol)
- Dashboard web responsive

---

## [1.0.0] - 2026-03-02

### 🎉 Version Initiale

#### Ajouté

**Backend (Python/FastAPI)**
- API REST sécurisée avec authentification JWT
- Modèle ML de détection d'intrusion réseau (Random Forest)
- Modèle ML de diagnostic médical (8 pathologies)
- Base de données PostgreSQL/SQLite
- Endpoints :
  - `/signup` - Création de compte
  - `/login` - Authentification
  - `/predict/cyber` - Analyse cybersécurité
  - `/predict/sante` - Diagnostic médical
  - `/historique` - Historique des analyses
  - `/chat` - Chatbot IA
- Tests unitaires avec pytest (couverture >80%)
- Documentation API avec Swagger/ReDoc
- Support Docker et docker-compose
- Configuration via variables d'environnement

**Application Android**
- Interface Material Design 3
- Authentification utilisateur (login/signup)
- Module Cybersécurité :
  - Analyse de trafic réseau
  - Détection d'attaques (DoS, DDoS, Probe, R2L, U2R)
  - Surveillance en temps réel
  - Watchlist d'IPs suspectes
- Module Santé :
  - Analyse de 14 symptômes
  - Prédiction de pathologies
  - Conseils de prévention
  - Alertes urgence
- Dashboard avec statistiques
- Historique des analyses
- Profil utilisateur avec rôles
- Service anti-phishing en arrière-plan
- Chatbot intégré
- Mode sombre/clair
- Tests unitaires

**Infrastructure**
- CI/CD avec GitHub Actions
- Configuration Render pour déploiement
- Scripts de déploiement Heroku
- Configuration Nginx pour VPS
- Certificats SSL avec Let's Encrypt

**Documentation**
- README complet avec instructions
- Guide de déploiement (DEPLOYMENT.md)
- Guide de contribution (CONTRIBUTING.md)
- Politique de sécurité (SECURITY.md)
- Licence MIT

#### Sécurité
- Authentification JWT avec expiration
- Hashage des mots de passe avec bcrypt
- HTTPS obligatoire en production
- Network security config Android
- ProGuard pour obfuscation du code
- Validation de toutes les entrées
- Protection contre les injections SQL
- Rate limiting sur l'API

---

## [0.9.0-beta] - 2026-02-15

### Ajouté
- Version beta pour tests internes
- Modèles ML de base
- Interface Android prototype
- API FastAPI minimale

### Corrigé
- Problèmes de performance sur les prédictions
- Bugs d'authentification
- Crashes sur Android 12+

---

## [0.5.0-alpha] - 2026-01-10

### Ajouté
- Proof of concept initial
- Modèle cybersécurité basique
- Interface Streamlit de démonstration

---

## Types de Changements

- `Ajouté` pour les nouvelles fonctionnalités
- `Modifié` pour les changements dans les fonctionnalités existantes
- `Déprécié` pour les fonctionnalités bientôt supprimées
- `Supprimé` pour les fonctionnalités supprimées
- `Corrigé` pour les corrections de bugs
- `Sécurité` pour les correctifs de vulnérabilités

---

## Liens

- [Unreleased]: https://github.com/votre-repo/kotighi-ai/compare/v1.0.0...HEAD
- [1.0.0]: https://github.com/votre-repo/kotighi-ai/releases/tag/v1.0.0
- [0.9.0-beta]: https://github.com/votre-repo/kotighi-ai/releases/tag/v0.9.0-beta
- [0.5.0-alpha]: https://github.com/votre-repo/kotighi-ai/releases/tag/v0.5.0-alpha
