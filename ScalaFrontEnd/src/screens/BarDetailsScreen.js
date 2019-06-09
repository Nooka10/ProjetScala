import React, { useState, useEffect } from 'react';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import {
  Map, TileLayer, Marker,
} from 'react-leaflet';
import L from 'leaflet';
import { Grid } from '@material-ui/core';
import PropTypes from 'prop-types';
import Loading from '../components/Loading';
import DaySchedule from '../components/DaySchedule';
import FetchBackend from '../api/FetchBackend';
import BeerItem from '../components/BeerItem';

const iconUrl = require('../assets/BeerPassMarker.png');

const greenIcon = L.icon({
  iconUrl,
  iconSize: [50, 50], // size of the icon
  iconAnchor: [24, 49], // point of the icon which will correspond to marker's location
  popupAnchor: [-3, -50], // point from which the popup should open relative to the iconAnchor
});

const useStyles = makeStyles({
  root: {
    paddingTop: 200,
    paddingLeft: '20vw',
    paddingRight: '20vw',
  },
  barImage: {
    width: '100%',
    height: 300,
  },
  map: {
    height: 400,
  },
  gridAddress: {
    paddingTop: 50,
  },
  beerTitle: {
    paddingTop: 40,
  },
});

export default function BarDetailsScreen({ match }) {
  const classes = useStyles();
  const [loading, setLoading] = useState(true);
  const [companyDetails, setCompanyDetails] = useState(0);
  const [companyBeers, setCompanyBeers] = useState(0);

  useEffect(() => {
    const { barId } = match.params;

    const fetchBar = async () => {
      const result = await FetchBackend.fetchCompanyDetails(barId);
      if (result) {
        setCompanyDetails(result);
      }
    };

    const fetchBeersForBar = async () => {
      const result = await FetchBackend.fetchBeersForCompany(barId);
      if (result) {
        setCompanyBeers(result);
      }
    };


    setLoading(true);
    Promise.all([fetchBar(), fetchBeersForBar()])
      .then(() => {
        setLoading(false);
      });
  }, [match]);

  return (
    loading ? <Loading />
      : (
        <div className={classes.root}>

          <img className={classes.barImage} src={companyDetails.image} alt="bar" />

          <Typography variant="h2" color="primary" gutterBottom>
            {companyDetails.name}
          </Typography>

          <Typography variant="body1" gutterBottom>
            {companyDetails.description}
          </Typography>

          <Typography className={classes.beerTitle} variant="h4" color="primary" gutterBottom>
            Bières disponibles
          </Typography>

          {companyBeers.map(beer => (<BeerItem key={beer.id + beer.name} beer={beer} />))}


          <Grid
            container
            className={classes.gridAddress}
            direction="row"
            justify="space-between"
            alignItems="flex-start"
          >

            <Grid item xs={12} md={6}>

              <Typography variant="h6" gutterBottom color="primary">
                Adresse
              </Typography>

              <Typography variant="body1" gutterBottom>
                {`${companyDetails.address.road} ${companyDetails.address.no}`}
              </Typography>
              <Typography variant="body1" gutterBottom>
                {`${companyDetails.address.postalCode} ${companyDetails.address.city}`}
              </Typography>
              <Typography variant="body1" gutterBottom>
                {`${companyDetails.address.country}`}
              </Typography>

              <Typography variant="h6" gutterBottom color="primary">
                Horaires
              </Typography>

              {companyDetails.schedules.map(schedule => (<DaySchedule key={schedule.id + schedule.day} schedule={schedule} />))}

            </Grid>

            <Grid item xs={12} md={6}>
              <Map className={classes.map} center={[companyDetails.address.lat, companyDetails.address.lng]} zoom={10}>
                <TileLayer
                  key="tileLayer"
                  attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
                  url="https://maps.tilehosting.com/styles/streets/{z}/{x}/{y}.png?key=YrAASUxwnBPU963DZEig"
                />
                <Marker
                  position={[companyDetails.address.lat, companyDetails.address.lng]}
                  icon={greenIcon}
                />
              </Map>
            </Grid>
          </Grid>

        </div>

      )

  );
}

BarDetailsScreen.propTypes = {
  match: PropTypes.shape().isRequired,
};
