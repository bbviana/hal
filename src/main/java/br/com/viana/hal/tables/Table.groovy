package br.com.viana.hal.tables

import groovy.transform.ToString

@ToString
class Table {

    String name

    List<String> columnsNames = []

    List<Map> rows = []

    Date lastUpdated
}
