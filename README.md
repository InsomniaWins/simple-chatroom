# Simple Chatroom
## COSC-4333 Distributed Systems Project - Fall 2024

### Description
Simple Chatroom consists of two modes: server and client. While running in server mode, the program will act as a server or hub for clients to connect to. While running in client mode, the program will act as a client. A client is able to connect to a server, join a chatroom, send a message to a chatroom, leave a chatroom, and leave a server.

### How to Edit and Compile
To edit or compile the program, we recommend using JetBrains IntelliJ Idea Community Edition. From the projects screen of IntelliJ, clone this repository.  For running the program, we recommend using Java 23. For artifacts/building, simply add a jar artifact using the "from modules and dependencies" option. Then build the artifact to compile the program.

### How to Run
After compiling a jar (or downloading a precompiled jar from the releases section of this repository), place the jar file into a directory. Next, download or locate Java 23 on your computer. Then run the following command(s):

<p>For running a server:</p>
<p>
    <code>[path/to/java-23] -jar [path/to/compiled/chatroom/jar/in/quotes] server [port]</code>
</p>

<p>For running a client:</p>
<p>
    <code>[path/to/java-23] -jar [path/to/compiled/chatroom/jar/in/quotes] client [server ip] [server port]</code>
</p>
