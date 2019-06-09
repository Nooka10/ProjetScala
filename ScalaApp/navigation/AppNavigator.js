import { createSwitchNavigator, createStackNavigator, createAppContainer } from 'react-navigation';

import SignInScreen from '../screens/SignInScreen'
import AuthLoadingScreen from '../screens/AuthLoadingScreen'
import MainTabNavigator from './MainTabNavigator'
import MainTabNavigatorBarman from './MainTabNavigatorBarman'

const AppClientStack = createAppContainer(createSwitchNavigator({
  // You could add another route here for authentication.
  // Read more at https://reactnavigation.org/docs/en/auth-flow.html
  Main: MainTabNavigator,
}));

const AppBarmanStack = createAppContainer(createSwitchNavigator({
  // You could add another route here for authentication.
  // Read more at https://reactnavigation.org/docs/en/auth-flow.html
  Main: MainTabNavigatorBarman,
}));
const AuthStack = createStackNavigator({ SignIn: SignInScreen });

export default createAppContainer(createSwitchNavigator(
  {
    AuthLoading: AuthLoadingScreen,
    AppClient: AppClientStack,
    AppBarman: AppBarmanStack,
    Auth: AuthStack,
  },
  {
    initialRouteName: 'AuthLoading',
  }
));