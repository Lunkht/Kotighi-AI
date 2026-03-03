#!/bin/bash

# ============================================================
#  KOTIGHI AI - Basculer vers Supabase
#  Script pour configurer et tester Supabase
# ============================================================

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
║         🚀 KOTIGHI AI - Configuration Supabase         ║
║                                                        ║
╚════════════════════════════════════════════════════════╝
EOF
echo -e "${NC}"

# Vérifier si on est dans le bon dossier
if [ ! -f "Kotighi-AI/api_kotighi_supabase.py" ]; then
    echo -e "${RED}✗ Erreur : Exécutez ce script depuis la racine du projet${NC}"
    exit 1
fi

cd Kotighi-AI

# ============================================================
#  ÉTAPE 1 : Vérifier les dépendances
# ============================================================

echo -e "${BLUE}[1/5] Vérification des dépendances...${NC}"

if [ ! -d "venv" ]; then
    echo -e "${YELLOW}⚠ Environnement virtuel non trouvé${NC}"
    echo -e "${CYAN}Création de l'environnement virtuel...${NC}"
    python3 -m venv venv
fi

source venv/bin/activate

echo -e "${CYAN}Installation des dépendances Supabase...${NC}"
pip install -q supabase postgrest-py python-dotenv

echo -e "${GREEN}✓ Dépendances installées${NC}"
echo ""

# ============================================================
#  ÉTAPE 2 : Configuration .env
# ============================================================

echo -e "${BLUE}[2/5] Configuration de .env...${NC}"

if [ ! -f ".env" ]; then
    echo -e "${CYAN}Création du fichier .env...${NC}"
    cp .env.example .env
    echo -e "${GREEN}✓ Fichier .env créé${NC}"
else
    echo -e "${GREEN}✓ Fichier .env existe déjà${NC}"
fi

# Demander les informations Supabase
echo ""
echo -e "${YELLOW}📋 Informations Supabase requises :${NC}"
echo -e "${CYAN}Vous pouvez les trouver dans : Dashboard → Settings → API${NC}"
echo ""

read -p "Supabase URL [https://izeukppgxagzvwfhiqra.supabase.co] : " SUPABASE_URL
SUPABASE_URL=${SUPABASE_URL:-https://izeukppgxagzvwfhiqra.supabase.co}

read -p "Supabase Anon Key : " SUPABASE_KEY

read -p "Supabase Service Role Key : " SUPABASE_SERVICE_KEY

read -sp "PostgreSQL Password : " DB_PASSWORD
echo ""

# Mettre à jour .env
echo -e "${CYAN}Mise à jour de .env...${NC}"

# Backup de .env
cp .env .env.backup

# Mettre à jour les valeurs
sed -i.bak "s|DATABASE_URL=.*|DATABASE_URL=postgresql://postgres:${DB_PASSWORD}@db.izeukppgxagzvwfhiqra.supabase.co:5432/postgres|" .env
sed -i.bak "s|SUPABASE_URL=.*|SUPABASE_URL=${SUPABASE_URL}|" .env
sed -i.bak "s|SUPABASE_KEY=.*|SUPABASE_KEY=${SUPABASE_KEY}|" .env
sed -i.bak "s|SUPABASE_SERVICE_KEY=.*|SUPABASE_SERVICE_KEY=${SUPABASE_SERVICE_KEY}|" .env

rm .env.bak 2>/dev/null || true

echo -e "${GREEN}✓ Configuration .env mise à jour${NC}"
echo ""

# ============================================================
#  ÉTAPE 3 : Afficher le SQL
# ============================================================

echo -e "${BLUE}[3/5] Schéma de base de données...${NC}"

echo -e "${YELLOW}📋 Copiez et exécutez ce SQL dans Supabase SQL Editor :${NC}"
echo ""
echo -e "${CYAN}Dashboard → SQL Editor → New Query → Coller et Run${NC}"
echo ""

python supabase_config.py

echo ""
read -p "Appuyez sur Entrée une fois le SQL exécuté dans Supabase..."

# ============================================================
#  ÉTAPE 4 : Tester la connexion
# ============================================================

echo -e "${BLUE}[4/5] Test de connexion...${NC}"

python << EOF
import os
from dotenv import load_dotenv
from supabase import create_client

load_dotenv()

try:
    supabase_url = os.getenv("SUPABASE_URL")
    supabase_key = os.getenv("SUPABASE_KEY")
    
    if not supabase_key:
        print("${RED}✗ SUPABASE_KEY non configurée${NC}")
        exit(1)
    
    supabase = create_client(supabase_url, supabase_key)
    
    # Tester la connexion
    response = supabase.table("users").select("count", count="exact").execute()
    
    print("${GREEN}✓ Connexion Supabase réussie !${NC}")
    print(f"${CYAN}Nombre d'utilisateurs : {response.count}${NC}")
    
except Exception as e:
    print(f"${RED}✗ Erreur de connexion : {e}${NC}")
    exit(1)
EOF

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Échec de la connexion${NC}"
    exit 1
fi

echo ""

# ============================================================
#  ÉTAPE 5 : Lancer l'API
# ============================================================

echo -e "${BLUE}[5/5] Lancement de l'API Supabase...${NC}"

echo -e "${CYAN}L'API va démarrer sur http://localhost:8000${NC}"
echo -e "${CYAN}Documentation : http://localhost:8000/docs${NC}"
echo ""
echo -e "${YELLOW}Appuyez sur Ctrl+C pour arrêter${NC}"
echo ""

sleep 2

uvicorn api_kotighi_supabase:app --reload --host 0.0.0.0 --port 8000
