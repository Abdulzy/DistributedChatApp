# Distributed chat application

## Project Introduction

The type of system we built is known as a distributed chat application which allows users to communicate with other users on the platform. Users can share text messages of varying lengths to individual users or to a group of users. The User interface of the application is built using the java SWING framework which allows us to create a user-friendly and robust interface quickly. The backend of the application is implemented using multiple frameworks to ide distributed features
like time/clock synchronization, distributed database management, group communication, peer-to-peer networks, etc. Users can access the application using an executable jar file that makes it easy to share with other users.

As stated in the proposal, this project is like the “Design a P2P solution for mobile users who want to connect with their friends or avoid some people”. We also allow users to specify whom they want to communicate with by creating a channel that is not limited to a single user.

# Key Algorithms/Architecture (Design)

Below, we follow the points that we covered in the proposal and discuss how these are involved in our project.

## Time and Clocks:


Clock synchronization is needed for the client page, server, and database. The clock of different servers can be solved by a time server. The solution to synchronize the time of the server and client is sending the server time to the client at a certain time. After receiving the timestamp from the
server, the client web starts counting down with the server. After the client clock countdown, there is latency for client requests to arrive at the server, so the server cannot receive client requests when the server clock countdown ends. To fix this time delay, the client timer should be cut shorter than the server timer, the shortened time is just equal to the time required to transfer a message to the server, making the order received by the server without delay. All client requests are inserted into the queue by FIFO (First Input First Output) order.

## Distributed Mutual Exclusion:

All sharing resources should be protected by Mutual Exclusion to prevent race conditions. At the database level, there are existing locking mechanisms to handle this problem. We use SQLite as a database engine, so every time we refresh the chat page to show the latest message, an exclusive row clock will keep the data from being changed by multiple connections at the same time. This is also true when inserting/updating rows in the database. Since we don’t have complex queries but only simple insert and update, we are good with the existing mutual exclusion provided by SQLite.

## Replicated data management:

As one of the required algorithms for this project, replicated data management is implemented using SQLite database’s group replication. Three replicated SQLite servers are set using the single master-multiple slave replication model. This means that one of the servers is the master server and others are slave servers. The slave servers replicate or copy data from the master server simultaneously.

![alt text](https://github.com/Abdulzy/DistributedChatApp/blob/main/images/clientRead.png?raw=true)

The implementation behind the replication of data from master to slave uses a binary log. The source server keeps track of all the changes to its databases (updates, deletes, and so on) in its binary log. The slave servers connect to the master server and get a copy of the binary log. Then, they execute the events recorded in the log so that the replicas can synchronize their data with the master/source server. The SELECT statements are not saved in the log because they do not modify the data. 

The replication can be used as a backup solution. The replica can be used to back up only the data or the data with the replica state. The replica can be paused or shut down to produce an effective snapshot of data so that it does not require the source to be shut down. With the snapshot, data can be restored even though there is a failure of the replica.

The three replicated servers are put in one group and this group is set to the single-primary mode. This concept is the same as the above single master- multiple slave mode. There is a single primary server in the group which plays the role of the master server. To be more specific, the primary server is set to read-write mode, while all the other servers in the group are set to read-only mode. SQLite also provides a multi-primary mode, which sets multiple servers to primary servers. This project uses the single-primary mode for group replication since this mode already meets our requirements.

![alt text](https://github.com/Abdulzy/DistributedChatApp/blob/main/images/primary.png?raw=true)

## Fault-tolerance:

The group replication provides fault tolerance of the SQLite. If the existing primary leaves the group, whether voluntarily or unexpectedly, a new primary is elected automatically.

![alt text](https://github.com/Abdulzy/DistributedChatApp/blob/main/images/faultTolerance.png?raw=true)

In the automatic primary member election process, each member looks at the new view of the group, orders the potential new primary members, and chooses the member that qualifies as the most suitable. Each member makes its own decision locally, following the primary election algorithm in its SQLite Server release. Different server versions may have a different result from the new primary. Because all members must reach the same decision, members adapt their primary election algorithm if other group members are running lower SQLite Server versions, so that they have the same behavior as the member with the lowest SQLite Server version in the group.

## Group communication and PAXOS:

The reason to use group replication instead of the normal master-slave mode is that group replication is much more powerful as it supports group communication among the servers in the group. The group communication among SQLite servers is powered by the provided Group Communication System (GCS) protocols. These protocols provide a failure detection mechanism, a group membership service, and safe and completely ordered message delivery. All these properties are key to creating a system that ensures that data is consistently replicated across the group of servers. The implementation of the Paxos algorithm is the core of this technology. It acts as the group communication engine.


# Technical stack

The technical stack that was mentioned in the proposal didn’t change. We used primarily Java to implement both the back end and front end. The backend is implemented with a database system, SQLite, which handles all the data for the project. For the front end, we implemented it with Java Swing, and it is a very straightforward user interface. Users are able to log in and then create or join a room, with a room just being the pathway to connect to other users. Also, with the options for users to leave the room and/or log out from the service.

Library/dependencies:

- Sqlite: java.sql
- Socket communication:
    o Java.net.DatagramPacket
    o Java.net.MulticastSocket
- SWING: javax.swing
- RMI: java.rmi
- Java.util
- Java.text

In this project, for the communication between the client and the server, we thought of a lot of ways. Because it is a multi-client application, the technology was finally selected between RMI and Java sockets. Among them, RMI The flexibility is not high, both the client and the server must be written in Java, but it is more convenient to use. In contrast, java-sockets, although more flexible, need to specify the communication protocol between the server and the client. It is more troublesome, and after several trade-offs, RMI is finally chosen for server-client communication.

# Module disassembly

It is divided into two parts, the server and the client.

The server includes a listening thread and a thread that handles sending and receiving messages:

1. Create a listener thread to listen for client connections. Add each connected client to the maintained list and start a thread to handle sending and receiving messages for each connected client.
2. In the sending and receiving thread of each client, the message sent by each client is received and forwarded to the corresponding receiving client, so as to create a multi-person chat room.
3. Add the judgment of processing the transmitted file and distinguish whether the transmission is a text message or a file by adding a flag bit in the transmitted byte array. 

The client includes a thread for sending messages and a thread for receiving messages:

4. The message-sending thread is used to process the user's input information, determine whether the input is text information or a file, and modify the transmitted byte array flag to distinguish. Finally, the information is transmitted to the server.
5. The message-receiving thread is used to process the information sent back by the server, judge whether the input is text information or a file according to the flag bit, and deal with it accordingly. If it is text information, it will be displayed on the console, if it is a file, it will be saved in the specified directory.

# Function Display (Demo)

This app allows seamless chatting and file-sharing facilities while the end-to-end system is connected through the socket. Upon Receiving a file, the app provides the option to save it in the local drive. Also, users will be able to access the application using an executable jar file. Once started, the user can choose to communicate with an individual user or join a group "community" channel. Each entity, either individual user or group channel, is identified by the combination of IP address and port number. User starting communication will require to enter this combination, along with user credentials. Each user maintains his own local database which will be a copy of the application database. Every time the user receives/sends a message it will also log that message to its database. Whenever a user logs back into a chat, either individual or group they will be able to recreate the chat history using their local copy of chats. Each user will also maintain his own time clock that will be used to order simultaneous events.

In our project, chat applications are based on a P2P client server. Two clients are connected through their IP address that matches the server. Initially, the home screen looks like this:

// initial login

After providing a Username, and IP Address to listen and send messages to the app which will be forwarded to the chatting window. This is what it looks like when a user(John) initially logs into the app:

// chat screen

This is what it looks like when 2 users are logged in at the same time. John’s view on the left, Abdul’s view on the right app:
// 2 users login


To try it out for yourself, here is a link to the applications:

//link to both jar files

## Limitations
"community' has to be manually logged into

# Conclusions / Lessons Learned

Before doing this project, we had little experience with Java socket, RMI, and SWING. We learned the basic syntax and structure of each and mixed and matched them to run normally. We are satisfied with what we achieved here. It is not easy to organize these technologies together; every technology deserves a deep dive into it.

This app is a simplified version of a distributed chat system; we only consider simple scenarios, chatting with friends in a stable network without large files to transit. In the real world, a chat system might also have other features like support for big files, recall message notifications, etc. Each module can add additional complexity to the code. Though we pick up the relatively easy one, it acts as a great starting point for us to build a more complete and robust system in the future.


