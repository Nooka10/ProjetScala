import React from 'react';
import {
  AsyncStorage,
  KeyboardAvoidingView,
  TextInput,
  StyleSheet,
  Button
} from 'react-native';
import Loader from '../components/Loader'

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

  
  render() {
    const { loading } = this.state;
    return (
      <KeyboardAvoidingView behavior="padding" style={{ flex: 1 }}>
        <Loader visible={loading}/>
        <TextInput
          style={styles.input}
          placeholder='Email'
          placeholderTextColor="grey"
          onChangeText={this.onChangeEmail}
          value={this.state.email}
        />

        <TextInput
          style={styles.input}
          placeholder='Mot de passe'
          placeholderTextColor="grey"
          onChangeText={this.onChangePassword}
          value={this.state.password}
          secureTextEntry
        />

        <Button title="Login" onPress={this._signInAsync} />

      </KeyboardAvoidingView>
    );
  }

  _signInAsync = async () => {
    this.setState({ loading: true });
    fetch('https://beerpass-scala.herokuapp.com/login', {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: this.state.email,
        password: this.state.password,
      }),
    }).then(result => (result.json()))
      .then(async (responseJson) => {
        if (responseJson.status === 'OK') {
          console.log(responseJson)
          await AsyncStorage.setItem('token', responseJson.token);
          await AsyncStorage.setItem('firstname', responseJson.userInfos.firstname);
          await AsyncStorage.setItem('lastname', responseJson.userInfos.lastname);
          await AsyncStorage.setItem('id', responseJson.userInfos.id.toString());
          
          const { userType } = responseJson.userInfos;
          await AsyncStorage.setItem('userType', userType);
          if (userType === 'EMPLOYEE') {
            await AsyncStorage.setItem('companyId', responseJson.userInfos.companyId.toString());
          }
          this.setState({ loading: false });
          this.props.navigation.navigate(userType === 'CLIENT' ? 'AppClient' : 'AppBarman');
        }
      })
      .catch((error) => {
        console.error(error);
        this.setState({ loading: false });

      });;
  };
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
});
