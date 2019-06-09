import React, { Component } from 'react'
import { View, Image, Text, ActivityIndicator, Modal, StyleSheet } from 'react-native';
import Layout from '../constants/Layout';


export default class Loader extends Component {
  render() {
    const { visible } = this.props;
    return (

      visible ? (
        <View style={styles.container}>
          <View style={styles.wrapper}>
            <View style={styles.loaderImage}>
              <ActivityIndicator size="large" color="#0000ff" />
            </View>
          </View>
        </View>
      ) : (
          <View />
        )


    )
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    position:'absolute',
    width: Layout.window.width,
    height: Layout.window.height,
    top:0, 
    left:0
  },
  wrapper: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 3000,
    backgroundColor: 'rgba(0,0,0,0.6)',
  },
  loaderImage: {
    width: 90,
    height: 90,
    borderRadius: 15,
    backgroundColor: 'white',
    justifyContent: 'center',
    alignItems: 'center',

  },
  loaderContent: {
    width: 90,
    height: 90,
  }
})
