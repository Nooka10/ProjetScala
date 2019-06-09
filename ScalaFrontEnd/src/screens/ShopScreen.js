import React, { useState } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import {
  Grid, Typography, Button, TextField,
} from '@material-ui/core';
import pass from '../assets/BeerPassLogo3.png'; // Tell Webpack this JS file uses this image

const useStyles = makeStyles({
  root: {
    paddingTop: 200,
    paddingLeft: '20vw',
    paddingRight: '20vw',
  },
  statsNumbers: {
    margin: 0,
  },
  passImage: {
    height: 300,
    width: '90%',
  },
  paddingTop: {
    paddingTop: 20,
  },
});

function ShopScreen() {
  const classes = useStyles();
  const [nbPasses, setnbPasses] = useState(1);

  return (
    <div className={classes.root}>


      <Grid
        container
        className={classes.gridStatsNumber}
        direction="row"
        justify="space-between"
        alignItems="flex-start"
      >

        <Grid item xs={12} lg={6}>
          <img className={classes.passImage} src={pass} alt="Pass" />
        </Grid>

        <Grid item xs={12} lg={6}>
          <>
            <Typography variant="h3" color="primary" gutterBottom>
              Beer Pass Vaud 2019
            </Typography>

            <Typography className={classes.paddingTop} variant="h5" gutterBottom>
              Bientôt disponible
            </Typography>
            <Typography className={classes.paddingTop} variant="h4" color="secondary" gutterBottom>
              CHF49.00
            </Typography>

            <Typography variant="h6" gutterBottom>
              {`${nbPasses}x Beer Pass 2019`}
            </Typography>


            <Grid
              container
              direction="row"
              justify="flex-start"
              alignItems="center"
            >

              <Grid item xs={4}>
                <TextField
                  id="outlined-number"
                  value={nbPasses}
                  onChange={(event) => {
                    const { value } = event.target;
                    if (value > 0) {
                      setnbPasses(value);
                    }
                  }}
                  type="number"
                  className={classes.textField}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  margin="normal"
                  variant="outlined"
                />
              </Grid>
              <Grid item xs={2} />
              <Grid item xs={6}>
                <Button variant="outlined" disabled className={classes.button} size="large">
                  Ajouter au panier
                </Button>
              </Grid>


            </Grid>
          </>
        </Grid>
      </Grid>


    </div>

  );
}

export default ShopScreen;
