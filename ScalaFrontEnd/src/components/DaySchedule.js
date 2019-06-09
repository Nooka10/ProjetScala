import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import { Typography, Grid, Hidden } from '@material-ui/core';
import PropTypes from 'prop-types';


const useStyles = makeStyles({
  scheduleView: {
    flexDirection: 'row',
  },
  scheduleDay: {
    width: 60,
  },
  scheduleHours: {
    marginLeft: 20,
  },
});

const dayTranslation = {
  MONDAY: 'Lundi',
  TUESDAY: 'Mardi',
  WEDNESDAY: 'Mercredi',
  THURSDAY: 'Jeudi',
  FRIDAY: 'Vendredi',
  SATURDAY: 'Samedi',
  SUNDAY: 'Dimanche',
};

export default function DaySchedule({ schedule }) {
  const classes = useStyles();

  return (

    <Grid container direction="row" alignItems="center">
      <Grid item xs={4} md={4}>
        <Typography className={classes.scheduleDay} classvariant="body1" gutterBottom>
          {dayTranslation[schedule.day]}
        </Typography>
      </Grid>

      {schedule.hCloseAM ? (

        <>
          <Grid item xs={8} md={4}>
            <Typography classvariant="body1" gutterBottom>
              {`${schedule.hOpenAM} - ${schedule.hCloseAM}`}
            </Typography>
          </Grid>

          <Hidden mdUp>
            <Grid item xs={4} />
          </Hidden>

          <Grid item xs={8} md={4}>
            <Typography classvariant="body1" gutterBottom>
              {`${schedule.hOpenAM} - ${schedule.hCloseAM}`}
            </Typography>
          </Grid>
        </>
      )
        : (
          <Grid item xs={8} md={8}>
            <Typography classvariant="body1" gutterBottom>
              {`${schedule.hOpenAM} - ${schedule.hClosePM}`}
            </Typography>
          </Grid>
        )}

    </Grid>

  );
}

DaySchedule.propTypes = {
  schedule: PropTypes.shape().isRequired,
};
