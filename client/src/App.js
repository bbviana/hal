import React, { Component } from 'react';
import {
    Table,
    TableBody,
    TableHeader,
    TableHeaderColumn,
    TableRow,
    TableRowColumn,
    TableFooter
} from 'material-ui/Table';

import ActionSearch from 'material-ui/svg-icons/action/search';

import './App.css';


class App extends Component {

    state = {
        query: "",
        tables: []
    };

    handleChange = e => {
        let query = e.target.value;

        this.setState({query: query});

        if (query.length >= 3) {
            fetch(`/tables/${query}`)
                .then(response => response.json())
                .then(result => this.setState({
                    tables: result
                }))
        }
    };

    render() {
        return (
            <div className="App">
                <div className="SearchBar">
                    <ActionSearch  />

                    <input
                        autoFocus
                        className="InputQuery"
                        name="query"
                        placeholder="Busca"
                        type="text"
                        value={this.state.query}
                        onChange={this.handleChange}
                    />
                </div>

                {this.state.tables.map((table, i) =>
                    <Table key={i}>
                        <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
                            <TableRow>
                                {table.columnsNames.map((colName, i) =>
                                    <TableHeaderColumn key={i}>{colName}</TableHeaderColumn>
                                )}
                            </TableRow>
                        </TableHeader>
                        <TableBody displayRowCheckbox={false}>
                            {table.rows.map((row, i) =>
                                <TableRow key={i}>
                                    {table.columnsNames.map((colName, i) =>
                                        <TableRowColumn key={i}>{row[colName]}</TableRowColumn>
                                    )}
                                </TableRow>
                            )}
                        </TableBody>
                        <TableFooter>
                            <TableRow>
                                <TableHeaderColumn colSpan={99}>
                                    atualizado em: {new Date(table.lastUpdated).toLocaleString("pt", "BR")}
                                </TableHeaderColumn>
                            </TableRow>
                        </TableFooter>
                    </Table>
                )}
            </div>
        );
    }
}

export default App;
