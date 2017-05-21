import React, { Component, cloneElement } from 'react';
//import PropTypes from 'prop-types';
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

import './Crud.css';


class Crud extends Component {
    state = {
        list: [],
        open: false,
        form: {
            name: "",
            sourceURL: "",
            elementsSelector: "",
            columns: []
        }
    };

    componentDidMount() {
        fetch('/api/table-defs/')
            .then(response => response.json())
            .then(result => this.setState({
                open: false,
                list: result._embedded["table-defs"]
            }))
    }

    handleOpen = () => {
        this.setState({
            open: true,
            form: {
                name: "",
                sourceURL: "",
                elementsSelector: "",
                columns: []
            }
        });
    };

    handleClose = () => {
        this.setState({open: false});
    };

    handleRowSelection = (selectedRows) => {
        this.setState({
            open: true,
            form: this.state.list[selectedRows[0]]
        });
    };

    handleCreate = () => {
        fetch('/api/table-defs/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(this.state.form)
        })
            .then(() => this.componentDidMount())
    };

    handleSave = () => {
        fetch(this.state.form._links.self.href, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(this.state.form)
        })
            .then(() => this.componentDidMount())
    };

    handleRemove = () => {
        fetch(this.state.form._links.self.href, {
            method: 'DELETE'
        })
            .then(() => this.componentDidMount())
    };

    handleChange = ({target}) => {
        this.setState({
            form: Object.assign({}, this.state.form, {
                [target.name]: target.value
            })
        });
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
                onTouchTap={this.handleRemove}
            />,

            <FlatButton
                label="Salvar"
                icon={<ContentSave />}
                primary={true}
                keyboardFocused={true}
                onTouchTap={this.state.form._links ? this.handleSave : this.handleCreate}
            />
        ];

        return (
            <div>
                <Table onRowSelection={this.handleRowSelection}>
                    <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
                        <TableRow>
                            {this.props.list.map(prop =>
                                <TableHeaderColumn key={prop.name}>
                                    {prop.label}
                                </TableHeaderColumn>
                            )}
                        </TableRow>
                    </TableHeader>
                    <TableBody displayRowCheckbox={false}>
                        {this.state.list.map((item, i) =>
                            <TableRow key={i} className="TableRow">
                                {this.props.list.map(prop =>
                                    <TableRowColumn key={prop.name}>
                                        {item[prop.name]}
                                    </TableRowColumn>
                                )}
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

                    {this.props.form.map((child, i) => {
                        return cloneElement(child, {
                            key: i,
                            fullWidth: true,
                            value: this.state.form[child.props.name],
                            onChange: this.handleChange
                        })
                    })}
                </Dialog>
            </div>
        );
    }
}

Crud.propTypes = {};
Crud.defaultProps = {};

export default Crud;
