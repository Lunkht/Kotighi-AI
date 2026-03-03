# Spécification API - Interface Moderne Cybersécurité

## Vue d'ensemble

Ce document spécifie les endpoints API nécessaires pour l'interface moderne de cybersécurité de KOTIGHI AI.

## Base URL

```
Production: https://kotighi-ai.onrender.com
Development: http://localhost:8000
```

## Authentification

Toutes les requêtes nécessitent un token JWT dans le header:
```
Authorization: Bearer <token>
```

## Endpoints

### 1. Scanner de Ports

#### POST /api/cyber/scan-ports

Scanne une plage de ports sur une adresse IP cible.

**Request Body:**
```json
{
  "target_ip": "192.168.1.1",
  "port_start": 1,
  "port_end": 1024,
  "timeout": 1000
}
```

**Response 200:**
```json
{
  "success": true,
  "scan_id": "scan_123456",
  "target_ip": "192.168.1.1",
  "scan_time": "2026-03-02T10:30:00Z",
  "total_ports": 1024,
  "open_ports_count": 3,
  "open_ports": [
    {
      "port": 22,
      "service": "SSH",
      "status": "OUVERT",
      "banner": "OpenSSH 8.2"
    },
    {
      "port": 80,
      "service": "HTTP",
      "status": "OUVERT",
      "banner": "nginx/1.18.0"
    },
    {
      "port": 443,
      "service": "HTTPS",
      "status": "OUVERT",
      "banner": "nginx/1.18.0"
    }
  ]
}
```

**Response 400:**
```json
{
  "success": false,
  "error": "Invalid IP address format"
}
```

---

### 2. Scan de Réseau

#### POST /api/cyber/scan-network

Scanne un réseau pour détecter les hôtes actifs et leurs services.

**Request Body:**
```json
{
  "target": "192.168.1.0/24",
  "scan_type": "full",
  "options": {
    "detect_os": true,
    "service_detection": true,
    "aggressive": false
  }
}
```

**Scan Types:**
- `full` - Scan complet (tous les ports)
- `quick` - Scan rapide (ports communs uniquement)
- `stealth` - Scan furtif (SYN scan)
- `udp` - Scan UDP

**Response 200:**
```json
{
  "success": true,
  "scan_id": "scan_789012",
  "target": "192.168.1.0/24",
  "scan_type": "full",
  "scan_time": "2026-03-02T10:35:00Z",
  "duration_seconds": 45,
  "hosts_found": 12,
  "hosts": [
    {
      "ip": "192.168.1.1",
      "hostname": "router.local",
      "status": "up",
      "os": "Linux 5.4",
      "open_ports": [80, 443],
      "services": [
        {
          "port": 80,
          "service": "HTTP",
          "version": "nginx 1.18.0"
        }
      ]
    },
    {
      "ip": "192.168.1.100",
      "hostname": "desktop-pc",
      "status": "up",
      "os": "Windows 10",
      "open_ports": [135, 445],
      "services": []
    }
  ]
}
```

---

### 3. Scanner de Vulnérabilités

#### POST /api/cyber/scan-vulnerabilities

Scanne une cible pour détecter les vulnérabilités de sécurité.

**Request Body:**
```json
{
  "target_url": "https://example.com",
  "categories": [
    "sql_injection",
    "xss",
    "csrf",
    "broken_auth",
    "sensitive_data",
    "xxe"
  ],
  "scan_level": 2,
  "max_depth": 3
}
```

**Scan Levels:**
- `1` - Rapide (scan de surface)
- `2` - Standard (scan équilibré)
- `3` - Approfondi (scan complet)

**Categories:**
- `sql_injection` - Injection SQL
- `xss` - Cross-Site Scripting
- `csrf` - Cross-Site Request Forgery
- `broken_auth` - Authentification cassée
- `sensitive_data` - Exposition de données sensibles
- `xxe` - XML External Entities
- `security_misconfig` - Mauvaise configuration
- `insecure_deserialization` - Désérialisation non sécurisée

**Response 200:**
```json
{
  "success": true,
  "scan_id": "vuln_345678",
  "target_url": "https://example.com",
  "scan_time": "2026-03-02T10:40:00Z",
  "duration_seconds": 120,
  "vulnerabilities_found": 5,
  "stats": {
    "critical": 1,
    "high": 2,
    "medium": 2,
    "low": 0
  },
  "vulnerabilities": [
    {
      "id": "vuln_001",
      "name": "SQL Injection",
      "category": "sql_injection",
      "severity": "CRITIQUE",
      "location": "/api/login",
      "parameter": "username",
      "description": "Le paramètre 'username' est vulnérable à l'injection SQL",
      "proof_of_concept": "username=' OR '1'='1",
      "remediation": "Utiliser des requêtes préparées (prepared statements)",
      "cve": "CVE-2021-12345",
      "cvss_score": 9.8
    },
    {
      "id": "vuln_002",
      "name": "Cross-Site Scripting (XSS)",
      "category": "xss",
      "severity": "ÉLEVÉ",
      "location": "/search",
      "parameter": "q",
      "description": "Le paramètre de recherche n'est pas correctement échappé",
      "proof_of_concept": "<script>alert('XSS')</script>",
      "remediation": "Encoder toutes les entrées utilisateur avant affichage",
      "cve": null,
      "cvss_score": 7.5
    }
  ]
}
```

---

### 4. Monitoring Temps Réel

#### GET /api/cyber/monitoring

Récupère les données de monitoring en temps réel.

**Query Parameters:**
- `user_id` (optional) - ID utilisateur pour filtrer
- `limit` (optional) - Nombre max d'alertes (défaut: 10)

**Response 200:**
```json
{
  "success": true,
  "timestamp": "2026-03-02T10:45:00Z",
  "metrics": {
    "packets_per_sec": 450,
    "bandwidth_mbps": 5.2,
    "active_connections": 8,
    "threats_blocked_today": 12,
    "scans_today": 5
  },
  "connections": [
    {
      "id": "conn_001",
      "ip": "192.168.1.100",
      "port": "443",
      "protocol": "HTTPS",
      "state": "ESTABLISHED",
      "data_transferred": "2.4 MB",
      "duration_seconds": 120,
      "is_suspicious": false
    },
    {
      "id": "conn_002",
      "ip": "192.168.1.105",
      "port": "80",
      "protocol": "HTTP",
      "state": "ESTABLISHED",
      "data_transferred": "1.2 MB",
      "duration_seconds": 45,
      "is_suspicious": false
    }
  ],
  "alerts": [
    {
      "id": "alert_001",
      "title": "Tentative d'intrusion détectée",
      "description": "Scan de ports détecté depuis 203.0.113.45",
      "level": "ÉLEVÉ",
      "timestamp": "2026-03-02T10:43:00Z",
      "source_ip": "203.0.113.45",
      "action_taken": "IP bloquée temporairement"
    },
    {
      "id": "alert_002",
      "title": "Trafic suspect",
      "description": "Volume de requêtes anormal détecté",
      "level": "MOYEN",
      "timestamp": "2026-03-02T10:40:00Z",
      "source_ip": "198.51.100.23",
      "action_taken": "Surveillance renforcée"
    }
  ],
  "system": {
    "cpu_usage": 45,
    "ram_usage": 62,
    "disk_usage": 38,
    "uptime_hours": 72
  }
}
```

---

### 5. Scan Rapide

#### POST /api/cyber/quick-scan

Lance un scan rapide de sécurité (combinaison de plusieurs scans).

**Request Body:**
```json
{
  "target": "192.168.1.1",
  "include_ports": true,
  "include_vulnerabilities": true
}
```

**Response 200:**
```json
{
  "success": true,
  "scan_id": "quick_901234",
  "target": "192.168.1.1",
  "scan_time": "2026-03-02T10:50:00Z",
  "duration_seconds": 30,
  "summary": {
    "open_ports": 3,
    "vulnerabilities": 2,
    "risk_level": "MOYEN"
  },
  "details": {
    "ports": [22, 80, 443],
    "vulnerabilities": [
      {
        "name": "Outdated SSL/TLS",
        "severity": "MOYEN"
      }
    ]
  }
}
```

---

### 6. Historique des Scans

#### GET /api/cyber/scan-history

Récupère l'historique des scans effectués.

**Query Parameters:**
- `limit` (optional) - Nombre de résultats (défaut: 20)
- `offset` (optional) - Pagination (défaut: 0)
- `scan_type` (optional) - Filtrer par type (ports, network, vulnerabilities)

**Response 200:**
```json
{
  "success": true,
  "total": 45,
  "limit": 20,
  "offset": 0,
  "scans": [
    {
      "scan_id": "scan_123456",
      "scan_type": "ports",
      "target": "192.168.1.1",
      "timestamp": "2026-03-02T10:30:00Z",
      "duration_seconds": 15,
      "results_summary": "3 ports ouverts",
      "risk_level": "FAIBLE"
    }
  ]
}
```

---

### 7. Détails d'un Scan

#### GET /api/cyber/scan/{scan_id}

Récupère les détails complets d'un scan spécifique.

**Response 200:**
```json
{
  "success": true,
  "scan": {
    "scan_id": "scan_123456",
    "scan_type": "ports",
    "target": "192.168.1.1",
    "timestamp": "2026-03-02T10:30:00Z",
    "duration_seconds": 15,
    "full_results": { /* Résultats complets */ }
  }
}
```

---

## Codes d'Erreur

| Code | Description |
|------|-------------|
| 400 | Requête invalide (paramètres manquants ou incorrects) |
| 401 | Non authentifié (token manquant ou invalide) |
| 403 | Non autorisé (permissions insuffisantes) |
| 404 | Ressource non trouvée |
| 429 | Trop de requêtes (rate limiting) |
| 500 | Erreur serveur interne |
| 503 | Service temporairement indisponible |

## Rate Limiting

- **Scans de ports:** 10 par heure
- **Scans de réseau:** 5 par heure
- **Scans de vulnérabilités:** 3 par heure
- **Monitoring:** 120 requêtes par minute
- **Scan rapide:** 20 par heure

## WebSocket (Monitoring Temps Réel)

Pour le monitoring en temps réel, une connexion WebSocket est disponible:

```
wss://kotighi-ai.onrender.com/ws/monitoring
```

**Message Format:**
```json
{
  "type": "monitoring_update",
  "data": {
    "metrics": { /* ... */ },
    "connections": [ /* ... */ ],
    "alerts": [ /* ... */ ]
  }
}
```

## Implémentation Backend Recommandée

### Python (FastAPI)

```python
from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
import nmap
import asyncio

app = FastAPI()

class PortScanRequest(BaseModel):
    target_ip: str
    port_start: int
    port_end: int
    timeout: int = 1000

@app.post("/api/cyber/scan-ports")
async def scan_ports(request: PortScanRequest, user = Depends(get_current_user)):
    try:
        nm = nmap.PortScanner()
        scan_result = nm.scan(
            request.target_ip,
            f"{request.port_start}-{request.port_end}"
        )
        
        open_ports = []
        for port in nm[request.target_ip]['tcp']:
            if nm[request.target_ip]['tcp'][port]['state'] == 'open':
                open_ports.append({
                    "port": port,
                    "service": nm[request.target_ip]['tcp'][port]['name'],
                    "status": "OUVERT",
                    "banner": nm[request.target_ip]['tcp'][port].get('product', '')
                })
        
        return {
            "success": True,
            "open_ports": open_ports,
            "open_ports_count": len(open_ports)
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
```

## Notes d'Implémentation

1. **Sécurité:** Tous les scans doivent être authentifiés et loggés
2. **Performance:** Utiliser des queues pour les scans longs
3. **Rate Limiting:** Implémenter des limites par utilisateur
4. **Stockage:** Sauvegarder les résultats dans Supabase
5. **Notifications:** Envoyer des notifications push pour alertes critiques

---

**Version:** 1.0  
**Date:** 2 Mars 2026  
**Auteur:** KOTIGHI AI Team
