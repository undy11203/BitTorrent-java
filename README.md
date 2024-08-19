# Bittorrent client

## Описание

Программа поддерживает 8 стандартных типов сообщений

- 0 - choke
- 1 - unchoke
- 2 - interested
- 3 - not interested
- 4 - have
- 5 - bitfield
- 6 - request
- 7 - piece
- 8 - cancel

Программа может:

- Распознавать bencode и парсить torrent файл
- Подключаться к пирам и по p2p протоколу скачивать файлы
- Поддерживает отключение пиров и ограничивает одновремменное количество подключенний к пирам
- Также поддерживает сидирование, т.е. может отдавать скачанные файлы

## Инструкция сборки

```mvn compile```

## Способы использования

```mvn exec:java -Dexec.mainClass="com.shabanov.lab3.Bittorrent" -Dexec.args="args"``` \
Args = [path, ...addr]:

- если указать только path до торрент файла, будет подключаться к торрент трекеру и парсить нужные пиры с сайта
- если указать path и конкретные пиры будет подключаться только к ним

```mvn exec:java -Dexec.mainClass="com.shabanov.lab3.BittorrentSeed" -Dexec.args="args"``` \
Args = [path]:

- path до торрент файла, это код для раздачи

```mvn exec:java```

## Примеры использования

dir: pic содержит уже готовые торрент файлы
dir: input содержит уже скачанные файлы(нужны для раздачи)

Скачивание из интернета: \
```mvn exec:java -Dexec.mainClass=com.shabanov.lab3.Bittorrent -Dexec.args="path/to/warPiece.torrent"``` 

Скачиванние по определённому пиру и раздача:

```mvn exec:java -Dexec.mainClass=com.shabanov.lab3.BittorrentSeed -Dexec.args="path/to/warPiece.torrent"```\
```mvn exec:java -Dexec.mainClass=com.shabanov.lab3.Bittorrent -Dexec.args="path/to/warPiece.torrent 192.168.56.1:8080"``` \

