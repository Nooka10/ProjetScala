import React, { Component } from 'react';
import {
  View, Text, Image, ScrollView, StyleSheet, TouchableOpacity
} from 'react-native';
import { MapView } from 'expo';
import AnimatedLoader from 'react-native-animated-loader';
import QRCodeGenerator from './QRCodeGenerator';
import Layout from '../constants/Layout';
import DaySchedule from './DaySchedule';
import FetchBackend from '../api/FetchBackend';

export default class BarDetails extends Component {
  state = {
    companyDetails: null,
    beers: [],
    loading: false,
  };

  componentDidMount() {
    this.fetchDatas();
  }

  fetchDatas = async () => {
    this.setState({ loading: true });
    Promise.all([this.fetchCompanyDetails(), this.fetchBeers()])
      .then(() => {
        this.setState({ loading: false });
      });
  }

  fetchCompanyDetails = async () => {
    const { data } = this.props;
    const result = await FetchBackend.fetchCompanyDetails(data.company.id);
    this.setState({ companyDetails: result });
  }

  fetchBeers = async () => {
    const { data } = this.props;
    const result = await FetchBackend.fetchBeersForCompany(data.company.id);
    this.setState({ beers: result });
  }

  render() {
    const { data, setQRDialogVisible } = this.props;
    const { loading, companyDetails, beers } = this.state;
    return (
      <ScrollView style={styles.scrollviewBarDetails}>
        <AnimatedLoader
          visible={loading}
          overlayColor="rgba(255,255,255,0.75)"
          source={require('../assets/loader/loader.json')}
          animationStyle={styles.lottie}
          speed={1}
        />

        <View>
          <Image
            style={{ width: '100%', height: 200 }}
            source={{ uri: data.company.image }}
          />
          <Text style={styles.titleText}>{data.company.name}</Text>
          <Text>{data.company.description}</Text>

          <Text style={styles.titleText}>Bières</Text>
          {beers.map(beer => <Text key={beer.id + beer.brand}>{beer.brand}</Text>)}

          {companyDetails && (
            <View>
              <Text style={styles.titleText}>Horaires</Text>
              {companyDetails.schedules.map(schedule => (<DaySchedule key={schedule.id + schedule.day} schedule={schedule} />))}

              <Text style={styles.titleText}>Adresse</Text>

              <Text>{`${companyDetails.address.road} ${companyDetails.address.no}`}</Text>
              <Text>{`${companyDetails.address.postalCode} ${companyDetails.address.city}`}</Text>
              <Text>{companyDetails.address.country}</Text>

              <MapView
                style={{ height: 200 }}
                provider={MapView.PROVIDER_GOOGLE}
                initialRegion={{
                  latitude: companyDetails.address.lat,
                  longitude: companyDetails.address.lng,
                  latitudeDelta: 0.0922,
                  longitudeDelta: 0.0421,
                }}
              >
                <MapView.Marker
                  coordinate={{
                    latitude: companyDetails.address.lat,
                    longitude: companyDetails.address.lng,
                  }}
                  title={data.company.name}
                />
              </MapView>

            </View>
          )}


          <View style={{ marginTop: 20, marginBottom: 50 }}>
            <TouchableOpacity onPress={e => setQRDialogVisible(e)}>
              <QRCodeGenerator size={Layout.window.width * 0.5} data={data} />
            </TouchableOpacity>
          </View>

        </View>
      </ScrollView>

    );
  }
}

const styles = StyleSheet.create({
  baseText: {
    fontFamily: 'Cochin',
  },
  titleText: {
    fontSize: 30,
    fontWeight: 'bold',
  },
  closeText: {
    fontSize: 25,
    fontWeight: 'bold',
  },
  qrCodeView: {
    flex: 1,
    overflow: 'hidden',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 50
  },
  scrollviewBarDetails: {
    marginLeft: 10,
    marginRight: 10,
  },
  lottie: {
    width: 200,
    height: 200
  }
});
