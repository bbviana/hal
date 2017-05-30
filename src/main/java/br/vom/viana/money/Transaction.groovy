package br.vom.viana.money

import groovy.transform.ToString

@ToString(includePackage = false)
class Transaction {

    Date date

    String description

    double valueReal

    double valueDolar

    String category
}
