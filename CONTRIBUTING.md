# 🤝 Guide de Contribution - KOTIGHI AI

Merci de votre intérêt pour contribuer à KOTIGHI AI ! Ce document vous guidera à travers le processus.

## 📋 Table des Matières

- [Code de Conduite](#code-de-conduite)
- [Comment Contribuer](#comment-contribuer)
- [Standards de Code](#standards-de-code)
- [Process de Pull Request](#process-de-pull-request)
- [Signaler des Bugs](#signaler-des-bugs)
- [Proposer des Fonctionnalités](#proposer-des-fonctionnalités)

---

## 📜 Code de Conduite

En participant à ce projet, vous acceptez de respecter notre code de conduite :

- Soyez respectueux et inclusif
- Acceptez les critiques constructives
- Concentrez-vous sur ce qui est meilleur pour la communauté
- Faites preuve d'empathie envers les autres membres

---

## 🚀 Comment Contribuer

### 1. Fork le Projet

```bash
# Cloner votre fork
git clone https://github.com/votre-username/kotighi-ai.git
cd kotighi-ai

# Ajouter le repo original comme upstream
git remote add upstream https://github.com/original-owner/kotighi-ai.git
```

### 2. Créer une Branche

```bash
# Créer une branche pour votre fonctionnalité
git checkout -b feature/ma-nouvelle-fonctionnalite

# Ou pour un bugfix
git checkout -b fix/correction-bug-xyz
```

### 3. Faire vos Modifications

- Écrivez du code propre et documenté
- Ajoutez des tests pour vos modifications
- Assurez-vous que tous les tests passent

### 4. Commit vos Changements

Utilisez des messages de commit clairs et descriptifs :

```bash
git commit -m "feat: ajout de la fonctionnalité X"
git commit -m "fix: correction du bug Y"
git commit -m "docs: mise à jour de la documentation"
```

Convention de nommage des commits :
- `feat:` Nouvelle fonctionnalité
- `fix:` Correction de bug
- `docs:` Documentation
- `style:` Formatage, point-virgules manquants, etc.
- `refactor:` Refactorisation du code
- `test:` Ajout de tests
- `chore:` Maintenance

### 5. Push vers votre Fork

```bash
git push origin feature/ma-nouvelle-fonctionnalite
```

### 6. Ouvrir une Pull Request

Allez sur GitHub et ouvrez une Pull Request vers la branche `main` du repo original.

---

## 💻 Standards de Code

### Backend Python

```python
# Suivre PEP 8
# Utiliser des docstrings
def ma_fonction(param1: str, param2: int) -> bool:
    """
    Description de la fonction.
    
    Args:
        param1: Description du paramètre 1
        param2: Description du paramètre 2
        
    Returns:
        Description du retour
    """
    pass
```

### Android Java

```java
// Suivre les conventions Java
// Documenter avec Javadoc
/**
 * Description de la classe
 */
public class MaClasse {
    /**
     * Description de la méthode
     * @param param1 Description du paramètre
     * @return Description du retour
     */
    public String maMethode(String param1) {
        return param1;
    }
}
```

### Tests

- Tous les nouveaux codes doivent avoir des tests
- Les tests doivent être clairs et maintenables
- Visez une couverture de code > 80%

```python
# Python
def test_ma_fonction():
    """Test de ma_fonction avec des données valides"""
    result = ma_fonction("test", 42)
    assert result == True
```

```java
// Java
@Test
public void testMaMethode() {
    String result = maMethode("test");
    assertEquals("test", result);
}
```

---

## 🔄 Process de Pull Request

1. **Vérifiez que votre code compile**
   ```bash
   # Backend
   cd Kotighi-AI && pytest tests/
   
   # Android
   ./gradlew test
   ```

2. **Mettez à jour la documentation** si nécessaire

3. **Décrivez vos changements** dans la PR :
   - Quel problème résolvez-vous ?
   - Comment l'avez-vous résolu ?
   - Y a-t-il des effets de bord ?

4. **Attendez la revue de code**
   - Répondez aux commentaires
   - Faites les modifications demandées

5. **Merge** une fois approuvé par un mainteneur

---

## 🐛 Signaler des Bugs

Utilisez les [GitHub Issues](https://github.com/votre-repo/kotighi-ai/issues) avec le template suivant :

```markdown
**Description du Bug**
Description claire et concise du bug.

**Étapes pour Reproduire**
1. Aller à '...'
2. Cliquer sur '...'
3. Voir l'erreur

**Comportement Attendu**
Ce qui devrait se passer.

**Captures d'Écran**
Si applicable, ajoutez des captures d'écran.

**Environnement**
- OS: [e.g. Android 12, Ubuntu 22.04]
- Version: [e.g. 1.0.0]
- Appareil: [e.g. Pixel 6, Émulateur]
```

---

## 💡 Proposer des Fonctionnalités

Ouvrez une [GitHub Issue](https://github.com/votre-repo/kotighi-ai/issues) avec :

```markdown
**Problème à Résoudre**
Quel problème cette fonctionnalité résout-elle ?

**Solution Proposée**
Comment proposez-vous de résoudre ce problème ?

**Alternatives Considérées**
Quelles autres solutions avez-vous envisagées ?

**Contexte Additionnel**
Toute autre information pertinente.
```

---

## 🏗️ Structure du Projet

```
kotighi-ai/
├── app/                    # Application Android
│   ├── src/
│   │   ├── main/          # Code source
│   │   └── test/          # Tests unitaires
│   └── build.gradle
├── Kotighi-AI/            # Backend Python
│   ├── api_kotighi.py    # API FastAPI
│   ├── tests/            # Tests backend
│   └── requirements.txt
└── README.md
```

---

## 🧪 Lancer les Tests

### Backend

```bash
cd Kotighi-AI
python -m pytest tests/ -v --cov=.
```

### Android

```bash
./gradlew test           # Tests unitaires
./gradlew lint          # Analyse statique
./gradlew build         # Build complet
```

---

## 📞 Questions ?

- 💬 Ouvrez une [Discussion GitHub](https://github.com/votre-repo/kotighi-ai/discussions)
- 📧 Contactez-nous : dev@vulsoft.com
- 📖 Consultez la [Documentation](https://github.com/votre-repo/kotighi-ai/wiki)

---

## 🙏 Remerciements

Merci à tous les contributeurs qui rendent ce projet possible !

---

**Bon code ! 🚀**
