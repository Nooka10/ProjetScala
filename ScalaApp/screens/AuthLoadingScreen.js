import React from 'react';
import {
  ActivityIndicator,
  AsyncStorage,
  StatusBar,
  StyleSheet,
  View,
} from 'react-native';
import Loader from '../components/Loader';

export default class AuthLoadingScreen extends React.Component {
  constructor(props) {
    super(props);
    this._bootstrapAsync();
  }

  // Fetch the token from storage then navigate to our appropriate place
  _bootstrapAsync = async () => {
    const token = await AsyncStorage.getItem('token');

    const userType = await AsyncStorage.getItem('userType');

    // This will switch to the App screen or Auth screen and this loading
    // screen will be unmounted and thrown away.
    this.props.navigation.navigate(token ? (userType === 'CLIENT' ? 'AppClient' : 'AppBarman') : 'Auth');
  };

  // Render any loading content that you like here
  render() {
    return (
      <View>
        <Loader visible={true} />
        <StatusBar barStyle="default" />
      </View>
    );
  }
}