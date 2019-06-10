
import React from 'react';
import QRCode from 'react-native-qrcode-svg';

import {
  StyleSheet,
  View,
} from 'react-native';

export default function QRCodeGenerator(props) {
  const { size, data } = props;
  const logoFromFile = require('../assets/images/BeerPassLogo4.png');
  const qrValue = JSON.stringify({
    company: data.company.id,
    user: data.user.id,
  });

  return (
    <View style={styles.container}>
      <QRCode
        value={qrValue}
        logo={logoFromFile}
        logoSize={50}
        size={size}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: 20,
    alignItems: 'center'
  },
});
