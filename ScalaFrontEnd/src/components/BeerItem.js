import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import { Typography, Grid } from '@material-ui/core';
import PropTypes from 'prop-types';


const useStyles = makeStyles({
  root: {
    paddingTop: 10,
    paddingBottom: 10,
  },
  imageBeer: {
    height: 50,
  },
});


export default function BeerItem({ beer }) {
  const classes = useStyles();

  return (

    <Grid className={classes.root} container direction="row" alignItems="center">
      <Grid item xs={6} sm={4} md={2}>
        <img className={classes.imageBeer} src={beer.image} alt="Logo Beer" />
      </Grid>
      <Grid item xs={6} sm={4} md={3}>
        <Typography classvariant="body1" gutterBottom>
          {beer.name}
        </Typography>
      </Grid>

      <Grid item xs={6} sm={4} md={3}>
        <Typography classvariant="body1" gutterBottom>
          {beer.brand}
        </Typography>
      </Grid>

      <Grid item xs={6} sm={4} md={3}>
        <Typography classvariant="body1" gutterBottom>
          {`${beer.degreeAlcohol}%`}
        </Typography>
      </Grid>
    </Grid>
  );
}

BeerItem.propTypes = {
  beer: PropTypes.shape().isRequired,
};
