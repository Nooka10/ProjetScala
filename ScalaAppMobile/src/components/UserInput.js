import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  StyleSheet, View, TextInput, Image
} from 'react-native';
import Layout from '../constants/Layout';

export default class UserInput extends Component {
  constructor(props) {
    super(props);
    this.state = {
      value: props.defaultValue !== '' ? props.defaultValue : '',
    };
    this.onChange = this.onChange.bind(this);
  }

  onChange(v) {
    const { onChange } = this.props;
    this.setState({ value: v });
    onChange(v);
  }

  render() {
    const {
      source, placeholder, secureTextEntry, autoCorrect, autoCapitalize, returnKeyType
    } = this.props;
    const { value } = this.state;

    return (
      <View style={styles.inputWrapper}>
        <Image source={source} style={styles.inlineImg} />
        <TextInput
          style={styles.input}
          placeholder={placeholder}
          secureTextEntry={secureTextEntry}
          autoCorrect={autoCorrect}
          autoCapitalize={autoCapitalize}
          returnKeyType={returnKeyType}
          placeholderTextColor="white"
          underlineColorAndroid="transparent"
          onChangeText={this.onChange}
          value={value}
        />
      </View>
    );
  }
}

UserInput.propTypes = {
  source: PropTypes.number.isRequired,
  placeholder: PropTypes.string.isRequired,
};

const DEVICE_WIDTH = Layout.window.width;
const MARGIN = 40;

const styles = StyleSheet.create({
  input: {
    backgroundColor: 'rgba(255, 255, 255, 0.4)',
    width: DEVICE_WIDTH - 40,
    height: 40,
    marginHorizontal: 20,
    paddingLeft: 45,
    borderRadius: 20,
    color: '#ffffff',
  },
  inputWrapper: {
    flex: 1,
    marginBottom: MARGIN / 2,
  },
  inlineImg: {
    position: 'absolute',
    zIndex: 99,
    width: 22,
    height: 22,
    left: 35,
    top: 9,
  },
});
