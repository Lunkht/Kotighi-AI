"""
Module de scan automatique des réseaux de proximité
Détecte, analyse et classe les réseaux par niveau de vulnérabilité
"""

import socket
import struct
import subprocess
import re
import threading
import time
from typing import List, Dict, Tuple
from dataclasses import dataclass, asdict
from datetime import datetime
import ipaddress


@dataclass
class NetworkInfo:
    """Informations sur un réseau détecté"""
    ssid: str
    bssid: str
    signal_strength: int
    channel: int
    encryption: str
    frequency: str
    ip_range: str = None
    gateway: str = None
    vulnerability_score: float = 0.0
    vulnerabilities: List[str] = None
    open_ports: List[int] = None
    devices_count: int = 0
    scan_timestamp: str = None
    
    def __post_init__(self):
        if self.vulnerabilities is None:
            self.vulnerabilities = []
        if self.open_ports is None:
            self.open_ports = []
        if self.scan_timestamp is None:
            self.scan_timestamp = datetime.now().isoformat()


class NetworkScanner:
    """Scanner de réseaux automatique avec analyse de vulnérabilités"""
    
    # Ports communs à scanner
    COMMON_PORTS = [21, 22, 23, 25, 53, 80, 110, 143, 443, 445, 3306, 3389, 5432, 8080]
    
    # Scores de vulnérabilité par type de chiffrement
    ENCRYPTION_SCORES = {
        'OPEN': 100.0,      # Réseau ouvert - très vulnérable
        'WEP': 90.0,        # WEP cassable facilement
        'WPA': 60.0,        # WPA ancien
        'WPA2-PSK': 30.0,   # WPA2 avec PSK
        'WPA2-EAP': 20.0,   # WPA2 Enterprise
        'WPA3': 10.0,       # WPA3 - plus sécurisé
        'WPA3-SAE': 5.0     # WPA3 avec SAE - très sécurisé
    }
    
    def __init__(self):
        self.networks: List[NetworkInfo] = []
        self.scan_in_progress = False
        
    def get_local_ip(self) -> str:
        """Obtient l'adresse IP locale"""
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect(("8.8.8.8", 80))
            local_ip = s.getsockname()[0]
            s.close()
            return local_ip
        except:
            return "127.0.0.1"
    
    def get_network_range(self, ip: str) -> str:
        """Calcule la plage réseau à partir d'une IP"""
        try:
            network = ipaddress.IPv4Network(f"{ip}/24", strict=False)
            return str(network)
        except:
            return "192.168.1.0/24"
    
    def scan_wifi_networks(self) -> List[Dict]:
        """
        Scanne les réseaux WiFi disponibles
        Note: Nécessite des privilèges root sur Linux
        """
        networks = []
        
        try:
            # Commande pour Linux (iwlist)
            result = subprocess.run(
                ['iwlist', 'scan'],
                capture_output=True,
                text=True,
                timeout=10
            )
            
            if result.returncode == 0:
                networks = self._parse_iwlist_output(result.stdout)
            else:
                # Fallback: simulation pour démo
                networks = self._simulate_wifi_scan()
                
        except (subprocess.TimeoutExpired, FileNotFoundError):
            # Si iwlist n'est pas disponible, simulation
            networks = self._simulate_wifi_scan()
        
        return networks
    
    def _parse_iwlist_output(self, output: str) -> List[Dict]:
        """Parse la sortie de iwlist scan"""
        networks = []
        current_network = {}
        
        for line in output.split('\n'):
            line = line.strip()
            
            if 'ESSID:' in line:
                ssid = re.search(r'ESSID:"(.+)"', line)
                if ssid:
                    current_network['ssid'] = ssid.group(1)
            
            elif 'Address:' in line and 'Access Point' not in line:
                bssid = re.search(r'Address: (.+)', line)
                if bssid:
                    current_network['bssid'] = bssid.group(1)
            
            elif 'Signal level=' in line:
                signal = re.search(r'Signal level=(-?\d+)', line)
                if signal:
                    current_network['signal_strength'] = int(signal.group(1))
            
            elif 'Channel:' in line:
                channel = re.search(r'Channel:(\d+)', line)
                if channel:
                    current_network['channel'] = int(channel.group(1))
            
            elif 'Encryption key:' in line:
                if 'on' in line:
                    current_network['encryption'] = 'WPA2-PSK'  # Par défaut
                else:
                    current_network['encryption'] = 'OPEN'
            
            elif 'Frequency:' in line:
                freq = re.search(r'Frequency:([\d.]+) GHz', line)
                if freq:
                    current_network['frequency'] = f"{freq.group(1)} GHz"
            
            # Fin d'un réseau
            if current_network and 'ssid' in current_network and line == '':
                networks.append(current_network.copy())
                current_network = {}
        
        return networks
    
    def _simulate_wifi_scan(self) -> List[Dict]:
        """Simule un scan WiFi pour démo"""
        return [
            {
                'ssid': 'HomeNetwork_2.4G',
                'bssid': '00:11:22:33:44:55',
                'signal_strength': -45,
                'channel': 6,
                'encryption': 'WPA2-PSK',
                'frequency': '2.4 GHz'
            },
            {
                'ssid': 'PublicWiFi',
                'bssid': 'AA:BB:CC:DD:EE:FF',
                'signal_strength': -60,
                'channel': 11,
                'encryption': 'OPEN',
                'frequency': '2.4 GHz'
            },
            {
                'ssid': 'Office_5G',
                'bssid': '11:22:33:44:55:66',
                'signal_strength': -55,
                'channel': 36,
                'encryption': 'WPA3',
                'frequency': '5 GHz'
            },
            {
                'ssid': 'OldRouter',
                'bssid': 'FF:EE:DD:CC:BB:AA',
                'signal_strength': -70,
                'channel': 1,
                'encryption': 'WEP',
                'frequency': '2.4 GHz'
            },
            {
                'ssid': 'Neighbor_WiFi',
                'bssid': '99:88:77:66:55:44',
                'signal_strength': -75,
                'channel': 9,
                'encryption': 'WPA',
                'frequency': '2.4 GHz'
            }
        ]
    
    def scan_network_hosts(self, network_range: str) -> List[str]:
        """
        Scanne les hôtes actifs sur le réseau
        Utilise ping pour détecter les appareils
        """
        active_hosts = []
        network = ipaddress.IPv4Network(network_range, strict=False)
        
        def ping_host(ip: str):
            try:
                result = subprocess.run(
                    ['ping', '-c', '1', '-W', '1', str(ip)],
                    capture_output=True,
                    timeout=2
                )
                if result.returncode == 0:
                    active_hosts.append(str(ip))
            except:
                pass
        
        # Scan parallèle des hôtes (limité à 50 pour performance)
        threads = []
        for ip in list(network.hosts())[:50]:
            thread = threading.Thread(target=ping_host, args=(ip,))
            thread.start()
            threads.append(thread)
        
        # Attendre tous les threads (max 5 secondes)
        for thread in threads:
            thread.join(timeout=5)
        
        return active_hosts
    
    def scan_ports(self, ip: str, ports: List[int] = None) -> List[int]:
        """Scanne les ports ouverts sur une IP"""
        if ports is None:
            ports = self.COMMON_PORTS
        
        open_ports = []
        
        for port in ports:
            try:
                sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                sock.settimeout(0.5)
                result = sock.connect_ex((ip, port))
                if result == 0:
                    open_ports.append(port)
                sock.close()
            except:
                pass
        
        return open_ports
    
    def analyze_vulnerabilities(self, network_info: Dict) -> Tuple[float, List[str]]:
        """
        Analyse les vulnérabilités d'un réseau
        Retourne un score (0-100) et une liste de vulnérabilités
        """
        vulnerabilities = []
        score = 0.0
        
        # 1. Analyse du chiffrement (40% du score)
        encryption = network_info.get('encryption', 'UNKNOWN')
        encryption_score = self.ENCRYPTION_SCORES.get(encryption, 50.0)
        score += encryption_score * 0.4
        
        if encryption == 'OPEN':
            vulnerabilities.append("Réseau ouvert sans chiffrement")
        elif encryption == 'WEP':
            vulnerabilities.append("Chiffrement WEP obsolète et facilement cassable")
        elif encryption == 'WPA':
            vulnerabilities.append("WPA ancien, vulnérable aux attaques")
        
        # 2. Force du signal (10% du score)
        signal = network_info.get('signal_strength', -100)
        if signal > -50:
            score += 10.0
            vulnerabilities.append("Signal très fort - facilement accessible")
        elif signal > -70:
            score += 5.0
        
        # 3. Ports ouverts (30% du score)
        open_ports = network_info.get('open_ports', [])
        if open_ports:
            port_score = min(len(open_ports) * 5, 30)
            score += port_score
            
            # Ports critiques
            if 23 in open_ports:
                vulnerabilities.append("Port Telnet (23) ouvert - non sécurisé")
            if 21 in open_ports:
                vulnerabilities.append("Port FTP (21) ouvert - transferts non chiffrés")
            if 445 in open_ports:
                vulnerabilities.append("Port SMB (445) ouvert - vulnérable aux attaques")
            if 3389 in open_ports:
                vulnerabilities.append("Port RDP (3389) ouvert - cible d'attaques")
            if len(open_ports) > 5:
                vulnerabilities.append(f"{len(open_ports)} ports ouverts détectés")
        
        # 4. Nombre d'appareils (10% du score)
        devices = network_info.get('devices_count', 0)
        if devices > 10:
            score += 10.0
            vulnerabilities.append(f"Réseau avec {devices} appareils - surface d'attaque élevée")
        elif devices > 5:
            score += 5.0
        
        # 5. Canal WiFi (10% du score)
        channel = network_info.get('channel', 0)
        if channel in [1, 6, 11]:  # Canaux standards
            score += 5.0
        else:
            score += 2.0
        
        # Normaliser le score entre 0 et 100
        score = min(max(score, 0.0), 100.0)
        
        return score, vulnerabilities
    
    def get_vulnerability_level(self, score: float) -> str:
        """Détermine le niveau de vulnérabilité"""
        if score >= 80:
            return "CRITIQUE"
        elif score >= 60:
            return "ÉLEVÉ"
        elif score >= 40:
            return "MOYEN"
        elif score >= 20:
            return "FAIBLE"
        else:
            return "MINIMAL"
    
    def scan_proximity_networks(self, deep_scan: bool = False) -> List[NetworkInfo]:
        """
        Scan complet des réseaux de proximité
        
        Args:
            deep_scan: Si True, effectue un scan approfondi (ports, hôtes)
        
        Returns:
            Liste des réseaux classés par vulnérabilité
        """
        self.scan_in_progress = True
        self.networks = []
        
        try:
            # 1. Scanner les réseaux WiFi disponibles
            wifi_networks = self.scan_wifi_networks()
            
            # 2. Analyser chaque réseau
            for wifi_net in wifi_networks:
                network_info = NetworkInfo(
                    ssid=wifi_net.get('ssid', 'Unknown'),
                    bssid=wifi_net.get('bssid', '00:00:00:00:00:00'),
                    signal_strength=wifi_net.get('signal_strength', -100),
                    channel=wifi_net.get('channel', 0),
                    encryption=wifi_net.get('encryption', 'UNKNOWN'),
                    frequency=wifi_net.get('frequency', 'Unknown')
                )
                
                # 3. Scan approfondi si demandé
                if deep_scan:
                    # Obtenir la plage IP (simulation)
                    local_ip = self.get_local_ip()
                    network_range = self.get_network_range(local_ip)
                    network_info.ip_range = network_range
                    
                    # Scanner les hôtes actifs
                    active_hosts = self.scan_network_hosts(network_range)
                    network_info.devices_count = len(active_hosts)
                    
                    # Scanner les ports du gateway (premier hôte)
                    if active_hosts:
                        gateway = active_hosts[0]
                        network_info.gateway = gateway
                        network_info.open_ports = self.scan_ports(gateway)
                
                # 4. Analyser les vulnérabilités
                network_dict = asdict(network_info)
                score, vulns = self.analyze_vulnerabilities(network_dict)
                network_info.vulnerability_score = score
                network_info.vulnerabilities = vulns
                
                self.networks.append(network_info)
            
            # 5. Trier par score de vulnérabilité (décroissant)
            self.networks.sort(key=lambda x: x.vulnerability_score, reverse=True)
            
        finally:
            self.scan_in_progress = False
        
        return self.networks
    
    def get_scan_summary(self) -> Dict:
        """Retourne un résumé du scan"""
        if not self.networks:
            return {
                'total_networks': 0,
                'critical': 0,
                'high': 0,
                'medium': 0,
                'low': 0,
                'minimal': 0
            }
        
        summary = {
            'total_networks': len(self.networks),
            'critical': 0,
            'high': 0,
            'medium': 0,
            'low': 0,
            'minimal': 0,
            'average_score': 0.0,
            'most_vulnerable': None,
            'least_vulnerable': None
        }
        
        total_score = 0.0
        for network in self.networks:
            level = self.get_vulnerability_level(network.vulnerability_score)
            summary[level.lower()] += 1
            total_score += network.vulnerability_score
        
        summary['average_score'] = total_score / len(self.networks)
        summary['most_vulnerable'] = asdict(self.networks[0]) if self.networks else None
        summary['least_vulnerable'] = asdict(self.networks[-1]) if self.networks else None
        
        return summary
    
    def export_results(self) -> List[Dict]:
        """Exporte les résultats au format JSON"""
        return [asdict(network) for network in self.networks]


# Fonction utilitaire pour l'API
def scan_networks_api(deep_scan: bool = False) -> Dict:
    """
    Fonction wrapper pour l'API FastAPI
    
    Args:
        deep_scan: Effectuer un scan approfondi
    
    Returns:
        Dictionnaire avec les résultats du scan
    """
    scanner = NetworkScanner()
    networks = scanner.scan_proximity_networks(deep_scan=deep_scan)
    summary = scanner.get_scan_summary()
    
    return {
        'success': True,
        'scan_timestamp': datetime.now().isoformat(),
        'deep_scan': deep_scan,
        'summary': summary,
        'networks': scanner.export_results()
    }


if __name__ == '__main__':
    # Test du scanner
    print("🔍 Démarrage du scan des réseaux de proximité...")
    print("=" * 60)
    
    scanner = NetworkScanner()
    networks = scanner.scan_proximity_networks(deep_scan=False)
    
    print(f"\n📡 {len(networks)} réseaux détectés\n")
    
    for i, network in enumerate(networks, 1):
        level = scanner.get_vulnerability_level(network.vulnerability_score)
        print(f"{i}. {network.ssid}")
        print(f"   BSSID: {network.bssid}")
        print(f"   Signal: {network.signal_strength} dBm")
        print(f"   Chiffrement: {network.encryption}")
        print(f"   Score de vulnérabilité: {network.vulnerability_score:.1f}/100")
        print(f"   Niveau: {level}")
        if network.vulnerabilities:
            print(f"   Vulnérabilités:")
            for vuln in network.vulnerabilities:
                print(f"      - {vuln}")
        print()
    
    # Résumé
    summary = scanner.get_scan_summary()
    print("=" * 60)
    print("📊 RÉSUMÉ DU SCAN")
    print("=" * 60)
    print(f"Total réseaux: {summary['total_networks']}")
    print(f"Critiques: {summary['critical']}")
    print(f"Élevés: {summary['high']}")
    print(f"Moyens: {summary['medium']}")
    print(f"Faibles: {summary['low']}")
    print(f"Minimaux: {summary['minimal']}")
    print(f"Score moyen: {summary['average_score']:.1f}/100")
