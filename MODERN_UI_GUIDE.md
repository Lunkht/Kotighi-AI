# Guide de l'Interface Moderne KOTIGHI AI

## Vue d'ensemble

L'interface moderne de KOTIGHI AI apporte une expérience utilisateur améliorée avec Material Design 3, des animations fluides et des fonctionnalités de cybersécurité avancées.

## Accès à l'Interface Moderne

### Depuis l'écran principal
- **Appui long** sur la carte "CYBERSECURITE" pour accéder à l'interface moderne
- L'interface classique reste accessible via un appui court

## Nouvelles Fonctionnalités

### 1. Scanner de Ports
**Localisation:** Premier onglet (icône cible)

**Fonctionnalités:**
- Scan de ports personnalisé avec plage configurable
- Présets rapides:
  - Ports communs (1-1024)
  - Ports web (80, 443)
  - Ports bases de données (3306, 5432)
- Affichage en temps réel de la progression
- Liste détaillée des ports ouverts avec services identifiés

**Utilisation:**
1. Entrez l'adresse IP cible
2. Définissez la plage de ports (ou utilisez un preset)
3. Cliquez sur "SCANNER LES PORTS"
4. Consultez les résultats dans la carte "Ports Ouverts"

### 2. Scan de Réseau
**Localisation:** Deuxième onglet (icône réseau)

**Fonctionnalités:**
- Types de scan multiples:
  - Scan Complet
  - Scan Rapide
  - Scan Furtif
  - Scan UDP
- Support notation CIDR (ex: 192.168.1.0/24)
- Options avancées configurables
- Détection automatique des hôtes actifs

**Utilisation:**
1. Sélectionnez le type de scan
2. Entrez l'adresse IP ou le réseau (notation CIDR)
3. Configurez les options avancées si nécessaire
4. Lancez le scan

### 3. Scanner de Vulnérabilités
**Localisation:** Troisième onglet (icône bug)

**Fonctionnalités:**
- Détection de vulnérabilités OWASP Top 10:
  - SQL Injection
  - XSS (Cross-Site Scripting)
  - CSRF (Cross-Site Request Forgery)
  - Authentification cassée
  - Exposition de données sensibles
  - XXE (XML External Entities)
- Trois niveaux de scan:
  - Rapide (scan de surface)
  - Standard (scan équilibré)
  - Approfondi (scan complet)
- Classification par sévérité:
  - CRITIQUE (rouge)
  - ÉLEVÉ (orange)
  - MOYEN (cyan)

**Utilisation:**
1. Sélectionnez les catégories à scanner
2. Entrez l'URL ou l'IP cible
3. Ajustez le niveau de scan avec le slider
4. Cliquez sur "SCANNER LES VULNÉRABILITÉS"
5. Consultez les statistiques et la liste détaillée

### 4. Monitoring Temps Réel
**Localisation:** Quatrième onglet (icône bouclier)

**Fonctionnalités:**
- Surveillance active du réseau
- Métriques en temps réel:
  - Paquets par seconde
  - Bande passante utilisée
- Graphique de trafic réseau
- Liste des connexions actives
- Alertes de sécurité récentes
- Mise à jour automatique toutes les 2 secondes

**Utilisation:**
1. Activez le monitoring avec le switch
2. Observez les métriques en temps réel
3. Consultez les connexions actives
4. Vérifiez les alertes récentes

## Tableau de Bord Principal

### Carte de Statut
Affiche en temps réel:
- Statut de protection (ACTIF/INACTIF)
- Nombre de menaces bloquées
- Nombre de scans effectués aujourd'hui
- Niveau de risque global (FAIBLE/MOYEN/ÉLEVÉ)

### Bouton Scan Rapide (FAB)
- Bouton flottant en bas à droite
- Lance un scan rapide de sécurité
- Mise à jour automatique des métriques

## Architecture Technique

### Composants Principaux

#### Activities
- `CyberActivityModern.java` - Activity principale avec ViewPager2

#### Fragments
- `PortScannerFragment.java` - Scanner de ports
- `NetworkScanFragment.java` - Scan de réseau
- `VulnerabilityFragment.java` - Scanner de vulnérabilités
- `RealtimeMonitorFragment.java` - Monitoring temps réel

#### Adapters
- `OpenPortsAdapter.java` - Liste des ports ouverts
- `VulnerabilitiesAdapter.java` - Liste des vulnérabilités
- `ConnectionsAdapter.java` - Connexions actives
- `AlertsAdapter.java` - Alertes de sécurité

#### Layouts
- `activity_cyber_modern.xml` - Layout principal
- `fragment_port_scanner.xml` - Interface scanner de ports
- `fragment_network_scan.xml` - Interface scan réseau
- `fragment_vulnerability_scan.xml` - Interface vulnérabilités
- `fragment_realtime_monitor.xml` - Interface monitoring
- `item_open_port.xml` - Item de port ouvert
- `item_vulnerability.xml` - Item de vulnérabilité
- `item_connection.xml` - Item de connexion
- `item_alert.xml` - Item d'alerte

### Dépendances
```gradle
implementation 'androidx.viewpager2:viewpager2:1.0.0'
implementation 'com.google.android.material:material:1.11.0'
```

## Intégration API

### Endpoints à Implémenter

#### Scanner de Ports
```
POST /api/cyber/scan-ports
Body: {
  "target_ip": "192.168.1.1",
  "port_start": 1,
  "port_end": 1024
}
Response: {
  "open_ports": [
    {"port": 80, "service": "HTTP", "status": "OUVERT"},
    {"port": 443, "service": "HTTPS", "status": "OUVERT"}
  ]
}
```

#### Scan de Réseau
```
POST /api/cyber/scan-network
Body: {
  "target": "192.168.1.0/24",
  "scan_type": "full"
}
Response: {
  "hosts": [
    {"ip": "192.168.1.1", "status": "up", "services": [...]}
  ]
}
```

#### Scanner de Vulnérabilités
```
POST /api/cyber/scan-vulnerabilities
Body: {
  "target_url": "https://example.com",
  "categories": ["sql_injection", "xss", "csrf"],
  "scan_level": 2
}
Response: {
  "vulnerabilities": [
    {
      "name": "SQL Injection",
      "location": "/api/login",
      "description": "...",
      "severity": "CRITIQUE"
    }
  ],
  "stats": {
    "critical": 1,
    "high": 2,
    "medium": 3
  }
}
```

#### Monitoring Temps Réel
```
GET /api/cyber/monitoring
Response: {
  "metrics": {
    "packets_per_sec": 450,
    "bandwidth_mbps": 5.2
  },
  "connections": [
    {
      "ip": "192.168.1.100",
      "port": "443",
      "protocol": "HTTPS",
      "data": "2.4 MB"
    }
  ],
  "alerts": [
    {
      "title": "Tentative d'intrusion",
      "time": "Il y a 2 minutes",
      "level": "ÉLEVÉ"
    }
  ]
}
```

## Personnalisation

### Couleurs
Définies dans `app/src/main/res/values/colors.xml`:
- `accent_cyan` - Couleur principale (#00B894)
- `accent_red` - Alertes critiques (#D63031)
- `accent_orange` - Alertes élevées (#E17055)
- `accent_green` - Statut sécurisé (#00B894)

### Thème
Material Design 3 avec support du mode sombre automatique

## État Actuel

### ✅ Implémenté
- Interface utilisateur complète avec Material Design 3
- Navigation par onglets avec ViewPager2
- 4 modules de cybersécurité
- Adapters et ViewHolders pour toutes les listes
- Layouts pour tous les items
- Simulation de données pour démonstration
- Intégration dans AndroidManifest
- Navigation depuis MainActivity (appui long)

### 🔄 À Implémenter
- Intégration API backend réelle
- Graphiques de trafic réseau (bibliothèque MPAndroidChart)
- Animations de transition
- Gestion des permissions réseau
- Export des rapports de scan
- Historique des scans
- Notifications push pour alertes critiques
- Mode hors ligne avec cache

## Prochaines Étapes

1. **Backend API**
   - Implémenter les endpoints dans `api_kotighi_supabase.py`
   - Ajouter les modèles de données dans Supabase
   - Créer les tables pour historique des scans

2. **Fonctionnalités Avancées**
   - Ajouter MPAndroidChart pour graphiques
   - Implémenter l'export PDF des rapports
   - Ajouter la planification de scans automatiques

3. **Tests**
   - Tests unitaires pour les Fragments
   - Tests d'intégration pour les Adapters
   - Tests UI avec Espresso

4. **Documentation**
   - Guide utilisateur complet
   - Documentation API
   - Tutoriels vidéo

## Support

Pour toute question ou problème:
- Consultez la documentation principale: `README.md`
- Guide de démarrage rapide: `QUICKSTART.md`
- Intégration Supabase: `SUPABASE_INTEGRATION.md`
