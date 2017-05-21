import React from 'react';
import ReactDOM from 'react-dom';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import Tables from './table/Tables';
import TableDefinitions from './tableDef/TablesDefnitions';

import './index.css';

import injectTapEventPlugin from 'react-tap-event-plugin';

// Needed for onTouchTap
// http://stackoverflow.com/a/34015469/988941
injectTapEventPlugin();

let component;
let path = window.location.pathname;

if(path === "/"){
    component = <Tables />
}

if(path === "/definitions" || path === "/definitions/"){
    component = <TableDefinitions />
}

ReactDOM.render(
    <MuiThemeProvider>
        {component}
    </MuiThemeProvider>,
    document.getElementById('root')
);
