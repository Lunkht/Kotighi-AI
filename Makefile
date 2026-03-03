# ============================================================
#  KOTIGHI AI - Makefile
#  Commandes utiles pour le développement et le déploiement
# ============================================================

.PHONY: help install test clean build deploy docker

# Couleurs pour l'affichage
BLUE := \033[0;34m
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
NC := \033[0m # No Color

help: ## Afficher l'aide
	@echo "$(BLUE)KOTIGHI AI - Commandes disponibles$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-20s$(NC) %s\n", $$1, $$2}'

# ============================================================
#  BACKEND (Python)
# ============================================================

install-backend: ## Installer les dépendances backend
	@echo "$(BLUE)Installation des dépendances backend...$(NC)"
	cd Kotighi-AI && python -m venv venv
	cd Kotighi-AI && . venv/bin/activate && pip install -r requirements.txt
	@echo "$(GREEN)✓ Dépendances installées$(NC)"

train-models: ## Entraîner les modèles ML
	@echo "$(BLUE)Entraînement des modèles...$(NC)"
	cd Kotighi-AI && . venv/bin/activate && python train_models.py
	@echo "$(GREEN)✓ Modèles entraînés$(NC)"

test-backend: ## Lancer les tests backend
	@echo "$(BLUE)Lancement des tests backend...$(NC)"
	cd Kotighi-AI && . venv/bin/activate && pytest tests/ -v --cov=.
	@echo "$(GREEN)✓ Tests terminés$(NC)"

run-api: ## Lancer l'API en mode développement
	@echo "$(BLUE)Démarrage de l'API...$(NC)"
	cd Kotighi-AI && . venv/bin/activate && uvicorn api_kotighi:app --reload --host 0.0.0.0 --port 8000

run-api-supabase: ## Lancer l'API avec Supabase
	@echo "$(BLUE)Démarrage de l'API Supabase...$(NC)"
	cd Kotighi-AI && . venv/bin/activate && uvicorn api_kotighi_supabase:app --reload --host 0.0.0.0 --port 8000

run-streamlit: ## Lancer l'interface Streamlit
	@echo "$(BLUE)Démarrage de Streamlit...$(NC)"
	cd Kotighi-AI && . venv/bin/activate && streamlit run app_kotighi.py

# ============================================================
#  ANDROID
# ============================================================

install-android: ## Installer les dépendances Android
	@echo "$(BLUE)Synchronisation Gradle...$(NC)"
	./gradlew clean build
	@echo "$(GREEN)✓ Dépendances installées$(NC)"

test-android: ## Lancer les tests Android
	@echo "$(BLUE)Lancement des tests Android...$(NC)"
	./gradlew test
	@echo "$(GREEN)✓ Tests terminés$(NC)"

lint-android: ## Lancer l'analyse lint Android
	@echo "$(BLUE)Analyse lint...$(NC)"
	./gradlew lint
	@echo "$(GREEN)✓ Lint terminé$(NC)"

build-debug: ## Builder l'APK debug
	@echo "$(BLUE)Build APK debug...$(NC)"
	./gradlew assembleDebug
	@echo "$(GREEN)✓ APK debug créé : app/build/outputs/apk/debug/app-debug.apk$(NC)"

build-release: ## Builder l'APK release
	@echo "$(BLUE)Build APK release...$(NC)"
	./gradlew assembleRelease
	@echo "$(GREEN)✓ APK release créé : app/build/outputs/apk/release/app-release.apk$(NC)"

install-app: ## Installer l'app sur un appareil connecté
	@echo "$(BLUE)Installation de l'app...$(NC)"
	./gradlew installDebug
	@echo "$(GREEN)✓ App installée$(NC)"

# ============================================================
#  DOCKER
# ============================================================

docker-build: ## Builder les images Docker
	@echo "$(BLUE)Build des images Docker...$(NC)"
	docker-compose build
	@echo "$(GREEN)✓ Images créées$(NC)"

docker-up: ## Démarrer les conteneurs Docker
	@echo "$(BLUE)Démarrage des conteneurs...$(NC)"
	docker-compose up -d
	@echo "$(GREEN)✓ Conteneurs démarrés$(NC)"
	@echo "$(YELLOW)API : http://localhost:8000$(NC)"
	@echo "$(YELLOW)Streamlit : http://localhost:8501$(NC)"

docker-down: ## Arrêter les conteneurs Docker
	@echo "$(BLUE)Arrêt des conteneurs...$(NC)"
	docker-compose down
	@echo "$(GREEN)✓ Conteneurs arrêtés$(NC)"

docker-logs: ## Afficher les logs Docker
	docker-compose logs -f

docker-clean: ## Nettoyer les conteneurs et volumes Docker
	@echo "$(BLUE)Nettoyage Docker...$(NC)"
	docker-compose down -v
	docker system prune -f
	@echo "$(GREEN)✓ Nettoyage terminé$(NC)"

# ============================================================
#  TESTS & QUALITÉ
# ============================================================

test: test-backend test-android ## Lancer tous les tests

coverage: ## Générer le rapport de couverture
	@echo "$(BLUE)Génération du rapport de couverture...$(NC)"
	cd Kotighi-AI && . venv/bin/activate && pytest tests/ --cov=. --cov-report=html
	@echo "$(GREEN)✓ Rapport généré : Kotighi-AI/htmlcov/index.html$(NC)"

lint: lint-android ## Lancer toutes les analyses lint

# ============================================================
#  NETTOYAGE
# ============================================================

clean: ## Nettoyer les fichiers temporaires
	@echo "$(BLUE)Nettoyage...$(NC)"
	find . -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
	find . -type f -name "*.pyc" -delete 2>/dev/null || true
	find . -type f -name "*.pyo" -delete 2>/dev/null || true
	find . -type d -name "*.egg-info" -exec rm -rf {} + 2>/dev/null || true
	find . -type d -name ".pytest_cache" -exec rm -rf {} + 2>/dev/null || true
	rm -rf Kotighi-AI/htmlcov Kotighi-AI/.coverage 2>/dev/null || true
	./gradlew clean 2>/dev/null || true
	@echo "$(GREEN)✓ Nettoyage terminé$(NC)"

clean-all: clean docker-clean ## Nettoyage complet (inclut Docker)

# ============================================================
#  DÉPLOIEMENT
# ============================================================

deploy-render: ## Déployer sur Render
	@echo "$(BLUE)Déploiement sur Render...$(NC)"
	git push origin main
	@echo "$(GREEN)✓ Push effectué - Render va déployer automatiquement$(NC)"

deploy-heroku: ## Déployer sur Heroku
	@echo "$(BLUE)Déploiement sur Heroku...$(NC)"
	cd Kotighi-AI && git push heroku main
	@echo "$(GREEN)✓ Déployé sur Heroku$(NC)"

# ============================================================
#  DÉVELOPPEMENT
# ============================================================

setup: install-backend train-models install-android ## Configuration initiale complète
	@echo "$(GREEN)✓ Configuration terminée !$(NC)"
	@echo ""
	@echo "$(YELLOW)Prochaines étapes :$(NC)"
	@echo "  1. Copier Kotighi-AI/.env.example vers Kotighi-AI/.env"
	@echo "  2. Configurer les variables d'environnement"
	@echo "  3. Lancer l'API : make run-api"
	@echo "  4. Ou avec Supabase : make run-api-supabase"
	@echo "  5. Lancer l'app Android depuis Android Studio"

setup-supabase: ## Configuration Supabase interactive
	@echo "$(BLUE)Configuration Supabase...$(NC)"
	./scripts/switch-to-supabase.sh

dev: ## Démarrer l'environnement de développement complet
	@echo "$(BLUE)Démarrage de l'environnement de développement...$(NC)"
	@echo "$(YELLOW)Lancement de l'API en arrière-plan...$(NC)"
	cd Kotighi-AI && . venv/bin/activate && uvicorn api_kotighi:app --reload --host 0.0.0.0 --port 8000 &
	@echo "$(GREEN)✓ API démarrée sur http://localhost:8000$(NC)"
	@echo "$(YELLOW)Ouvrez Android Studio pour lancer l'app mobile$(NC)"

check: ## Vérifier l'état du projet
	@echo "$(BLUE)Vérification du projet...$(NC)"
	@echo ""
	@echo "$(YELLOW)Backend :$(NC)"
	@test -d Kotighi-AI/venv && echo "  $(GREEN)✓ Environnement virtuel présent$(NC)" || echo "  $(RED)✗ Environnement virtuel manquant$(NC)"
	@test -f Kotighi-AI/model_cyber.pkl && echo "  $(GREEN)✓ Modèles ML présents$(NC)" || echo "  $(RED)✗ Modèles ML manquants$(NC)"
	@test -f Kotighi-AI/.env && echo "  $(GREEN)✓ Fichier .env présent$(NC)" || echo "  $(YELLOW)⚠ Fichier .env manquant$(NC)"
	@echo ""
	@echo "$(YELLOW)Android :$(NC)"
	@test -f gradlew && echo "  $(GREEN)✓ Gradle wrapper présent$(NC)" || echo "  $(RED)✗ Gradle wrapper manquant$(NC)"
	@test -f app/build.gradle && echo "  $(GREEN)✓ Configuration Gradle présente$(NC)" || echo "  $(RED)✗ Configuration Gradle manquante$(NC)"

# ============================================================
#  DOCUMENTATION
# ============================================================

docs: ## Générer la documentation API
	@echo "$(BLUE)Documentation API disponible sur :$(NC)"
	@echo "  $(GREEN)http://localhost:8000/docs$(NC) (Swagger)"
	@echo "  $(GREEN)http://localhost:8000/redoc$(NC) (ReDoc)"

# ============================================================
#  DÉFAUT
# ============================================================

.DEFAULT_GOAL := help
