package br.com.viana.money

import groovy.transform.ToString

@ToString(includePackage = false)
class Transaction implements Comparable<Transaction> {

    Date date

    String description

    double valueReal

    double valueDolar

    String category

    @Override
    int compareTo(Transaction o) {
        return (-1) * date.compareTo(o.date)
    }
}
