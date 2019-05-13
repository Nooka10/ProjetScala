- # Projet Scala - BeerPass:

  Membres du groupe: Antoine Rochat et Benoît Schopfer

  

  ## Description du projet:

  Le Bier Pass vous permet d'obtenir une bière gratuite dans tous les établissements partenaires (+100 établissements) sur une année civile. Le prix du pass serait de 49 CHF.

  Une fois le Bier Pass commandé, les utilisateurs pourront consulter les bons restants sur l'application mobile dédiée. Ils pourront profiter de leurs offres grâce à un QR Code dans les différents établissements partenaires.

  Ce projet permettrait aux utilisateurs de découvrir de nouveaux établissements (bars ou brasseries) dans leurs régions et ceci à un prix attractif.

  L'attrait pour les établissements est double : Se faire connaître par de nouveaux potientiels clients et ainsi que l'interêt financier direct (il a été montré que les clients consommaient dvanatage ou passaient commande dans les différents établissements où ils ont fait valoir un bon).

  ## Technologies utilisées:

  Pour ce projet, nous utiliserons:

  - Scala pour écrire le Backend.
  - Slick pour écrire la base de données (utilisateurs, établissements, bons restants/utilisés par les utilisateurs dans tel établissement).
  - Pour le Frontend Web, nous utiliserons un framework tel que ReactJs ou VueJs qui permettra simplement de commander un Bier Pass ainsi que de voir les établissements partenaires. (A faire si le temps)
  - Nous auront aussi une application mobile écrite en React Native qui permettra :
    - Pour les utilisateurs : de consulter les bons restants et générer un QR Code pour avoir une bière dans un bar particulier.
    - Pour les établissements : de scanner les QR Code des utilisateurs afin de valider leurs commandes.