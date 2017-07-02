# Instalação Hadoop, Compilando e executando o código.

## Requisitos

1. [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
2. [Hadoop](http://www.apache.org/dyn/closer.cgi/hadoop/common/)
3. ssh

   `  $ sudo apt-get install ssh`

4. rsync

   `  $ sudo apt-get install rsync`

### EV

Adicionar as seguintes linhas de código no `~/.bashrc`:
  
  ```
  export JAVA_HOME={[CAMINHO DO SEU JAVA AQUI]}
  export PATH=${JAVA_HOME}/bin:${PATH}
  export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
  ```
  
## Configurando Hadoop

Editar o arquivo `etc/hadoop/hadoop-env.sh`, adicionando a seguinte linha:

  ```
  export JAVA_HOME={[CAMINHO DO SEU JAVA AQUI]}
  ```
  
Pode parecer estranho, mas o hadoop pede o caminho do java também.

Após isso, inicie um terminal dentro da pasta onde o hadoop esta instalado e digite `bin/hadoop`, se tudo deu certo vai aparecer uma mensagem parecida com a que aparece quando digita `java` sem parâmetros.

### Configuração Pseudo-Distribuida

Edite os seguintes arquivos da seguinte maneira:

  `etc/hadoop/core-site.xml:`
  ```
  <configuration>
      <property>
          <name>fs.defaultFS</name>
          <value>hdfs://localhost:9000</value>
      </property>
  </configuration>
  ```

  `etc/hadoop/hdfs-site.xml:`
  ```
  <configuration>
      <property>
          <name>dfs.replication</name>
          <value>1</value>
      </property>
  </configuration>
  ```
  
#### Configurando o SSH

Digite `  $ ssh localhost` e teste se é possivel acessar sem usar uma chave.

Caso funcione, pule para a proxima etapa, caso não:

  ```
    $ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
    $ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
    $ chmod 0600 ~/.ssh/authorized_keys
  ```
  
## Execução

Formata o sistema de arquivos:

  ```
    $ bin/hdfs namenode -format
  ```
  
Inicia o NameNode daemon e o DataNode daemon:

  ```
    $ sbin/start-dfs.sh
  ```
  
### Mexendo no sistema de arquivos

Essa é a parte que pode confundir um pouco. Temos duas opções, ou usar a url `http://localhost:50070/` ou por linha de comando. Eu particularmente acho a linha de comando mais interessante, visto que adicionar os arquivos só da por ela.

Podemos criar pastas com o comando:

  ```
    $ bin/hdfs dfs -mkdir {[PASTA]}
  ```
  Ex.:
  ```
    $ bin/hdfs dfs -mkdir /user
    $ bin/hdfs dfs -mkdir /user/usuario
    $ bin/hdfs dfs -mkdir /user/usuario/wordcount
  ```

Para colocar arquivos dentro das pastas criadas, vamos adicionar uma pasta input onde temos os dados salvos em nossa maquina:

  ```
    $ bin/hdfs dfs -put {[CAMINHO DA PASTA INPUT COM OS ARQUIVOS]} {[CAMINHO DA PASTA QUE VC CRIOU AGORA]}
  ```
  Ex.:
  ```
    $ bin/hdfs dfs -put /home/Documents/wordcount/input /user/usuario
  ```
  
Podemos conferir o estado das pastas com os comandos:

  ```
    $ bin/hadoop fs -ls {[CAMINHO]}
    $ bin/hadoop fs -cat {[CAMINHO/ARQUIVO]}
  ```
  Ex.:
  ```
    $ bin/hadoop fs -ls /user/usuario/input
  ```
  
### Compilando, gerando o jar e rodando o programa

Agora que ja temos os dados, falta o programa.

Coloque o arquivo `.java` dentro da pasta do hadoop e execute o comando:

  ```
    $ bin/hadoop com.sun.tools.javac.Main {[ARQUIVO]}.java
    $ jar cf {[NOME DO JAR A SER CRIADO]}.jar {[BINÁRIO DA CLASSE PRINCIPAL]}*.class
  ```
  Ex.:
  ```
    $ bin/hadoop com.sun.tools.javac.Main WordCount.java
    $ jar cf wc.jar WordCount*.class
  ```
  
Esse comando vai compilar o arquivo com suas dependencias e gerar um `.jar` pronto para ser executado.

Para rodar, usamos o comando:

  ```
    $ bin/hadoop jar {[JAR QUE FOI CRIADO]} {[NOME DA CLASSE PRINCIPAL]} {[CAMINHO DA PASTA INPUT]} {[CAMINHO DA PASTA CRIADA]}/output
  ```
  Ex.:
  ```
    $ bin/hadoop jar wc.jar WordCount /user/usuario/wordcount/input /user/usuario/wordcount/output
  ```

Perceba que aponto a pasta `/output` mesmo sem ter criado ela, visto que o nosso programa cria a mesma.

Após o programa rodar, podemos conferir os resultados usando o comando:

  ```
    $ bin/hadoop fs -cat {[PASTA DE OUTPUT]}/part-r-00000
  ```
  Ex.:
  ```
    $ bin/hadoop fs -cat /user/usuario/wordcount/output/part-r-00000
  ```

Para encerrar usamos:

  ```
    $ sbin/stop-dfs.sh
  ```
  
# Executando o código criado

Os agumentos são Data de inicio, data de fim e o local do input (pasta pai de todos os arquivos dos anos).
Toda vez será deletado e criado um nova nova pasta para o output

Ex:
```
bin/hadoop jar DataWeather.jar 01/01/1920 06/06/1930 /user/root/input
```

___

Source:

https://hadoop.apache.org/docs/r2.8.0/hadoop-project-dist/hadoop-common/SingleCluster.html#Pseudo-Distributed_Operation

https://hadoop.apache.org/docs/r2.8.0/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html
