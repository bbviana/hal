import React, { Component } from 'react';
//import PropTypes from 'prop-types';

import Crud from '../crud'

import TextField from 'material-ui/TextField';

class TablesDefnitions extends Component {
    render() {
        return (
            <Crud
                list={[
                    {name: "name", label:"Nome"},
                    {name: "sourceURL", label:"URL"}
                ]}

                form={[
                    <TextField
                        hintText="Ex: Futebol - Campeões da Champions League UCL"
                        floatingLabelText="Nome"
                        name="name"
                    />,

                    <TextField
                        hintText="http://wikipedia.com"
                        floatingLabelText="URL"
                        name="sourceURL"
                    />,

                    <TextField
                        hintText=".foo"
                        floatingLabelText="Selector"
                    />,

                    <TextField
                        hintText="Ano | .foo td | integer"
                        floatingLabelText="Colunas"
                        multiLine={true}
                    />
                ]}
            />
        );
    }
}

TablesDefnitions.propTypes = {};
TablesDefnitions.defaultProps = {};

export default TablesDefnitions;
