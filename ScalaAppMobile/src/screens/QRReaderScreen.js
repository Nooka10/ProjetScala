
import React, { Component } from 'react';
import {
  Modal, TouchableHighlight, Text, View, AsyncStorage, StyleSheet, TouchableOpacity, ScrollView, Alert, Image
} from 'react-native';
import { BarCodeScanner, Permissions, Svg } from 'expo';
import AnimatedLoader from 'react-native-animated-loader';
import { AntDesign } from '@expo/vector-icons';
import Layout from '../constants/Layout';
import FetchBackend from '../api/FetchBackend';
import Colors from '../constants/Colors';

export default class QRReader extends Component {
  static navigationOptions = {
    header: null,
  };

  state = {
    hasCameraPermission: null,
    qrCodeScanned: false,
    companyId: null,
    userId: null,
    loading: false,
    beers: []
  };

  componentDidMount() {
    this.requestCameraPermission();
    this.fetchBeers();
  }

  requestCameraPermission = async () => {
    const { status } = await Permissions.askAsync(Permissions.CAMERA);
    this.setState({
      hasCameraPermission: status === 'granted',
    });
  };

  handleBarCodeRead = (result) => {
    const { companyId } = this.state;
    const resultJson = JSON.parse(result.data);
    this.setState({
      qrCodeScanned: true,
    });
    if (resultJson.company === companyId) {
      this.setState({
        userId: resultJson.user,
      });
    } else {
      this.setState({ hasCameraPermission: false });
      Alert.alert(
        'Erreur',
        'Ce bon ne correspond pas au bar',
        [
          {
            text: 'OK',
            onPress: () => this.setState({
              qrCodeScanned: false,
              hasCameraPermission: true,
            })
          },
        ],
        { cancelable: false },
      );
    }
  };

  fetchBeers = async () => {
    const { navigation } = this.props;
    this.setState({ loading: true });
    const companyId = parseInt(await AsyncStorage.getItem('companyId'), 10);
    if (companyId) {
      this.setState({ companyId });
      const result = await FetchBackend.fetchBeersForCompany(companyId);
      this.setState({ beers: result, loading: false });
    } else {
      navigation.navigate('Auth');
    }
  }

  beerSelection = async (beerId) => {
    const { userId, companyId } = this.state;

    this.setState({ loading: true });

    const result = await FetchBackend.useOffer(userId, companyId, beerId);

    this.setState({ loading: false });

    console.log(result);
    // TODO
    if (result.status === 'Not Found or already used') {
      Alert.alert(
        'Pas valable',
        'Ce bon a déjà été utilisé ou n\'a pas été trouvé',
        [
          {
            text: 'OK',
            onPress: () => this.setState({
              qrCodeScanned: false,
              userId: null,
            })
          },
        ],
        { cancelable: false },
      );
    } else if (result.beerId === beerId && result.clientId === userId && result.companyId === companyId) {
      Alert.alert(
        'Validé',
        'Le bon a été validé',
        [
          {
            text: 'OK',
            onPress: () => this.setState({
              qrCodeScanned: false,
              userId: null,
            })
          },
        ],
        { cancelable: false },
      );
    } else {
      Alert.alert(
        'Erreur',
        'Erreur avec la base de donnée',
        [
          {
            text: 'OK',
            onPress: () => this.setState({
              qrCodeScanned: false,
              userId: null,
            })
          },
        ],
        { cancelable: false },
      );
    }
  }

  setModalVisible = (e) => {
    e.preventDefault();
    this.setState({ qrCodeScanned: true });
  }

  setModalInvisible = (e) => {
    e.preventDefault();
    this.setState({ qrCodeScanned: false });
  }

  render() {
    const {
      hasCameraPermission, qrCodeScanned, loading, beers
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
        {hasCameraPermission === null
          ? <Text>Requesting for camera permission</Text>
          : hasCameraPermission === false
            ? (
              <Text style={{ color: '#fff' }}>
                Camera permission is not granted
              </Text>
            )
            : (
              <View style={{ flex: 1 }}>
                <BarCodeScanner
                  onBarCodeRead={this.handleBarCodeRead}
                  style={{
                    height: Layout.window.height,
                    width: Layout.window.width,
                  }}
                />
                <View style={styles.scannerView}>
                  <Text style={styles.scannerText}>Scannez le QR Code</Text>
                  <Svg height={Layout.window.width * 0.8} width={Layout.window.width * 0.8}>

                    <Svg.Rect
                      x={0}
                      y={0}
                      width={Layout.window.width * 0.8}
                      height={Layout.window.width * 0.8}
                      strokeWidth={3}
                      stroke="#F4C44E"
                      fill="none"
                    />
                  </Svg>
                </View>
                <Modal
                  animationType="slide"
                  transparent={false}
                  visible={qrCodeScanned}
                  onRequestClose={this.setModalInvisible}
                >
                  <View style={{ marginTop: 12 }}>
                    <TouchableHighlight
                      onPress={this.setModalInvisible}
                    >
                      <View style={{ flexDirection: 'row' }}>
                        <AntDesign
                          name="close"
                          backgroundColor="transparent"
                          size={30}
                          color="black"
                        />
                        <Text style={styles.closeText}>Annuler</Text>
                      </View>
                    </TouchableHighlight>

                    <ScrollView>
                      <Text style={styles.beerTitle}>Sélectionnez la bière commandée</Text>

                      {beers.map(beer => (
                        <TouchableOpacity
                          key={beer.id}
                          onPress={() => this.beerSelection(beer.id)}
                        >

                          <View key={beer.id + beer.brand} style={styles.beerView}>
                            <Image
                              style={{ width: 50, height: 50 }}
                              source={{ uri: beer.image }}
                            />
                            <Text>{beer.name}</Text>
                            <Text>{beer.brand}</Text>
                            <Text>{`${beer.degreeAlcohol}%`}</Text>
                          </View>
                        </TouchableOpacity>

                      ))}
                    </ScrollView>
                  </View>
                </Modal>
              </View>
            )
        }
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#000',
  },
  bottomBar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
    padding: 15,
    flexDirection: 'row',
  },
  url: {
    flex: 1,
  },
  urlText: {
    color: '#fff',
    fontSize: 20,
  },
  cancelButton: {
    marginLeft: 10,
    alignItems: 'center',
    justifyContent: 'center',
  },
  cancelButtonText: {
    color: 'rgba(255,255,255,0.8)',
    fontSize: 18,
  },
  scannerText: {
    color: '#F4C44E',
    fontSize: 18,
    textAlign: 'center',
    fontWeight: 'bold',

  },
  scannerView: {
    flex: 1,
    position: 'absolute',
    justifyContent: 'center',
    alignItems: 'center',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
  },
  closeText: {
    fontSize: 25,
    fontWeight: 'bold',
  },
  beerView: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between'
  },
  beerTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: Colors.tintColor,
    marginTop: 20,
    marginBottom: 20,
  }
});
