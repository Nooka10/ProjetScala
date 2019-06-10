## Instructions pour runner le Backend

1. Préparer la base de données
   1. Installer MySQl sur votre machine
   2. Créer une base de données 
   3. Exécuter le script *conf/evolution.default/beerPass.sql* dans cette nouvelle base de données. Le script créera les tables et populera la base de données avec des données d'exemple.
2. Démarrer le projet dans IntelliJ
3. Ajoutez les variables d'environnement suivantes:
   1. DB_URL: l'url de connexion à la base de données (Défaut: jdbc:mysql://localhost:3306/beerPass)
   2. DB_USER: le nom de l'utilisateur MySQL (Défaut: beerPass)
   3. DB_PWD: le mot de passe de l'utilisateur MySQL (Défaut: beerPass)
4. Run le projet
