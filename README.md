# SoundStreamer

Server that streams microphone to clients.

Server captures the microphone with [Java Sound API](https://docs.oracle.com/javase/tutorial/sound/capturing.html). Server uses [Java Spark Websockets](https://sparktutorials.github.io/2015/11/08/spark-websocket-chat.html) to stream the data to client. The client's browser uses [pcm-player](https://github.com/pkjy/pcm-player) to play [PCM](https://en.wikipedia.org/wiki/Pulse-code_modulation)-encoded audio byte arrays.

## Deploy

1. Clone repo ```git clone git@github.com:jokinen77/SoundStreamer.git```
1. Change directory ```cd SoundStreamer```
1. Build ```mvn clean install```
1. Run ```java -jar target/AudioStreamer-1.0-SNAPSHOT.jar```

When app starts, it scans for suitable audio data lines and ask you to choose one. Example:
```
Choose device to stream:
0: Primary Sound Capture Driver
1: Microphone (Logitech PRO X Wire
2: Microphone (NVIDIA Broadcast)
3: CABLE Output (VB-Audio Virtual 
Enter device > <input number here>
```
Then server starts on port *4567*, so you can connect to the server from [localhost:4567](http://localhost:4567) or use ip address on another computer.


## Stream system sound with Ubuntu

1. Create virtual microphone and speaker and remap speaker output to microphone input.
    ```
    pactl load-module module-null-sink sink_name="virtual_speaker" sink_properties=device.description="virtual_speaker"
    pactl load-module module-remap-source master="virtual_speaker.monitor" source_name="virtual_mic" source_properties=device.description="virtual_mic"
    ```
1. Select virtual devices as defaults from system settings.
1. Start the server and select default device.
