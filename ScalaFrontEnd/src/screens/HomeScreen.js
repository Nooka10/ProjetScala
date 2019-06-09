import React, { useState, useEffect } from 'react';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import {
  Grid, Paper, Card, CardActionArea, CardContent,
} from '@material-ui/core';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import Loading from '../components/Loading';
import FetchBackend from '../api/FetchBackend';
import HomePageImage from '../assets/HomePageImage.jpg'; // Tell Webpack this JS file uses this image
import BeerItem from '../components/BeerItem';

const useStyles = makeStyles({
  homePageImage: {
    paddingTop: 105,
    height: '30vw',
    width: '100vw',
  },
  root: {
    paddingTop: 50,
    paddingLeft: '20vw',
    paddingRight: '20vw',
  },
  statsNumbers: {
    margin: 0,
  },
  gridStatsNumber: {
    paddingTop: 30,
  },
  mostFamousBeerPaper: {
    marginTop: 30,
    padding: 10,
  },
  descriptionText: {
    paddingTop: 20,
  },
  mostFamousCompanyCard: {
    marginTop: 30,
  },
  imageCompany: {
    height: 300,
  },
  footer: {
    height: 100,
  },
  link: {
    color: 'black',
    textDecoration: 'none',
  },
});

function HomeScreen() {
  const classes = useStyles();
  const [loading, setLoading] = useState(true);
  const [nbBars, setNbBars] = useState(0);
  const [nbBeers, setNbBeers] = useState(0);
  const [mostPopularBar, setmostPopularBar] = useState(null);
  const [mostFamousBeer, setmostFamousBeer] = useState(null);

  useEffect(() => {
    const fetchBars = async () => {
      const result = await FetchBackend.fetchAllCompanies();
      if (result && result.status === 'OK') {
        setNbBars(result.companiesInfos.length);
      }
    };

    const fetchBeers = async () => {
      const result = await FetchBackend.fetchAllBeers();
      if (result) {
        setNbBeers(result.length);
      }
    };


    const fetchMostPopularBar = async () => {
      const result = await FetchBackend.fetchMostPopularCompany();
      if (result) {
        setmostPopularBar(result);
      }
    };

    const fetchMostFamousBeer = async () => {
      const result = await FetchBackend.fetchMostFamousBeer();
      if (result) {
        setmostFamousBeer(result);
      }
    };

    const waitingForAnimation = new Promise(((resolve) => {
      setTimeout(resolve, 2200, 'foo');
    }));

    setLoading(true);
    Promise.all([fetchBars(), fetchBeers(), fetchMostPopularBar(), fetchMostFamousBeer(), waitingForAnimation])
      .then(() => {
        setLoading(false);
      });
  }, []);

  let linkToBar;
  if (mostPopularBar) {
    linkToBar = `/bar/${mostPopularBar.mostFamousCompany.id}`;
  }

  return (
    loading ? <Loading />
      : (
        <>
          <img className={classes.homePageImage} src={HomePageImage} alt="Home Page" />

          <div className={classes.root}>

            <Typography variant="h2" color="primary" align="center" gutterBottom>
              Beer Pass Vaud 2019
            </Typography>

            <Typography className={classes.descriptionText} variant="h6" align="center" gutterBottom>
              Le Beer Pass c’est ce fameux sésame qui vous donne accès tout au long de l’année à une bière gratuite dans tous nos établissements partenaires.
            </Typography>


            <Typography className={classes.descriptionText} variant="h6" align="center" gutterBottom>
              {'C\'est l\'occasion de découvrir de nouveaux bars et brasseries dans votre région ainsi que de déguster de nouvelles bières.'}
            </Typography>


            <Grid
              container
              className={classes.gridStatsNumber}
              direction="row"
              justify="space-between"
              alignItems="center"
            >
              <Grid item xs={12} sm={6}>
                <Typography className={classes.statsNumbers} variant="h1" align="center" color="secondary" gutterBottom>
                  {nbBars}
                </Typography>

                <Typography variant="h6" align="center" gutterBottom>
                  Etablissements partenaires
                </Typography>
              </Grid>

              <Grid item xs={12} sm={6}>
                <Typography className={classes.statsNumbers} variant="h1" align="center" color="secondary" gutterBottom>
                  {nbBeers}
                </Typography>

                <Typography variant="h6" align="center" gutterBottom>
                  Bières à tester
                </Typography>
              </Grid>

            </Grid>

            <Paper className={classes.mostFamousBeerPaper}>
              <Typography variant="h5" align="center" color="primary" gutterBottom>
                Bière la plus commandée
              </Typography>
              <BeerItem beer={mostFamousBeer.mostFamousBeer} />
              <Typography variant="h6" align="center" color="secondary" gutterBottom>
                {`Nombre de commandes ${mostFamousBeer.nbClients}`}
              </Typography>
            </Paper>

            <Link to={linkToBar} className={classes.link}>
              <Card className={classes.mostFamousCompanyCard}>
                <CardActionArea>
                  <CardContent>
                    <Typography variant="h5" align="center" color="primary" gutterBottom>
                      Etablissement le plus populaire
                    </Typography>
                    <Grid
                      container
                      className={classes.gridStatsNumber}
                      direction="row"
                      justify="space-between"
                      alignItems="center"
                    >
                      <Grid item xs={12} sm={6}>
                        <Typography variant="h4" align="center" color="primary" gutterBottom>
                          {mostPopularBar.mostFamousCompany.name}
                        </Typography>
                        <Typography align="center" gutterBottom>
                          {mostPopularBar.mostFamousCompany.description}
                        </Typography>
                        <Typography variant="h6" align="center" color="secondary" gutterBottom>
                          {`Nombre de visites : ${mostPopularBar.nbClients}`}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={6}>
                        <img className={classes.imageCompany} src={mostPopularBar.mostFamousCompany.image} alt="Logo Beer" />
                      </Grid>
                    </Grid>
                  </CardContent>
                </CardActionArea>
              </Card>
            </Link>
          </div>
        </>
      )

  );
}

export default HomeScreen;
