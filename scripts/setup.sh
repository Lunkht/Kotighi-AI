#!/bin/bash

# ============================================================
#  KOTIGHI AI - Setup Script
#  Configuration automatique du projet
# ============================================================

set -e  # Arrêter en cas d'erreur

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}"
cat << "EOF"
╔════════════════════════════════════════════════════════╗
║                                                        ║
║              🛡️  KOTIGHI AI SETUP  🛡️                 ║
║                                                        ║
║     Cybersécurité & Santé avec Intelligence           ║
║              Artificielle                             ║
║                                                        ║
╚════════════════════════════════════════════════════════╝
EOF
echo -e "${NC}"

# ============================================================
#  VÉRIFICATIONS PRÉALABLES
# ============================================================

echo -e "${BLUE}[1/6] Vérification des prérequis...${NC}"

# Python
if ! command -v python3 &> /dev/null; then
    echo -e "${RED}✗ Python 3 n'est pas installé${NC}"
    echo -e "${YELLOW}Installez Python 3.9+ depuis https://www.python.org/${NC}"
    exit 1
fi
PYTHON_VERSION=$(python3 --version | cut -d' ' -f2)
echo -e "${GREEN}✓ Python $PYTHON_VERSION détecté${NC}"

# Java
if ! command -v java &> /dev/null; then
    echo -e "${YELLOW}⚠ Java n'est pas installé (nécessaire pour Android)${NC}"
    echo -e "${YELLOW}Installez JDK 11+ depuis https://adoptium.net/${NC}"
else
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo -e "${GREEN}✓ Java $JAVA_VERSION détecté${NC}"
fi

# Git
if ! command -v git &> /dev/null; then
    echo -e "${RED}✗ Git n'est pas installé${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Git détecté${NC}"

echo ""

# ============================================================
#  BACKEND SETUP
# ============================================================

echo -e "${BLUE}[2/6] Configuration du Backend Python...${NC}"

cd Kotighi-AI

# Créer environnement virtuel
if [ ! -d "venv" ]; then
    echo -e "${CYAN}Création de l'environnement virtuel...${NC}"
    python3 -m venv venv
    echo -e "${GREEN}✓ Environnement virtuel créé${NC}"
else
    echo -e "${GREEN}✓ Environnement virtuel déjà présent${NC}"
fi

# Activer et installer dépendances
echo -e "${CYAN}Installation des dépendances Python...${NC}"
source venv/bin/activate
pip install --upgrade pip > /dev/null 2>&1
pip install -r requirements.txt
echo -e "${GREEN}✓ Dépendances installées${NC}"

# Créer .env si nécessaire
if [ ! -f ".env" ]; then
    echo -e "${CYAN}Création du fichier .env...${NC}"
    cp .env.example .env
    
    # Générer une clé secrète
    if command -v openssl &> /dev/null; then
        SECRET_KEY=$(openssl rand -hex 32)
        sed -i.bak "s/changez_cette_cle_secrete_en_production_utilisez_openssl_rand_hex_32/$SECRET_KEY/" .env
        rm .env.bak 2>/dev/null || true
        echo -e "${GREEN}✓ Clé secrète générée automatiquement${NC}"
    else
        echo -e "${YELLOW}⚠ OpenSSL non disponible, veuillez générer une clé secrète manuellement${NC}"
    fi
    
    echo -e "${GREEN}✓ Fichier .env créé${NC}"
else
    echo -e "${GREEN}✓ Fichier .env déjà présent${NC}"
fi

# Entraîner les modèles
if [ ! -f "model_cyber.pkl" ] || [ ! -f "model_sante.pkl" ]; then
    echo -e "${CYAN}Entraînement des modèles ML (cela peut prendre 1-2 minutes)...${NC}"
    python train_models.py
    echo -e "${GREEN}✓ Modèles ML entraînés${NC}"
else
    echo -e "${GREEN}✓ Modèles ML déjà présents${NC}"
fi

cd ..
echo ""

# ============================================================
#  ANDROID SETUP
# ============================================================

echo -e "${BLUE}[3/6] Configuration Android...${NC}"

# Rendre gradlew exécutable
if [ -f "gradlew" ]; then
    chmod +x gradlew
    echo -e "${GREEN}✓ Gradle wrapper configuré${NC}"
else
    echo -e "${RED}✗ gradlew manquant${NC}"
    exit 1
fi

# Créer local.properties si SDK_HOME est défini
if [ ! -f "local.properties" ] && [ -n "$ANDROID_HOME" ]; then
    echo -e "${CYAN}Création de local.properties...${NC}"
    echo "sdk.dir=$ANDROID_HOME" > local.properties
    echo -e "${GREEN}✓ local.properties créé${NC}"
elif [ -f "local.properties" ]; then
    echo -e "${GREEN}✓ local.properties déjà présent${NC}"
else
    echo -e "${YELLOW}⚠ local.properties sera créé par Android Studio${NC}"
fi

echo ""

# ============================================================
#  TESTS
# ============================================================

echo -e "${BLUE}[4/6] Exécution des tests...${NC}"

# Tests backend
echo -e "${CYAN}Tests backend...${NC}"
cd Kotighi-AI
source venv/bin/activate
if pytest tests/ -v --tb=short > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Tests backend réussis${NC}"
else
    echo -e "${YELLOW}⚠ Certains tests backend ont échoué (non bloquant)${NC}"
fi
cd ..

# Tests Android (si Java est disponible)
if command -v java &> /dev/null; then
    echo -e "${CYAN}Tests Android...${NC}"
    if ./gradlew test > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Tests Android réussis${NC}"
    else
        echo -e "${YELLOW}⚠ Certains tests Android ont échoué (non bloquant)${NC}"
    fi
fi

echo ""

# ============================================================
#  SÉCURITÉ
# ============================================================

echo -e "${BLUE}[5/6] Vérifications de sécurité...${NC}"

# Vérifier que .env n'est pas commité
if git ls-files --error-unmatch Kotighi-AI/.env &> /dev/null; then
    echo -e "${RED}✗ ATTENTION : .env est commité dans Git !${NC}"
    echo -e "${RED}Exécutez : git rm --cached Kotighi-AI/.env${NC}"
else
    echo -e "${GREEN}✓ .env n'est pas commité${NC}"
fi

# Vérifier .gitignore
if grep -q ".env" .gitignore; then
    echo -e "${GREEN}✓ .gitignore configuré correctement${NC}"
else
    echo -e "${YELLOW}⚠ .env devrait être dans .gitignore${NC}"
fi

echo ""

# ============================================================
#  RÉSUMÉ
# ============================================================

echo -e "${BLUE}[6/6] Configuration terminée !${NC}"
echo ""

echo -e "${GREEN}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║              ✓ SETUP RÉUSSI !                          ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${CYAN}📋 Prochaines étapes :${NC}"
echo ""
echo -e "${YELLOW}1. Backend (API)${NC}"
echo -e "   ${CYAN}cd Kotighi-AI${NC}"
echo -e "   ${CYAN}source venv/bin/activate${NC}"
echo -e "   ${CYAN}uvicorn api_kotighi:app --reload${NC}"
echo -e "   ${GREEN}→ API disponible sur http://localhost:8000${NC}"
echo -e "   ${GREEN}→ Documentation sur http://localhost:8000/docs${NC}"
echo ""

echo -e "${YELLOW}2. Application Android${NC}"
echo -e "   ${CYAN}Ouvrir le projet dans Android Studio${NC}"
echo -e "   ${CYAN}Synchroniser Gradle${NC}"
echo -e "   ${CYAN}Lancer sur un émulateur ou appareil${NC}"
echo ""

echo -e "${YELLOW}3. Configuration (optionnel)${NC}"
echo -e "   ${CYAN}Éditer Kotighi-AI/.env pour personnaliser${NC}"
echo -e "   ${CYAN}Modifier app/build.gradle pour l'URL de l'API${NC}"
echo ""

echo -e "${CYAN}📚 Documentation :${NC}"
echo -e "   ${GREEN}README.md${NC}         - Guide principal"
echo -e "   ${GREEN}DEPLOYMENT.md${NC}     - Guide de déploiement"
echo -e "   ${GREEN}CONTRIBUTING.md${NC}   - Guide de contribution"
echo -e "   ${GREEN}SECURITY.md${NC}       - Politique de sécurité"
echo ""

echo -e "${CYAN}🛠️  Commandes utiles :${NC}"
echo -e "   ${GREEN}make help${NC}         - Afficher toutes les commandes"
echo -e "   ${GREEN}make test${NC}         - Lancer tous les tests"
echo -e "   ${GREEN}make check${NC}        - Vérifier l'état du projet"
echo -e "   ${GREEN}make docker-up${NC}    - Démarrer avec Docker"
echo ""

echo -e "${GREEN}Bon développement ! 🚀${NC}"
