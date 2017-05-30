package br.vom.viana.money

import groovy.transform.ToString

@ToString(includePackage = false)class Summary {
    List<Account> accounts = []

    List<Category> categories = []
}
