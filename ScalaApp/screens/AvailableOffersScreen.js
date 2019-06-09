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

const entries = [{
  title: 'Beautiful and dramatic Antelope Canyon',
  subtitle: 'Lorem ipsum dolor sit amet et nuncat mergitur',
  illustration: 'https://i.imgur.com/UYiroysl.jpg'
},
{
  title: 'Earlier this morning, NYC',
  subtitle: 'Lorem ipsum dolor sit amet',
  illustration: 'https://i.imgur.com/UPrs1EWl.jpg'
},
{
  title: 'White Pocket Sunset',
  subtitle: 'Lorem ipsum dolor sit amet et nuncat ',
  illustration: 'https://i.imgur.com/MABUbpDl.jpg'
},
{
  title: 'Acrocorinth, Greece',
  subtitle: 'Lorem ipsum dolor sit amet et nuncat mergitur',
  illustration: 'https://i.imgur.com/KZsmUi2l.jpg'
},
{
  title: 'The lone tree, majestic landscape of New Zealand',
  subtitle: 'Lorem ipsum dolor sit amet',
  illustration: 'https://i.imgur.com/2nCt3Sbl.jpg'
},
{
  title: 'Middle Earth, Germany',
  subtitle: 'Lorem ipsum dolor sit amet',
  illustration: 'https://i.imgur.com/lceHsT6l.jpg'
}];

export default class AvailableOffersScreen extends React.Component {

  static navigationOptions = {
    title: 'Offres',
  };

  state = {
    bars: []
  }


  componentDidMount() {
    this.fetchDatas();
  }


  fetchDatas = async () => {
    const id = await AsyncStorage.getItem('id');

    fetch(`https://beerpass-scala.herokuapp.com/users/${id}/offers/unused`)
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
