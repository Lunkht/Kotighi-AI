from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import pandas as pd
import numpy as np
import pickle
import os
import hashlib
from typing import List, Optional

# Import des fonctions de chargement de modèles (on réutilise la logique de app_kotighi)
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.preprocessing import StandardScaler

app = FastAPI(title="KOTIGHI AI API", description="API pour la détection d'intrusion et le diagnostic médical")

# --- AUTHENTIFICATION ---
def h(p): return hashlib.sha256(p.encode()).hexdigest()

USERS = {
    "admin":    {"hash":h("kotighi2024"),"role":"Administrateur","nom":"Admin Principal",   "acces":["Dashboard","Cybersecurite","Sante","Gestion"]},
    "analyste": {"hash":h("analyse123"), "role":"Analyste Cyber", "nom":"Jean Dupont",      "acces":["Dashboard","Cybersecurite"]},
    "medecin":  {"hash":h("sante456"),   "role":"Medecin",        "nom":"Dr. House",        "acces":["Dashboard","Sante"]},
}

# --- SCHÉMAS DE DONNÉES ---
class LoginInput(BaseModel):
    username: str
    password: str

class SignupInput(BaseModel):
    username: str
    password: str
    nom: str
    role: str = "Utilisateur"

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

# --- CHARGEMENT DES MODÈLES ---
def load_models():
    print("📦 Chargement des modèles...")
    try:
        # Cyber
        with open("model_cyber.pkl", "rb") as f:
            m_cyber = pickle.load(f)
        with open("scaler_cyber.pkl", "rb") as f:
            sc_cyber = pickle.load(f)
        
        # Sante
        with open("model_sante.pkl", "rb") as f:
            m_sante = pickle.load(f)
        with open("labels_sante.pkl", "rb") as f:
            labels_sante = pickle.load(f)
            
        print("✅ Modèles chargés avec succès !")
        return m_cyber, sc_cyber, m_sante, labels_sante

    except FileNotFoundError:
        print("⚠️ Modèles non trouvés ! Exécutez train_models.py d'abord.")
        # Fallback : on recrée des modèles vides pour éviter le crash (mode dégradé)
        # Mais en production Docker, cela ne devrait pas arriver si les fichiers sont copiés
        from train_models import train_and_save
        train_and_save()
        return load_models()

m_cyber, sc_cyber, m_sante, labels_sante = load_models()

# --- ENDPOINTS ---

@app.get("/")
def home():
    return {"status": "online", "message": "Bienvenue sur l'API KOTIGHI AI"}

@app.post("/login")
def login(data: LoginInput):
    user = data.username.strip().lower()
    if user in USERS and USERS[user]["hash"] == h(data.password):
        u = USERS[user]
        return {
            "token": "fake-jwt-token-123456",  # En prod, utiliser JWT
            "nom": u["nom"],
            "role": u["role"],
            "acces": u["acces"]
        }
    raise HTTPException(status_code=401, detail="Identifiants incorrects")

@app.post("/signup")
def signup(data: SignupInput):
    user = data.username.strip().lower()
    if user in USERS:
        raise HTTPException(status_code=400, detail="Ce nom d'utilisateur existe deja")
    
    USERS[user] = {
        "hash": h(data.password),
        "role": data.role,
        "nom": data.nom,
        "acces": ["Dashboard", "Sante", "Cybersecurite"] # Acces par defaut
    }
    
    return {"message": "Compte cree avec succes", "username": user}

@app.post("/predict/cyber")
def predict_cyber(data: CyberInput):
    try:
        feat = pd.DataFrame([data.dict()])
        feat_scaled = sc_cyber.transform(feat)
        pred = int(m_cyber.predict(feat_scaled)[0])
        proba = m_cyber.predict_proba(feat_scaled)[0].tolist()
        
        verdict = "Attaque" if pred == 1 else "Normal"
        return {
            "prediction": pred,
            "verdict": verdict,
            "confiance": round(max(proba) * 100, 2),
            "probabilites": proba
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/predict/sante")
def predict_sante(data: SanteInput):
    try:
        feat = pd.DataFrame([data.dict()])
        pred = int(m_sante.predict(feat)[0])
        proba = m_sante.predict_proba(feat)[0].tolist()
        
        diag = labels_sante[pred]
        urgent = "cardiaque" in diag.lower() or "covid" in diag.lower()
        
        return {
            "prediction_id": pred,
            "diagnostic": diag,
            "urgent": urgent,
            "confiance": round(proba[pred] * 100, 2),
            "toutes_probabilites": {labels_sante[i]: round(p*100, 2) for i, p in enumerate(proba)}
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
