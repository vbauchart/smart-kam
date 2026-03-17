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
- [ ] **Mode "What-if"** : simulation de l'impact d'une fiche Open sans valider, vue côte-à-côte
- [ ] **Snapshots versionnés** : photo des prix à chaque PCICN envoyé, comparaison diff entre deux snapshots

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
- [ ] Plan de charge semaine/mois + **granularité jour** (Sprint 18)
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
- [ ] **Budget auto à court et moyen terme** (CA prév. = prix × volumes × mix) (Sprint 17)
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

Signature électronique, génération et import de documents.

- [ ] Signature électronique PDF
- [ ] Export email client avec pièces jointes
- [ ] Génération de documents standards
- [ ] **Import Excel initial** : wizard d'import depuis `.xlsm` existant pour bootstrapper un projet

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

## 4. Plan d'Implémentation — MVPs Successifs

> **Philosophie** : Chaque sprint livre une fonctionnalité **visible et démontrable**.
> Le coeur métier (modification/recalcul de prix) arrive dès le Sprint 1.
> L'infrastructure technique (users, rôles, admin) est repoussée en fin de parcours.
> Les sprints sont de **2 semaines**. Chaque MVP est un jalon de démonstration.

---

### Moteur de Pricing — Règles Métier

> Ces règles sont extraites des formules du fichier Excel de suivi des prix.
> Elles constituent le coeur algorithmique de l'application et sont implémentées progressivement dans les MVPs 0 et 1.

#### Règle 1 — Prix SOP Initial

```
Prix SOP = Prix de base (hors packaging, hors R&D)
         + Amortissement R&D
         + Coûts de packaging
```

Les trois composantes sont saisies manuellement par le KAM lors de la création du contrat.

#### Règle 2 — Matrice d'Application & Recalcul

Chaque fiche de modification (F4) possède :
- Un **impact prix pièce** (Part Price) et un **impact amortissement outillage** (TEF)
- Une **matrice d'application** : pour chaque référence produit, `Y` ou `N`
- Un **statut** : `Open`, `Validated`, ou `Canceled`

Le prix SOP actualisé est recalculé ainsi :

```
Nouveau prix de base = Prix de base initial
    + SOMME(impact Part Price des fiches Validated ayant Y pour cette référence)

Nouvel amortissement = Amortissement R&D initial
    + SOMME(impact TEF des fiches Validated ayant Y pour cette référence)

Prix SOP actualisé = Nouveau prix de base + Nouvel amortissement + Packaging
```

**Règle critique** : seules les fiches au statut `Validated` et marquées `Y` pour la référence concernée participent au recalcul. Les fiches `Open` et `Canceled` n'impactent jamais les prix.

#### Règle 3 — Productivité Contractuelle

La productivité annuelle (ex : -1%/an) s'applique **uniquement sur le prix de base nu** :

```
Prix année N+1 = (Prix année N - Amortissement - Packaging) × (1 + taux_productivité)
               + Amortissement
               + Packaging
```

L'amortissement R&D et le packaging sont **exclus** de la base de calcul de la productivité. C'est une subtilité métier majeure : le client ne peut pas demander de productivité sur les rondelles d'amortissement.

#### Règle 4 — Tombée des Rondelles

À une date définie (fin de la période d'amortissement, ex : SOP+7), les rondelles R&D "tombent" :

```
Prix après tombée = Prix courant - Amortissement R&D total
```

Le prix baisse brutalement à cette date. C'est un événement ponctuel, pas une baisse progressive.

---

### MVP 0 — Preuve de Concept Prix (Sprints 0-2, 6 semaines)

> **Objectif** : Démontrer le moteur de pricing et le recalcul automatique bout en bout.
> À la fin de ce MVP, on peut montrer le coeur de l'application à un utilisateur.
> Implémente les **Règles 1 et 2** du moteur de pricing.

#### Sprint 0 : Bootstrap technique (2 semaines)

- [ ] Initialiser le projet Spring Boot 3.x / Java 21 / Maven + Spring Modulith
- [ ] Créer le `docker-compose.yml` (PostgreSQL 16, pgAdmin)
- [ ] Configurer Flyway, créer le schéma initial
- [ ] Layout Thymeleaf + Bootstrap 5 + HTMX + Alpine.js (navbar, sidebar, page blanche)
- [ ] **Moteur de pricing en pur Java d'abord** : implémenter les 4 règles comme des fonctions pures sans UI, sans JPA
- [ ] **Test oracle Excel** : parser le fichier PF1 existant avec Apache POI → extraire les valeurs attendues → tests JUnit qui vérifient que le moteur produit les mêmes résultats
- [ ] Seed Flyway réaliste : données complètes du PF1 de l'Excel (pas de données fictives)
- [ ] Aucune authentification à ce stade (accès libre)

> **Livrable visuel** : L'application démarre, la page d'accueil s'affiche, la base tourne dans Docker. Les tests du moteur de pricing passent au vert et les résultats sont vérifiables contre l'Excel.

#### Sprint 1 : Tableau de prix & Règle 1 (2 semaines)

Implémentation de la **Règle 1 — Prix SOP Initial**.

- [ ] Entités `Project`, `ProductFamily`, `ProductReference` (CRUD minimal)
- [ ] Entité `PriceBreakdown` : les 3 composantes du prix (base, R&D, packaging)
- [ ] **Moteur de calcul** : `Prix SOP = base + R&D + packaging`
- [ ] **Page projet** : vue tabulaire des familles et références avec colonnes :

| Famille | Référence | Prix de base | Amort. R&D | Packaging | **Prix SOP** |
|---|---|---|---|---|---|
| PF1 | PF1-1 | 10.00 | 4.50 | 0.50 | **15.00** |
| PF1 | PF1-2 | 12.00 | 4.50 | 0.50 | **17.00** |

- [ ] Saisie inline des prix (HTMX) : modifier un prix de base → le SOP se recalcule en temps réel
- [ ] Seed Flyway avec données réalistes issues des Excel existants (PF1 complet)

> **Livrable visuel** : On voit le tableau de prix d'un projet, on modifie un prix de base, le SOP se recalcule live. On peut comparer avec le fichier Excel.

#### Sprint 2 : Matrice d'application & Règle 2 (2 semaines)

Implémentation de la **Règle 2 — Matrice d'Application & Recalcul**.

- [ ] Entité `ModificationSheet` (F4) : numéro, description, statut (`Open` / `Validated` / `Canceled`)
- [ ] Entité `ModificationImpact` : impact Part Price + impact TEF (amortissement outillage) + tooling + cash
- [ ] Entité `ModificationApplication` : matrice modification × références, valeur `Y` ou `N` (ou vide)
- [ ] **Page fiche de modification** : formulaire avec la matrice interactive (checkboxes par référence)

| Fiche | Modification | Impact | PF1-1 | PF1-2 | PF1-3 | PF1-4 | PF1-5 | Statut |
|---|---|---|---|---|---|---|---|---|
| F005 | Nouvelles zones d'arrêt cuir | -1.20 | N | N | N | Y | Y | Validated |
| F012 | MAJ poids matières (PU) | 0.32 | Y | Y | Y | N | N | Validated |
| F017 | MAJ prix PF1itchs | 0.48 | | Y | | Y | | Open |

- [ ] **Moteur de recalcul** (coeur de l'application) :
  - `Nouveau base = base initial + SUMIFS(Part Price, statut="Validated", matrice="Y")`
  - `Nouvel amort = amort initial + SUMIFS(TEF, statut="Validated", matrice="Y")`
  - `Prix SOP actualisé = Nouveau base + Nouvel amort + Packaging`
- [ ] **Recalcul live** : changer le statut d'une fiche de `Open` à `Validated` → les prix se mettent à jour
- [ ] Liste des fiches de modification avec badges de statut (vert=Validated, orange=Open, gris=Canceled)
- [ ] **Colonne Écart vs LOI** sur le tableau de prix : `Prix SOP actualisé - Prix SOP initial`
- [ ] Tests unitaires du moteur de recalcul avec les données PF1 de l'Excel (résultats vérifiables)

> **Livrable visuel** : On crée une fiche F4, on coche les références impactées dans la matrice, on passe en "Validated" → les prix du tableau se mettent à jour automatiquement. Les fiches "Open" et "Canceled" n'impactent rien. C'est la démo clé du produit.

---

### MVP 1 — Moteur de Prix Complet (Sprints 3-4, 4 semaines)

> **Objectif** : Compléter le moteur de prix avec les Règles 3 et 4, les Incoterms et la projection temporelle.
> Le tableau de suivi ressemble maintenant au fichier Excel qu'il remplace.
> Implémente les **Règles 3 et 4** du moteur de pricing.

#### Sprint 3 : Productivité & projection annuelle — Règle 3 (2 semaines)

Implémentation de la **Règle 3 — Productivité Contractuelle**.

- [ ] Entité `Productivity` : taux (ex : -1%, -2%), durée (nb années), date de début
- [ ] **Moteur de productivité** : `(prix - amort - packaging) × (1 + taux) + amort + packaging`
- [ ] La productivité ne s'applique **que sur le prix pièce nu**, pas sur les rondelles ni le packaging
- [ ] **Projection annuelle** : tableau SOP, SOP+1, SOP+2... montrant les prix futurs par référence

| Année | PF1-1 | PF1-2 | PF1-3 | PF1-4 | PF1-5 |
|---|---|---|---|---|---|
| SOP (2014) | 15.004 | 17.004 | 19.004 | 23.964 | 28.964 |
| SOP+1 | 14.900 | 16.884 | 18.868 | 23.774 | 28.774 |
| SOP+2 | 14.797 | 16.765 | 18.733 | 23.585 | 28.585 |

- [ ] **Timeline mensuelle** : navigation mois par mois avec les modifications appliquées à chaque date
- [ ] Possibilité d'insérer une modification de prix à une date précise (ex : "MAJ matières en mars 2015")
- [ ] Colonne de simulation : comparaison productivité appliquée sur totalité du prix vs sur prix nu uniquement
- [ ] **Mode "What-if" (simulation)** : le KAM peut tester l'impact d'une modification avant de la valider
  - [ ] Bouton "Simuler" sur une fiche `Open` : affiche les prix recalculés en surbrillance sans toucher aux prix réels
  - [ ] Comparaison côte-à-côte : prix actuels vs prix si la fiche était validée
  - [ ] Annulation de la simulation sans aucun effet sur les données

> **Livrable visuel** : On paramètre "-1%/an pendant 4 ans", et le tableau projette les prix sur toute la durée. On voit la différence entre l'application correcte (sur prix nu) et la simulation naïve (sur tout le prix). On simule l'impact d'une fiche Open avant de la valider.

#### Sprint 4 : Tombée des rondelles, Incoterms, Price Walk — Règle 4 (2 semaines)

Implémentation de la **Règle 4 — Tombée des Rondelles** + enrichissement multi-Incoterm.

- [ ] **Tombée des rondelles R&D** : à la date d'échéance, `prix = prix courant - amortissement R&D`
- [ ] Configuration de la date de tombée par amortissement (ex : SOP+7)
- [ ] Visualisation dans la timeline : le prix chute à la date de tombée (ligne rouge sur le graphique)
- [ ] Table `Incoterm` : seed des données standards (EXW, FCA, CIP...)
- [ ] Entité `Customer` et `CustomerReference` : N références client par produit
- [ ] Surcoûts transport par destination et par Incoterm
- [ ] Calcul automatique du prix par Incoterm : `CIP = EXW + surcoût transport`
- [ ] **Colonnes Incoterm** sur le tableau de prix :

| Réf | EXW | CIP Tier1 Siemar | CIP Tier1bis Luton |
|---|---|---|---|
| PF1-1 | 14.94 | — | — |
| PF6-1 | 2.87 | 3.39 | 3.55 |

- [ ] **Price Walk** : graphique en cascade (Chart.js) du prix initial au prix courant
- [ ] **Comparaison vs LOI** : colonne écart automatique SOP actualisé vs LOI initial

> **Livrable visuel** : Le tableau complet ressemble à l'Excel. Le Price Walk montre visuellement la décomposition. La tombée des rondelles est visible dans la projection temporelle. On peut vérifier chaque valeur avec le fichier Excel original.

---

### MVP 2 — Gestion de Projet Complète (Sprints 5-6, 4 semaines)

> **Objectif** : Structurer la gestion des projets, documents, et le référentiel.
> L'application devient utilisable au quotidien par un KAM.

#### Sprint 5 : Projets, familles, workflow (2 semaines)

- [ ] CRUD complet `Project` : création, édition, liste avec filtres et recherche
- [ ] Machine à états visuelle : `RFQ → Offer → LOI → Development → SOP → Production → EoP`
- [ ] CRUD `ProductFamily` et `ProductReference` complets avec formulaires
- [ ] **Page liste projets** : tableau filtrable avec état, client, SOP date, nb références
- [ ] **Page détail projet** : vue avec onglets (Références/Prix, Modifications, Documents, Historique)
- [ ] Saisie initiale du projet : volumes prévisionnels, date SOP, sites de production

> **Livrable visuel** : Navigation complète entre la liste des projets et la vue détaillée. Workflow visuel de l'état du projet.

#### Sprint 6 : Documents, composants dirigés, multi-sites (2 semaines)

- [ ] Entité `ProjectDocument` : upload, stockage (filesystem ou S3), téléchargement
- [ ] Types de documents : RFQ, Offre, LOI, Contrat, CSR, matrice de responsabilité
- [ ] **Onglet Documents** : liste des documents attachés avec upload drag & drop
- [ ] Composants dirigés (directed components) : coût et prix de vente par composant
- [ ] Entités `Company`, `Plant` : CRUD minimal pour associer les sites de production
- [ ] **Sélection usine** sur les références (ex : "FCA Ponte de Lima - Safe Bag")
- [ ] Templates contractuels : choix standard / client / société

> **Livrable visuel** : On peut uploader des documents sur un projet, voir les composants dirigés, choisir le site de production.

---

### MVP 3 — Génération de Documents (Sprints 7-8, 4 semaines)

> **Objectif** : Générer automatiquement les documents métier (PCICN, exports Excel).
> Le KAM ne remplit plus manuellement les formulaires.

#### Sprint 7 : PCICN & Export Excel (2 semaines)

- [ ] Génération PCICN automatique (PDF via iText/JasperReports) pré-remplie avec les données du projet
- [ ] Template PCICN configurable (format standard, format client)
- [ ] Export Excel du tableau de suivi des prix (Apache POI) — format identique à l'Excel existant
- [ ] Export de la liste des fiches de modification avec statuts et impacts
- [ ] **Boutons d'export** sur les pages projet et modifications
- [ ] **Import Excel initial** : le KAM peut importer son fichier `.xlsm` existant pour bootstrapper un projet
  - [ ] Parsing Apache POI des feuilles PF1..PFn : prix de base, R&D, packaging, modifications, statuts
  - [ ] Wizard de mapping : colonnes Excel → champs applicatifs, avec prévisualisation avant import
  - [ ] Rapport d'import : lignes ignorées, erreurs de format, doublons

> **Livrable visuel** : Bouton "Générer PCICN" → PDF téléchargé. Bouton "Export Excel" → fichier identique aux anciens tableaux. Bouton "Importer depuis Excel" → les données d'un fichier existant sont chargées en quelques secondes.

#### Sprint 8 : Matières premières & DCL (2 semaines)

- [ ] Entité `RawMaterial` : suivi des cours matières premières
- [ ] Saisie manuelle des cours (LME) avec historique
- [ ] Calcul impact matière par référence et par sous-groupe
- [ ] MAJ groupée des prix matière sur les références impactées
- [ ] DCL (Dossier Conditions Logistiques) avec impact sur le packaging
- [ ] Checklist des prix actés (contrat signé, PO reçu, etc.)

> **Livrable visuel** : On met à jour un cours de matière → les prix impactés sont recalculés. Checklist visuelle des prix actés.

---

### MVP 4 — Dashboard & Visibilité (Sprints 9-10, 4 semaines)

> **Objectif** : Donner une vue d'ensemble aux managers et KAM.
> Premiers tableaux de bord et consolidation.

#### Sprint 9 : Dashboard projet, santé du compte & snapshots prix (2 semaines)

- [ ] **Page Dashboard** : vue d'ensemble par projet (marge, écart LOI, nb modifications, statut)
- [ ] Indicateurs clés : CA estimé, marge, écart budget
- [ ] Graphiques interactifs (Chart.js/ApexCharts) : évolution des prix, répartition par famille
- [ ] Consolidation multi-projets par client
- [ ] Alertes visuelles : prix NOK, PO manquants, fiches F4 en attente
- [ ] **Snapshots versionnés des prix** : avant tout envoi de PCICN, le système fige l'état complet des prix
  - [ ] Entité `PriceSnapshot` : photo datée de tous les prix d'un projet à un instant T
  - [ ] **Comparaison entre deux snapshots** : vue diff qui montre exactement ce qui a bougé entre deux envois
  - [ ] Historique des snapshots sur la page projet (timeline des PCICN envoyés)

> **Livrable visuel** : Page dashboard avec graphiques et indicateurs de santé. Vue consolidée par client. Historique visuel des snapshots de prix avec diff entre deux versions.

#### Sprint 10 : Reporting & exports avancés (2 semaines)

- [ ] Platform summary : synthèse par plateforme véhicule
- [ ] Business case dynamique mis à jour avec les données réelles
- [ ] Performance commerciale : extracts par KAM, par compte
- [ ] Exports PDF/Excel des dashboards et rapports
- [ ] Filtres et personnalisation des vues (période, client, famille, usine)

> **Livrable visuel** : Rapports exportables. Le manager peut suivre la performance de ses KAM.

---

### MVP 5 — Logistique & Packaging (Sprints 11-12, 4 semaines)

> **Objectif** : Gérer le packaging et le capacitaire usine.

#### Sprint 11 : Usines, packaging, catalogue (2 semaines)

- [ ] CRUD `Plant` et `ProductionLine` complets : lignes, capacités, contacts
- [ ] Entité `PackagingCatalog` : catalogue emballages avec prix standards
- [ ] Configurateur UC/UM (drag & drop ou sélection) pour constituer les unités de conditionnement
- [ ] Import de photos d'emballages
- [ ] Calcul automatique du coût packaging par pièce
- [ ] **Fiches usines** : vue détaillée avec lignes, capacité, contacts, packaging dispo

> **Livrable visuel** : Fiche usine complète. Configurateur packaging avec calcul de coût.

#### Sprint 12 : Capacitaire & plan de charge (2 semaines)

- [ ] Plan de charge semaine/mois par ligne de production
- [ ] Vérification capacitaire vs volumes commandés
- [ ] Alertes de dépassement de capacité
- [ ] Vue consolidée par usine
- [ ] Lien entre les références et les lignes de production

> **Livrable visuel** : Diagramme de charge par usine. Alertes rouges si dépassement capacité.

---

### MVP 6 — Prévisions & Intégrations (Sprints 13-14, 4 semaines)

> **Objectif** : Prévisions de vente, EDI, tooling.

#### Sprint 13 : Forecast, EDI, B101 (2 semaines)

- [ ] Forecast de vente par référence et par période
- [ ] Import fichiers EDI clients
- [ ] Comparaison volumes réels (MAD) vs budget
- [ ] Rappels des productivités à venir avec demande d'accord finance
- [ ] Génération automatique B101

> **Livrable visuel** : Tableau de forecast avec écarts vs budget. Alertes productivités à venir.

#### Sprint 14 : Tooling, facturation, signature (2 semaines)

- [ ] Suivi commandes tooling : PO manquant, en cours, reçu
- [ ] Trigger facturation : email automatique vers finance
- [ ] PO tracking sur les fiches de modification
- [ ] Signature électronique PDF (intégration API ou signature simple)
- [ ] Email automatique vers client avec pièces jointes (Spring Mail)

> **Livrable visuel** : Suivi tooling avec statuts visuels. Signature et envoi en un clic.

---

### MVP 7 — Offres, RFQ & Administration (Sprints 15-16, 4 semaines)

> **Objectif** : RFQ en ligne, workflow de validation, et enfin la gestion des utilisateurs.

#### Sprint 15 : RFQ & offres prototypes (2 semaines)

- [ ] Gestion des offres prototypes (quantités, prix)
- [ ] Formulaire RFQ en ligne partageable avec le client
- [ ] Calcul NPV automatique sur la durée du projet
- [ ] Workflow de validation RFQ par managers (type CAA Valeo)

> **Livrable visuel** : Formulaire RFQ interactif. Workflow d'approbation avec statuts.

#### Sprint 16 : Utilisateurs, rôles, sécurité (2 semaines)

- [ ] Spring Security : authentification (form login)
- [ ] Gestion des utilisateurs : CRUD, activation/désactivation
- [ ] Rôles et permissions : KAM, Manager, Finance, Admin
- [ ] Pages d'administration : référentiel, utilisateurs, paramétrage
- [ ] Restriction d'accès par rôle sur les pages et actions
- [ ] Audit trail : qui a modifié quoi et quand

> **Livrable visuel** : Login, gestion des utilisateurs. Chaque KAM ne voit que ses projets.

---

### MVP 8 — Intégrations Externes (Sprints 17-18, 4 semaines)

> **Objectif** : Connexion aux systèmes externes. Dernier jalon.

#### Sprint 17 : Intégration LME & Budget automatique (2 semaines)

- [ ] API d'import des cours LME automatique (ou semi-auto)
- [ ] Recalcul automatique des impacts matière à la mise à jour des cours
- [ ] Notifications aux KAM concernés lors d'une variation significative
- [ ] **Budget auto à court et moyen terme** : génération automatique du budget commercial
  - [ ] CA prévisionnel = prix × volumes × mix, calculé par référence, famille et projet
  - [ ] Projections court terme (année en cours) et moyen terme (SOP → EoP)
  - [ ] Export du budget au format Excel (compatible finance)
  - [ ] Comparaison budget vs réalisé (écarts en % et en valeur absolue)

> **Livrable visuel** : Les cours se mettent à jour automatiquement, les prix impactés sont signalés. Page budget avec CA prévisionnel généré automatiquement et écarts vs réalisé.

#### Sprint 18 : Intégration SAP & Plan de charge journalier (2 semaines)

- [ ] Étude de faisabilité SAP (API RFC/BAPI ou fichier plat)
- [ ] Prototype d'import/export de données SAP
- [ ] **Plan de charge par jour** : granularité journalière du capacitaire
  - [ ] Vue plan de charge jour/semaine/mois (toggle) par ligne de production
  - [ ] Calendrier interactif avec visualisation de la charge quotidienne
  - [ ] Alertes de dépassement de capacité à la journée
  - [ ] Lissage automatique suggéré en cas de pic journalier
- [ ] CI/CD complet (GitHub Actions) : build, tests, déploiement
- [ ] Documentation technique et utilisateur

> **Livrable visuel** : Démo d'échange de données avec SAP. Plan de charge avec vue journalière et calendrier interactif. Documentation accessible.

---

## 5. Stack Technique

| Couche | Technologie | Justification |
|---|---|---|
| **Backend** | Spring Boot 3.x, Java 21 | Écosystème mature, support LTS, records Java |
| **Architecture** | Spring Modulith | Frontières modulaires avec vérification runtime, sans complexité multi-module Maven |
| **Persistence CRUD** | Spring Data JPA / Hibernate, PostgreSQL 16 | ORM standard, JSONB pour données flexibles |
| **Persistence Pricing** | jOOQ | Requêtes SUMIFS/filtering complexes plus lisibles et performantes en SQL typé |
| **Dev local** | Docker Compose (PostgreSQL, pgAdmin) | Environnement reproductible, zéro install locale |
| **Migrations** | Flyway | Versioning du schéma, reproductibilité |
| **Frontend** | Thymeleaf + HTMX + Bootstrap 5 | Server-side rendering avec interactivité AJAX |
| **Frontend micro-interactions** | Alpine.js | Toggles, dropdowns, calculs locaux côté client — complémentaire à HTMX, zéro build |
| **Sécurité** | Spring Security | Rôles (KAM, Manager, Finance, Admin), CSRF, sessions |
| **Documents** | Apache POI (Excel import/export), iText / JasperReports (PDF) | Génération ET import Excel, PDF natifs |
| **Graphiques** | Chart.js ou ApexCharts | Graphiques interactifs (Price Walk, dashboards) |
| **Email** | Spring Mail | Notifications et envois automatiques |
| **Tests** | JUnit 5, Testcontainers, Mockito | Tests unitaires, intégration avec PostgreSQL réel |
| **Test oracle** | JUnit 5 + Apache POI | Parser le fichier Excel PF1 → vérifier que le moteur produit les mêmes valeurs |
| **Build** | Maven | Standard Spring Boot, simple et fiable |
| **CI/CD** | GitHub Actions | Intégration continue, déploiement automatisé |

### Architecture Spring Modulith

Un seul module Maven, des frontières modulaires vérifiées au runtime par Spring Modulith :

```
torchebald/
└── src/main/java/
    └── com.torchebald/
        ├── pricing/          # Moteur de pricing : règles 1-4, SUMIFS, projections
        │     └── (API publique exposée aux autres modules)
        ├── project/          # Projets, familles, références, workflow états
        ├── modification/     # Fiches F4/PCICN, matrice d'application
        ├── document/         # Génération PDF, export Excel, import Excel
        ├── dashboard/        # Reporting, snapshots, agrégats
        ├── logistics/        # Usines, packaging, capacitaire
        └── shared/           # Value objects, entités partagées, Flyway
```

> Spring Modulith vérifie au démarrage que les modules ne s'appellent pas en dehors de leurs APIs publiques — sans la complexité d'un build multi-modules Maven.

### Rôles Utilisateurs

| Rôle | Périmètre |
|---|---|
| **KAM** | Gestion de ses projets, prix, modifications, documents |
| **Manager** | Validation des RFQ, vue consolidée, reporting |
| **Finance** | Validation productivités, facturation tooling, business case |
| **Admin** | Référentiel, utilisateurs, paramétrage global |

---

## 6. Jalons & Critères de Succès

### MVP 0 — Preuve de Concept (Sprint 0-2) — Le plus important

Implémente les **Règles Métier 1 et 2**. À la fin de ce jalon, on peut démontrer :

- [ ] Tableau de prix avec décomposition (base + R&D + packaging) → **Règle 1**
- [ ] Fiche de modification F4 avec matrice d'application Y/N → **Règle 2**
- [ ] **Recalcul automatique** : seules les fiches `Validated` + `Y` impactent le prix ← coeur du produit
- [ ] Écart vs prix initial affiché sur chaque référence
- [ ] Résultats vérifiables avec le fichier Excel PF1 existant

### MVP 1 — Moteur de Prix Complet (Sprints 3-4)

Implémente les **Règles Métier 3 et 4**. Le moteur de pricing est complet :

- [ ] Productivité sur prix pièce nu uniquement (exclut amort + packaging) → **Règle 3**
- [ ] Projection annuelle (SOP, SOP+1, SOP+2...) avec timeline mensuelle
- [ ] Tombée des rondelles R&D à date d'échéance → **Règle 4**
- [ ] Prix par Incoterm (EXW, FCA, CIP) avec colonnes multi-clients
- [ ] Price Walk visuel (graphique en cascade)

### MVP 2 — Utilisable au Quotidien (Sprints 5-6)

- [ ] Navigation projet complète (liste, détail, onglets)
- [ ] Upload de documents (RFQ, LOI, contrats)
- [ ] Workflow visuel de l'état du projet

### MVP 3-8 — Enrichissements Progressifs (Sprints 7-18)

Chaque MVP ajoute une couche de valeur démontrable (documents, dashboard, logistique, intégrations) sans jamais casser ce qui fonctionne.

---

## Annexe - Estimation Globale

| MVP | Sprints | Durée | Contenu principal |
|---|---|---|---|
| MVP 0 | 0-2 | 6 sem. | Bootstrap + Tableau de prix + Fiches modif + Recalcul auto |
| MVP 1 | 3-4 | 4 sem. | Incoterms, R&D, productivités, Price Walk |
| MVP 2 | 5-6 | 4 sem. | Gestion de projet complète, documents, workflow |
| MVP 3 | 7-8 | 4 sem. | Génération PCICN, export Excel, matières premières |
| MVP 4 | 9-10 | 4 sem. | Dashboard, reporting, consolidation |
| MVP 5 | 11-12 | 4 sem. | Logistique, packaging, capacitaire |
| MVP 6 | 13-14 | 4 sem. | Forecast, EDI, tooling, signature |
| MVP 7 | 15-16 | 4 sem. | RFQ en ligne, utilisateurs, rôles, sécurité |
| MVP 8 | 17-18 | 4 sem. | Intégrations LME auto, **budget auto**, SAP, **plan de charge jour**, documentation |
| **Total** | **0-18** | **~38 sem.** | **12 modules, 9 MVPs démontrables** |

> **Note :** Sprints de 2 semaines. Les estimations seront affinées à chaque revue de MVP. L'ordre des MVPs 3+ peut être réarrangé selon le feedback utilisateur — c'est le principe.
