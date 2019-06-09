import React from 'react';
import { Platform, Text } from 'react-native';
import { createStackNavigator, createBottomTabNavigator } from 'react-navigation';

import TabBarIcon from '../components/TabBarIcon';
import AvailableOffersScreen from '../screens/AvailableOffersScreen';
import UsedOffersScreen from '../screens/UsedOffersScreen';
import SettingsScreen from '../screens/SettingsScreen';
import Colors from '../constants/Colors'

 

const AvailableOffersStack = createStackNavigator({
  AvailableOffers: AvailableOffersScreen,
});

AvailableOffersStack.navigationOptions = {
  tabBarLabel: ({ focused }) => (
    <Text style={{ textAlign: 'center', fontSize: 10, color: focused ? Colors.tabIconSelected : Colors.tabIconDefault }}>Offres</Text>
  ),
  tabBarIcon: ({ focused }) => (
    <TabBarIcon
      focused={focused}
      name={
        Platform.OS === 'ios'
          ? 'ios-beer'
          : 'md-beer'
      }
    />
  ),
};

const UsedOffersStack = createStackNavigator({
  UsedOffers: UsedOffersScreen,
});

UsedOffersStack.navigationOptions = {
  tabBarLabel: ({ focused }) => (
    <Text style={{ textAlign: 'center', fontSize: 10, color: focused ? Colors.tabIconSelected : Colors.tabIconDefault }}>Utilisés</Text>
  ),
  tabBarIcon: ({ focused }) => (
    <TabBarIcon
      focused={focused}
      name={
        Platform.OS === 'ios'
          ? 'ios-close-circle-outline' 
          : 'md-close-circle-outline'
      }
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
  AvailableOffersStack,
  UsedOffersStack,
  SettingsStack,
});
