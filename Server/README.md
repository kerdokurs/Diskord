# Diskord Server

## Server

Server is an abstract class that has the boilerplate to receive and send data over socket channels.
Each implementation must have its own logic for handling payloads and must keep track of all the connected clients.

## MainServer

MainServer is responsible for handling main server tasks, for example creating and starting chat rooms.