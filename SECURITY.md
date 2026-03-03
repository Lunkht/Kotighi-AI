# 🔒 Politique de Sécurité - KOTIGHI AI

## 🛡️ Versions Supportées

| Version | Supportée          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

---

## 🚨 Signaler une Vulnérabilité

Si vous découvrez une vulnérabilité de sécurité dans KOTIGHI AI, merci de nous la signaler de manière responsable.

### Comment Signaler

**NE PAS** créer une issue publique GitHub pour les vulnérabilités de sécurité.

À la place :

1. **Email** : Envoyez un email à `security@vulsoft.com` avec :
   - Description détaillée de la vulnérabilité
   - Étapes pour reproduire
   - Impact potentiel
   - Suggestions de correction (si possible)

2. **Réponse** : Nous nous engageons à répondre sous 48 heures

3. **Correction** : Nous travaillerons sur un correctif et vous tiendrons informé

4. **Divulgation** : Nous coordonnerons avec vous pour une divulgation responsable

### Ce que nous promettons

- Accuser réception de votre rapport sous 48h
- Vous tenir informé de l'avancement
- Créditer votre découverte (si vous le souhaitez)
- Publier un correctif dès que possible

---

## 🔐 Bonnes Pratiques de Sécurité

### Pour les Développeurs

#### Backend (Python/FastAPI)

1. **Variables d'Environnement**
   ```bash
   # Ne JAMAIS commiter .env
   # Utiliser des clés secrètes fortes
   SECRET_KEY=$(openssl rand -hex 32)
   ```

2. **Base de Données**
   ```python
   # Toujours utiliser des requêtes paramétrées
   # SQLAlchemy le fait automatiquement
   db.query(User).filter(User.username == username).first()
   ```

3. **Authentification**
   ```python
   # Utiliser JWT avec expiration
   # Hasher les mots de passe avec bcrypt
   pwd_context = CryptContext(schemes=["bcrypt"])
   ```

4. **Validation des Entrées**
   ```python
   # Utiliser Pydantic pour valider
   class UserInput(BaseModel):
       username: str = Field(..., min_length=3, max_length=20)
       password: str = Field(..., min_length=8)
   ```

5. **HTTPS Obligatoire**
   ```python
   # En production, forcer HTTPS
   if not request.url.scheme == "https":
       raise HTTPException(status_code=403)
   ```

#### Android (Java)

1. **Network Security**
   ```xml
   <!-- Utiliser network_security_config.xml -->
   <!-- Désactiver cleartext en production -->
   <base-config cleartextTrafficPermitted="false">
   ```

2. **Stockage Sécurisé**
   ```java
   // Utiliser EncryptedSharedPreferences pour les tokens
   SharedPreferences prefs = EncryptedSharedPreferences.create(
       "secure_prefs",
       masterKey,
       context,
       EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
       EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
   );
   ```

3. **Validation des Certificats SSL**
   ```java
   // Ne JAMAIS désactiver la validation SSL
   // Utiliser certificate pinning si possible
   ```

4. **ProGuard**
   ```groovy
   // Activer l'obfuscation en release
   buildTypes {
       release {
           minifyEnabled true
           proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
       }
   }
   ```

5. **Permissions Minimales**
   ```xml
   <!-- Ne demander que les permissions nécessaires -->
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   ```

### Pour les Utilisateurs

1. **Mots de Passe Forts**
   - Minimum 8 caractères
   - Mélange de majuscules, minuscules, chiffres
   - Utiliser un gestionnaire de mots de passe

2. **Mises à Jour**
   - Toujours utiliser la dernière version
   - Activer les mises à jour automatiques

3. **Réseau**
   - Éviter les WiFi publics non sécurisés
   - Utiliser un VPN si nécessaire

4. **Permissions**
   - Vérifier les permissions demandées
   - Ne pas installer d'APK de sources inconnues

---

## 🔍 Audits de Sécurité

### Derniers Audits

| Date | Type | Résultat |
|------|------|----------|
| 2026-03 | Code Review | Aucune vulnérabilité critique |
| 2026-02 | Penetration Test | 2 vulnérabilités mineures corrigées |

### Outils Utilisés

- **OWASP ZAP** : Tests de pénétration web
- **Bandit** : Analyse statique Python
- **Android Lint** : Analyse statique Android
- **Dependabot** : Surveillance des dépendances

---

## 📋 Checklist de Sécurité

### Backend

- [ ] Variables d'environnement pour les secrets
- [ ] HTTPS activé (Let's Encrypt)
- [ ] JWT avec expiration courte
- [ ] Rate limiting sur les endpoints
- [ ] Validation de toutes les entrées
- [ ] Logs de sécurité activés
- [ ] Backups chiffrés de la DB
- [ ] CORS configuré correctement
- [ ] Headers de sécurité (HSTS, CSP, etc.)
- [ ] Dépendances à jour

### Android

- [ ] Network security config configuré
- [ ] ProGuard activé en release
- [ ] Certificat SSL validé
- [ ] Tokens stockés de manière sécurisée
- [ ] Permissions minimales
- [ ] Validation des entrées utilisateur
- [ ] Pas de données sensibles dans les logs
- [ ] APK signé avec keystore sécurisé
- [ ] Obfuscation du code
- [ ] Dépendances à jour

---

## 🚫 Vulnérabilités Connues

Aucune vulnérabilité critique connue actuellement.

Les vulnérabilités mineures sont suivies dans les [Security Advisories](https://github.com/votre-repo/kotighi-ai/security/advisories).

---

## 📚 Ressources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Mobile Top 10](https://owasp.org/www-project-mobile-top-10/)
- [FastAPI Security](https://fastapi.tiangolo.com/tutorial/security/)
- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)

---

## 📞 Contact

- 🔒 Sécurité : security@vulsoft.com
- 📧 Support : support@vulsoft.com
- 🐛 Bugs : [GitHub Issues](https://github.com/votre-repo/kotighi-ai/issues)

---

**La sécurité est l'affaire de tous. Merci de contribuer à rendre KOTIGHI AI plus sûr ! 🛡️**
