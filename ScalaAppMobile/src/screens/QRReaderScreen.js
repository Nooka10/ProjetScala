
import React, { Component } from 'react';
import { Alert, Modal, Dimensions, TouchableHighlight, Text, View, AsyncStorage, StyleSheet, TouchableOpacity } from 'react-native';
import { BarCodeScanner, Permissions, Svg } from 'expo';
import { AntDesign } from '@expo/vector-icons';
import { ScrollView } from 'react-native-gesture-handler';
import Layout from '../constants/Layout'

// TODO : charger les biere grace a userInfos.companyId puis vérifier si companie de qr code correspknd
export default class QRReader extends Component {

  constructor(props) {
    super(props);

  }
  state = {
    hasCameraPermission: null,
    barCorrespondance: false,
    qrCodeScanned: false,
    beerSelected: false,
    companyId: null,
    userId: null,
    beerId: null,
    beers: []
  };

  static navigationOptions = {
    header: null,
  };
  componentDidMount() {
    this._requestCameraPermission();
    this.fetchBeers();
  }

  _requestCameraPermission = async () => {
    const { status } = await Permissions.askAsync(Permissions.CAMERA);
    this.setState({
      hasCameraPermission: status === 'granted',
    });
  };

  handleBarCodeRead = (result) => {
    const resultJson = JSON.parse(result.data);
    if (resultJson.company === this.state.companyId) {
      this.setState({
        userId: resultJson.user,
        barCorrespondance: true
      });
    } else {
      this.setState({
        qrCodeScanned: true,
        barCorrespondance: false
      });
    }

  };

  fetchBeers = async () => {
    const companyId = await AsyncStorage.getItem('companyId');

    this.setState({ companyId })
    fetch(`https://beerpass-scala.herokuapp.com/companies/${companyId}/beers`)
      .then(response => response.json())
      .then(async (responseJson) => {
        this.setState({ beers: responseJson })
      })
      .catch((error) => {
        console.error(error);
      });;

  }

  beerSelection = id => {
    this.setState({ beerId: id, beerSelected: true })

    fetch('https://beerpass-scala.herokuapp.com/useOffer', {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        clientId: this.state.userId,
        companyId: this.state.companyId,
        beerId: id
      }),
    }).then(result => (result.json()))
      .then(async (responseJson) => {
        console.log(responseJson)
        //Object {
        // "beerId": 2,
        // "clientId": 2,
        //"companyId": 1,
        //}
      })
      .catch((error) => {
        console.error(error);
      });;

    this.setState({
      qrCodeScanned: false,
      beerSelected: false,
      companyId: null,
      userId: null,
      beerId: null,
    })

  }

  setModalVisible = e => {
    e.preventDefault();
    this.setState({ qrCodeScanned: true });
  }

  setModalInvisible = e => {
    e.preventDefault();
    this.setState({ qrCodeScanned: false });
  }

  render() {
    return (
      <View style={styles.container}>

        {this.state.hasCameraPermission === null
          ? <Text>Requesting for camera permission</Text>
          : this.state.hasCameraPermission === false
            ? <Text style={{ color: '#fff' }}>
              Camera permission is not granted
                </Text>
            :
            (
              <View style={{ flex: 1 }}>
                <BarCodeScanner
                  onBarCodeRead={this.handleBarCodeRead}
                  style={{
                    height: Dimensions.get('window').height,
                    width: Dimensions.get('window').width,
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
                      stroke="#F4C44E" // todo
                      fill="none"
                    />
                  </Svg>
                </View>
                <Modal
                  animationType="slide"
                  transparent={false}
                  visible={this.state.qrCodeScanned}
                  onRequestClose={this.setModalInvisible}
                >
                  <View style={{ marginTop: 12 }}>
                    <TouchableHighlight
                      onPress={this.setModalInvisible}>
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
                    {this.state.barCorrespondance ? (
                      this.state.beerSelected ? (

                        <View>
                          <Text>Bon validé</Text>
                        </View>
                      )
                        : (

                          <ScrollView>
                            {this.state.beers.map(beer =>
                              (
                                <TouchableOpacity
                                  key={beer.id}
                                  onPress={() => this.beerSelection(beer.id)}>

                                  <View style={{ height: 50 }}>
                                    <Text>{beer.brand}</Text>
                                  </View>

                                </TouchableOpacity>

                              ))}
                          </ScrollView>
                        )
                    ) :
                      (<Text>Le bon ne correspond pas au bar</Text>)
                    }

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
    top: 0, left: 0, right: 0, bottom: 0,
  },
  closeText: {
    fontSize: 25,
    fontWeight: 'bold',
  },
});

