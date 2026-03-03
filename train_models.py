"""
KOTIGHI AI - Entraînement des Modèles ML
Génère les fichiers .pkl pour la cybersécurité et la santé
"""
import numpy as np
import pandas as pd
import pickle
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, accuracy_score

print("🚀 Démarrage de l'entraînement des modèles KOTIGHI AI...")

# ============================================================
#  MODÈLE CYBERSÉCURITÉ (NSL-KDD inspiré)
# ============================================================
print("\n📡 Entraînement du modèle Cybersécurité...")

np.random.seed(42)
N = 4000

# Génération de données synthétiques (trafic normal)
normal_data = pd.DataFrame({
    "requetes_min": np.random.randint(5, 300, N//2),
    "duree": np.random.randint(10, 120, N//2),
    "octets": np.random.randint(500, 10000, N//2),
    "ports_scanes": np.random.randint(1, 4, N//2),
    "taux_erreur": np.random.uniform(0, 0.1, N//2),
    "flag_suspect": np.zeros(N//2)
})

# Génération de données synthétiques (attaques)
attack_data = pd.DataFrame({
    "requetes_min": np.random.randint(500, 8000, N//2),
    "duree": np.random.randint(0, 5, N//2),
    "octets": np.random.randint(10, 300, N//2),
    "ports_scanes": np.random.randint(20, 200, N//2),
    "taux_erreur": np.random.uniform(0.5, 1.0, N//2),
    "flag_suspect": np.ones(N//2)
})

# Fusion et mélange
df_cyber = pd.concat([
    normal_data.assign(label=0),
    attack_data.assign(label=1)
]).sample(frac=1, random_state=42).reset_index(drop=True)

X_cyber = df_cyber.drop("label", axis=1)
y_cyber = df_cyber["label"]

# Split train/test
X_train, X_test, y_train, y_test = train_test_split(
    X_cyber, y_cyber, test_size=0.2, random_state=42
)

# Normalisation
scaler_cyber = StandardScaler()
X_train_scaled = scaler_cyber.fit_transform(X_train)
X_test_scaled = scaler_cyber.transform(X_test)

# Entraînement
model_cyber = RandomForestClassifier(
    n_estimators=150,
    max_depth=15,
    random_state=42,
    n_jobs=-1
)
model_cyber.fit(X_train_scaled, y_train)

# Évaluation
y_pred = model_cyber.predict(X_test_scaled)
accuracy = accuracy_score(y_test, y_pred)
print(f"✅ Précision Cybersécurité : {accuracy*100:.2f}%")
print("\nRapport de classification :")
print(classification_report(y_test, y_pred, target_names=["Normal", "Attaque"]))

# Sauvegarde
with open("model_cyber.pkl", "wb") as f:
    pickle.dump(model_cyber, f)
with open("scaler_cyber.pkl", "wb") as f:
    pickle.dump(scaler_cyber, f)

print("💾 Modèle cybersécurité sauvegardé : model_cyber.pkl, scaler_cyber.pkl")

# ============================================================
#  MODÈLE SANTÉ (Diagnostic Médical)
# ============================================================
print("\n🏥 Entraînement du modèle Santé...")

np.random.seed(99)
N_sante = 4000

# Symptômes
symptomes = [
    "fievre", "toux", "fatigue", "maux_tete", "douleur_gorge",
    "nausees", "douleur_thorax", "essoufflement", "diarrhee",
    "frissons", "perte_odorat", "douleurs_musculaires",
    "palpitations", "vertiges"
]

# Génération de données synthétiques
df_sante = pd.DataFrame({
    symptome: np.random.randint(0, 2, N_sante) for symptome in symptomes
})

# Fonction de diagnostic basée sur des règles médicales
def diagnostic_rule(row):
    if row["fievre"] and row["toux"] and row["perte_odorat"]:
        return 0  # COVID-19
    if row["fievre"] and row["toux"] and row["fatigue"] and row["douleurs_musculaires"]:
        return 1  # Grippe
    if row["douleur_thorax"] and row["essoufflement"] and row["palpitations"]:
        return 2  # Problème cardiaque
    if row["nausees"] and row["diarrhee"] and row["fatigue"]:
        return 3  # Gastro-entérite
    if row["maux_tete"] and row["fatigue"] and row["vertiges"]:
        return 4  # Migraine
    if row["douleur_gorge"] and row["fievre"] and row["frissons"]:
        return 5  # Angine
    if row["essoufflement"] and row["douleur_thorax"] and not row["fievre"]:
        return 6  # Asthme/Stress
    return 7  # Symptômes non spécifiques

df_sante["label"] = df_sante.apply(diagnostic_rule, axis=1)

X_sante = df_sante.drop("label", axis=1)
y_sante = df_sante["label"]

# Split train/test
X_train_s, X_test_s, y_train_s, y_test_s = train_test_split(
    X_sante, y_sante, test_size=0.2, random_state=42
)

# Entraînement
model_sante = RandomForestClassifier(
    n_estimators=200,
    max_depth=20,
    random_state=42,
    n_jobs=-1
)
model_sante.fit(X_train_s, y_train_s)

# Évaluation
y_pred_s = model_sante.predict(X_test_s)
accuracy_s = accuracy_score(y_test_s, y_pred_s)
print(f"✅ Précision Santé : {accuracy_s*100:.2f}%")

# Labels et conseils
labels_sante = [
    "COVID-19",
    "Grippe",
    "Problème cardiaque",
    "Gastro-entérite",
    "Migraine",
    "Angine",
    "Asthme/Stress",
    "Symptômes non spécifiques"
]

conseils_sante = {
    "COVID-19": [
        "Isolement immédiat pendant 7-10 jours",
        "Test PCR ou antigénique recommandé",
        "Port du masque obligatoire",
        "Surveillance de la saturation en oxygène (oxymètre)",
        "Consulter si essoufflement ou fièvre persistante"
    ],
    "Grippe": [
        "Repos complet au lit",
        "Hydratation régulière (eau, tisanes)",
        "Paracétamol si fièvre >38.5°C",
        "Éviter contact avec personnes fragiles",
        "Consulter si symptômes >5 jours"
    ],
    "Problème cardiaque": [
        "⚠️ URGENCE : Appeler le 15 (SAMU) immédiatement",
        "Ne pas conduire soi-même",
        "Rester assis ou allongé",
        "Prendre aspirine si non-allergique (sur avis médical)",
        "Noter l'heure de début des symptômes"
    ],
    "Gastro-entérite": [
        "Boire solutions de réhydratation orale (SRO)",
        "Régime riz, carottes, bananes",
        "Lavage des mains fréquent",
        "Éviter produits laitiers temporairement",
        "Consulter si sang dans les selles ou déshydratation"
    ],
    "Migraine": [
        "Repos dans une pièce sombre et silencieuse",
        "Caféine peut aider (café, thé)",
        "Compresse froide sur le front",
        "Éviter écrans et lumières vives",
        "Consulter si migraines fréquentes (>2/semaine)"
    ],
    "Angine": [
        "Consulter pour test TDR (détection streptocoque)",
        "Boissons chaudes avec miel et citron",
        "Gargarisme eau salée tiède",
        "Surveiller la fièvre",
        "Antibiotiques si angine bactérienne confirmée"
    ],
    "Asthme/Stress": [
        "Exercices de respiration (cohérence cardiaque 365)",
        "S'asseoir bien droit",
        "Utiliser inhalateur si prescrit",
        "Éloigner allergènes potentiels",
        "Consulter si crises fréquentes"
    ],
    "Symptômes non spécifiques": [
        "Surveiller l'évolution sur 24-48h",
        "Prendre température matin et soir",
        "Tenir un journal des symptômes",
        "Consulter si aggravation ou nouveaux symptômes",
        "Repos et hydratation"
    ]
}

# Sauvegarde
with open("model_sante.pkl", "wb") as f:
    pickle.dump(model_sante, f)
with open("labels_sante.pkl", "wb") as f:
    pickle.dump(labels_sante, f)
with open("conseils_sante.pkl", "wb") as f:
    pickle.dump(conseils_sante, f)

print("💾 Modèle santé sauvegardé : model_sante.pkl, labels_sante.pkl, conseils_sante.pkl")

# ============================================================
#  RÉSUMÉ
# ============================================================
print("\n" + "="*60)
print("✨ ENTRAÎNEMENT TERMINÉ AVEC SUCCÈS")
print("="*60)
print(f"📊 Modèle Cybersécurité : {accuracy*100:.2f}% de précision")
print(f"📊 Modèle Santé : {accuracy_s*100:.2f}% de précision")
print("\n📦 Fichiers générés :")
print("   - model_cyber.pkl")
print("   - scaler_cyber.pkl")
print("   - model_sante.pkl")
print("   - labels_sante.pkl")
print("   - conseils_sante.pkl")
print("\n🚀 Vous pouvez maintenant lancer l'API avec :")
print("   uvicorn api_kotighi:app --reload")
print("="*60)
