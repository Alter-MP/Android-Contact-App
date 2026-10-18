# Android-Contact-App

# TP1 – Application de gestion de contacts

**Cours :** 420-5GM-BB – Applications mobiles, groupe 02
**Session :** Automne 2026
**Étudiant :** Duy Khoa Marcel Pham – 1345373

Application Android de gestion de contacts écrite en Kotlin avec Jetpack Compose
Elle affiche la liste des contacts et permet de créer, modifier et supprimer un contact
Les données sont enregistrées localement avec Room

## Exécution

1. Ouvrir le dossier `TP1ContactApp1345373` dans Android Studio
2. Avoir installer un émulateur **Medium Phone** : API 29 ou plus
3. Lancer l'app

## Fonctionnalités requises
**Fonctionnement du programme**
- Affichage d'une liste des contacts
- L'ajout d'un contact
- Modification d'un contact
    - Champs : prénom, nom, téléphone, courriel, adresse, âge, favori et photo
    - Champs obligatoires validés, avec un message d'erreur
- Suppression d'un contact
- Photo prise avec la caméra ou choisie dans la galerie
- Enregistrement local avec Room : les données restent après la fermeture de l'application

**Ergonomie / qualité de l’interface**
- Le même composant de formulaire sert à la création et à la modification
- Aucune perte de données pendant la rotation portrait / paysage 
- Toasts de confirmation pour l'ajout, la modification et la suppression 
- Interface en anglais et en français
- Thème MaterialTheme modifié

**Programmation**
- Gestion des états dans le viewModele pour survivre la rotation
- Séparation de code en composabes et fonctions réutilisations
    - Séparation additionnel dans des fichiers kt séparer
- Utilsation des ressources strings pour l'affichage du texte

## Fonctionnalités supplémentaires

- **Recherche** 
Trie l'affichage de la liste de contact selon élément récherché
    - Prénom, Nom, # téléphone, Email
- **Filtre des favoris** 
    - Bouton TopBar qui filtre la liste selon les favoris
- **Favori modifiable dans la liste** 
    - Bouton étoiles dans la liste qui modifie l'état favoris du contact
- **Tri alphabétique ASC / DESC** 
    - Trier la liste de contacts en ordre alphabétique
- **Index alphabétique**
    - en-têtes de lettres (#, A, B, C…), plaçant les contacts dans la catégorie associé
- **Mode sombre / clair** 
    - Bouton TopBar qui change le thème clair ou sombre
- **Disposition adaptative** 
    - Selon l'orientation de l'écran, la liste et le formulaire sont afficher dans 2 colonnes ou affichage singulier de chaque page 
- **Avatar avec initiales**
    - Affichage des initiales si aucune photo est sélection pour un contact
- **Formatage automatique du numéro de téléphone**
    - Formattage automatique du numéros de téléphone
- **Compteur et limite de caractères**
    - Limite des charactères différents pour chaque champs du formulaire
- **Clavier adapté** 
    - Clavier adapté pour chaque champs du formulaire

## Fonctionnalités requises modifiées

- **Nom de famille facultatif** 
    - Pour permettre d'avoir un contact avec seule le prénom et numéros de téléphone

- **Numéros de téléphone limité à 10**
    - Formattage automatique du numéros de téléphone, donc le formulaire est restraint à 10 chiffre pour le inputs de numéros de téléphone
