"""
API KOTIGHI AI - Version Supabase
Backend avec FastAPI + Supabase PostgreSQL
"""
from fastapi import FastAPI, HTTPException, Depends, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr
import pandas as pd
import numpy as np
import pickle
import os
from typing import List, Optional
from datetime import datetime, timedelta

# JWT & Sécurité
from jose import JWTError, jwt
from passlib.context import CryptContext
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm

# Supabase
from supabase_config import get_supabase_client, SUPABASE_URL
from supabase import Client

# Configuration
from dotenv import load_dotenv
load_dotenv()

SECRET_KEY = os.getenv("SECRET_KEY", "kotighi_secret_key_change_me_in_prod")
ALGORITHM = os.getenv("ALGORITHM", "HS256")
ACCESS_TOKEN_EXPIRE_MINUTES = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES", 1440))

# --- SÉCURITÉ ---
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

def get_password_hash(password):
    return pwd_context.hash(password)

def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

# --- DÉPENDANCES ---
def get_supabase() -> Client:
    return get_supabase_client()

async def get_current_user(
    token: str = Depends(oauth2_scheme),
    supabase: Client = Depends(get_supabase)
):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            raise credentials_exception
    except JWTError:
        raise credentials_exception
    
    # Récupérer l'utilisateur depuis Supabase
    response = supabase.table("users").select("*").eq("username", username).execute()
    
    if not response.data or len(response.data) == 0:
        raise credentials_exception
    
    return response.data[0]

# --- API INIT ---
app = FastAPI(
    title="KOTIGHI AI API - Supabase Edition",
    description="API Sécurisée avec JWT, Supabase PostgreSQL et ML",
    version="2.0.0"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # À restreindre en production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- SCHÉMAS Pydantic ---
class UserCreate(BaseModel):
    username: str
    password: str
    full_name: Optional[str] = None
    role: str = "user"
    email: EmailStr
    address: Optional[str] = None
    phone: Optional[str] = None
    country: Optional[str] = None
    organisation: Optional[str] = None

class UserUpdate(BaseModel):
    full_name: Optional[str] = None
    email: Optional[EmailStr] = None
    phone: Optional[str] = None
    role: Optional[str] = None

class Token(BaseModel):
    access_token: str
    token_type: str
    user_id: str  # id is uuid
    username: str
    full_name: Optional[str] = None
    role: str

class CyberInput(BaseModel):
    requetes_min: int
    duree: int
    octets: int
    ports_scanes: int
    taux_erreur: float
    flag_suspect: int

class SanteInput(BaseModel):
    fievre: int
    toux: int
    fatigue: int
    maux_tete: int
    douleur_gorge: int
    nausees: int
    douleur_thorax: int
    essoufflement: int
    diarrhee: int
    frissons: int
    perte_odorat: int
    douleurs_musculaires: int
    palpitations: int
    vertiges: int

class ChatInput(BaseModel):
    message: str

class NotificationCreate(BaseModel):
    titre: str
    message: str
    type: str = "info"

# --- CHARGEMENT DES MODÈLES ---
def load_models():
    print("📦 Chargement des modèles ML...")
    try:
        with open("model_cyber.pkl", "rb") as f:
            m_cyber = pickle.load(f)
        with open("scaler_cyber.pkl", "rb") as f:
            sc_cyber = pickle.load(f)
        
        with open("model_sante.pkl", "rb") as f:
            m_sante = pickle.load(f)
        with open("labels_sante.pkl", "rb") as f:
            labels_sante = pickle.load(f)
        with open("conseils_sante.pkl", "rb") as f:
            conseils_sante = pickle.load(f)
            
        print("✅ Modèles chargés avec succès !")
        return m_cyber, sc_cyber, m_sante, labels_sante, conseils_sante

    except FileNotFoundError:
        print("⚠️ Modèles non trouvés ! Exécutez train_models.py d'abord.")
        from train_models import train_and_save
        train_and_save()
        return load_models()

m_cyber, sc_cyber, m_sante, labels_sante, conseils_sante = load_models()

# --- ENDPOINTS ---

@app.get("/")
def home():
    return {
        "status": "online",
        "message": "API KOTIGHI AI Supabase Edition",
        "version": "2.0.0",
        "database": "Supabase PostgreSQL",
        "supabase_url": SUPABASE_URL
    }

@app.post("/signup", response_model=Token)
def signup(user: UserCreate, supabase: Client = Depends(get_supabase)):
    """Créer un nouveau compte utilisateur"""
    
    # Vérifier si l'utilisateur existe déjà
    existing = supabase.table("users").select("id").eq("username", user.username).execute()
    if existing.data and len(existing.data) > 0:
        raise HTTPException(status_code=400, detail="Ce nom d'utilisateur existe déjà")
    
    # Hasher le mot de passe
    password_hash = get_password_hash(user.password)
    
    # Créer l'utilisateur dans Supabase
    new_user_data = {
        "username": user.username,
        "password_hash": password_hash,
        "full_name": user.full_name,
        "role": user.role,
        "email": user.email,
        "phone": user.phone,
        "address": user.address,
        "country": user.country,
        "organisation": user.organisation
    }
    
    response = supabase.table("users").insert(new_user_data).execute()
    
    if not response.data or len(response.data) == 0:
        raise HTTPException(status_code=500, detail="Erreur lors de la création du compte")
    
    new_user = response.data[0]
    
    # Créer le profil associé
    profil_data = {
        "user_id": new_user["id"],
        "preferences": {}
    }
    supabase.table("profils").insert(profil_data).execute()
    
    # Créer notification de bienvenue
    notif_data = {
        "user_id": new_user["id"],
        "titre": "Bienvenue sur KOTIGHI AI ! 🎉",
        "message": f"Bonjour {user.full_name}, votre compte a été créé avec succès.",
        "type": "success"
    }
    supabase.table("notifications").insert(notif_data).execute()
    
    # Générer le token JWT
    access_token = create_access_token(
        data={"sub": new_user["username"]},
        expires_delta=timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    )
    
    return {
        "access_token": access_token,
        "token_type": "bearer",
        "user_id": new_user["id"],
        "username": new_user["username"],
        "full_name": new_user["full_name"],
        "role": new_user["role"]
    }

@app.post("/login", response_model=Token)
def login(user_data: UserCreate, supabase: Client = Depends(get_supabase)):
    """Connexion utilisateur"""
    
    # Récupérer l'utilisateur
    response = supabase.table("users").select("*").eq("username", user_data.username).execute()
    
    if not response.data or len(response.data) == 0:
        raise HTTPException(status_code=401, detail="Identifiants incorrects")
    
    user = response.data[0]
    
    # Vérifier le mot de passe
    if not verify_password(user_data.password, user["password_hash"]):
        raise HTTPException(status_code=401, detail="Identifiants incorrects")
    
    # Générer le token JWT
    access_token = create_access_token(
        data={"sub": user["username"]},
        expires_delta=timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    )
    
    return {
        "access_token": access_token,
        "token_type": "bearer",
        "user_id": user["id"],
        "username": user["username"],
        "full_name": user["full_name"],
        "role": user["role"]
    }

@app.post("/token", response_model=Token)
def login_for_access_token(
    form_data: OAuth2PasswordRequestForm = Depends(),
    supabase: Client = Depends(get_supabase)
):
    """Connexion OAuth2 standard"""
    
    response = supabase.table("users").select("*").eq("username", form_data.username).execute()
    
    if not response.data or len(response.data) == 0:
        raise HTTPException(status_code=401, detail="Incorrect username or password")
    
    user = response.data[0]
    
    if not verify_password(form_data.password, user["password_hash"]):
        raise HTTPException(status_code=401, detail="Incorrect username or password")
    
    access_token = create_access_token(
        data={"sub": user["username"]},
        expires_delta=timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    )
    
    return {
        "access_token": access_token,
        "token_type": "bearer",
        "user_id": user["id"],
        "username": user["username"],
        "full_name": user["full_name"],
        "role": user["role"]
    }

@app.get("/me")
def get_current_user_info(
    current_user: dict = Depends(get_current_user),
    supabase: Client = Depends(get_supabase)
):
    """Récupérer les informations de l'utilisateur connecté"""
    
    # Récupérer le profil
    profil_response = supabase.table("profils").select("*").eq("user_id", current_user["id"]).execute()
    profil = profil_response.data[0] if profil_response.data else None
    
    # Compter les analyses
    historique_response = supabase.table("historique").select("id", count="exact").eq("user_id", current_user["id"]).execute()
    total_analyses = historique_response.count if historique_response.count else 0
    
    # Compter les notifications non lues
    notif_response = supabase.table("notifications").select("id", count="exact").eq("user_id", current_user["id"]).eq("lu", False).execute()
    notifications_non_lues = notif_response.count if notif_response.count else 0
    
    return {
        "user": current_user,
        "profil": profil,
        "stats": {
            "total_analyses": total_analyses,
            "notifications_non_lues": notifications_non_lues
        }
    }

@app.put("/me")
def update_current_user(
    user_update: UserUpdate,
    current_user: dict = Depends(get_current_user),
    supabase: Client = Depends(get_supabase)
):
    """Mettre à jour les informations de l'utilisateur"""
    
    update_data = user_update.dict(exclude_unset=True)
    
    if update_data:
        response = supabase.table("users").update(update_data).eq("id", current_user["id"]).execute()
        
        if not response.data:
            raise HTTPException(status_code=500, detail="Erreur lors de la mise à jour")
        
        return {"message": "Profil mis à jour avec succès", "user": response.data[0]}
    
    return {"message": "Aucune modification"}

@app.post("/predict/cyber")
def predict_cyber(
    data: CyberInput,
    current_user: dict = Depends(get_current_user),
    supabase: Client = Depends(get_supabase)
):
    """Analyse cybersécurité"""
    try:
        feat = pd.DataFrame([data.dict()])
        feat_scaled = sc_cyber.transform(feat)
        pred = int(m_cyber.predict(feat_scaled)[0])
        proba = m_cyber.predict_proba(feat_scaled)[0].tolist()
        
        verdict = "Attaque" if pred == 1 else "Normal"
        confiance = round(max(proba) * 100, 2)
        
        # Sauvegarder dans l'historique Supabase
        hist_data = {
            "user_id": current_user["id"],
            "type_analyse": "Cyber",
            "resultat": verdict,
            "details": f"Confiance: {confiance}%",
            "confiance": confiance,
            "urgent": pred == 1 and confiance > 90
        }
        supabase.table("historique").insert(hist_data).execute()
        
        # Créer notification si attaque détectée
        if pred == 1:
            notif_data = {
                "user_id": current_user["id"],
                "titre": "⚠️ Attaque détectée !",
                "message": f"Une attaque a été détectée avec {confiance}% de confiance.",
                "type": "warning"
            }
            supabase.table("notifications").insert(notif_data).execute()

        return {
            "prediction": pred,
            "verdict": verdict,
            "confiance": confiance,
            "probabilites": proba
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/predict/sante")
def predict_sante(
    data: SanteInput,
    current_user: dict = Depends(get_current_user),
    supabase: Client = Depends(get_supabase)
):
    """Diagnostic médical"""
    try:
        feat = pd.DataFrame([data.dict()])
        pred = int(m_sante.predict(feat)[0])
        proba = m_sante.predict_proba(feat)[0].tolist()
        
        diag = labels_sante[pred]
        urgent = "cardiaque" in diag.lower() or "covid" in diag.lower()
        conseils = conseils_sante.get(diag, [])
        confiance = round(proba[pred] * 100, 2)
        
        # Sauvegarder dans l'historique
        hist_data = {
            "user_id": current_user["id"],
            "type_analyse": "Sante",
            "resultat": diag,
            "details": f"Urgent: {urgent}, Confiance: {confiance}%",
            "confiance": confiance,
            "urgent": urgent
        }
        supabase.table("historique").insert(hist_data).execute()
        
        # Créer notification si urgent
        if urgent:
            notif_data = {
                "user_id": current_user["id"],
                "titre": "🚨 Diagnostic urgent !",
                "message": f"Diagnostic: {diag}. Consultez immédiatement un médecin.",
                "type": "danger"
            }
            supabase.table("notifications").insert(notif_data).execute()

        return {
            "prediction_id": pred,
            "diagnostic": diag,
            "urgent": urgent,
            "confiance": confiance,
            "conseils": conseils,
            "toutes_probabilites": {labels_sante[i]: round(p*100, 2) for i, p in enumerate(proba)}
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/historique")
def get_historique(
    limit: int = 20,
    type_analyse: Optional[str] = None,
    current_user: dict = Depends(get_current_user),
    supabase: Client = Depends(get_supabase)
):
    """Récupérer l'historique des analyses"""
    
    query = supabase.table("historique").select("*").eq("user_id", current_user["id"]).order("created_at", desc=True).limit(limit)
    
    if type_analyse:
        query = query.eq("type_analyse", type_analyse)
    
    response = query.execute()
    
    return {
        "total": len(response.data),
        "historique": response.data
    }

@app.get("/notifications")
def get_notifications(
    non_lues_seulement: bool = False,
    current_user: dict = Depends(get_current_user),
    supabase: Client = Depends(get_supabase)
):
    """Récupérer les notifications"""
    
    query = supabase.table("notifications").select("*").eq("user_id", current_user["id"]).order("created_at", desc=True)
    
    if non_lues_seulement:
        query = query.eq("lu", False)
    
    response = query.execute()
    
    return {
        "total": len(response.data),
        "notifications": response.data
    }

@app.put("/notifications/{notification_id}/lire")
def marquer_notification_lue(
    notification_id: int,
    current_user: dict = Depends(get_current_user),
    supabase: Client = Depends(get_supabase)
):
    """Marquer une notification comme lue"""
    
    response = supabase.table("notifications").update({"lu": True}).eq("id", notification_id).eq("user_id", current_user["id"]).execute()
    
    if not response.data:
        raise HTTPException(status_code=404, detail="Notification non trouvée")
    
    return {"message": "Notification marquée comme lue"}

@app.post("/chat")
def chat_bot(
    data: ChatInput,
    current_user: dict = Depends(get_current_user)
):
    """Chatbot IA (FAQ)"""
    user_msg = data.message.lower().strip()
    
    FAQ_KNOWLEDGE = {
        "ddos": "Une attaque DDoS (Déni de Service Distribué) vise à rendre un serveur indisponible en le submergeant de requêtes provenant de multiples sources.",
        "dos": "Une attaque DoS (Déni de Service) sature un serveur avec des requêtes pour le rendre inaccessible, mais depuis une seule source.",
        "probe": "Une attaque Probe (Sonde) scanne les ports d'un réseau pour détecter des vulnérabilités.",
        "phishing": "Le phishing (hameçonnage) consiste à tromper un utilisateur pour qu'il révèle des informations sensibles (mots de passe, CB).",
        "covid": "Les symptômes courants du COVID-19 incluent fièvre, toux sèche, fatigue et perte de goût/odorat.",
        "grippe": "La grippe se manifeste par une fièvre brutale, des courbatures, une toux sèche et une grande fatigue.",
    }
    
    reponse = "Je ne suis pas sûr de comprendre. Pouvez-vous préciser votre question sur la cybersécurité ou la santé ?"
    
    for key, val in FAQ_KNOWLEDGE.items():
        if key in user_msg:
            reponse = val
            break
            
    if "bonjour" in user_msg or "salut" in user_msg:
        reponse = f"Bonjour {current_user['full_name']} ! Comment puis-je vous aider aujourd'hui ?"
    elif "merci" in user_msg:
        reponse = "Avec plaisir ! N'hésitez pas si vous avez d'autres questions."

    return {"response": reponse}

@app.get("/stats")
def get_stats(
    current_user: dict = Depends(get_current_user),
    supabase: Client = Depends(get_supabase)
):
    """Statistiques utilisateur"""
    
    # Total analyses
    total_response = supabase.table("historique").select("id", count="exact").eq("user_id", current_user["id"]).execute()
    total_analyses = total_response.count if total_response.count else 0
    
    # Analyses cyber
    cyber_response = supabase.table("historique").select("id", count="exact").eq("user_id", current_user["id"]).eq("type_analyse", "Cyber").execute()
    analyses_cyber = cyber_response.count if cyber_response.count else 0
    
    # Analyses santé
    sante_response = supabase.table("historique").select("id", count="exact").eq("user_id", current_user["id"]).eq("type_analyse", "Sante").execute()
    analyses_sante = sante_response.count if sante_response.count else 0
    
    # Analyses urgentes
    urgent_response = supabase.table("historique").select("id", count="exact").eq("user_id", current_user["id"]).eq("urgent", True).execute()
    analyses_urgentes = urgent_response.count if urgent_response.count else 0
    
    return {
        "total_analyses": total_analyses,
        "analyses_cyber": analyses_cyber,
        "analyses_sante": analyses_sante,
        "analyses_urgentes": analyses_urgentes,
        "membre_depuis": current_user["created_at"]
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

# Import du scanner réseau
from network_scanner import scan_networks_api, NetworkScanner

# --- MODÈLES PYDANTIC POUR SCAN RÉSEAU ---
class NetworkScanRequest(BaseModel):
    deep_scan: bool = False
    scan_type: str = "proximity"  # proximity, full, quick

class NetworkScanResponse(BaseModel):
    success: bool
    scan_timestamp: str
    deep_scan: bool
    summary: dict
    networks: List[dict]

# --- ENDPOINTS SCAN RÉSEAU ---

@app.post("/api/network/scan-proximity", response_model=NetworkScanResponse)
async def scan_proximity_networks(
    request: NetworkScanRequest,
    current_user: dict = Depends(get_current_user)
):
    """
    Scanne les réseaux WiFi de proximité et les classe par vulnérabilité
    
    - **deep_scan**: Si True, effectue un scan approfondi (ports, hôtes)
    - **scan_type**: Type de scan (proximity, full, quick)
    """
    try:
        # Effectuer le scan
        result = scan_networks_api(deep_scan=request.deep_scan)
        
        # Sauvegarder dans l'historique
        supabase = get_supabase_client()
        scan_data = {
            'user_id': current_user['id'],
            'scan_type': 'network_proximity',
            'deep_scan': request.deep_scan,
            'networks_found': result['summary']['total_networks'],
            'critical_count': result['summary']['critical'],
            'high_count': result['summary']['high'],
            'medium_count': result['summary']['medium'],
            'results': result,
            'created_at': datetime.now().isoformat()
        }
        
        supabase.table('historique').insert(scan_data).execute()
        
        # Créer notification si réseaux critiques détectés
        if result['summary']['critical'] > 0:
            notification = {
                'user_id': current_user['id'],
                'type': 'alerte',
                'titre': 'Réseaux vulnérables détectés',
                'message': f"{result['summary']['critical']} réseau(x) avec vulnérabilité critique détecté(s) à proximité",
                'priorite': 'haute',
                'lu': False
            }
            supabase.table('notifications').insert(notification).execute()
        
        return result
        
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erreur lors du scan réseau: {str(e)}"
        )

@app.get("/api/network/scan-history")
async def get_network_scan_history(
    limit: int = 20,
    current_user: dict = Depends(get_current_user)
):
    """Récupère l'historique des scans réseau"""
    try:
        supabase = get_supabase_client()
        
        response = supabase.table('historique')\
            .select('*')\
            .eq('user_id', current_user['id'])\
            .eq('scan_type', 'network_proximity')\
            .order('created_at', desc=True)\
            .limit(limit)\
            .execute()
        
        return {
            'success': True,
            'scans': response.data
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erreur lors de la récupération de l'historique: {str(e)}"
        )

@app.get("/api/network/vulnerability-stats")
async def get_vulnerability_stats(
    current_user: dict = Depends(get_current_user)
):
    """Statistiques sur les vulnérabilités détectées"""
    try:
        supabase = get_supabase_client()
        
        # Récupérer tous les scans de l'utilisateur
        response = supabase.table('historique')\
            .select('*')\
            .eq('user_id', current_user['id'])\
            .eq('scan_type', 'network_proximity')\
            .execute()
        
        scans = response.data
        
        if not scans:
            return {
                'success': True,
                'total_scans': 0,
                'total_networks': 0,
                'vulnerability_distribution': {
                    'critical': 0,
                    'high': 0,
                    'medium': 0,
                    'low': 0,
                    'minimal': 0
                },
                'most_common_vulnerabilities': [],
                'encryption_distribution': {}
            }
        
        # Calculer les statistiques
        total_networks = sum(scan.get('networks_found', 0) for scan in scans)
        total_critical = sum(scan.get('critical_count', 0) for scan in scans)
        total_high = sum(scan.get('high_count', 0) for scan in scans)
        total_medium = sum(scan.get('medium_count', 0) for scan in scans)
        
        # Analyser les vulnérabilités communes
        vulnerability_counts = {}
        encryption_counts = {}
        
        for scan in scans:
            results = scan.get('results', {})
            networks = results.get('networks', [])
            
            for network in networks:
                # Compter les vulnérabilités
                for vuln in network.get('vulnerabilities', []):
                    vulnerability_counts[vuln] = vulnerability_counts.get(vuln, 0) + 1
                
                # Compter les types de chiffrement
                encryption = network.get('encryption', 'UNKNOWN')
                encryption_counts[encryption] = encryption_counts.get(encryption, 0) + 1
        
        # Top 10 vulnérabilités
        most_common = sorted(
            vulnerability_counts.items(),
            key=lambda x: x[1],
            reverse=True
        )[:10]
        
        return {
            'success': True,
            'total_scans': len(scans),
            'total_networks': total_networks,
            'vulnerability_distribution': {
                'critical': total_critical,
                'high': total_high,
                'medium': total_medium,
                'low': 0,  # À calculer si nécessaire
                'minimal': 0
            },
            'most_common_vulnerabilities': [
                {'vulnerability': vuln, 'count': count}
                for vuln, count in most_common
            ],
            'encryption_distribution': encryption_counts
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erreur lors du calcul des statistiques: {str(e)}"
        )

@app.post("/api/network/quick-scan")
async def quick_network_scan(current_user: dict = Depends(get_current_user)):
    """Scan rapide des réseaux (sans scan approfondi)"""
    try:
        result = scan_networks_api(deep_scan=False)
        
        return {
            'success': True,
            'networks_found': result['summary']['total_networks'],
            'critical_networks': result['summary']['critical'],
            'summary': result['summary'],
            'top_vulnerable': result['networks'][:3] if result['networks'] else []
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erreur lors du scan rapide: {str(e)}"
        )
