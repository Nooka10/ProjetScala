import React from 'react';
import { Platform, Text } from 'react-native';
import { createStackNavigator, createBottomTabNavigator } from 'react-navigation';

import TabBarIcon from '../components/TabBarIcon';
import QRReaderScreen from '../screens/QRReaderScreen';
import SettingsScreen from '../screens/SettingsScreen';
import Colors from '../constants/Colors'



const QRReaderStack = createStackNavigator({
  QRReader: QRReaderScreen,
});

QRReaderStack.navigationOptions = {
  tabBarLabel: ({ focused }) => (
    <Text style={{ textAlign: 'center', fontSize: 10, color: focused ? Colors.tabIconSelected : Colors.tabIconDefault }}>QR Scanner</Text>
  ),
  tabBarIcon: ({ focused }) => (
    <TabBarIcon
      focused={focused}
      name={Platform.OS === 'ios' ? 'ios-qr-scanner' : 'md-qr-scanner'}
    />
  ),
};

const SettingsStack = createStackNavigator({
  Settings: SettingsScreen,
});

SettingsStack.navigationOptions = {
  tabBarLabel: ({ focused }) => (
    <Text style={{ textAlign: 'center', fontSize: 10, color: focused ? Colors.tabIconSelected : Colors.tabIconDefault }}>Réglages</Text>
  ),
  tabBarIcon: ({ focused }) => (
    <TabBarIcon
      focused={focused}
      name={Platform.OS === 'ios' ? 'ios-options' : 'md-options'}
    />
  ),
};

export default createBottomTabNavigator({
  QRReaderStack,
  SettingsStack,
});
