package br.com.viana.hal.money

import groovy.transform.ToString

@ToString(includePackage = false)
class Account {
    String name

    double total

    SortedSet<Transaction> transactions = [] as SortedSet

    boolean processCategories = false
}
