-----------------------------------------------------------------------------------
Project: OpenDMTP Reference Implementation - Java server
URL    : http://www.opendmtp.org
File   : README.txt
-----------------------------------------------------------------------------------

This is version v1.3.2 release of the OpenDMTP Java server protocol reference 
implementation.  This release contains the following minor changes:
- The utilities library has been updated.
- See 'CHANGELOG.txt' for details.

README Contents:
   1) Introduction
   2) Supported Platforms
   3) Quick-Start Build/Installation
   4) Source Package Layout
   5) Future OpenDMTP Server Reference Implementation Features
   6) Contact Info

-----------------------------------------------------------------------------------
1) Introduction:

The "Open Device Monitoring and Tracking Protocol", otherwise known as OpenDMTP(tm), 
is a protocol and framework that allows bi-directional data communications between 
servers and client devices over the Internet and similar networks.  OpenDMTP is 
particularly geared towards location-based information, such as GPS, as well as 
other types of data collected in remote-monitoring devices. OpenDMTP is small, and 
is especially suited for micro-devices such as PDA's, mobile phones, and custom OEM
devices.

This server reference implementation is intended to be used as a developer kit for 
creating customized feature-rich back-end services for the OpenDMTP device clients.
This OpenDMTP server reference implementation is divided into 2 parts: the OpenDMTP
protocol support, and the data store.  

The OpenDMTP protocol support is a full featured reference implementation and 
includes the following features:
  - Supports both TCP (Duplex) and UDP (Simplex) connections from the client.
  - Full support of client custom event packet negotiation.
  - Supports multiple accounts and devices.
  - Supports connection accounting.
  - Support for Flat-file datastore.

This reference implementaion supports a simple flat-file CSV format data store,
which is very simple to set up, but does not take advantage of all the features the 
protocol can provide.  If a client device attaches to this server to send data, the 
data store simply accepts the events and stores them in a file named after the 
incoming account and device (received events will be appended to a file named after 
the owning account and device in the format "<Account>_<Device>.csv").  This 
simplistic flat-file data store imposes the following restrictions:
   - Limited support of client custom event packet negotiation (all devices will
     utilize the same PacketTemplate cache, and the cache is reset each time the
     server is restarted).
   - Does not provide connection accounting information.
   - Does not provide a scalable solution and should not be used to collect data 
     for more than just a few devices.
(A full-featured MySQL data store which takes full advantage of all the features of 
the protocol can be found in the project "OpenGTS".)

Once the data is stored in the file, it is then up to the application developer to 
create a means of presenting the data to a user, such as through a web interface 
with various types of reports, and a map.

Documentation is included with this release for setting up a very simple static web 
page using Google Maps to provide web-based display and mapping of received data.  
For more information see the file "webserve/WEBSERVE.txt".

-----------------------------------------------------------------------------------
2) Supported Platforms:

This reference implementation is completely implemented in Java and should run on 
any system that fully supports the Java Runtime Environment.

-----------------------------------------------------------------------------------
3) QuickStart Build/Installation:

Compiling Prerequisites:
In addition to the tools needed for the specific datastore in use, compiling the
OpenDMTP Java server reference implementation requires that the following packages
or application be installed, configured, and running of the local system:
  - Java SDK v1.5+  [http://java.sun.com/javase/downloads/index_jdk5.jsp]
    Important Note: To avoid potential headaches trying to get the code to compile,
    make sure you are using the "Sun Microsystems" version of the Java compiler.
    The 'other' versions have problems compiling this code.  After installing the
    Java compiler, check your version with the command "java -version" and make
    sure it says "Java(TM)" and "Java HotSpot(TM) Client VM".
  - Ant v1.6.5+ [http://ant.apache.org/]

[Note: OpenDMTP server commands referenced below must be executed from the OpenDMTP
installation directory.  Each server command is provided in a Linux version (ending 
with '.sh'), and a Windows-XP version (ending with '.bat'). The Linux version of
the server commands are referenced below.  Window-XP commands must have arguments 
enclosed in quotes (eg.> bin\initdb.bat "-rootUser=root"), or the options may be
specified with ':', instead of '=' (eg.> bin\initdb.bat -rootUser:root).]

Compiling/Running the OpenDMTP server with the Flat-file datastore:
  1) Unzip the OpenDMTP Java server package in a convenient directory.
  2) Compile the Java application using the supplied Ant 'build.xml' script:
      % ant filestore
  3) Start the server:
      % bin/server_file.sh
     The server will initialize and start listening on port 31000 (default) for
     TCP & UDP connections. Received data will be stored in the directory "./data".
     The default service port number can be overridden in the runtime config file 
     "default.conf" if necessary. The default file store directory can be overidden
     on the command line (eg. "-storedir=<directory>").

-----------------------------------------------------------------------------------
4) Source Package Layout:

org.opengts.util
    General miscellaneous utilities

org.opendmtp.codes
    DMTP protocol constants/codes

org.opendmtp.server
    Server protocol support specific implementation

org.opendmtp.server.base
    Basic DMTP protocol support

org.opendmtp.server.db
    Interface definitions for use by backend data store implementation.
    
org.opendmtp.server_file
    Main entry point and implementation of flat-file event data store.

-------------------------------------------------------------------------------
6) Future OpenDMTP Server Reference Implementation Features:

The following items are also in the works for future OpenDMTP server support:
  - Additional protocol enhancements.
  - etc.

-------------------------------------------------------------------------------
7) Contact Info:

Please feel free to contact us regarding questions on this package.  Or if you
would like to be be kept up to date on the progress of this project, please let
us know.

Thanks,
Martin D. Flynn
devstaff@opendmtp.org
