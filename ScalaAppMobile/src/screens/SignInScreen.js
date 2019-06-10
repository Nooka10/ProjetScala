import React from 'react';
import {
  AsyncStorage,
  KeyboardAvoidingView,
  TextInput,
  StyleSheet,
  Button
} from 'react-native';
import AnimatedLoader from 'react-native-animated-loader';
import FetchBackend from '../api/FetchBackend';

export default class SignInScreen extends React.Component {
  static navigationOptions = {
    title: 'Connexion',
  };

  constructor(props) {
    super(props);
    this.state = {
      email: 'tonio@boiiiire.ch',
      password: '1234abcd',
      loading: false,
    };
  }

  onChangeEmail = (value) => {
    this.setState({ email: value });
  }

  onChangePassword = (value) => {
    this.setState({ password: value });
  }

  signInAsync = async () => {
    const { email, password } = this.state;
    const { navigation } = this.props;
    this.setState({ loading: true });
    const result = await FetchBackend.login(email, password);
    if (result.status === 'OK') {
      await AsyncStorage.setItem('token', result.token);
      await AsyncStorage.setItem('firstname', result.userInfos.firstname);
      await AsyncStorage.setItem('lastname', result.userInfos.lastname);
      await AsyncStorage.setItem('id', result.userInfos.id.toString());

      const { userType } = result.userInfos;
      await AsyncStorage.setItem('userType', userType);
      if (userType === 'EMPLOYEE') {
        await AsyncStorage.setItem('companyId', result.userInfos.companyId.toString());
      }
      this.setState({ loading: false });
      navigation.navigate(userType === 'CLIENT' ? 'AppClient' : 'AppBarman');
    }
  };

  render() {
    const { loading, email, password } = this.state;
    return (
      <KeyboardAvoidingView behavior="padding" style={{ flex: 1 }}>
        <AnimatedLoader
          visible={loading}
          overlayColor="rgba(255,255,255,0.75)"
          source={require('../assets/loader/loader.json')}
          animationStyle={styles.lottie}
          speed={1}
        />

        <TextInput
          style={styles.input}
          placeholder="Email"
          placeholderTextColor="grey"
          onChangeText={this.onChangeEmail}
          value={email}
        />

        <TextInput
          style={styles.input}
          placeholder="Mot de passe"
          placeholderTextColor="grey"
          onChangeText={this.onChangePassword}
          value={password}
          secureTextEntry
        />

        <Button title="Login" onPress={this.signInAsync} />

      </KeyboardAvoidingView>
    );
  }
}

const styles = StyleSheet.create({
  input: {
    backgroundColor: 'rgba(255, 255, 255, 0.4)',
    width: 300,
    height: 40,
    marginHorizontal: 20,
    paddingLeft: 45,
    borderRadius: 20,
  },
  lottie: {
    width: 200,
    height: 200
  }
});
