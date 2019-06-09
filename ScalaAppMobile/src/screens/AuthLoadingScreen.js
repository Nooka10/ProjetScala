import React from 'react';
import {
  AsyncStorage,
  StatusBar,
  View,
} from 'react-native';
import Loader from '../components/Loader';

export default class AuthLoadingScreen extends React.Component {
  constructor(props) {
    super(props);
    this.bootstrapAsync();
  }

  // Fetch the token from storage then navigate to our appropriate place
  bootstrapAsync = async () => {
    const { navigation } = this.props;

    const token = await AsyncStorage.getItem('token');
    const userType = await AsyncStorage.getItem('userType');

    // This will switch to the App screen or Auth screen and this loading
    // screen will be unmounted and thrown away.
    navigation.navigate(token ? (userType === 'CLIENT' ? 'AppClient' : 'AppBarman') : 'Auth');
  };

  // Render any loading content that you like here
  render() {
    return (
      <View>
        <Loader visible />
        <StatusBar barStyle="default" />
      </View>
    );
  }
}
