package br.com.viana.money

import groovy.transform.ToString

@ToString(includePackage = false)
class Category {

    String name

    double budget

    double total
}
