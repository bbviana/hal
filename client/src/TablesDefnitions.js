import React, { Component } from 'react';
//import PropTypes from 'prop-types';
import TextField from 'material-ui/TextField';
import {
    Table,
    TableBody,
    TableHeader,
    TableHeaderColumn,
    TableRow,
    TableRowColumn
} from 'material-ui/Table';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';

import FloatingActionButton from 'material-ui/FloatingActionButton';
import ContentAdd from 'material-ui/svg-icons/content/add';
import ContentSave from 'material-ui/svg-icons/content/save';
import ActionDelete from 'material-ui/svg-icons/action/delete';

import './TablesDefnitions.css';

class TablesDefnitions extends Component {
    state = {
        tables: [
            {name: "Tabela 1", url: "http://foo", selector: ".foo"},
            {name: "Tabela 2", url: "http://foo", selector: ".foo"},
            {name: "Tabela 3", url: "http://foo", selector: ".foo"}
        ],

        open: true
    };

    handleOpen = () => {
        this.setState({open: true});
    };

    handleClose = () => {
        this.setState({open: false});
    };

    render() {
        const actions = [
            <FlatButton
                label="Cancel"
                primary={false}
                onTouchTap={this.handleClose}
            />,

            <FlatButton
                label="Remover"
                icon={<ActionDelete />}
                primary={true}
                onTouchTap={this.handleClose}
            />,

            <FlatButton
                label="Salvar"
                icon={<ContentSave />}
                primary={true}
                keyboardFocused={true}
                onTouchTap={this.handleClose}
            />
        ];

        return (
            <div>
                <Table>
                    <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
                        <TableRow>
                            <TableHeaderColumn>Nome</TableHeaderColumn>
                            <TableHeaderColumn>URL</TableHeaderColumn>
                            <TableHeaderColumn>Selector</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody displayRowCheckbox={false}>
                        {this.state.tables.map((table, i) =>
                            <TableRow key={i}>
                                <TableRowColumn>{table.name}</TableRowColumn>
                                <TableRowColumn>{table.url}</TableRowColumn>
                                <TableRowColumn>{table.selector}</TableRowColumn>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>

                <FloatingActionButton className="AddButton" onTouchTap={this.handleOpen}>
                    <ContentAdd />
                </FloatingActionButton>

                <Dialog
                    actions={actions}
                    modal={true}
                    open={this.state.open}
                    onRequestClose={this.handleClose}
                >
                    <TextField
                        hintText="Ex: Futebol - Campeões da Champions League UCL"
                        floatingLabelText="Nome"
                        fullWidth={true}
                    /><br/>

                    <TextField
                        hintText="http://wikipedia.com"
                        floatingLabelText="URL"
                        fullWidth={true}
                    /><br/>

                    <TextField
                        hintText=".foo"
                        floatingLabelText="Selector"
                        fullWidth={true}
                    /><br/>

                    <TextField
                        hintText="Ano | .foo td | integer"
                        floatingLabelText="Colunas"
                        fullWidth={true}
                        multiLine={true}
                    /><br/>
                </Dialog>
            </div>
        );
    }
}

TablesDefnitions.propTypes = {};
TablesDefnitions.defaultProps = {};

export default TablesDefnitions;
