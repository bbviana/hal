package br.com.viana.hal.tables.tableDef

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OrderColumn

import static javax.persistence.GenerationType.AUTO

@Entity
class TableDefinition implements Serializable {

    @Id
    @GeneratedValue(strategy = AUTO)
    Long id

    String name

    String sourceURL

    String elementsSelector

    @OrderColumn
    @ElementCollection
    List<TableColumnDefinition> columns

    boolean reverseRows

    void setColumns(TableColumnDefinition[] columns) {
        this.columns = columns?.eachWithIndex { def el, int i -> el.position = i }
    }
}
