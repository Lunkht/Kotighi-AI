# ============================================================
#  API KOTIGHI AI - Backend avec FastAPI
#  Authentification JWT, PostgreSQL, IA
# ============================================================
from fastapi import FastAPI, HTTPException, Depends, status
from pydantic import BaseModel
import pandas as pd
import numpy as np
import pickle
import os
import hashlib
from typing import List, Optional
from datetime import datetime, timedelta

# JWT & Securité
from jose import JWTError, jwt
from passlib.context import CryptContext
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm

# Base de données
from sqlalchemy import create_engine, Column, Integer, String, Float, DateTime, ForeignKey, Boolean
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session

# Import des fonctions de chargement de modèles
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler

# --- CONFIGURATION ---
SECRET_KEY = "kotighi_secret_key_change_me_in_prod" # A changer en prod
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60 * 24 # 24 heures

# Database URL (PostgreSQL sur Render ou SQLite local)
DATABASE_URL = os.getenv("DATABASE_URL", "sqlite:///./kotighi.db")
if DATABASE_URL.startswith("postgres://"):
    DATABASE_URL = DATABASE_URL.replace("postgres://", "postgresql://", 1)

# --- DATABASE SETUP ---
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# Modèles DB
class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, index=True)
    hashed_password = Column(String)
    full_name = Column(String)
    role = Column(String) # "Utilisateur", "Medecin", "Analyste", "Admin"
    created_at = Column(DateTime, default=datetime.utcnow)

class Historique(Base):
    __tablename__ = "historique"
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    type_analyse = Column(String) # "Sante" ou "Cyber"
    resultat = Column(String)
    details = Column(String) # JSON stringifié
    created_at = Column(DateTime, default=datetime.utcnow)

# Création des tables
Base.metadata.create_all(bind=engine)

# Dependency DB
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# --- SECURITE ---
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

async def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)):
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
    user = db.query(User).filter(User.username == username).first()
    if user is None:
        raise credentials_exception
    return user

# --- API INIT ---
app = FastAPI(title="KOTIGHI AI API", description="API Sécurisée avec JWT et PostgreSQL")

# --- SCHÉMAS Pydantic ---
class UserCreate(BaseModel):
    username: str
    password: str
    full_name: str
    role: str = "Utilisateur"

class Token(BaseModel):
    access_token: str
    token_type: str
    nom: str
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

# --- CHARGEMENT DES MODÈLES ---
def load_models():
    print("📦 Chargement des modèles...")
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


# --- ENDPOINTS AUTH ---

@app.post("/signup", response_model=Token)
def signup(user: UserCreate, db: Session = Depends(get_db)):
    db_user = db.query(User).filter(User.username == user.username).first()
    if db_user:
        raise HTTPException(status_code=400, detail="Ce nom d'utilisateur existe déjà")
    
    hashed_password = get_password_hash(user.password)
    new_user = User(username=user.username, hashed_password=hashed_password, full_name=user.full_name, role=user.role)
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    
    access_token = create_access_token(data={"sub": new_user.username})
    return {"access_token": access_token, "token_type": "bearer", "nom": new_user.full_name, "role": new_user.role}

@app.post("/login", response_model=Token) # Route compatible Android
def login_api(user_data: UserCreate, db: Session = Depends(get_db)): # On réutilise UserCreate mais on ignore full_name/role
    user = db.query(User).filter(User.username == user_data.username).first()
    if not user or not verify_password(user_data.password, user.hashed_password):
        raise HTTPException(status_code=401, detail="Identifiants incorrects")
    
    access_token = create_access_token(data={"sub": user.username})
    return {"access_token": access_token, "token_type": "bearer", "nom": user.full_name, "role": user.role}

@app.post("/token", response_model=Token) # Route standard OAuth2
def login_for_access_token(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user = db.query(User).filter(User.username == form_data.username).first()
    if not user or not verify_password(form_data.password, user.hashed_password):
        raise HTTPException(status_code=401, detail="Incorrect username or password")
    
    access_token = create_access_token(data={"sub": user.username})
    return {"access_token": access_token, "token_type": "bearer", "nom": user.full_name, "role": user.role}


# --- ENDPOINTS PREDICTION (PROTEGÉS) ---

@app.get("/")
def home():
    return {"status": "online", "message": "API KOTIGHI AI Sécurisée"}

@app.post("/predict/cyber")
def predict_cyber(data: CyberInput, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    try:
        feat = pd.DataFrame([data.dict()])
        feat_scaled = sc_cyber.transform(feat)
        pred = int(m_cyber.predict(feat_scaled)[0])
        proba = m_cyber.predict_proba(feat_scaled)[0].tolist()
        
        verdict = "Attaque" if pred == 1 else "Normal"
        confiance = round(max(proba) * 100, 2)
        
        # Sauvegarde historique
        hist = Historique(
            user_id=current_user.id,
            type_analyse="Cyber",
            resultat=verdict,
            details=f"Confiance: {confiance}%"
        )
        db.add(hist)
        db.commit()

        return {
            "prediction": pred,
            "verdict": verdict,
            "confiance": confiance,
            "probabilites": proba
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/predict/sante")
def predict_sante(data: SanteInput, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    try:
        feat = pd.DataFrame([data.dict()])
        pred = int(m_sante.predict(feat)[0])
        proba = m_sante.predict_proba(feat)[0].tolist()
        
        diag = labels_sante[pred]
        urgent = "cardiaque" in diag.lower() or "covid" in diag.lower()
        conseils = conseils_sante.get(diag, [])
        confiance = round(proba[pred] * 100, 2)
        
        # Sauvegarde historique
        hist = Historique(
            user_id=current_user.id,
            type_analyse="Sante",
            resultat=diag,
            details=f"Urgent: {urgent}, Confiance: {confiance}%"
        )
        db.add(hist)
        db.commit()

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
def get_historique(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    history = db.query(Historique).filter(Historique.user_id == current_user.id).order_by(Historique.created_at.desc()).limit(20).all()
    return history

# --- CHATBOT IA (FAQ) ---
class ChatInput(BaseModel):
    message: str

FAQ_KNOWLEDGE = {
    # CYBER
    "ddos": "Une attaque DDoS (Déni de Service Distribué) vise à rendre un serveur indisponible en le submergeant de requêtes provenant de multiples sources.",
    "dos": "Une attaque DoS (Déni de Service) sature un serveur avec des requêtes pour le rendre inaccessible, mais depuis une seule source.",
    "probe": "Une attaque Probe (Sonde) scanne les ports d'un réseau pour détecter des vulnérabilités.",
    "phishing": "Le phishing (hameçonnage) consiste à tromper un utilisateur pour qu'il révèle des informations sensibles (mots de passe, CB).",
    "r2l": "R2L (Remote to Local) : Un attaquant distant tente d'obtenir un accès utilisateur local sur une machine.",
    "u2r": "U2R (User to Root) : Un utilisateur normal tente d'obtenir les privilèges administrateur (root).",
    "sql": "L'injection SQL consiste à insérer du code malveillant dans une requête de base de données.",
    
    # SANTE
    "covid": "Les symptômes courants du COVID-19 incluent fièvre, toux sèche, fatigue et perte de goût/odorat.",
    "grippe": "La grippe se manifeste par une fièvre brutale, des courbatures, une toux sèche et une grande fatigue.",
    "fievre": "Une fièvre supérieure à 38°C peut indiquer une infection. Restez hydraté et surveillez votre température.",
    "urgence": "En cas de douleur thoracique intense, difficulté à respirer ou perte de connaissance, appelez immédiatement les secours (15 ou 112).",
    "prevention": "Lavez-vous les mains régulièrement, portez un masque si malade et maintenez une distance physique.",
}

@app.post("/chat")
def chat_bot(data: ChatInput, current_user: User = Depends(get_current_user)):
    user_msg = data.message.lower().strip()
    
    # Recherche simple par mots-clés
    reponse = "Je ne suis pas sûr de comprendre. Pouvez-vous préciser votre question sur la cybersécurité ou la santé ?"
    
    # Priorité aux correspondances exactes
    for key, val in FAQ_KNOWLEDGE.items():
        if key in user_msg:
            reponse = val
            break
            
    # Réponses contextuelles basiques
    if "bonjour" in user_msg or "salut" in user_msg:
        reponse = f"Bonjour {current_user.full_name} ! Comment puis-je vous aider aujourd'hui ?"
    elif "merci" in user_msg:
        reponse = "Avec plaisir ! N'hésitez pas si vous avez d'autres questions."
    elif "aide" in user_msg:
        reponse = "Je peux vous renseigner sur les types d'attaques (DDoS, Phishing...) ou les symptômes médicaux (Grippe, COVID...)."

    return {"response": reponse}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
