import React, { useState, useEffect } from 'react';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import {
  Map, TileLayer, Marker, Popup,
} from 'react-leaflet';
import L from 'leaflet';
import { Grid } from '@material-ui/core';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import Loading from '../components/Loading';
import FetchBackend from '../api/FetchBackend';

const iconUrl = require('../assets/BeerPassMarker.png');

const greenIcon = L.icon({
  iconUrl,
  iconSize: [50, 50], // size of the icon
  iconAnchor: [24, 49], // point of the icon which will correspond to marker's location
  popupAnchor: [-3, -50], // point from which the popup should open relative to the iconAnchor
});

const useStyles = makeStyles({
  root: {
    height: '100vh',
    width: '100vw',
  },
  statsNumbers: {
    margin: 0,
  },
  gridStatsNumber: {
    paddingTop: 30,
  },
  link: {
    color: 'black',
    textDecoration: 'none',
  },
});

function BarSreen() {
  const classes = useStyles();
  const [loading, setLoading] = useState(true);
  const [bars, setBars] = useState(0);

  useEffect(() => {
    const fetchBars = async () => {
      const result = await FetchBackend.fetchAllCompanies();
      if (result && result.status === 'OK') {
        setBars(result.companiesInfos);
      }
    };

    const waitingForAnimation = new Promise(((resolve) => {
      setTimeout(resolve, 2200, 'foo');
    }));

    setLoading(true);
    Promise.all([fetchBars(), waitingForAnimation])
      .then(() => {
        setLoading(false);
      });
  }, []);

  return (
    loading ? <Loading />
      : (
        <Map className={classes.root} center={[46.6333, 6.6333]} zoom={10}>
          <TileLayer
            key="tileLayer"
            attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
            url="https://maps.tilehosting.com/styles/streets/{z}/{x}/{y}.png?key=YrAASUxwnBPU963DZEig"
          />

          {bars.map((bar) => {
            const linkToBar = `/bar/${bar.id}`;
            return (
              <Marker
                position={[bar.address.lat, bar.address.lng]}
                icon={greenIcon}
              >
                <Popup>
                  <Link to={linkToBar} className={classes.link}>
                    {bar.name}
                  </Link>

                </Popup>

              </Marker>
            );
          })}

        </Map>
      )

  );
}

export default BarSreen;
