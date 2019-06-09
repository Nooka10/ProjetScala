import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Dimensions from 'Dimensions';
import { StyleSheet, View, TextInput, Image } from 'react-native';

export default class UserInput extends Component {
  constructor(props) {
    super(props);
    this.state = {
      value: this.props.defaultValue !== '' ? this.props.defaultValue : '',
    };
    this.onChange = this.onChange.bind(this);
  }

  onChange(v) {
    this.setState({ value: v });
    this.props.onChange(v);
  }

  render() {
    return (
      <View style={styles.inputWrapper}>
        <Image source={this.props.source} style={styles.inlineImg} />
        <TextInput
          style={styles.input}
          placeholder={this.props.placeholder}
          secureTextEntry={this.props.secureTextEntry}
          autoCorrect={this.props.autoCorrect}
          autoCapitalize={this.props.autoCapitalize}
          returnKeyType={this.props.returnKeyType}
          placeholderTextColor="white"
          underlineColorAndroid="transparent"
          onChangeText={this.onChange}
          value={this.state.value}
        />
      </View>
    )
  }
}

UserInput.propTypes = {
  source: PropTypes.number.isRequired,
  placeholder: PropTypes.string.isRequired,
  secureTextEntry: PropTypes.bool,
  autoCorrect: PropTypes.bool,
  autoCapitalize: PropTypes.string,
  returnKeyType: PropTypes.string,
  defaultValue: PropTypes.string,
  onChange: PropTypes.func,
};

const DEVICE_WIDTH = Dimensions.get('window').width;
const MARGIN = 40;

const styles = StyleSheet.create({
  input: {
    backgroundColor: 'rgba(255, 255, 255, 0.4)',
    width: DEVICE_WIDTH - 40,
    height: 40,
    marginHorizontal: 20,
    paddingLeft: 45,
    borderRadius: 20,
    color: '#ffffff',
  },
  inputWrapper: {
    flex: 1,
    marginBottom: MARGIN/2,
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