# 📡 Guide du Scanner de Réseaux Automatique

## Vue d'ensemble

Le scanner de réseaux automatique de KOTIGHI AI détecte, analyse et classe les réseaux WiFi de proximité par niveau de vulnérabilité. Il utilise un algorithme sophistiqué pour évaluer la sécurité de chaque réseau détecté.

## 🎯 Fonctionnalités

### 1. Détection Automatique
- Scan des réseaux WiFi à proximité
- Détection du SSID, BSSID, signal, canal, fréquence
- Identification du type de chiffrement

### 2. Analyse de Vulnérabilités
- Score de vulnérabilité (0-100)
- Classification par niveau (CRITIQUE, ÉLEVÉ, MOYEN, FAIBLE, MINIMAL)
- Détection de vulnérabilités spécifiques

### 3. Scan Approfondi (Optionnel)
- Scan des hôtes actifs sur le réseau
- Détection des ports ouverts
- Comptage des appareils connectés
- Identification de la passerelle

### 4. Classement Intelligent
- Tri automatique par score de vulnérabilité
- Priorisation des réseaux critiques
- Statistiques globales

## 🔍 Algorithme de Scoring

### Critères d'Évaluation (Score 0-100)

#### 1. Chiffrement (40% du score)
```
OPEN (réseau ouvert)     : 100 points → CRITIQUE
WEP (obsolète)           : 90 points  → CRITIQUE
WPA (ancien)             : 60 points  → ÉLEVÉ
WPA2-PSK                 : 30 points  → MOYEN
WPA2-EAP (Enterprise)    : 20 points  → FAIBLE
WPA3                     : 10 points  → MINIMAL
WPA3-SAE                 : 5 points   → MINIMAL
```

#### 2. Force du Signal (10% du score)
```
Signal > -50 dBm  : +10 points (très accessible)
Signal > -70 dBm  : +5 points  (accessible)
Signal < -70 dBm  : +0 points  (difficile d'accès)
```

#### 3. Ports Ouverts (30% du score)
```
Chaque port ouvert : +5 points (max 30)

Ports critiques détectés:
- Port 23 (Telnet)    : Non sécurisé
- Port 21 (FTP)       : Transferts non chiffrés
- Port 445 (SMB)      : Vulnérable aux attaques
- Port 3389 (RDP)     : Cible d'attaques
- Port 3306 (MySQL)   : Base de données exposée
- Port 5432 (PostgreSQL) : Base de données exposée
```

#### 4. Nombre d'Appareils (10% du score)
```
> 10 appareils : +10 points (surface d'attaque élevée)
> 5 appareils  : +5 points
< 5 appareils  : +0 points
```

#### 5. Canal WiFi (10% du score)
```
Canaux standards (1, 6, 11) : +5 points
Autres canaux               : +2 points
```

### Niveaux de Vulnérabilité

```
Score >= 80 : CRITIQUE  🔴
Score >= 60 : ÉLEVÉ     🟠
Score >= 40 : MOYEN     🟡
Score >= 20 : FAIBLE    🟢
Score < 20  : MINIMAL   ⚪
```

## 📱 Utilisation Android

### Accès au Scanner

**Méthode 1: Depuis l'interface moderne**
1. Ouvrir KOTIGHI AI
2. Aller dans "Cybersécurité" (interface moderne)
3. Cliquer sur l'icône de mise à jour (🔄) dans le menu
4. L'écran de scan s'ouvre

**Méthode 2: Navigation directe**
```java
Intent intent = new Intent(context, NetworkScanActivity.class);
startActivity(intent);
```

### Types de Scan

#### Scan Standard
- Détecte les réseaux WiFi
- Analyse le chiffrement et le signal
- Calcule le score de vulnérabilité
- **Rapide** (2-5 secondes)

#### Scan Approfondi
- Tout du scan standard +
- Scan des hôtes actifs
- Détection des ports ouverts
- Comptage des appareils
- **Plus lent** (10-30 secondes)

#### Scan Rapide
- Top 3 des réseaux les plus vulnérables
- Résumé uniquement
- **Très rapide** (1-2 secondes)

### Interface Utilisateur

#### Carte de Contrôle
- Switch "Scan approfondi"
- Bouton "SCANNER" (scan complet)
- Bouton "RAPIDE" (scan rapide)
- Indicateur de progression

#### Résumé du Scan
- Nombre total de réseaux
- Compteurs par niveau (Critique, Élevé, Moyen)
- Score moyen
- Niveau de risque global

#### Liste des Réseaux
- Affichage par ordre de vulnérabilité
- Carte colorée selon le niveau
- Informations détaillées
- Clic pour voir tous les détails

## 🔧 Backend Python

### Module: `network_scanner.py`

#### Classe Principale: `NetworkScanner`

```python
scanner = NetworkScanner()

# Scan standard
networks = scanner.scan_proximity_networks(deep_scan=False)

# Scan approfondi
networks = scanner.scan_proximity_networks(deep_scan=True)

# Résumé
summary = scanner.get_scan_summary()

# Export JSON
results = scanner.export_results()
```

#### Méthodes Principales

**scan_wifi_networks()**
- Détecte les réseaux WiFi disponibles
- Utilise `iwlist scan` sur Linux
- Simulation pour démo si non disponible

**scan_network_hosts(network_range)**
- Scanne les hôtes actifs (ping)
- Scan parallèle pour performance
- Limite à 50 hôtes

**scan_ports(ip, ports)**
- Scanne les ports ouverts
- Timeout 0.5s par port
- Ports communs par défaut

**analyze_vulnerabilities(network_info)**
- Calcule le score de vulnérabilité
- Identifie les vulnérabilités spécifiques
- Retourne score + liste de vulnérabilités

### API FastAPI

#### Endpoints

**POST /api/network/scan-proximity**
```json
Request:
{
  "deep_scan": false,
  "scan_type": "proximity"
}

Response:
{
  "success": true,
  "scan_timestamp": "2026-03-02T10:30:00",
  "deep_scan": false,
  "summary": {
    "total_networks": 5,
    "critical": 2,
    "high": 1,
    "medium": 2,
    "average_score": 65.5
  },
  "networks": [...]
}
```

**POST /api/network/quick-scan**
- Scan rapide sans paramètres
- Retourne top 3 réseaux vulnérables

**GET /api/network/scan-history**
- Historique des scans
- Paramètre: `limit` (défaut: 20)

**GET /api/network/vulnerability-stats**
- Statistiques globales
- Distribution des vulnérabilités
- Types de chiffrement détectés

## 📊 Exemples de Résultats

### Réseau Critique (Score: 100)
```
SSID: PublicWiFi
Chiffrement: OPEN
Signal: -45 dBm (excellent)
Score: 100/100
Niveau: CRITIQUE

Vulnérabilités:
• Réseau ouvert sans chiffrement
• Signal très fort - facilement accessible
```

### Réseau Élevé (Score: 75)
```
SSID: OldRouter
Chiffrement: WEP
Signal: -60 dBm (bon)
Ports ouverts: 23, 80, 445
Score: 75/100
Niveau: ÉLEVÉ

Vulnérabilités:
• Chiffrement WEP obsolète et facilement cassable
• Port Telnet (23) ouvert - non sécurisé
• Port SMB (445) ouvert - vulnérable aux attaques
```

### Réseau Sécurisé (Score: 15)
```
SSID: SecureNetwork
Chiffrement: WPA3-SAE
Signal: -75 dBm (faible)
Score: 15/100
Niveau: MINIMAL

Aucune vulnérabilité majeure détectée
```

## 🔐 Sécurité et Permissions

### Permissions Android Requises
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
```

### Permissions Linux (Backend)
- Scan WiFi: nécessite `root` ou `CAP_NET_ADMIN`
- Scan ports: aucune permission spéciale
- Ping: aucune permission spéciale

## 📈 Performance

### Temps de Scan

| Type de Scan | Durée Moyenne | Réseaux | Détails |
|--------------|---------------|---------|---------|
| Rapide | 1-2s | Top 3 | Résumé uniquement |
| Standard | 2-5s | Tous | WiFi + Chiffrement |
| Approfondi | 10-30s | Tous | + Ports + Hôtes |

### Optimisations
- Scan parallèle des hôtes (threading)
- Timeout courts (0.5-1s)
- Limite de 50 hôtes par réseau
- Cache des résultats

## 🎯 Cas d'Usage

### 1. Audit de Sécurité
- Identifier les réseaux vulnérables
- Prioriser les corrections
- Rapport de conformité

### 2. Surveillance Continue
- Scan automatique périodique
- Alertes sur nouveaux réseaux critiques
- Historique des scans

### 3. Éducation Utilisateur
- Sensibilisation aux risques
- Recommandations de sécurité
- Comparaison des réseaux

## 🚀 Prochaines Améliorations

### Court Terme
- [ ] Scan automatique en arrière-plan
- [ ] Notifications push pour réseaux critiques
- [ ] Export PDF des rapports

### Moyen Terme
- [ ] Détection d'attaques actives
- [ ] Analyse du trafic réseau
- [ ] Recommandations personnalisées

### Long Terme
- [ ] Machine Learning pour détection d'anomalies
- [ ] Base de données de vulnérabilités connues
- [ ] Intégration avec CVE database

## 📚 Ressources

### Documentation
- [WiFi Security Standards](https://www.wi-fi.org/discover-wi-fi/security)
- [OWASP Mobile Security](https://owasp.org/www-project-mobile-security/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)

### Outils Complémentaires
- Wireshark (analyse de paquets)
- Nmap (scan réseau avancé)
- Aircrack-ng (audit WiFi)

---

**Version:** 1.0  
**Date:** 2 Mars 2026  
**Auteur:** KOTIGHI AI Team
