import React from 'react';
import {
  AsyncStorage,
  KeyboardAvoidingView,
  TextInput,
  StyleSheet,
  Button,
  Image,
  Alert
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
      email: 'user1@beerpass.ch',
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
      await AsyncStorage.setItem('email', result.userInfos.email);
      await AsyncStorage.setItem('id', result.userInfos.id.toString());

      const { userType } = result.userInfos;
      await AsyncStorage.setItem('userType', userType);
      if (userType === 'EMPLOYEE') {
        await AsyncStorage.setItem('companyId', result.userInfos.companyId.toString());
      }
      this.setState({ loading: false });
      navigation.navigate(userType === 'CLIENT' ? 'AppClient' : 'AppBarman');
    } else {
      this.setState({ loading: false });
      Alert.alert(
        'Erreur',
        'Identifiants incorrects',
        [
          { text: 'OK', onPress: () => { } },
        ],
        { cancelable: false },
      );
    }
  };

  render() {
    const { loading, email, password } = this.state;
    return (
      <KeyboardAvoidingView behavior="padding" style={styles.container} enabled>
        <AnimatedLoader
          visible={loading}
          overlayColor="rgba(255,255,255,0.75)"
          source={require('../assets/loader/loader.json')}
          animationStyle={styles.lottie}
          speed={1}
        />

        <Image
          source={require('../assets/images/BeerPassLogo4.png')}
          style={styles.imageLogo}
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
  container: {
    flex: 1,
    alignItems: 'center'
  },
  imageLogo: {
    height: 100,
    width: 100,
  },
  input: {
    backgroundColor: 'rgba(254, 198, 82, 0.4)',
    width: 300,
    height: 40,
    marginHorizontal: 20,
    paddingLeft: 45,
    marginBottom: 30,
    borderRadius: 20,
  },
  lottie: {
    width: 200,
    height: 200
  }
});
