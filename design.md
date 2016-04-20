# PBB Design

There will be 3 layers:
* Elections layer.
* Generalized PBB (Public Bulletin Board) layer.
* Context Broker layer.

Each layer corresponds to a REST api. The Context Broker layer is it's api so we don't need to define it, but we need to define how it will be used specifically. 

The Generalized PBB will be based on the paper “A Generic Design for a Public Bulletin Board” (Hauser, Henni, Dubuis). Specifically, our design architecture is different than the one described in the paper, but our design aims to fulfil the 9 properties described in the paper (section 3):

* Sectioned
* Grouped
* Typed
* Ordered
* Chronological
* Interlinked
* Access-Control
* Certified Publishing

agora-board will implement the GPBB REST api, using underneath the fiware-orion context broker. GPBB will be implemented using Scala and the library Play (or Akka). Scala is a funcional and object-oriented, statically typed system, and it is compiled to Java bytecode, running on the Java VM. Using a statically typed system means that many programming errors can be checked at compile time. Running in the Java VM means that the exe is highly portable, as in principle it can run on any system that supports Java. Also, using a compiled language instead of an interpreted one like Python in general means execution is normally much faster.

The GPBB REST api consists of two basic operations: Get and Post.

## POST

Publishes a post in the PBB, and returns the board attributes for that post.  
POST /bulletin/api/v1/post  

section + group will be mapped to orion's service paths.
``` 
message: m,  
user_attributes: {  
  section: s,              // a hash-like number, must be randomly generated. Base64 encoded, with 132 bits, which means 22 characters  
  group: g,                // alphanumeric text, separated by slashes, for different paths  
  pk: public key,  
  signature: S              // Sign(m,[s,g])  
}

Reponse:  
board_attributes: {  
  index_general: iG,     // index for all messages  
  timestamp: t,  
  hash: Hi,              // Hi = H(pi-1) for example. A hash of the previous post *from this section*  
  signature: Spost       // Spost = Sign(m, user_attributes, [i,t, Hi])  
}
``` 
## GET

Sends a query and returns a set of posts retrieved from the PBB corresponding to the filter/query.

GET /bulletin/api/v1/get
``` 
query: {    
    user_attributes: {  
        "section" : s,  
        "group": g  
    },  
    board_attributes: {  
        index_general: iG,    
        index_section: iS,  
        timestamp: t,  
    }  
    "isPattern": false,  
}  
``` 
Response:  
``` 
response: [  // the response is a list of posts  
  {  
    message: m, 
    user_attributes: {  
      section: s,              // a hash-like number, must be randomly generated. Base64 encoded, with 132 bits, which means 22 characters  
      group: g,  
      pk: public key,  
      signature: S              // Sign(m,[s,g])  
    },  
    board_attributes: {  
      index_general: iG,     // index for all messages  
      index_section: iS,     // index for all messages in this election  
      timestamp: t,  
      hash: Hi,              // Hi = H(pi-1) for example. A hash of the previous post *from this section*  
      signature: Spost       // Spost = Sign(m, user_attributes, [i,t, Hi])  
    }  
  }  
],  
result_attributes: {  
  index_general,        // works as a "snapshot indicator"  
  timestamp: t,  
  signature: Sget       // Sget = Sign(Q,R,[t])  
}  
``` 
# Fiware-orion

Fiware-orion will be used to store and retrieve the post messages of the Public Bulletin Board. The capabilities to query the context broker using filters will be used and translated into the GPBB layer as a feature. In particular it will be used for the Get query in the GPBB. The specific properties of a Public Bulletin Board mean that the only operations that will be required from fiware-orion will be to create and find entities. The concept of sections in the GPBB, linked to votings and election processes will be translated into fiware orion service paths. General indexes in the GPBB layer will be used as IDs in fiware-orion.

# Deployment

Ansible playbooks will be used for deployment. The reference platform will be Ubuntu 14.04 LTS.

## Section/Election configuration parameters:

* section: s                   // a hash-like number, must be randomly generated. Base64 encoded, with 132 bits, which means 22 characters
* timestamp_server: local_unix
* init_election: start time
* hash_method: sha512
* Access control method:
    // K is a function that gives the set of authorized keys for posting. 
    //It can be dynamic or static (K = const). If dynamic: K = K(Pt, user_attrs, board_attrs).
    - K: 
    - Initially accepted PKs
    - admin: pk    // pk for the admin
    
* PBB admin: pk

The PBB admin will post the first message of a section, creating the section and setting the initial parameters for the section/election. Following posts in the section will include section admin posts and  post from other parties, when those parties for that section state use signatures with accepted PKs.


