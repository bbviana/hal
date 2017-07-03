# TASKS

- criar graficos pra categorias
- categoria vazia -> outros
- terminar de configurar categorias (metas e regex)
- Conta Corrente está com os sinais invertidos (debito e credito)
- mostrar seção resumida com os 3 ultimos dias
- Enum despesa, receita
- Corrigir sinais + -
- Ordenar mais recentes antes
- Múltiplas contas
    - Mudar processCategories pra accounts.txt
    - bbviana@gmail.com=Conta Corrente, Bruno Cartao
    - lody2005@gmail.com=Laudenite Cartao, Edvar Cartao
- Destacar as transações de três dias anteriores

# Auto Installer
```
# Fazer via Java:

cd ~

# if tables exists
cd tables
git reset --hard origin/master
git pull
# else
git clone ...

# Commits de Hoje
git log --after="2017-04-20 00:00" --before="2017-04-20 23:59"

# Parse dos log: split('commit')
itera
procura na msg por [release] (break quando achar)
# se achar
verifica se SHA é a versao atual

# se achou
git checkout commit
./mvnw install
Altera tables-current-version.txt com commit SHA
kill tomcat
java -jar ...

```

