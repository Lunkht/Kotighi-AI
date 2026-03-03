"""
Tests pour l'API KOTIGHI AI
"""
import pytest
from fastapi.testclient import TestClient
from api_kotighi import app
import os

# Configuration pour les tests
os.environ["DATABASE_URL"] = "sqlite:///./test_kotighi.db"
os.environ["SECRET_KEY"] = "test_secret_key_for_testing_only"

client = TestClient(app)

# ============================================================
#  TESTS AUTHENTIFICATION
# ============================================================

def test_home():
    """Test endpoint racine"""
    response = client.get("/")
    assert response.status_code == 200
    assert response.json()["status"] == "online"

def test_signup_success():
    """Test création de compte réussie"""
    response = client.post("/signup", json={
        "username": "test_user",
        "password": "TestPass123",
        "full_name": "Test User",
        "role": "Utilisateur"
    })
    assert response.status_code == 200
    assert "access_token" in response.json()
    assert response.json()["token_type"] == "bearer"

def test_signup_duplicate():
    """Test création de compte avec username existant"""
    # Créer un premier utilisateur
    client.post("/signup", json={
        "username": "duplicate_user",
        "password": "Pass123",
        "full_name": "User One",
        "role": "Utilisateur"
    })
    
    # Tenter de créer le même username
    response = client.post("/signup", json={
        "username": "duplicate_user",
        "password": "Pass456",
        "full_name": "User Two",
        "role": "Utilisateur"
    })
    assert response.status_code == 400
    assert "existe déjà" in response.json()["detail"]

def test_login_success():
    """Test connexion réussie"""
    # Créer un utilisateur
    client.post("/signup", json={
        "username": "login_test",
        "password": "LoginPass123",
        "full_name": "Login Test",
        "role": "Utilisateur"
    })
    
    # Se connecter
    response = client.post("/login", json={
        "username": "login_test",
        "password": "LoginPass123"
    })
    assert response.status_code == 200
    assert "access_token" in response.json()

def test_login_wrong_password():
    """Test connexion avec mauvais mot de passe"""
    # Créer un utilisateur
    client.post("/signup", json={
        "username": "wrong_pass_test",
        "password": "CorrectPass123",
        "full_name": "Wrong Pass Test",
        "role": "Utilisateur"
    })
    
    # Tenter connexion avec mauvais mot de passe
    response = client.post("/login", json={
        "username": "wrong_pass_test",
        "password": "WrongPass123"
    })
    assert response.status_code == 401

# ============================================================
#  TESTS PRÉDICTIONS (NÉCESSITENT AUTHENTIFICATION)
# ============================================================

@pytest.fixture
def auth_token():
    """Fixture pour obtenir un token d'authentification"""
    response = client.post("/signup", json={
        "username": f"test_auth_{os.urandom(4).hex()}",
        "password": "AuthPass123",
        "full_name": "Auth Test",
        "role": "Utilisateur"
    })
    return response.json()["access_token"]

def test_predict_cyber_unauthorized():
    """Test prédiction cyber sans authentification"""
    response = client.post("/predict/cyber", json={
        "requetes_min": 1000,
        "duree": 2,
        "octets": 100,
        "ports_scanes": 50,
        "taux_erreur": 0.8,
        "flag_suspect": 1
    })
    assert response.status_code == 401

def test_predict_cyber_success(auth_token):
    """Test prédiction cyber avec authentification"""
    response = client.post(
        "/predict/cyber",
        json={
            "requetes_min": 1000,
            "duree": 2,
            "octets": 100,
            "ports_scanes": 50,
            "taux_erreur": 0.8,
            "flag_suspect": 1
        },
        headers={"Authorization": f"Bearer {auth_token}"}
    )
    assert response.status_code == 200
    data = response.json()
    assert "prediction" in data
    assert "verdict" in data
    assert "confiance" in data
    assert data["verdict"] in ["Normal", "Attaque"]

def test_predict_sante_success(auth_token):
    """Test prédiction santé avec authentification"""
    response = client.post(
        "/predict/sante",
        json={
            "fievre": 1,
            "toux": 1,
            "fatigue": 1,
            "maux_tete": 0,
            "douleur_gorge": 0,
            "nausees": 0,
            "douleur_thorax": 0,
            "essoufflement": 0,
            "diarrhee": 0,
            "frissons": 0,
            "perte_odorat": 1,
            "douleurs_musculaires": 1,
            "palpitations": 0,
            "vertiges": 0
        },
        headers={"Authorization": f"Bearer {auth_token}"}
    )
    assert response.status_code == 200
    data = response.json()
    assert "diagnostic" in data
    assert "urgent" in data
    assert "confiance" in data
    assert "conseils" in data
    assert isinstance(data["conseils"], list)

def test_chat_success(auth_token):
    """Test chatbot avec authentification"""
    response = client.post(
        "/chat",
        json={"message": "Qu'est-ce qu'une attaque DDoS ?"},
        headers={"Authorization": f"Bearer {auth_token}"}
    )
    assert response.status_code == 200
    data = response.json()
    assert "response" in data
    assert len(data["response"]) > 0

def test_historique_success(auth_token):
    """Test récupération historique"""
    # Faire une prédiction d'abord
    client.post(
        "/predict/cyber",
        json={
            "requetes_min": 100,
            "duree": 50,
            "octets": 5000,
            "ports_scanes": 2,
            "taux_erreur": 0.05,
            "flag_suspect": 0
        },
        headers={"Authorization": f"Bearer {auth_token}"}
    )
    
    # Récupérer l'historique
    response = client.get(
        "/historique",
        headers={"Authorization": f"Bearer {auth_token}"}
    )
    assert response.status_code == 200
    assert isinstance(response.json(), list)

# ============================================================
#  TESTS VALIDATION DONNÉES
# ============================================================

def test_predict_cyber_invalid_data(auth_token):
    """Test prédiction cyber avec données invalides"""
    response = client.post(
        "/predict/cyber",
        json={
            "requetes_min": "invalid",  # Devrait être un int
            "duree": 2,
            "octets": 100,
            "ports_scanes": 50,
            "taux_erreur": 0.8,
            "flag_suspect": 1
        },
        headers={"Authorization": f"Bearer {auth_token}"}
    )
    assert response.status_code == 422  # Validation error

def test_predict_sante_missing_field(auth_token):
    """Test prédiction santé avec champ manquant"""
    response = client.post(
        "/predict/sante",
        json={
            "fievre": 1,
            "toux": 1,
            # Champs manquants...
        },
        headers={"Authorization": f"Bearer {auth_token}"}
    )
    assert response.status_code == 422  # Validation error

# ============================================================
#  CLEANUP
# ============================================================

def teardown_module(module):
    """Nettoyer la base de données de test après tous les tests"""
    import os
    if os.path.exists("test_kotighi.db"):
        os.remove("test_kotighi.db")
