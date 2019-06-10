import React, { Component } from 'react';
import {
  View, Text, Image, StyleSheet, Modal, TouchableHighlight
} from 'react-native';
import { AntDesign } from '@expo/vector-icons';
import QRCodeGenerator from './QRCodeGenerator';
import Layout from '../constants/Layout';
import BarDetails from './BarDetails';

export default class SliderEntry extends Component {
  state = {
    modalVisible: false,
    QRDialogvisible: false,
  };

  setModalVisible = (e) => {
    e.preventDefault();
    this.setState({ modalVisible: true });
  }

  setModalInvisible = (e) => {
    e.preventDefault();
    this.setState({ modalVisible: false });
  }

  setQRDialogVisible = (e) => {
    e.preventDefault();
    this.setState({ QRDialogvisible: true });
  }

  setQRDialogInvisible = (e) => {
    e.preventDefault();
    this.setState({ QRDialogvisible: false });
  }

  close = (e) => {
    const { QRDialogvisible } = this.state;
    if (QRDialogvisible) { this.setQRDialogInvisible(e); } else { this.setModalInvisible(e); }
  }

  render() {
    const { modalVisible, QRDialogvisible } = this.state;
    const { data } = this.props;
    return (

      <View style={{ flex: 0.5 }}>

        <View style={{ flex: 1 }}>

          <TouchableHighlight
            onPress={this.setModalVisible}
            style={{ flex: 1, backgroundColor: '#CCCCCC' }}
          >
            <View style={{ flex: 1 }}>
              <Image
                style={{ width: '100%', height: '100%' }}
                source={{ uri: data.company.image }}
              />
              <Text style={styles.titleText}>{data.company.name}</Text>
            </View>
          </TouchableHighlight>

          <Modal
            animationType="slide"
            transparent={false}
            visible={modalVisible}
            onRequestClose={this.close}
          >

            <View>
              <TouchableHighlight
                style={{ marginTop: 12 }}
                onPress={this.close}
              >
                <View style={{ flexDirection: 'row' }}>
                  <AntDesign
                    name="close"
                    backgroundColor="transparent"
                    size={30}
                    color="black"
                  />
                  <Text style={styles.closeText}>Fermer</Text>
                </View>
              </TouchableHighlight>

              {QRDialogvisible ? (
                <View>
                  <QRCodeGenerator size={Layout.window.width * 0.9} data={data} />
                </View>
              )
                : (
                  <BarDetails data={data} setQRDialogVisible={this.setQRDialogVisible} onLoadingBegin={this.onLoadingBegin} onLoadingEnd={this.onLoadingEnd} />
                )}
            </View>
          </Modal>
        </View>
      </View>
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
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 50
  }
});
