import React from 'react';
import {
  Image,
  Platform,
  ScrollView,
  StyleSheet,
  View,
  AsyncStorage
} from 'react-native';
import Carousel, { Pagination } from 'react-native-snap-carousel';
import SliderEntry from '../components/SliderEntry'
import Layout from '../constants/Layout'


export default class UsedOffersStack extends React.Component {

  static navigationOptions = {
    title: 'Utilisés',
  };

  state = {
    bars: []
  }

  componentDidMount() {
    this.fetchDatas();
  }


  fetchDatas = async () => {
    const id = await AsyncStorage.getItem('id');

    fetch(`https://beerpass-scala.herokuapp.com/users/${id}/offers/used`)
      .then(response => response.json())
      .then(async (responseJson) => {
        this.setState({ bars: responseJson })
      })
      .catch((error) => {
        console.error(error);
      });;

  }

  _renderItem({ item, index }) {
    return <SliderEntry data={item} even={(index + 1) % 2 === 0} />;
  }


  render() {
    const { bars } = this.state;
    return (
      <View style={styles.container}>
        {bars.length > 0 &&
        (<Carousel
          layout={'default'}
          layoutCardOffset={18}
          ref={(c) => { this._carousel = c; }}
          data={bars}
          renderItem={this._renderItem}
          sliderWidth={Layout.window.width}
          itemWidth={Layout.window.width * 0.7}
        />)}
      </View>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    marginTop: 100
  },
});
