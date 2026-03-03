"""
Module d'analyse de blessures physiques
Évalue la gravité, recommande les premiers soins et détermine l'urgence
"""

from dataclasses import dataclass, asdict
from typing import List, Dict, Tuple, Optional
from datetime import datetime
from enum import Enum


class InjuryType(Enum):
    """Types de blessures"""
    FRACTURE = "fracture"
    ENTORSE = "entorse"
    CONTUSION = "contusion"
    PLAIE = "plaie"
    BRULURE = "brulure"
    COUPURE = "coupure"
    LUXATION = "luxation"
    DECHIRURE = "dechirure"
    COMMOTION = "commotion"
    HEMORRAGIE = "hemorragie"


class BodyPart(Enum):
    """Parties du corps"""
    TETE = "tête"
    COU = "cou"
    EPAULE = "épaule"
    BRAS = "bras"
    COUDE = "coude"
    POIGNET = "poignet"
    MAIN = "main"
    DOIGT = "doigt"
    THORAX = "thorax"
    ABDOMEN = "abdomen"
    DOS = "dos"
    HANCHE = "hanche"
    CUISSE = "cuisse"
    GENOU = "genou"
    JAMBE = "jambe"
    CHEVILLE = "cheville"
    PIED = "pied"
    ORTEIL = "orteil"


class UrgencyLevel(Enum):
    """Niveaux d'urgence"""
    CRITIQUE = "critique"  # Appeler le 15 immédiatement
    URGENT = "urgent"      # Aller aux urgences rapidement
    MODERE = "modéré"      # Consulter dans les 24h
    LEGER = "léger"        # Soins à domicile possibles
    MINIMAL = "minimal"    # Surveillance simple


@dataclass
class InjurySymptom:
    """Symptôme d'une blessure"""
    name: str
    severity: int  # 1-10
    description: str


@dataclass
class InjuryAnalysis:
    """Résultat de l'analyse d'une blessure"""
    injury_type: str
    body_part: str
    severity_score: float  # 0-100
    urgency_level: str
    pain_level: int  # 1-10
    
    # Symptômes et signes
    symptoms: List[str]
    warning_signs: List[str]
    
    # Recommandations
    first_aid: List[str]
    do_not_do: List[str]
    when_to_call_emergency: List[str]
    
    # Traitement
    treatment_steps: List[str]
    medications: List[str]
    recovery_time: str
    
    # Prévention
    prevention_tips: List[str]
    
    # Métadonnées
    timestamp: str
    requires_xray: bool
    requires_surgery: bool
    can_self_treat: bool


class InjuryAnalyzer:
    """Analyseur de blessures physiques"""
    
    # Scores de gravité par type de blessure
    INJURY_BASE_SCORES = {
        InjuryType.FRACTURE: 80.0,
        InjuryType.LUXATION: 75.0,
        InjuryType.HEMORRAGIE: 90.0,
        InjuryType.COMMOTION: 85.0,
        InjuryType.BRULURE: 70.0,
        InjuryType.DECHIRURE: 65.0,
        InjuryType.ENTORSE: 50.0,
        InjuryType.COUPURE: 45.0,
        InjuryType.PLAIE: 40.0,
        InjuryType.CONTUSION: 30.0,
    }
    
    # Multiplicateurs de gravité par partie du corps
    BODY_PART_MULTIPLIERS = {
        BodyPart.TETE: 1.5,
        BodyPart.COU: 1.4,
        BodyPart.THORAX: 1.3,
        BodyPart.ABDOMEN: 1.3,
        BodyPart.DOS: 1.2,
        BodyPart.GENOU: 1.1,
        BodyPart.EPAULE: 1.1,
        BodyPart.HANCHE: 1.1,
        BodyPart.CHEVILLE: 1.0,
        BodyPart.COUDE: 1.0,
        BodyPart.POIGNET: 1.0,
        BodyPart.MAIN: 0.9,
        BodyPart.PIED: 0.9,
        BodyPart.BRAS: 0.9,
        BodyPart.JAMBE: 0.9,
        BodyPart.CUISSE: 0.9,
        BodyPart.DOIGT: 0.7,
        BodyPart.ORTEIL: 0.7,
    }
    
    def __init__(self):
        self.analyses: List[InjuryAnalysis] = []
    
    def analyze_injury(
        self,
        injury_type: str,
        body_part: str,
        pain_level: int,
        symptoms: List[str],
        mechanism: str = "",
        bleeding: bool = False,
        swelling: bool = False,
        deformity: bool = False,
        loss_of_function: bool = False,
        numbness: bool = False
    ) -> InjuryAnalysis:
        """
        Analyse une blessure et retourne les recommandations
        
        Args:
            injury_type: Type de blessure
            body_part: Partie du corps affectée
            pain_level: Niveau de douleur (1-10)
            symptoms: Liste des symptômes
            mechanism: Mécanisme de la blessure
            bleeding: Présence de saignement
            swelling: Présence de gonflement
            deformity: Présence de déformation
            loss_of_function: Perte de fonction
            numbness: Engourdissement
        """
        
        # Calculer le score de gravité
        severity_score = self._calculate_severity(
            injury_type, body_part, pain_level,
            bleeding, swelling, deformity, loss_of_function, numbness
        )
        
        # Déterminer le niveau d'urgence
        urgency_level = self._determine_urgency(severity_score, symptoms)
        
        # Identifier les signes d'alerte
        warning_signs = self._identify_warning_signs(
            injury_type, body_part, symptoms,
            bleeding, deformity, loss_of_function, numbness
        )
        
        # Premiers soins
        first_aid = self._get_first_aid(injury_type, body_part)
        
        # Ce qu'il ne faut PAS faire
        do_not_do = self._get_contraindications(injury_type, body_part)
        
        # Quand appeler les urgences
        when_to_call = self._get_emergency_criteria(injury_type, body_part)
        
        # Étapes de traitement
        treatment_steps = self._get_treatment_steps(injury_type, body_part, severity_score)
        
        # Médicaments recommandés
        medications = self._get_medications(injury_type, pain_level)
        
        # Temps de récupération
        recovery_time = self._estimate_recovery_time(injury_type, severity_score)
        
        # Conseils de prévention
        prevention_tips = self._get_prevention_tips(injury_type, mechanism)
        
        # Examens nécessaires
        requires_xray = self._requires_xray(injury_type, body_part, deformity)
        requires_surgery = self._requires_surgery(injury_type, severity_score, deformity)
        can_self_treat = self._can_self_treat(severity_score, urgency_level)
        
        analysis = InjuryAnalysis(
            injury_type=injury_type,
            body_part=body_part,
            severity_score=severity_score,
            urgency_level=urgency_level,
            pain_level=pain_level,
            symptoms=symptoms,
            warning_signs=warning_signs,
            first_aid=first_aid,
            do_not_do=do_not_do,
            when_to_call_emergency=when_to_call,
            treatment_steps=treatment_steps,
            medications=medications,
            recovery_time=recovery_time,
            prevention_tips=prevention_tips,
            timestamp=datetime.now().isoformat(),
            requires_xray=requires_xray,
            requires_surgery=requires_surgery,
            can_self_treat=can_self_treat
        )
        
        self.analyses.append(analysis)
        return analysis
    
    def _calculate_severity(
        self,
        injury_type: str,
        body_part: str,
        pain_level: int,
        bleeding: bool,
        swelling: bool,
        deformity: bool,
        loss_of_function: bool,
        numbness: bool
    ) -> float:
        """Calcule le score de gravité (0-100)"""
        
        # Score de base selon le type de blessure
        try:
            injury_enum = InjuryType(injury_type.lower())
            base_score = self.INJURY_BASE_SCORES.get(injury_enum, 50.0)
        except ValueError:
            base_score = 50.0
        
        # Multiplicateur selon la partie du corps
        try:
            body_enum = BodyPart(body_part.lower())
            multiplier = self.BODY_PART_MULTIPLIERS.get(body_enum, 1.0)
        except ValueError:
            multiplier = 1.0
        
        score = base_score * multiplier
        
        # Ajustements selon les symptômes
        if pain_level >= 8:
            score += 10
        elif pain_level >= 6:
            score += 5
        
        if bleeding:
            score += 15
        if deformity:
            score += 20
        if loss_of_function:
            score += 15
        if numbness:
            score += 10
        if swelling:
            score += 5
        
        # Normaliser entre 0 et 100
        return min(max(score, 0.0), 100.0)
    
    def _determine_urgency(self, severity_score: float, symptoms: List[str]) -> str:
        """Détermine le niveau d'urgence"""
        
        # Symptômes critiques
        critical_symptoms = [
            "difficulté à respirer", "perte de conscience", "saignement abondant",
            "douleur thoracique", "confusion", "paralysie", "convulsions"
        ]
        
        for symptom in symptoms:
            if any(crit in symptom.lower() for crit in critical_symptoms):
                return UrgencyLevel.CRITIQUE.value
        
        if severity_score >= 80:
            return UrgencyLevel.CRITIQUE.value
        elif severity_score >= 60:
            return UrgencyLevel.URGENT.value
        elif severity_score >= 40:
            return UrgencyLevel.MODERE.value
        elif severity_score >= 20:
            return UrgencyLevel.LEGER.value
        else:
            return UrgencyLevel.MINIMAL.value
    
    def _identify_warning_signs(
        self,
        injury_type: str,
        body_part: str,
        symptoms: List[str],
        bleeding: bool,
        deformity: bool,
        loss_of_function: bool,
        numbness: bool
    ) -> List[str]:
        """Identifie les signes d'alerte"""
        
        warning_signs = []
        
        if bleeding:
            warning_signs.append("⚠️ Saignement actif - appliquer une pression")
        
        if deformity:
            warning_signs.append("⚠️ Déformation visible - ne pas manipuler")
        
        if loss_of_function:
            warning_signs.append("⚠️ Perte de fonction - immobiliser")
        
        if numbness:
            warning_signs.append("⚠️ Engourdissement - possible atteinte nerveuse")
        
        # Signes spécifiques par type
        if injury_type == InjuryType.FRACTURE.value:
            warning_signs.append("⚠️ Fracture suspectée - ne pas bouger la zone")
        
        if injury_type == InjuryType.COMMOTION.value:
            warning_signs.append("⚠️ Commotion possible - surveillance 24h nécessaire")
        
        if body_part in [BodyPart.TETE.value, BodyPart.COU.value]:
            warning_signs.append("⚠️ Zone critique - évaluation médicale urgente")
        
        return warning_signs
    
    def _get_first_aid(self, injury_type: str, body_part: str) -> List[str]:
        """Retourne les premiers soins à appliquer"""
        
        first_aid = []
        
        # Protocole RICE (Repos, Ice, Compression, Élévation)
        if injury_type in [InjuryType.ENTORSE.value, InjuryType.CONTUSION.value]:
            first_aid.extend([
                "1. REPOS: Arrêter toute activité et immobiliser la zone",
                "2. GLACE: Appliquer de la glace 15-20 min toutes les 2-3h",
                "3. COMPRESSION: Bander modérément sans bloquer la circulation",
                "4. ÉLÉVATION: Surélever la zone au-dessus du cœur"
            ])
        
        # Fracture
        elif injury_type == InjuryType.FRACTURE.value:
            first_aid.extend([
                "1. NE PAS BOUGER la zone blessée",
                "2. Immobiliser avec une attelle improvisée",
                "3. Appliquer de la glace (sans contact direct)",
                "4. Appeler le 15 ou aller aux urgences",
                "5. Ne rien donner à manger ou boire (anesthésie possible)"
            ])
        
        # Plaie/Coupure
        elif injury_type in [InjuryType.PLAIE.value, InjuryType.COUPURE.value]:
            first_aid.extend([
                "1. Se laver les mains",
                "2. Nettoyer la plaie à l'eau et au savon",
                "3. Appliquer une pression pour arrêter le saignement",
                "4. Désinfecter avec antiseptique",
                "5. Couvrir avec un pansement stérile",
                "6. Vérifier la vaccination antitétanique"
            ])
        
        # Brûlure
        elif injury_type == InjuryType.BRULURE.value:
            first_aid.extend([
                "1. Refroidir immédiatement à l'eau tiède (15-25°C) pendant 15 min",
                "2. Retirer bijoux et vêtements non collés",
                "3. Couvrir avec un linge propre et humide",
                "4. NE PAS percer les cloques",
                "5. NE PAS appliquer de glace, beurre ou dentifrice",
                "6. Consulter si brûlure > paume de la main"
            ])
        
        # Hémorragie
        elif injury_type == InjuryType.HEMORRAGIE.value:
            first_aid.extend([
                "1. APPELER LE 15 IMMÉDIATEMENT",
                "2. Allonger la personne",
                "3. Comprimer fortement la plaie avec un linge propre",
                "4. Maintenir la pression sans relâcher",
                "5. Surélever le membre si possible",
                "6. Ne pas retirer le premier pansement (ajouter par-dessus)"
            ])
        
        # Commotion
        elif injury_type == InjuryType.COMMOTION.value:
            first_aid.extend([
                "1. Arrêter immédiatement toute activité",
                "2. Allonger la personne au calme",
                "3. Surveiller la conscience et la respiration",
                "4. Appeler le 15 si perte de conscience",
                "5. Ne rien donner à manger ou boire",
                "6. Surveillance médicale obligatoire"
            ])
        
        return first_aid
    
    def _get_contraindications(self, injury_type: str, body_part: str) -> List[str]:
        """Ce qu'il ne faut PAS faire"""
        
        do_not = []
        
        if injury_type == InjuryType.FRACTURE.value:
            do_not.extend([
                "❌ Ne pas essayer de remettre l'os en place",
                "❌ Ne pas bouger la personne si fracture du dos/cou",
                "❌ Ne pas appliquer de chaleur",
                "❌ Ne pas masser la zone"
            ])
        
        if injury_type == InjuryType.BRULURE.value:
            do_not.extend([
                "❌ Ne pas appliquer de glace directement",
                "❌ Ne pas percer les cloques",
                "❌ Ne pas appliquer de corps gras (beurre, huile)",
                "❌ Ne pas utiliser de coton (fibres collent)"
            ])
        
        if injury_type == InjuryType.ENTORSE.value:
            do_not.extend([
                "❌ Ne pas appliquer de chaleur les 48 premières heures",
                "❌ Ne pas masser immédiatement",
                "❌ Ne pas continuer l'activité sportive"
            ])
        
        if body_part in [BodyPart.TETE.value, BodyPart.COU.value]:
            do_not.extend([
                "❌ Ne pas bouger la tête/cou si traumatisme",
                "❌ Ne pas donner de médicaments sans avis médical",
                "❌ Ne pas laisser dormir les 2 premières heures"
            ])
        
        return do_not
    
    def _get_emergency_criteria(self, injury_type: str, body_part: str) -> List[str]:
        """Critères pour appeler les urgences"""
        
        criteria = [
            "🚨 Appeler le 15 si:",
            "• Saignement abondant qui ne s'arrête pas",
            "• Douleur intense insupportable",
            "• Déformation visible d'un membre",
            "• Perte de conscience même brève",
            "• Difficulté à respirer",
            "• Engourdissement ou paralysie",
            "• Confusion ou désorientation",
            "• Convulsions",
            "• Vomissements répétés",
            "• Aggravation rapide des symptômes"
        ]
        
        if body_part in [BodyPart.TETE.value, BodyPart.COU.value, BodyPart.THORAX.value]:
            criteria.append("• Traumatisme de zone critique (tête/cou/thorax)")
        
        return criteria
    
    def _get_treatment_steps(self, injury_type: str, body_part: str, severity: float) -> List[str]:
        """Étapes de traitement"""
        
        steps = []
        
        if severity >= 60:
            steps.append("1. Consultation médicale urgente obligatoire")
            steps.append("2. Radiographie ou imagerie médicale")
            steps.append("3. Traitement selon diagnostic médical")
        else:
            steps.append("1. Appliquer les premiers soins")
            steps.append("2. Repos et protection de la zone")
            steps.append("3. Surveillance de l'évolution (24-48h)")
            steps.append("4. Consulter si aggravation ou pas d'amélioration")
        
        steps.append("5. Rééducation si nécessaire")
        steps.append("6. Reprise progressive des activités")
        
        return steps
    
    def _get_medications(self, injury_type: str, pain_level: int) -> List[str]:
        """Médicaments recommandés (à valider avec un médecin)"""
        
        meds = []
        
        if pain_level >= 6:
            meds.append("💊 Antalgiques: Paracétamol 1g toutes les 6h (max 4g/jour)")
            meds.append("💊 Anti-inflammatoires: Ibuprofène 400mg si pas de contre-indication")
        elif pain_level >= 3:
            meds.append("💊 Paracétamol 500mg-1g si besoin")
        
        if injury_type in [InjuryType.ENTORSE.value, InjuryType.CONTUSION.value]:
            meds.append("💊 Gel anti-inflammatoire en application locale")
            meds.append("💊 Arnica en gel ou crème (après 48h)")
        
        meds.append("\n⚠️ Toujours respecter les contre-indications")
        meds.append("⚠️ Consulter un médecin ou pharmacien avant prise")
        
        return meds
    
    def _estimate_recovery_time(self, injury_type: str, severity: float) -> str:
        """Estime le temps de récupération"""
        
        recovery_times = {
            InjuryType.CONTUSION: "3-7 jours",
            InjuryType.PLAIE: "7-14 jours",
            InjuryType.COUPURE: "7-14 jours",
            InjuryType.ENTORSE: "2-6 semaines",
            InjuryType.DECHIRURE: "4-8 semaines",
            InjuryType.BRULURE: "1-4 semaines",
            InjuryType.FRACTURE: "6-12 semaines",
            InjuryType.LUXATION: "4-8 semaines",
            InjuryType.COMMOTION: "1-4 semaines",
        }
        
        try:
            injury_enum = InjuryType(injury_type.lower())
            base_time = recovery_times.get(injury_enum, "Variable")
        except ValueError:
            base_time = "Variable"
        
        if severity >= 80:
            return f"{base_time} (ou plus selon gravité)"
        elif severity >= 60:
            return base_time
        else:
            return f"{base_time} (récupération rapide possible)"
    
    def _get_prevention_tips(self, injury_type: str, mechanism: str) -> List[str]:
        """Conseils de prévention"""
        
        tips = [
            "🛡️ Prévention:",
            "• Échauffement avant toute activité physique",
            "• Équipement de protection adapté",
            "• Progression graduelle dans l'effort",
            "• Hydratation suffisante",
            "• Repos et récupération adéquats"
        ]
        
        if "sport" in mechanism.lower():
            tips.extend([
                "• Technique correcte et encadrement",
                "• Renforcement musculaire régulier",
                "• Étirements après l'effort"
            ])
        
        if "chute" in mechanism.lower():
            tips.extend([
                "• Attention aux sols glissants",
                "• Éclairage suffisant",
                "• Chaussures adaptées"
            ])
        
        return tips
    
    def _requires_xray(self, injury_type: str, body_part: str, deformity: bool) -> bool:
        """Détermine si une radiographie est nécessaire"""
        
        if injury_type in [InjuryType.FRACTURE.value, InjuryType.LUXATION.value]:
            return True
        
        if deformity:
            return True
        
        if body_part in [BodyPart.TETE.value, BodyPart.COU.value, BodyPart.THORAX.value]:
            return True
        
        return False
    
    def _requires_surgery(self, injury_type: str, severity: float, deformity: bool) -> bool:
        """Détermine si une chirurgie peut être nécessaire"""
        
        if injury_type == InjuryType.FRACTURE.value and (severity >= 80 or deformity):
            return True
        
        if injury_type == InjuryType.LUXATION.value and severity >= 75:
            return True
        
        if injury_type == InjuryType.DECHIRURE.value and severity >= 80:
            return True
        
        return False
    
    def _can_self_treat(self, severity: float, urgency: str) -> bool:
        """Détermine si l'auto-traitement est possible"""
        
        if urgency in [UrgencyLevel.CRITIQUE.value, UrgencyLevel.URGENT.value]:
            return False
        
        if severity >= 60:
            return False
        
        return True
    
    def export_analysis(self, analysis: InjuryAnalysis) -> Dict:
        """Exporte l'analyse au format JSON"""
        return asdict(analysis)


# Fonction utilitaire pour l'API
def analyze_injury_api(
    injury_type: str,
    body_part: str,
    pain_level: int,
    symptoms: List[str],
    mechanism: str = "",
    bleeding: bool = False,
    swelling: bool = False,
    deformity: bool = False,
    loss_of_function: bool = False,
    numbness: bool = False
) -> Dict:
    """
    Fonction wrapper pour l'API FastAPI
    """
    analyzer = InjuryAnalyzer()
    analysis = analyzer.analyze_injury(
        injury_type=injury_type,
        body_part=body_part,
        pain_level=pain_level,
        symptoms=symptoms,
        mechanism=mechanism,
        bleeding=bleeding,
        swelling=swelling,
        deformity=deformity,
        loss_of_function=loss_of_function,
        numbness=numbness
    )
    
    return {
        'success': True,
        'analysis': analyzer.export_analysis(analysis)
    }


if __name__ == '__main__':
    # Test de l'analyseur
    print("🏥 Test de l'analyseur de blessures\n")
    print("=" * 60)
    
    analyzer = InjuryAnalyzer()
    
    # Test 1: Entorse de cheville
    print("\n📋 Test 1: Entorse de cheville")
    print("-" * 60)
    analysis1 = analyzer.analyze_injury(
        injury_type="entorse",
        body_part="cheville",
        pain_level=7,
        symptoms=["douleur", "gonflement", "difficulté à marcher"],
        mechanism="sport - torsion du pied",
        swelling=True,
        loss_of_function=True
    )
    
    print(f"Type: {analysis1.injury_type}")
    print(f"Partie: {analysis1.body_part}")
    print(f"Gravité: {analysis1.severity_score:.1f}/100")
    print(f"Urgence: {analysis1.urgency_level}")
    print(f"Récupération: {analysis1.recovery_time}")
    print(f"Auto-traitement: {'Oui' if analysis1.can_self_treat else 'Non'}")
    
    # Test 2: Fracture du bras
    print("\n\n📋 Test 2: Fracture suspectée du bras")
    print("-" * 60)
    analysis2 = analyzer.analyze_injury(
        injury_type="fracture",
        body_part="bras",
        pain_level=9,
        symptoms=["douleur intense", "impossibilité de bouger", "déformation"],
        mechanism="chute",
        deformity=True,
        loss_of_function=True
    )
    
    print(f"Type: {analysis2.injury_type}")
    print(f"Gravité: {analysis2.severity_score:.1f}/100")
    print(f"Urgence: {analysis2.urgency_level}")
    print(f"Radiographie: {'Oui' if analysis2.requires_xray else 'Non'}")
    print(f"Chirurgie possible: {'Oui' if analysis2.requires_surgery else 'Non'}")
    
    print("\nPremiers soins:")
    for aid in analysis2.first_aid[:3]:
        print(f"  {aid}")
