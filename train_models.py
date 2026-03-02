# ============================================================
#  Script d'entraînement des modèles IA (SYNCHRONISÉ AVEC STREAMLIT)
#  Génère des fichiers .pkl pour une utilisation en production
# ============================================================
import pandas as pd
import numpy as np
import pickle
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.preprocessing import StandardScaler

def train_and_save():
    print("🚀 Démarrage de l'entraînement des modèles (Sync Streamlit)...")

    # --- 1. MODULE CYBER (Logique app_kotighi.py) ---
    print("🔒 Entraînement du modèle Cybersécurité...")
    np.random.seed(42)
    N = 4000
    
    # Génération données normales
    n = pd.DataFrame({
        "requetes_min": np.random.randint(5, 300, N//2),
        "duree":        np.random.randint(10, 120, N//2),
        "octets":       np.random.randint(500, 10000, N//2),
        "ports_scanes": np.random.randint(1, 4, N//2),
        "taux_erreur":  np.random.uniform(0, 0.1, N//2),
        "flag_suspect": np.zeros(N//2)
    })
    
    # Génération données attaques
    a = pd.DataFrame({
        "requetes_min": np.random.randint(500, 8000, N//2),
        "duree":        np.random.randint(0, 5, N//2),
        "octets":       np.random.randint(10, 300, N//2),
        "ports_scanes": np.random.randint(20, 200, N//2),
        "taux_erreur":  np.random.uniform(0.5, 1.0, N//2),
        "flag_suspect": np.ones(N//2)
    })

    df_cyber = pd.concat([n.assign(label=0), a.assign(label=1)]).sample(frac=1, random_state=42)
    X_cyber = df_cyber.drop("label", axis=1)
    y_cyber = df_cyber["label"]

    # Normalisation
    scaler_cyber = StandardScaler()
    X_cyber_scaled = scaler_cyber.fit_transform(X_cyber)

    # Entraînement (Random Forest comme principal)
    model_cyber = RandomForestClassifier(n_estimators=150, random_state=42, n_jobs=-1)
    model_cyber.fit(X_cyber_scaled, y_cyber)

    # Sauvegarde
    with open("model_cyber.pkl", "wb") as f:
        pickle.dump(model_cyber, f)
    with open("scaler_cyber.pkl", "wb") as f:
        pickle.dump(scaler_cyber, f)
    print("✅ Modèle Cyber sauvegardé")


    # --- 2. MODULE SANTÉ (Logique app_kotighi.py) ---
    print("🏥 Entraînement du modèle Santé...")
    np.random.seed(99)
    N = 4000
    
    # Extension des symptômes pour plus de précision (nouveaux symptômes ajoutés dans app_kotighi.py)
    cols_sante = ["fievre","toux","fatigue","maux_tete","douleur_gorge","nausees","douleur_thorax","essoufflement","diarrhee","frissons","perte_odorat","douleurs_musculaires","palpitations","vertiges"]
    
    # Génération aléatoire de base
    d = pd.DataFrame({c: np.random.randint(0, 2, N) for c in cols_sante})
    
    # Fonction de diagnostic exacte de app_kotighi.py
    def diag(r):
        # Règles plus complexes pour l'amélioration de la santé
        if r["fievre"] and r["toux"] and r["perte_odorat"]: return 0 # COVID-19
        if r["fievre"] and r["toux"] and r["fatigue"] and r["douleurs_musculaires"]: return 1 # Grippe
        if r["douleur_thorax"] and r["essoufflement"] and r["palpitations"]: return 2 # Problème cardiaque
        if r["nausees"] and r["diarrhee"] and r["fatigue"]: return 3 # Gastro-entérite
        if r["maux_tete"] and r["fatigue"] and r["vertiges"]: return 4 # Migraine / Fatigue intense
        if r["douleur_gorge"] and r["fievre"] and r["frissons"]: return 5 # Angine
        if r["essoufflement"] and r["douleur_thorax"] and not r["fievre"]: return 6 # Asthme / Stress
        return 7 # Symptômes non spécifiques

    d["label"] = d.apply(diag, axis=1)
    
    X_sante = d.drop("label", axis=1)
    y_sante = d["label"]

    model_sante = RandomForestClassifier(n_estimators=200, random_state=42)
    model_sante.fit(X_sante, y_sante)

    # Labels et Conseils (identiques à app_kotighi.py)
    labels_sante = ["COVID-19","Grippe","Problème cardiaque","Gastro-entérite","Migraine","Angine","Asthme/Stress","Symptômes non spécifiques"]
    
    conseils_sante = {
        "COVID-19": ["Isolement immédiat", "Test PCR/Antigénique", "Port du masque", "Surveillance saturation oxygène"],
        "Grippe": ["Repos complet", "Hydratation régulière", "Paracétamol si fièvre", "Éviter contact personnes fragiles"],
        "Problème cardiaque": ["Appeler le 15 (SAMU) immédiatement", "Ne pas conduire", "Rester assis/allongé", "Prendre aspirine si non-allergique (sur avis médical)"],
        "Gastro-entérite": ["Boire solutions réhydratation", "Régime riz/carottes", "Lavage mains fréquent", "Éviter laitages"],
        "Migraine": ["Repos dans le noir/silence", "Caféine peut aider", "Compresse froide sur front", "Éviter écrans"],
        "Angine": ["Consulter pour test TDR", "Boissons chaudes miel/citron", "Gargarisme eau salée", "Surveiller fièvre"],
        "Asthme/Stress": ["Exercices respiration (cohérence cardiaque)", "S'asseoir droit", "Utiliser inhalateur si prescrit", "Éloigner allergènes"],
        "Symptômes non spécifiques": ["Surveiller évolution 24h", "Prendre température matin/soir", "Consulter si aggravation"]
    }

    with open("model_sante.pkl", "wb") as f:
        pickle.dump(model_sante, f)
    
    with open("labels_sante.pkl", "wb") as f:
        pickle.dump(labels_sante, f)
        
    with open("conseils_sante.pkl", "wb") as f:
        pickle.dump(conseils_sante, f)

    print("✅ Modèle Santé sauvegardé (avec conseils)")
    print("\n🎉 Synchronisation terminée ! Les modèles sont identiques au projet Streamlit.")

if __name__ == "__main__":
    train_and_save()
