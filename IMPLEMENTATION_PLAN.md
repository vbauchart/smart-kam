# Torchebald - Smart KAM : Plan d'Implémentation

> Application de Key Account Management pour l'industrie automobile.
> Gestion du cycle de vie complet des projets de vente de pièces sur mesure aux constructeurs (OEM/Tier1).

---

## Table des matières

1. [Synthèse du Projet](#1-synthèse-du-projet)
2. [Modules Fonctionnels](#2-modules-fonctionnels)
3. [Modèle de Données](#3-modèle-de-données)
4. [Plan d'Implémentation par Phases](#4-plan-dimplémentation-par-phases)
5. [Stack Technique](#5-stack-technique)
6. [MVP et Priorités](#6-mvp-et-priorités)

---

## 1. Synthèse du Projet

**Torchebald (Smart KAM)** est une application de Key Account Management destinée aux commerciaux de l'industrie automobile. Elle couvre le cycle de vie complet d'un projet commercial :

```
RFQ → Offre → LOI → Développement → SOP → Production → EoP
```

L'application a pour objectif de **remplacer les tableurs Excel existants** utilisés par les équipes commerciales (KAM) pour le suivi des projets, des prix, des modifications, des amortissements et du reporting.

**Périmètre fonctionnel :**
- Gestion de projets multi-clients, multi-références, multi-sites
- Pricing structuré avec Incoterms, productivités, amortissements R&D
- Fiches de modification (F4/PCICN) avec recalcul automatique des prix
- Suivi matières premières (cours LME)
- Logistique, packaging, capacitaire usines
- Génération de documents (PCICN, exports Excel/PDF, signatures)
- Dashboard et reporting consolidé

---

## 2. Modules Fonctionnels

### Module 1 - Gestion des Projets

Gestion complète du référentiel projet et de son cycle de vie.

- [ ] Création de projet (nom, description, date SOP, sites de production, volumes prévisionnels)
- [ ] Familles de produits (PF1, PF2...) avec désignation
- [ ] Références produit : référence interne, référence client, référence Tier1 — une même pièce possède N références selon le client
- [ ] Multi-clients par projet (Client1, Client1bis, Tier1, Tier1bis)
- [ ] États du projet avec transitions : `RFQ > Offer > LOI > Development > SOP > Production > EoP`
- [ ] Archivage documentaire (RFQ, offres, LOI, matrice de responsabilité, CSR, contrats)
- [ ] Templates contractuels (standard, client, société)

### Module 2 - Pricing & Suivi des Prix

Structure de prix complète avec traçabilité et calculs automatiques.

- [ ] Structure prix initiale : base (hors packaging, hors R&D) + amortissement R&D + coûts packaging = **prix SOP initial**
- [ ] Table Incoterms normalisée (EXW, FCA, CIP...) avec surcoûts transport par destination
- [ ] Prix par Incoterm : calcul automatique EXW → FCA → CIP selon la destination
- [ ] Suivi mensuel des prix avec traçabilité de chaque modification
- [ ] Composants dirigés (directed components) : suivi des composants imposés par le client avec coût et prix de vente
- [ ] Productivité contractuelle : application automatique des baisses annuelles (ex : -1%/an x 5 ans)
- [ ] **Price Walk** : décomposition visuelle du prix, du contrat initial au prix courant
- [ ] Comparaison vs LOI : écart automatique entre prix SOP actualisé et prix LOI initial

### Module 3 - Fiches de Modification (F4/PCICN)

Gestion des modifications techniques et de leur impact sur les prix.

- [ ] Création de fiche modification : N° fiche, description technique, motivation, date
- [ ] Impact prix : calcul global (EUR/pièce) décomposé en prix pièce + amortissement outillage (TEF) + packaging
- [ ] Matrice d'application : quelle modification s'applique à quelles références (matrice Y/N)
- [ ] Workflow de validation : `Open > Validated / Canceled`, signatures fournisseur et acheteur
- [ ] Documents attachés aux fiches
- [ ] Recalcul automatique des prix après validation d'une fiche
- [ ] Amortissement des modifications : montant, volume, durée, coût/pièce automatique
- [ ] Tooling/Cash : suivi des investissements outillage liés aux modifications
- [ ] Génération PCICN : formulaire pré-rempli au format client

### Module 4 - Amortissement R&D

Calcul et répartition de l'amortissement des coûts de développement.

- [ ] Saisie des coûts R&D (coût total programme)
- [ ] Calcul automatique : coût total / nb années / nb pièces
- [ ] Recalcul automatique si variation des volumes
- [ ] Répartition par référence et par famille de produits
- [ ] Transfert d'amortissement entre références

### Module 5 - Matières Premières & Coûts

Suivi des matières premières et de leur impact sur les prix.

- [ ] Suivi des matières premières, intégration des cours LME
- [ ] Mise à jour automatique des prix, recalcul de l'impact matière
- [ ] Groupement par sous-groupes de références
- [ ] DCL (Dossier Conditions Logistiques) avec impact prix
- [ ] Checklist des prix actés

### Module 6 - Usines & Capacitaire

Gestion du footprint industriel et du plan de charge.

- [ ] Footprint industriel (fiches usines, lignes de production, capacités)
- [ ] Plan de charge semaine/mois
- [ ] Vérification capacitaire vs volumes commandés
- [ ] Contacts usine, packaging disponible par site

### Module 7 - Logistique & Packaging

Gestion des emballages et calcul du coût packaging.

- [ ] Catalogue d'emballages (prix standards)
- [ ] Constitution UC/UM (drag & drop)
- [ ] Import de photos d'emballages
- [ ] Calcul du coût packaging par pièce

### Module 8 - Prévisions & EDI

Forecast de ventes et intégration EDI clients.

- [ ] Forecast de vente par référence
- [ ] Récupération des EDI clients
- [ ] Comparaison MAD/Budget
- [ ] Rappels des productivités à venir avec accord finance
- [ ] Génération B101

### Module 9 - Tooling & Facturation

Suivi des commandes d'outillage et déclenchement de la facturation.

- [ ] Suivi des commandes tooling (PO manquant, en cours, reçu)
- [ ] Trigger facturation (email automatique vers finance)
- [ ] PO tracking

### Module 10 - Offres & Prototypes

Gestion des offres prototypes et des RFQ.

- [ ] Gestion des offres proto (quantités, prix)
- [ ] RFQ en ligne partagé avec le client
- [ ] Calcul NPV automatique
- [ ] Validation RFQ par managers (workflow type CAA Valeo)

### Module 11 - Dashboard & Reporting

Tableaux de bord et consolidation.

- [ ] Santé du compte (qualité, budget, marge)
- [ ] Consolidation multi-projets par compte / PL / BU
- [ ] Platform summary
- [ ] Performance commerciale et achat
- [ ] Business case dynamique
- [ ] Export PDF/Excel

### Module 12 - Signature & Export

Signature électronique et génération de documents.

- [ ] Signature électronique PDF
- [ ] Export email client avec pièces jointes
- [ ] Génération de documents standards

---

## 3. Modèle de Données

### Diagramme des entités principales

```
Company ──────┐
  │           │
  ▼           │
Plant ────────┤
  │           │
  ▼           │
ProductionLine│
              │
Customer ─────┤
  │           │
  ▼           │
CustomerSite  │
              │
Project ──────┘
  ├── ProjectStatus
  ├── ProjectDocument
  │
  ├── ProductFamily
  │     └── ProductReference
  │           ├── CustomerReference
  │           ├── PriceHistory
  │           └── PriceBreakdown
  │
  ├── ModificationSheet
  │     ├── ModificationApplication  (matrice ref × modif)
  │     └── ModificationImpact
  │
  ├── RdAmortization
  ├── Productivity
  └── Forecast

Incoterm          (table de référence)
RawMaterial       (cours matières premières)
PackagingCatalog  (catalogue emballages)
```

### Entités détaillées

| Entité | Description | Relations principales |
|---|---|---|
| `Company` | Société (notre entreprise) | → Plant (1:N) |
| `Plant` | Usine de production | → ProductionLine (1:N), → Company (N:1) |
| `ProductionLine` | Ligne de production | → Plant (N:1) |
| `Customer` | Client (OEM ou Tier1) | → CustomerSite (1:N), → Project (N:M) |
| `CustomerSite` | Site de livraison client | → Customer (N:1) |
| `Project` | Projet commercial | → ProductFamily (1:N), → ModificationSheet (1:N), → Customer (N:M) |
| `ProjectStatus` | Historique des états du projet | → Project (N:1) |
| `ProjectDocument` | Documents archivés | → Project (N:1) |
| `ProductFamily` | Famille de produits (PF1, PF2...) | → Project (N:1), → ProductReference (1:N) |
| `ProductReference` | Référence produit (interne) | → ProductFamily (N:1), → CustomerReference (1:N) |
| `CustomerReference` | Référence d'un produit chez un client | → ProductReference (N:1), → Customer (N:1) |
| `PriceHistory` | Historique mensuel des prix | → ProductReference (N:1) |
| `PriceBreakdown` | Décomposition du prix (base, R&D, packaging, transport) | → ProductReference (N:1), → Incoterm (N:1) |
| `ModificationSheet` | Fiche de modification (F4) | → Project (N:1), → ModificationApplication (1:N) |
| `ModificationApplication` | Application d'une modif à une référence | → ModificationSheet (N:1), → ProductReference (N:1) |
| `ModificationImpact` | Impact prix d'une modification | → ModificationSheet (N:1) |
| `RdAmortization` | Amortissement R&D | → Project (N:1) |
| `Productivity` | Productivité contractuelle (baisses annuelles) | → Project (N:1) |
| `Forecast` | Prévisions de vente | → ProductReference (N:1) |
| `Incoterm` | Table de référence Incoterms | — |
| `RawMaterial` | Cours matières premières | — |
| `PackagingCatalog` | Catalogue emballages | — |

---

## 4. Plan d'Implémentation par Phases

### Phase 0 - Setup Technique (2 semaines)

Mise en place de l'infrastructure technique et de l'environnement de développement.

- [ ] Initialiser le projet Spring Boot 3.x avec Java 21
- [ ] Créer le `docker-compose.yml` pour le dev local (PostgreSQL 16, pgAdmin)
- [ ] Configurer Flyway pour les migrations
- [ ] Structurer le projet en multi-modules : `domain`, `service`, `web`
- [ ] Configurer Spring Security avec les rôles : KAM, Manager, Finance, Admin
- [ ] Mettre en place l'UI : Bootstrap 5 + HTMX + fragments Thymeleaf
- [ ] Créer le layout principal et la navigation
- [ ] Configurer le build Maven (pom.xml parent + modules)
- [ ] Mettre en place le CI/CD (GitHub Actions)
- [ ] Configurer les tests (JUnit 5, Testcontainers, Mockito)
- [ ] Créer les premiers scripts Flyway (schéma initial)

### Phase 1 - Socle Fonctionnel (6 semaines, Sprints 1-6)

#### Sprints 1-2 : Référentiel

- [ ] Entité `Company` : CRUD complet
- [ ] Entité `Plant` : CRUD avec lien Company
- [ ] Entité `Customer` et `CustomerSite` : CRUD complet
- [ ] Table de référence `Incoterm` : seed des données standards (EXW, FCA, CIP...)
- [ ] Gestion des utilisateurs et rôles (Users/Roles)
- [ ] Pages d'administration du référentiel
- [ ] Tests unitaires et d'intégration du référentiel

#### Sprints 3-4 : Projets

- [ ] Entité `Project` : création, édition, liste, recherche
- [ ] Machine à états du projet : RFQ → Offer → LOI → Development → SOP → Production → EoP
- [ ] Entité `ProductFamily` : CRUD avec lien Project
- [ ] Entité `ProductReference` : CRUD avec lien ProductFamily
- [ ] Entité `CustomerReference` : gestion des N références client par produit
- [ ] Gestion documentaire : upload, stockage, téléchargement (`ProjectDocument`)
- [ ] Saisie initiale du projet (volumes, dates, sites)
- [ ] Vue détaillée projet avec onglets

#### Sprints 5-6 : Structure de Prix

- [ ] Entité `PriceBreakdown` : prix base + R&D + packaging = prix SOP
- [ ] Calcul automatique du prix par Incoterm (EXW → FCA → CIP)
- [ ] Surcoûts transport par destination
- [ ] Entité `PriceHistory` : suivi mensuel avec traçabilité
- [ ] Interface de saisie et modification des prix
- [ ] Historique des prix avec graphique d'évolution
- [ ] Tests des calculs de prix

### Phase 2 - Coeur Métier Prix (6 semaines, Sprints 7-12)

#### Sprints 7-8 : Fiches de Modification (F4)

- [ ] Entité `ModificationSheet` : création, workflow (Open → Validated / Canceled)
- [ ] Impact prix : décomposition EUR/pièce (prix pièce + TEF + packaging)
- [ ] Matrice d'application (`ModificationApplication`) : modification × références
- [ ] Interface de la matrice Y/N interactive
- [ ] Workflow de validation avec signatures
- [ ] Documents attachés aux fiches
- [ ] Recalcul automatique des prix après validation
- [ ] Amortissement des modifications (montant, volume, durée → coût/pièce)
- [ ] Tests du recalcul automatique

#### Sprints 9-10 : Amortissement R&D & Productivité

- [ ] Entité `RdAmortization` : saisie coût total programme
- [ ] Calcul auto : coût total / nb années / nb pièces
- [ ] Recalcul si variation des volumes
- [ ] Répartition par référence et famille
- [ ] Transfert d'amortissement entre références
- [ ] Entité `Productivity` : productivité contractuelle
- [ ] Application automatique des baisses annuelles (ex : -1%/an x 5 ans)
- [ ] Impact sur le PriceHistory

#### Sprints 11-12 : Matières Premières

- [ ] Entité `RawMaterial` : suivi cours matières premières
- [ ] Intégration cours LME (manuelle ou API)
- [ ] Mise à jour automatique des prix avec recalcul impact matière
- [ ] Groupement par sous-groupes de références
- [ ] DCL (Dossier Conditions Logistiques) avec impact prix
- [ ] Checklist des prix actés

### Phase 3 - Génération Documents (4 semaines, Sprints 13-16)

#### Sprints 13-14 : PCICN & Exports

- [ ] Génération PCICN automatique : formulaire pré-rempli format client
- [ ] Export Excel via Apache POI (données projets, prix, modifications)
- [ ] **Price Walk** : décomposition visuelle du prix (graphique en cascade)
- [ ] Comparaison vs LOI : écart automatique SOP actualisé vs LOI initial

#### Sprints 15-16 : Signature & Communication

- [ ] Signature électronique PDF (iText / JasperReports)
- [ ] Email automatique vers client avec pièces jointes (Spring Mail)
- [ ] Checklist de validation avant envoi
- [ ] Génération de documents standards (templates)

### Phase 4 - Logistique & Capacitaire (4 semaines, Sprints 17-20)

#### Sprints 17-18 : Usines & Packaging

- [ ] Fiches usines complètes (contacts, capacités, packaging disponible)
- [ ] Entité `ProductionLine` : lignes et capacités
- [ ] Entité `PackagingCatalog` : catalogue emballages avec prix standards
- [ ] Configurateur UC/UM avec drag & drop
- [ ] Import de photos d'emballages
- [ ] Calcul du coût packaging par pièce

#### Sprints 19-20 : Plan de Charge & Capacitaire

- [ ] Plan de charge semaine/mois par ligne de production
- [ ] Vérification capacitaire vs volumes commandés
- [ ] Alertes de dépassement de capacité
- [ ] Vue consolidée par usine

### Phase 5 - Dashboard & Reporting (4 semaines, Sprints 21-24)

#### Sprints 21-22 : Dashboards

- [ ] Dashboard santé du compte (qualité, budget, marge)
- [ ] Consolidation multi-projets par compte / PL / BU
- [ ] Platform summary
- [ ] Graphiques interactifs (Chart.js ou ApexCharts)

#### Sprints 23-24 : Reporting Avancé

- [ ] Business case dynamique
- [ ] Performance commerciale et achat
- [ ] Exports PDF/Excel des rapports
- [ ] Filtres et personnalisation des vues

### Phase 6 - Intégrations & Avancé (6 semaines, Sprints 25-30)

#### Sprints 25-26 : Forecast & EDI

- [ ] Forecast de vente par référence
- [ ] Récupération EDI clients
- [ ] Comparaison MAD/Budget
- [ ] Rappels des productivités à venir avec accord finance
- [ ] Génération B101

#### Sprints 27-28 : RFQ & Workflow

- [ ] RFQ en ligne partagé avec le client
- [ ] Calcul NPV automatique
- [ ] Workflow de validation RFQ par managers (type CAA Valeo)
- [ ] Gestion des offres prototypes (quantités, prix)

#### Sprints 29-30 : Tooling & Intégrations

- [ ] Suivi commandes tooling (PO manquant, en cours, reçu)
- [ ] Trigger facturation (email automatique vers finance)
- [ ] PO tracking
- [ ] Étude d'intégration SAP

---

## 5. Stack Technique

| Couche | Technologie | Justification |
|---|---|---|
| **Backend** | Spring Boot 3.x, Java 21 | Écosystème mature, support LTS, records Java |
| **Persistence** | Spring Data JPA / Hibernate, PostgreSQL 16 | ORM standard, JSONB pour données flexibles |
| **Dev local** | Docker Compose (PostgreSQL, pgAdmin) | Environnement reproductible, zéro install locale |
| **Migrations** | Flyway | Versioning du schéma, reproductibilité |
| **Frontend** | Thymeleaf + HTMX + Bootstrap 5 | Server-side rendering avec interactivité AJAX |
| **Sécurité** | Spring Security | Rôles (KAM, Manager, Finance, Admin), CSRF, sessions |
| **Documents** | Apache POI (Excel), iText / JasperReports (PDF) | Génération Excel et PDF natifs |
| **Graphiques** | Chart.js ou ApexCharts | Graphiques interactifs (Price Walk, dashboards) |
| **Email** | Spring Mail | Notifications et envois automatiques |
| **Tests** | JUnit 5, Testcontainers, Mockito | Tests unitaires, intégration avec PostgreSQL réel |
| **Build** | Maven | Standard Spring Boot, multi-module simple et fiable |
| **CI/CD** | GitHub Actions | Intégration continue, déploiement automatisé |

### Architecture Multi-Module

```
torchebald/
├── torchebald-domain/        # Entités JPA, enums, value objects
├── torchebald-service/       # Logique métier, calculs prix, services
├── torchebald-web/           # Contrôleurs, Thymeleaf, HTMX, sécurité
├── torchebald-infra/         # Configuration, Flyway, intégrations externes
└── pom.xml                   # POM parent
```

### Rôles Utilisateurs

| Rôle | Périmètre |
|---|---|
| **KAM** | Gestion de ses projets, prix, modifications, documents |
| **Manager** | Validation des RFQ, vue consolidée, reporting |
| **Finance** | Validation productivités, facturation tooling, business case |
| **Admin** | Référentiel, utilisateurs, paramétrage global |

---

## 6. MVP et Priorités

### Périmètre MVP : Phases 0 à 2 (~14 semaines)

Le MVP couvre les fonctionnalités critiques qui remplacent directement les tableurs Excel existants :

| Priorité | Fonctionnalité | Phase | Modules |
|---|---|---|---|
| **P0** | Gestion des projets, familles, références | Phase 1 | Module 1 |
| **P0** | Structure de prix avec Incoterms | Phase 1 | Module 2 (partiel) |
| **P0** | Fiches de modification (F4) avec recalcul auto | Phase 2 | Module 3 |
| **P0** | Amortissement R&D et productivités | Phase 2 | Modules 4, 2 (partiel) |
| **P0** | Suivi mensuel des prix | Phase 2 | Module 2 (partiel) |

### Critères de succès du MVP

- [ ] Un KAM peut créer un projet et saisir les références avec leurs prix
- [ ] Les prix sont calculés automatiquement par Incoterm
- [ ] Les fiches de modification recalculent automatiquement les prix impactés
- [ ] L'amortissement R&D est calculé et réparti par référence
- [ ] Les productivités contractuelles sont appliquées automatiquement
- [ ] L'historique mensuel des prix est traçable et consultable
- [ ] Les données sont persistées en base et non plus dans des fichiers Excel

### Après le MVP

| Priorité | Fonctionnalité | Phase |
|---|---|---|
| **P1** | Génération PCICN, exports Excel/PDF, Price Walk | Phase 3 |
| **P1** | Signature électronique, emails automatiques | Phase 3 |
| **P2** | Logistique, packaging, capacitaire usines | Phase 4 |
| **P2** | Dashboards et reporting consolidé | Phase 5 |
| **P3** | Forecast, EDI, RFQ en ligne, tooling | Phase 6 |

---

## Annexe - Estimation Globale

| Phase | Durée | Sprints | Contenu principal |
|---|---|---|---|
| Phase 0 | 2 semaines | — | Setup technique |
| Phase 1 | 6 semaines | 1-6 | Référentiel, projets, structure prix |
| Phase 2 | 6 semaines | 7-12 | Modifications, R&D, matières |
| Phase 3 | 4 semaines | 13-16 | Documents, exports, signatures |
| Phase 4 | 4 semaines | 17-20 | Logistique, packaging, capacitaire |
| Phase 5 | 4 semaines | 21-24 | Dashboards, reporting |
| Phase 6 | 6 semaines | 25-30 | Intégrations, EDI, tooling |
| **Total** | **~32 semaines** | **30 sprints** | **12 modules** |

> **Note :** Les estimations sont indicatives et basées sur des sprints d'une semaine. Elles seront affinées lors du démarrage de chaque phase.
