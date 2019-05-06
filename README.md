# Projet Scala:

Membres du groupe: Antoine Rochat et Benoît Schopfer



## Description du projet:

Nous souhaitons créer un petit site internet permettant hébergeant des documents pdf, tels que les très nombreux pdf que nous recevons dans le cadre de nos études à la HEIG. Le texte des documents téléchargés sur notre site sera parsé et indexé de sorte à ce que l'utilisateur puisse effectuer des recherches basées sur le contenu de ces documents.

Lors de l'ajout d'un document, l'utilisateur pourra entrer des tags qui seront enregistrés comme métadonnées dans une base de données. Ces tags permettront de filtrer les documents selon un ou plusieurs tags (par cours, par professeur, …).

L'utilisateur pourra également parcourir l'arborescence de fichiers stockés et ouvrir un document sélectionné.

## Technologies utilisées:

Pour ce projet, nous utiliserons:

- Scala pour écrire le Backend
- Slick pour écrire la base de données
- Lucène pour gérer l'indexation
  - Si le temps nous le permet, nous remplacerons Lucène par notre propre code écrit en Spark.
- Pour le Frontend, nous utiliserons un framework tel que ReactJs ou VueJs.