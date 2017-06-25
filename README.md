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
