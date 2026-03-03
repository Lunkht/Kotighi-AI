#!/bin/bash

# ============================================================
#  KOTIGHI AI - Health Check Script
#  Vérifie l'état de santé du projet
# ============================================================

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║         KOTIGHI AI - Health Check                     ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

ERRORS=0
WARNINGS=0

# ============================================================
#  BACKEND CHECKS
# ============================================================

echo -e "${BLUE}[1/5] Vérification Backend...${NC}"

# Python
if command -v python3 &> /dev/null; then
    PYTHON_VERSION=$(python3 --version | cut -d' ' -f2)
    echo -e "  ${GREEN}✓${NC} Python installé (version $PYTHON_VERSION)"
else
    echo -e "  ${RED}✗${NC} Python non installé"
    ((ERRORS++))
fi

# Environnement virtuel
if [ -d "Kotighi-AI/venv" ]; then
    echo -e "  ${GREEN}✓${NC} Environnement virtuel présent"
else
    echo -e "  ${YELLOW}⚠${NC} Environnement virtuel manquant (exécutez: make install-backend)"
    ((WARNINGS++))
fi

# Fichier .env
if [ -f "Kotighi-AI/.env" ]; then
    echo -e "  ${GREEN}✓${NC} Fichier .env présent"
else
    echo -e "  ${YELLOW}⚠${NC} Fichier .env manquant (copiez .env.example)"
    ((WARNINGS++))
fi

# Modèles ML
if [ -f "Kotighi-AI/model_cyber.pkl" ] && [ -f "Kotighi-AI/model_sante.pkl" ]; then
    echo -e "  ${GREEN}✓${NC} Modèles ML présents"
else
    echo -e "  ${YELLOW}⚠${NC} Modèles ML manquants (exécutez: make train-models)"
    ((WARNINGS++))
fi

# Requirements
if [ -f "Kotighi-AI/requirements.txt" ]; then
    echo -e "  ${GREEN}✓${NC} requirements.txt présent"
else
    echo -e "  ${RED}✗${NC} requirements.txt manquant"
    ((ERRORS++))
fi

echo ""

# ============================================================
#  ANDROID CHECKS
# ============================================================

echo -e "${BLUE}[2/5] Vérification Android...${NC}"

# Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo -e "  ${GREEN}✓${NC} Java installé (version $JAVA_VERSION)"
else
    echo -e "  ${RED}✗${NC} Java non installé"
    ((ERRORS++))
fi

# Gradle wrapper
if [ -f "gradlew" ]; then
    echo -e "  ${GREEN}✓${NC} Gradle wrapper présent"
    chmod +x gradlew
else
    echo -e "  ${RED}✗${NC} Gradle wrapper manquant"
    ((ERRORS++))
fi

# Build.gradle
if [ -f "app/build.gradle" ]; then
    echo -e "  ${GREEN}✓${NC} Configuration Gradle présente"
else
    echo -e "  ${RED}✗${NC} Configuration Gradle manquante"
    ((ERRORS++))
fi

# AndroidManifest
if [ -f "app/src/main/AndroidManifest.xml" ]; then
    echo -e "  ${GREEN}✓${NC} AndroidManifest.xml présent"
else
    echo -e "  ${RED}✗${NC} AndroidManifest.xml manquant"
    ((ERRORS++))
fi

# local.properties
if [ -f "local.properties" ]; then
    echo -e "  ${GREEN}✓${NC} local.properties présent"
else
    echo -e "  ${YELLOW}⚠${NC} local.properties manquant (sera créé par Android Studio)"
    ((WARNINGS++))
fi

echo ""

# ============================================================
#  DOCUMENTATION CHECKS
# ============================================================

echo -e "${BLUE}[3/5] Vérification Documentation...${NC}"

DOCS=("README.md" "LICENSE" "CONTRIBUTING.md" "SECURITY.md" "DEPLOYMENT.md" "CHANGELOG.md")
for doc in "${DOCS[@]}"; do
    if [ -f "$doc" ]; then
        echo -e "  ${GREEN}✓${NC} $doc présent"
    else
        echo -e "  ${YELLOW}⚠${NC} $doc manquant"
        ((WARNINGS++))
    fi
done

echo ""

# ============================================================
#  SECURITY CHECKS
# ============================================================

echo -e "${BLUE}[4/5] Vérification Sécurité...${NC}"

# .gitignore
if [ -f ".gitignore" ]; then
    if grep -q ".env" .gitignore && grep -q "*.key" .gitignore; then
        echo -e "  ${GREEN}✓${NC} .gitignore configuré correctement"
    else
        echo -e "  ${YELLOW}⚠${NC} .gitignore incomplet"
        ((WARNINGS++))
    fi
else
    echo -e "  ${RED}✗${NC} .gitignore manquant"
    ((ERRORS++))
fi

# Vérifier si .env est commité (ne devrait pas)
if git ls-files --error-unmatch Kotighi-AI/.env &> /dev/null; then
    echo -e "  ${RED}✗${NC} DANGER : .env est commité dans Git !"
    ((ERRORS++))
else
    echo -e "  ${GREEN}✓${NC} .env n'est pas commité"
fi

# Network security config
if [ -f "app/src/main/res/xml/network_security_config.xml" ]; then
    echo -e "  ${GREEN}✓${NC} Network security config présent"
else
    echo -e "  ${YELLOW}⚠${NC} Network security config manquant"
    ((WARNINGS++))
fi

# ProGuard rules
if [ -f "app/proguard-rules.pro" ]; then
    echo -e "  ${GREEN}✓${NC} ProGuard rules présent"
else
    echo -e "  ${YELLOW}⚠${NC} ProGuard rules manquant"
    ((WARNINGS++))
fi

echo ""

# ============================================================
#  CI/CD CHECKS
# ============================================================

echo -e "${BLUE}[5/5] Vérification CI/CD...${NC}"

if [ -d ".github/workflows" ]; then
    WORKFLOW_COUNT=$(ls -1 .github/workflows/*.yml 2>/dev/null | wc -l)
    if [ $WORKFLOW_COUNT -gt 0 ]; then
        echo -e "  ${GREEN}✓${NC} GitHub Actions configuré ($WORKFLOW_COUNT workflows)"
    else
        echo -e "  ${YELLOW}⚠${NC} Aucun workflow GitHub Actions"
        ((WARNINGS++))
    fi
else
    echo -e "  ${YELLOW}⚠${NC} Dossier .github/workflows manquant"
    ((WARNINGS++))
fi

# Docker
if [ -f "docker-compose.yml" ]; then
    echo -e "  ${GREEN}✓${NC} docker-compose.yml présent"
else
    echo -e "  ${YELLOW}⚠${NC} docker-compose.yml manquant"
    ((WARNINGS++))
fi

if [ -f "Kotighi-AI/Dockerfile" ]; then
    echo -e "  ${GREEN}✓${NC} Dockerfile présent"
else
    echo -e "  ${YELLOW}⚠${NC} Dockerfile manquant"
    ((WARNINGS++))
fi

echo ""

# ============================================================
#  RÉSUMÉ
# ============================================================

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                    RÉSUMÉ                              ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✓ Tout est OK ! Le projet est prêt.${NC}"
    exit 0
elif [ $ERRORS -eq 0 ]; then
    echo -e "${YELLOW}⚠ $WARNINGS avertissement(s) détecté(s)${NC}"
    echo -e "${YELLOW}Le projet peut fonctionner mais certains éléments sont manquants.${NC}"
    exit 0
else
    echo -e "${RED}✗ $ERRORS erreur(s) et $WARNINGS avertissement(s) détecté(s)${NC}"
    echo -e "${RED}Veuillez corriger les erreurs avant de continuer.${NC}"
    exit 1
fi
