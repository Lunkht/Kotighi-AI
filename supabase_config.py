"""
Configuration et utilitaires Supabase pour KOTIGHI AI
"""
import os
from supabase import create_client, Client
from dotenv import load_dotenv

load_dotenv()

# Configuration Supabase
SUPABASE_URL = os.getenv("SUPABASE_URL", "https://izeukppgxagzvwfhiqra.supabase.co")
SUPABASE_KEY = os.getenv("SUPABASE_KEY", "")
SUPABASE_SERVICE_KEY = os.getenv("SUPABASE_SERVICE_KEY", "")

# Client Supabase
supabase: Client = None

def get_supabase_client() -> Client:
    """
    Retourne le client Supabase (singleton)
    """
    global supabase
    if supabase is None:
        if not SUPABASE_KEY:
            raise ValueError("SUPABASE_KEY non configurée dans .env")
        supabase = create_client(SUPABASE_URL, SUPABASE_KEY)
    return supabase

def get_supabase_admin_client() -> Client:
    """
    Retourne le client Supabase avec privilèges admin (service role)
    """
    if not SUPABASE_SERVICE_KEY:
        raise ValueError("SUPABASE_SERVICE_KEY non configurée dans .env")
    return create_client(SUPABASE_URL, SUPABASE_SERVICE_KEY)

# SQL pour créer les tables dans Supabase
SUPABASE_SCHEMA_SQL = """
-- Table users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(50) DEFAULT 'Utilisateur',
    email VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table historique
CREATE TABLE IF NOT EXISTS historique (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    type_analyse VARCHAR(20) NOT NULL CHECK (type_analyse IN ('Cyber', 'Sante')),
    resultat VARCHAR(100),
    details TEXT,
    confiance FLOAT,
    urgent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table profils (informations supplémentaires)
CREATE TABLE IF NOT EXISTS profils (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    avatar_url TEXT,
    bio TEXT,
    specialite VARCHAR(100),
    organisation VARCHAR(100),
    preferences JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table notifications
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    titre VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'info',
    lu BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pour performance
CREATE INDEX IF NOT EXISTS idx_historique_user_id ON historique(user_id);
CREATE INDEX IF NOT EXISTS idx_historique_created_at ON historique(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_historique_type ON historique(type_analyse);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_lu ON notifications(lu);

-- Trigger pour updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_profils_updated_at BEFORE UPDATE ON profils
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Row Level Security (RLS)
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE historique ENABLE ROW LEVEL SECURITY;
ALTER TABLE profils ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;

-- Policies pour users (les utilisateurs peuvent voir et modifier leur propre profil)
CREATE POLICY "Users can view own profile" ON users
    FOR SELECT USING (auth.uid()::text = id::text);

CREATE POLICY "Users can update own profile" ON users
    FOR UPDATE USING (auth.uid()::text = id::text);

-- Policies pour historique (les utilisateurs peuvent voir leur propre historique)
CREATE POLICY "Users can view own history" ON historique
    FOR SELECT USING (auth.uid()::text = user_id::text);

CREATE POLICY "Users can insert own history" ON historique
    FOR INSERT WITH CHECK (auth.uid()::text = user_id::text);

-- Policies pour profils
CREATE POLICY "Users can view own profile details" ON profils
    FOR SELECT USING (auth.uid()::text = user_id::text);

CREATE POLICY "Users can update own profile details" ON profils
    FOR ALL USING (auth.uid()::text = user_id::text);

-- Policies pour notifications
CREATE POLICY "Users can view own notifications" ON notifications
    FOR SELECT USING (auth.uid()::text = user_id::text);

CREATE POLICY "Users can update own notifications" ON notifications
    FOR UPDATE USING (auth.uid()::text = user_id::text);
"""

def init_supabase_schema():
    """
    Initialise le schéma Supabase (à exécuter une seule fois)
    Note: Exécutez ce SQL directement dans le SQL Editor de Supabase
    """
    print("📋 Schéma SQL pour Supabase:")
    print(SUPABASE_SCHEMA_SQL)
    print("\n⚠️  Copiez ce SQL et exécutez-le dans le SQL Editor de Supabase")
    print("    Dashboard → SQL Editor → New Query → Coller et Run")

if __name__ == "__main__":
    init_supabase_schema()
