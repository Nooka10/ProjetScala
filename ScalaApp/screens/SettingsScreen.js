import React from 'react';
import {
  Image,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  Button,
  View,
  AsyncStorage
} from 'react-native';
import { WebBrowser } from 'expo';

export default class SettingsScreen extends React.Component {
  static navigationOptions = {
    title: 'Réglages',
  };

  constructor(props) {
    super(props);
    this.state = {
      mostPopularCompany: null,
      mostFamousBeer: null,
    }

    this.mostPopularCompany()
    this.getMostFamousBeer()
  }





  mostPopularCompany = async () => {

    fetch(`https://beerpass-scala.herokuapp.com/stats/mostPopularCompany`)
      .then(response => response.json())
      .then(async (responseJson) => {
        this.setState({ mostPopularCompany: responseJson })
      })
      .catch((error) => {
        console.error(error);
      });

  }

  getMostFamousBeer = async () => {

    fetch(`https://beerpass-scala.herokuapp.com/stats/getMostFamousBeer`)
      .then(response => response.json())
      .then(async (responseJson) => {
        this.setState({ mostFamousBeer: responseJson })
      })
      .catch((error) => {
        console.error(error);
      });

  }


  _signOutAsync = async () => {
    await AsyncStorage.clear();
    this.props.navigation.navigate('Auth');
  };


  render() {
    const { mostFamousBeer, mostPopularCompany } = this.state;
    return (
      <View>
        {mostPopularCompany &&
          (<View>
            <Text>{mostPopularCompany.mostFamousCompany.name}</Text>
            <Text>{mostPopularCompany.nbClients}</Text>
          </View>
          )
        }
        {mostFamousBeer &&
          (<View>
            <Text>{mostFamousBeer.mostFamousBeer.brand}</Text>
            <Text>{mostFamousBeer.nbClients}</Text>
          </View>)
        }
        <Button title="Se déconnecter" onPress={this._signOutAsync} />
      </View>
    );
  }
}
