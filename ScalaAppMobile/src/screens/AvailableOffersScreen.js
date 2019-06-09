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
import SliderEntry from '../components/SliderEntry';
import Layout from '../constants/Layout';
import FetchBackend from '../api/FetchBackend';

export default class AvailableOffersScreen extends React.Component {
  static navigationOptions = {
    title: 'Offres',
  };

  constructor(props) {
    super(props);

    this.state = {
      bars: [],
      loading: false,
    };

    props.navigation.addListener(
      'didBlur',
      () => {
        this.state = {
          bars: [],
        };
      }
    );
    props.navigation.addListener(
      'didFocus',
      () => {
        this.fetchDatas();
      }
    );
  }

  fetchDatas = async () => {
    const id = await AsyncStorage.getItem('id');
    this.setState({ loading: true });

    const result = await FetchBackend.fetchUnusedPasses(id);
    this.setState({ bars: result, loading: false });
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
