# ============================================================
#  KOTIGHI AI - Dockerfile pour l'API FastAPI
# ============================================================
FROM python:3.9-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de dépendances
COPY requirements.txt .

# Installer les dépendances
RUN pip install --no-cache-dir -r requirements.txt

# Copier tout le code source
COPY . .

# Entraîner les modèles si les fichiers .pkl n'existent pas
RUN python train_models.py || echo "Modèles déjà présents"

# Exposer le port 8000
EXPOSE 8000

# Variables d'environnement par défaut
ENV API_HOST=0.0.0.0
ENV API_PORT=8000
ENV DATABASE_URL=sqlite:///./kotighi.db

# Commande de démarrage
CMD ["uvicorn", "api_kotighi:app", "--host", "0.0.0.0", "--port", "8000"]
