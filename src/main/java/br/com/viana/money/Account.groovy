package br.com.viana.money

import groovy.transform.ToString

@ToString(includePackage = false)
class Account {
    String name

    double total

    List<Transaction> transactions = []
}
