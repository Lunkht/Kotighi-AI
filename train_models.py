# ============================================================
#  Script d'entraînement des modèles IA
#  Génère des fichiers .pkl pour une utilisation en production
# ============================================================
import pandas as pd
import numpy as np
import pickle
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler

def train_and_save():
    print("🚀 Démarrage de l'entraînement des modèles...")

    # --- 1. MODULE CYBER (Simulation NSL-KDD) ---
    print("🔒 Entraînement du modèle Cybersécurité...")
    np.random.seed(42)
    N = 2000
    
    # Données simulées (mais plus réalistes)
    # 0 = Normal, 1 = Attaque
    X_cyber = pd.DataFrame({
        "requetes_min": np.concatenate([np.random.randint(10, 200, 1000), np.random.randint(800, 5000, 1000)]),
        "duree":        np.concatenate([np.random.randint(1, 60, 1000),   np.random.randint(1, 5, 1000)]),
        "octets":       np.concatenate([np.random.randint(100, 5000, 1000), np.random.randint(50, 200, 1000)]),
        "ports_scanes": np.concatenate([np.random.randint(1, 5, 1000),    np.random.randint(10, 100, 1000)]),
        "taux_erreur":  np.concatenate([np.random.uniform(0, 0.1, 1000),  np.random.uniform(0.5, 1.0, 1000)]),
        "flag_suspect": np.concatenate([np.zeros(1000),                   np.ones(1000)])
    })
    y_cyber = np.concatenate([np.zeros(1000), np.ones(1000)]) # 0=Normal, 1=Attaque

    # Normalisation
    scaler_cyber = StandardScaler()
    X_cyber_scaled = scaler_cyber.fit_transform(X_cyber)

    # Entraînement
    model_cyber = RandomForestClassifier(n_estimators=100, random_state=42)
    model_cyber.fit(X_cyber_scaled, y_cyber)

    # Sauvegarde
    with open("model_cyber.pkl", "wb") as f:
        pickle.dump(model_cyber, f)
    with open("scaler_cyber.pkl", "wb") as f:
        pickle.dump(scaler_cyber, f)
    print("✅ Modèle Cyber sauvegardé (model_cyber.pkl, scaler_cyber.pkl)")


    # --- 2. MODULE SANTÉ (Simulation Symptômes) ---
    print("🏥 Entraînement du modèle Santé...")
    # Labels : 0=COVID, 1=Grippe, 2=Cardiaque, 3=Gastro, 4=Migraine, 5=Angine, 6=Stress, 7=Rien
    labels_sante = ["COVID-19", "Grippe", "Problème cardiaque", "Gastro-entérite", "Migraine", "Angine", "Asthme/Stress", "Symptômes non spécifiques"]
    
    # On crée un dataset manuel pour garantir la logique médicale de base
    data = []
    
    # Helper pour générer n cas d'une maladie avec du bruit
    def generate_cases(n, maladie_idx, base_symptoms):
        # base_symptoms = [fievre, toux, ..., vertiges] (14 items)
        for _ in range(n):
            symptoms = list(base_symptoms)
            # Ajouter un peu de bruit (changer 1 symptôme au hasard)
            if np.random.rand() < 0.2:
                idx = np.random.randint(0, 14)
                symptoms[idx] = 1 - symptoms[idx]
            data.append(symptoms + [maladie_idx])

    # Définition des profils types (14 symptômes)
    # fievre, toux, fatigue, tete, gorge, nausees, thorax, essoufl, diarrhee, frissons, odorat, musc, palpit, vertiges
    
    # 0: COVID (Fievre, Toux, Fatigue, Odorat)
    generate_cases(200, 0, [1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0])
    
    # 1: Grippe (Fievre, Toux, Fatigue, Frissons, Musc)
    generate_cases(200, 1, [1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0])
    
    # 2: Cardiaque (Thorax, Essoufflement, Palpitations)
    generate_cases(200, 2, [0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0])
    
    # 3: Gastro (Nausees, Diarrhee, Fatigue)
    generate_cases(200, 3, [0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0])
    
    # 4: Migraine (Tete, Nausees)
    generate_cases(200, 4, [0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1])
    
    # 5: Angine (Fievre, Gorge)
    generate_cases(200, 5, [1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0])

    # 6: Stress (Palpitations, Essoufflement, Vertiges)
    generate_cases(200, 6, [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1])

    # 7: Rien (Peu de symptômes)
    generate_cases(200, 7, [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])

    df_sante = pd.DataFrame(data, columns=[
        "fievre","toux","fatigue","maux_tete","douleur_gorge","nausees","douleur_thorax",
        "essoufflement","diarrhee","frissons","perte_odorat","douleurs_musculaires","palpitations","vertiges",
        "label"
    ])
    
    X_sante = df_sante.drop("label", axis=1)
    y_sante = df_sante["label"]

    model_sante = RandomForestClassifier(n_estimators=100, random_state=42)
    model_sante.fit(X_sante, y_sante)

    with open("model_sante.pkl", "wb") as f:
        pickle.dump(model_sante, f)
    
    # On sauvegarde aussi la liste des labels pour que l'API puisse les lire
    with open("labels_sante.pkl", "wb") as f:
        pickle.dump(labels_sante, f)

    print("✅ Modèle Santé sauvegardé (model_sante.pkl, labels_sante.pkl)")
    print("\n🎉 Terminé ! Les fichiers .pkl sont prêts.")

if __name__ == "__main__":
    train_and_save()
