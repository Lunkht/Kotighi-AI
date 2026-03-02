# Utiliser une image Python légère
FROM python:3.9-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers requis
COPY requirements.txt .
COPY api_kotighi.py .
COPY cyber_nslkdd.py .
COPY kotighi_ai.py .
# Copier les modèles s'ils existent (sinon ils seront simulés/ré-entraînés au démarrage)
COPY *.pkl .
COPY train_models.py .

# Installer les dépendances
RUN pip install --no-cache-dir -r requirements.txt

# Exposer le port
EXPOSE 8000

# Lancer l'API avec Uvicorn
CMD ["uvicorn", "api_kotighi:app", "--host", "0.0.0.0", "--port", "8000"]
