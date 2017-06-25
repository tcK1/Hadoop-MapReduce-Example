## Iniciar o projeto com Eclipse+Mavem

Crie um novo "run configuration" e execute ela.

Verifique se o compilador do proejto está usando java 1.7 (properties > java compiler)

Exporte o projeto para .jar (botão direito no projeto > export > runnable jar file > coloque um nome e finish). 

O arquivo .jar será criado na pasta pai do projeto.


## Como usar a imagem Docker

Instale docker seguindo [estas instruções](https://docs.docker.com/engine/installation/linux/ubuntu/).

Crie uma pasta chamada `gsod` e coloque os arquivos que quiser adicionar ao hadoop nela (recomendado, mas não obrigatório).

Para levantar o container, execute:
```
docker-compose run --service-ports --rm hadoop
```

Todo o conteúdo desta pasta é automaticamente montado na pasta `/app` dentro do container (nesse endereço, você poderá encontrar seus arquivos Java e os dados). Qualquer mudança feita nesses arquivos passa para dentro do container automaticamente (não e necessário derrubar o container e reerguê-lo).

ATENÇÃO: usando o comando acima, você sempre terá um container novo para usar. Nenhuma informação do container anterior será persistida, exceto pelo que você salvar na pasta `/app`.

Binário do hadoop está disponível em
```
/usr/local/hadoop/bin/hadoop
```
Para rodar um .jar com o hadoop, use o comando
```
/usr/local/hadoop/bin/hadoop jar <nome_do_arquivo>.jar 
```


### Links

[Maven no Eclipse](https://stackoverflow.com/questions/8620127/maven-in-eclipse-step-by-step-installation)

[MapReduce Tutorial](https://hadoop.apache.org/docs/r2.8.0/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html).
