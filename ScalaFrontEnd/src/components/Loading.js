import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import { Dialog, DialogContent } from '@material-ui/core';
import logo from '../assets/loading.gif';

const styles = () => ({
  root: {
    display: 'flex',
    alignItems: 'center',
    height: '20vw',
    width: '20vw',
  },
  dialogContent: {
    padding: 0,
  },
  imgLogo: {
    height: '20vw',
    outline: 'none',
  },
});

// Permet de faire apparaitre le logo pour le loading
function CircularIndeterminate(props) {
  const { classes } = props;
  return (
    <Dialog open>
      <DialogContent className={classes.dialogContent}>
        <div className={classes.root}>
          <img src={logo} className={classes.imgLogo} alt="logo" readOnly tabIndex="-1" />
        </div>
      </DialogContent>
    </Dialog>
  );
}

CircularIndeterminate.propTypes = {
  classes: PropTypes.shape().isRequired,
};

export default withStyles(styles)(CircularIndeterminate);
