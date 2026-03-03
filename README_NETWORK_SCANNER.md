# 📡 Network Scanner Module

Module Python pour le scan automatique et l'analyse de vulnérabilités des réseaux WiFi.

## Installation

```bash
# Aucune dépendance externe requise
# Utilise uniquement la bibliothèque standard Python
python network_scanner.py
```

## Utilisation Rapide

### Scan Simple

```python
from network_scanner import NetworkScanner

# Créer le scanner
scanner = NetworkScanner()

# Scanner les réseaux (standard)
networks = scanner.scan_proximity_networks(deep_scan=False)

# Afficher les résultats
for network in networks:
    print(f"{network.ssid}: {network.vulnerability_score}/100 ({network.vulnerabilityLevel})")
```

### Scan Approfondi

```python
# Scan avec analyse des ports et hôtes
networks = scanner.scan_proximity_networks(deep_scan=True)

for network in networks:
    print(f"\n{network.ssid}")
    print(f"  Score: {network.vulnerability_score}/100")
    print(f"  Appareils: {network.devices_count}")
    print(f"  Ports ouverts: {network.open_ports}")
    print(f"  Vulnérabilités:")
    for vuln in network.vulnerabilities:
        print(f"    - {vuln}")
```

### Résumé et Statistiques

```python
# Obtenir le résumé
summary = scanner.get_scan_summary()

print(f"Total réseaux: {summary['total_networks']}")
print(f"Critiques: {summary['critical']}")
print(f"Élevés: {summary['high']}")
print(f"Score moyen: {summary['average_score']:.1f}/100")
```

### Export JSON

```python
# Exporter les résultats
results = scanner.export_results()

import json
with open('scan_results.json', 'w') as f:
    json.dump(results, f, indent=2)
```

## API FastAPI

### Intégration

```python
from network_scanner import scan_networks_api

# Dans votre endpoint FastAPI
@app.post("/api/network/scan")
async def scan_networks(deep_scan: bool = False):
    result = scan_networks_api(deep_scan=deep_scan)
    return result
```

### Réponse

```json
{
  "success": true,
  "scan_timestamp": "2026-03-02T10:30:00",
  "deep_scan": false,
  "summary": {
    "total_networks": 5,
    "critical": 2,
    "high": 1,
    "medium": 2,
    "low": 0,
    "minimal": 0,
    "average_score": 65.5
  },
  "networks": [
    {
      "ssid": "PublicWiFi",
      "bssid": "AA:BB:CC:DD:EE:FF",
      "signal_strength": -45,
      "channel": 11,
      "encryption": "OPEN",
      "frequency": "2.4 GHz",
      "vulnerability_score": 100.0,
      "vulnerabilities": [
        "Réseau ouvert sans chiffrement",
        "Signal très fort - facilement accessible"
      ],
      "open_ports": [],
      "devices_count": 0
    }
  ]
}
```

## Algorithme de Scoring

### Critères (Score 0-100)

1. **Chiffrement (40%)**
   - OPEN: 100 points
   - WEP: 90 points
   - WPA: 60 points
   - WPA2-PSK: 30 points
   - WPA2-EAP: 20 points
   - WPA3: 10 points
   - WPA3-SAE: 5 points

2. **Ports Ouverts (30%)**
   - 5 points par port ouvert (max 30)
   - Ports critiques identifiés: 21, 23, 445, 3389

3. **Signal (10%)**
   - > -50 dBm: +10 points
   - > -70 dBm: +5 points

4. **Appareils (10%)**
   - > 10 appareils: +10 points
   - > 5 appareils: +5 points

5. **Canal (10%)**
   - Canaux standards (1, 6, 11): +5 points
   - Autres: +2 points

### Niveaux

- **CRITIQUE** (≥80): Action immédiate
- **ÉLEVÉ** (≥60): Risque important
- **MOYEN** (≥40): Attention nécessaire
- **FAIBLE** (≥20): Risque limité
- **MINIMAL** (<20): Bien sécurisé

## Classe NetworkScanner

### Méthodes Principales

#### `scan_proximity_networks(deep_scan=False)`
Scanne les réseaux WiFi à proximité.

**Paramètres:**
- `deep_scan` (bool): Si True, effectue un scan approfondi

**Retourne:**
- Liste de `NetworkInfo` triée par vulnérabilité

#### `scan_wifi_networks()`
Détecte les réseaux WiFi disponibles.

**Retourne:**
- Liste de dictionnaires avec infos réseau

#### `scan_network_hosts(network_range)`
Scanne les hôtes actifs sur un réseau.

**Paramètres:**
- `network_range` (str): Plage IP (ex: "192.168.1.0/24")

**Retourne:**
- Liste d'adresses IP actives

#### `scan_ports(ip, ports=None)`
Scanne les ports ouverts sur une IP.

**Paramètres:**
- `ip` (str): Adresse IP cible
- `ports` (list): Liste de ports (défaut: ports communs)

**Retourne:**
- Liste de ports ouverts

#### `analyze_vulnerabilities(network_info)`
Analyse les vulnérabilités d'un réseau.

**Paramètres:**
- `network_info` (dict): Informations réseau

**Retourne:**
- Tuple (score, liste_vulnérabilités)

#### `get_scan_summary()`
Génère un résumé du dernier scan.

**Retourne:**
- Dictionnaire avec statistiques

#### `export_results()`
Exporte les résultats au format JSON.

**Retourne:**
- Liste de dictionnaires

## Classe NetworkInfo

### Attributs

```python
@dataclass
class NetworkInfo:
    ssid: str                    # Nom du réseau
    bssid: str                   # Adresse MAC
    signal_strength: int         # Force du signal (dBm)
    channel: int                 # Canal WiFi
    encryption: str              # Type de chiffrement
    frequency: str               # Fréquence (2.4/5 GHz)
    ip_range: str               # Plage IP (optionnel)
    gateway: str                # Passerelle (optionnel)
    vulnerability_score: float  # Score 0-100
    vulnerabilities: List[str]  # Liste vulnérabilités
    open_ports: List[int]       # Ports ouverts
    devices_count: int          # Nombre d'appareils
    scan_timestamp: str         # Date/heure du scan
```

## Exemples Avancés

### Scan Personnalisé

```python
scanner = NetworkScanner()

# Modifier les ports à scanner
scanner.COMMON_PORTS = [80, 443, 22, 3306, 5432]

# Modifier les scores de chiffrement
scanner.ENCRYPTION_SCORES['WPA2-PSK'] = 25.0

# Scanner
networks = scanner.scan_proximity_networks()
```

### Filtrage des Résultats

```python
# Obtenir uniquement les réseaux critiques
critical_networks = [
    n for n in networks 
    if n.vulnerability_score >= 80
]

# Réseaux avec ports ouverts
networks_with_ports = [
    n for n in networks 
    if n.open_ports
]

# Réseaux ouverts
open_networks = [
    n for n in networks 
    if n.encryption == 'OPEN'
]
```

### Monitoring Continu

```python
import time

scanner = NetworkScanner()

while True:
    print("\n🔍 Nouveau scan...")
    networks = scanner.scan_proximity_networks()
    
    critical_count = sum(
        1 for n in networks 
        if n.vulnerability_score >= 80
    )
    
    if critical_count > 0:
        print(f"⚠️ ALERTE: {critical_count} réseau(x) critique(s)!")
        for network in networks[:critical_count]:
            print(f"  - {network.ssid}: {network.vulnerability_score}/100")
    
    # Attendre 5 minutes
    time.sleep(300)
```

## Permissions Linux

### Scan WiFi
Nécessite des privilèges root ou CAP_NET_ADMIN:

```bash
# Avec sudo
sudo python network_scanner.py

# Ou donner les capacités
sudo setcap cap_net_admin=eip /usr/bin/python3
```

### Scan Ports
Aucune permission spéciale requise.

## Limitations

1. **Scan WiFi**: Nécessite `iwlist` sur Linux (simulation si non disponible)
2. **Performance**: Scan approfondi peut prendre 10-30 secondes
3. **Hôtes**: Limité à 50 hôtes par réseau pour performance
4. **Ports**: Scan de 14 ports communs par défaut

## Dépannage

### "iwlist: command not found"
```bash
# Installer wireless-tools
sudo apt-get install wireless-tools
```

### Scan lent
```python
# Réduire le nombre de ports
scanner.COMMON_PORTS = [80, 443, 22]

# Ou désactiver le scan approfondi
networks = scanner.scan_proximity_networks(deep_scan=False)
```

### Aucun réseau détecté
- Vérifier que le WiFi est activé
- Exécuter avec sudo si nécessaire
- Vérifier les permissions

## Tests

```bash
# Exécuter le module en standalone
python network_scanner.py

# Sortie attendue:
# 🔍 Démarrage du scan...
# 📡 5 réseaux détectés
# 
# 1. PublicWiFi
#    Score: 100.0/100
#    Niveau: CRITIQUE
#    ...
```

## Intégration

### Avec FastAPI
Voir `api_kotighi_supabase.py` pour exemple complet.

### Avec Flask
```python
from flask import Flask, jsonify
from network_scanner import scan_networks_api

app = Flask(__name__)

@app.route('/api/scan')
def scan():
    result = scan_networks_api(deep_scan=False)
    return jsonify(result)
```

### Avec Django
```python
from django.http import JsonResponse
from network_scanner import scan_networks_api

def scan_view(request):
    deep_scan = request.GET.get('deep', 'false') == 'true'
    result = scan_networks_api(deep_scan=deep_scan)
    return JsonResponse(result)
```

## Licence

Partie du projet KOTIGHI AI.

## Support

Pour questions ou problèmes:
- Voir `NETWORK_SCANNER_GUIDE.md` pour documentation complète
- Consulter `NETWORK_SCANNER_SUMMARY.md` pour résumé
