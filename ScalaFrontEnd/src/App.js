import React from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles';
import AppBar from './components/AppBar';
import HomeScreen from './screens/HomeScreen';
import ShopScreen from './screens/ShopScreen';
import BarsScreen from './screens/BarsScreen';
import BarsDetailsScreen from './screens/BarDetailsScreen';

const theme = createMuiTheme({
  palette: {
    primary: {
      main: '#FEC652',
    },
    secondary: {
      main: '#FFCBA0',
    },
  },
});

function App() {
  return (
    <MuiThemeProvider theme={theme}>

      <Router>

        <AppBar />
        <Route exact path="/" component={HomeScreen} />
        <Route path="/bars" component={BarsScreen} />
        <Route path="/shop" component={ShopScreen} />
        <Route path="/bar/:barId" component={BarsDetailsScreen} />
        <div style={{ height: 100 }} />
      </Router>
    </MuiThemeProvider>
  );
}

export default App;
