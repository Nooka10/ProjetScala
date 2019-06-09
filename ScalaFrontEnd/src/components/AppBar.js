import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import ShoppingCart from '@material-ui/icons/ShoppingCart';
import Grid from '@material-ui/core/Grid';
import logo from '../assets/BeerPassLogo2.png';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";

const useStyles = makeStyles(theme => ({
  root: {
    flexGrow: 1,
  },
  title: {
    flexGrow: 1,
  },
  logo: {
    height: 100,
    marginLeft: '10vw',
  },
  menus: {
    position: 'absolute',
    right: '10vw',
  },
  link: {
    color: 'black',
    textDecoration: 'none',
    marginRight: 20,
  },
}));

export default function AppBarHeader() {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <AppBar color="inherit">

        <Grid
          container
          direction="row"
          justify="space-between"
          alignItems="center"
        >

          <Grid item xs={6}>
            <Link to="/">
              <img src={logo} className={classes.logo} alt="Logo" />
            </Link>

          </Grid>

          <Grid item xs={6} className={classes.menus}>

            <Link to="/" className={classes.link}>
              <Button color="inherit">Accueil</Button>
            </Link>

            <Link to="/bars/" className={classes.link}>
              <Button color="inherit">Etablissements</Button>
            </Link>

            <Link to="/shop/" className={classes.link}>
              <Button color="inherit">Shop</Button>
            </Link>

            <IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="Menu" disabled>
              <ShoppingCart />
            </IconButton>


          </Grid>

        </Grid>
      </AppBar>
    </div>
  );
}