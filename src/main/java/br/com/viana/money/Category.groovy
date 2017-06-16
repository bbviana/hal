package br.com.viana.money

import groovy.transform.ToString

@ToString(includePackage = false)
class Category {

    String name

    double budget

    double total

    double getPercent() {
        if(budget == 0){
            return 0
        }

        return 100 * total / budget
    }

    boolean isOverflow(){
        return getPercent() > 100
    }
}
