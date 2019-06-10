import React from 'react';
import {
  Button,
  View,
  AsyncStorage,
  StyleSheet,
  Text,
  Image
} from 'react-native';
import AnimatedLoader from 'react-native-animated-loader';
import FetchBackend from '../api/FetchBackend';
import Colors from '../constants/Colors';

export default class SettingsScreen extends React.Component {
  static navigationOptions = {
    title: 'Réglages',
  };

  state = {
    firstname: null,
    lastname: null,
    email: null,
    companyName: null,
    loading: false
  }

  componentDidMount() {
    this.storageDatas();
  }

  storageDatas = async () => {
    this.setState({ loading: true });
    const firstname = await AsyncStorage.getItem('firstname');
    const lastname = await AsyncStorage.getItem('lastname');
    const email = await AsyncStorage.getItem('email');

    this.setState({
      firstname,
      lastname,
      email,
    });

    const companyId = await AsyncStorage.getItem('companyId');
    if (companyId) {
      const result = await FetchBackend.fetchCompanyDetails(companyId);
      this.setState({
        companyName: result.name,
        loading: false
      });
    } else {
      this.setState({ loading: false });
    }
  }

  signOutAsync = async () => {
    const { navigation } = this.props;
    await AsyncStorage.clear();
    navigation.navigate('Auth');
  };

  render() {
    const {
      loading, firstname, lastname, email, companyName
    } = this.state;
    return (
      <View style={styles.container}>
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

        {firstname && <Text style={styles.text}>{`${firstname} ${lastname}`}</Text>}
        {email && <Text style={styles.text}>{email}</Text>}
        {companyName && (
          <View style={styles.containerCompany}>
            <Text style={styles.textBold}>Etablissement</Text>
            <Text style={styles.text}>{companyName}</Text>
          </View>
        )}

        <Button title="Se déconnecter" onPress={this.signOutAsync} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  containerCompany: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  imageLogo: {
    height: 100,
    width: 100,
  },
  text: {
    fontSize: 20,
    marginBottom: 20,
  },
  textBold: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    marginTop: 50,
    color: Colors.tintColor,
  },
});
