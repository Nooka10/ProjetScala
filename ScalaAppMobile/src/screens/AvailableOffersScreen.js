import React from 'react';
import {
  StyleSheet,
  View,
  AsyncStorage
} from 'react-native';
import Carousel from 'react-native-snap-carousel';
import AnimatedLoader from 'react-native-animated-loader';
import SliderEntry from '../components/SliderEntry';
import Layout from '../constants/Layout';
import FetchBackend from '../api/FetchBackend';

function renderItem({ item, index }) {
  return <SliderEntry data={item} even={(index + 1) % 2 === 0} />;
}

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

  render() {
    const { bars, loading } = this.state;
    return (
      <View style={styles.container}>

        <AnimatedLoader
          visible={loading}
          overlayColor="rgba(255,255,255,0.75)"
          source={require('../assets/loader/loader.json')}
          animationStyle={styles.lottie}
          speed={1}
        />

        {bars.length > 0 && (
          <Carousel
            layout="default"
            layoutCardOffset={18}
            data={bars}
            renderItem={renderItem}
            sliderWidth={Layout.window.width}
            itemWidth={Layout.window.width * 0.7}
          />
        )}
      </View>

    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: 100
  },
  lottie: {
    width: 200,
    height: 200
  }
});
