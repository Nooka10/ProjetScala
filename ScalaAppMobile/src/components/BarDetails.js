import React, { Component } from 'react';
import {
  View, Text, Image, ScrollView, StyleSheet, TouchableOpacity
} from 'react-native';
import QRCodeGenerator from './QRCodeGenerator';
import Layout from '../constants/Layout';
import DaySchedule from './DaySchedule';
import Loader from './Loader';
import { MapView } from 'expo';

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
    const { onLoadingBegin, onLoadingEnd } = this.props;

    onLoadingBegin();
    Promise.all([onLoadingBegin(), this.fetchCompanyDetails(), this.fetchBeers(), fetchMostFamousBeer(), waitingForAnimation])
      .then(() => {
        onLoadingEnd();
      });
  }

  fetchCompanyDetails = async () => {
    const { data } = this.props;

    fetch(`https://beerpass-scala.herokuapp.com/companies/${data.company.id}`)
      .then(response => response.json())
      .then(async (responseJson) => {
        this.setState({ companyDetails: responseJson })
      })
      .catch((error) => {
        console.error(error);
      });

  }

  fetchBeers = async () => {
    const { data } = this.props;

    fetch(`https://beerpass-scala.herokuapp.com/companies/${data.company.id}/beers`)
      .then(response => response.json())
      .then(async (responseJson) => {
        this.setState({ beers: responseJson })
      })
      .catch((error) => {
        console.error(error);
      });

  }





  render() {
    const { data, setQRDialogVisible } = this.props;
    const { loading, companyDetails } = this.state;
    return (


      <ScrollView style={styles.scrollviewBarDetails}>
        <View>
          <Image
            style={{ width: '100%', height: 200 }}
            source={{ uri: data.company.image }}
          />
          <Text style={styles.titleText}>{data.company.name}</Text>

          <Text>{data.company.description}</Text>

          <Text style={styles.titleText}>Bières</Text>
          {this.state.beers.map(beer => <Text key={beer.id + beer.brand}>{beer.brand}</Text>)}

          {this.state.companyDetails && (
            <View>
              <Text style={styles.titleText}>Horaires</Text>
              {this.state.companyDetails.schedules.map(schedule => {
                console.log(schedule, schedule.day)

                return (<DaySchedule key={schedule.id + schedule.day} schedule={schedule} />)
              })}

              <Text style={styles.titleText}>Adresse</Text>

              <Text >{`${companyDetails.address.road} ${companyDetails.address.no}`}</Text>
              <Text >{`${companyDetails.address.postalCode} ${companyDetails.address.city}`}</Text>
              <Text >{companyDetails.address.country}</Text>

              {console.log(this.state.companyDetails.address)}
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
            <TouchableOpacity onPress={(e) => setQRDialogVisible(e)}>
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
  }
});


