import React from 'react';
import {
  Button,
  View,
  AsyncStorage
} from 'react-native';

export default class SettingsScreen extends React.Component {
  static navigationOptions = {
    title: 'Réglages',
  };

  signOutAsync = async () => {
    const { navigation } = this.props;
    await AsyncStorage.clear();
    navigation.navigate('Auth');
  };

  render() {
    return (
      <View>
        <Button title="Se déconnecter" onPress={this.signOutAsync} />
      </View>
    );
  }
}
