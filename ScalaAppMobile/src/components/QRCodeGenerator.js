
import React, { Component } from 'react';
import QRCode from 'react-native-qrcode-svg';

import {
  StyleSheet,
  View,
} from 'react-native';

export default class QRCodeGenerator extends Component {

  render() {
    const { size, data } = this.props;
    let logoFromFile = require('../assets/images/BeerPassLogo4.png');
    const qrValue = JSON.stringify({
      company: data.company.id,
      user: data.user.id,
    })

    return (

      <View style={styles.container}>
        <QRCode
          value={qrValue}
          logo={logoFromFile}
          logoSize={30}
          size={size}
        />
      </View>


    );
  };
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: 20,
    alignItems: 'center'
  },
});
