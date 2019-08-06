# Short Title

Blockchain enabled Crowdfunding



# Long Title

Demonstrate the use of Hyperledger Fabric for building a collaboration platform for Crowdfunding.


# Author


* [Shikha Maheshwari](https://www.linkedin.com/in/shikha-maheshwari) 
* [Muralidhar Chavan](https://www.linkedin.com/in/muralidhar-chavan-3335b638/) 


# URLs

### Github repo

> "Get the code": 
* https://github.com/IBM/blockchain-enabled-crowdfunding

### Other URLs

* Demo URL

NA

# Summary






# Technologies

* [Blockchain](https://en.wikipedia.org/wiki/Blockchain): A blockchain is a digitized, decentralized, public ledger of all transactions in a network.

* [Java](https://en.wikipedia.org/wiki/Java_(programming_language)): Java is a general-purpose computer-programming language that is concurrent, class-based and object-oriented.

* [NodeJS](https://nodejs.org/en/): Node.js® is a JavaScript runtime built on Chrome's V8 JavaScript engine.


# Description

Crowdfunding is the practice of funding a project or venture by raising small amounts of money from a large number of people, typically via the Internet. It's the quickest, easiest way to gather a large amount of money through donations from supporters. There are several types of crowdfunding(donation based/rewards based/debt/equity) but not all types of crowdfunding are regulated by Financial Conduct Authority.

Crowdfunding idea has lots of good but at the same time there is a big opportunity for fraud. It involves some level of risk like loss of money, locked-in investment, no income, lack of information. People are concerned about being scammed by a fraudulent request. It is because of lack of transparency and trust. There were few reported cases of fraud – particularly when compared to the number of transactions and amounts involved. The greater the number of crowdfunding platforms, the greater the risk that platform operators themselves may engage in fraud, or enable fraud. 

This pattern showcases the blockchain enabled crowdfunding which uses Hyperledger Fabric network to build a platform to aid crowdfunding. This network gives a holistic view of the requirement and its current status. This way, a need is addressed efficiently, and the problems of over-collection or under-collection for a need are reduced. The platform creates trust, accountability, and transparency of operations.

# Flow

![Architecture](https://github.com/IBM/blockchain-enabled-crowdfunding/blob/master/images/architecture.png)


# Instructions

> Find the detailed steps for this pattern in the [readme file](https://github.com/IBM/blockchain-enabled-crowdfunding/blob/master/README.md) 

The steps will show you how to:

1. Get the code
2. Create IBM Cloud Services
3. Setup Hyperledger Fabric Network using IBM Blockchain Platform
4. Build the client application using Fabric Java SDK
5. Build and deploy webapp
6. Analyze the Results

# Components and services

* [Hyperledger Fabric](https://hyperledger-fabric.readthedocs.io/): Hyperledger Fabric is a platform for distributed ledger solutions underpinned by a modular architecture delivering high degrees of confidentiality, resiliency, flexibility and scalability.

* [Hyperledger Fabric Java SDK](https://github.com/hyperledger/fabric-sdk-java)

* [IBM Cloud Kubernetes Service](https://cloud.ibm.com/containers-kubernetes/catalog/cluster): IBM Kubernetes Service enables the orchestration of intelligent scheduling, self-healing, and horizontal scaling.

* [IBM Blockchain Platform](https://cloud.ibm.com/catalog/services/blockchain-platform): 

# Runtimes

* [Liberty for Java](https://console.bluemix.net/catalog/starters/liberty-for-java): Develop, deploy, and scale Java web apps with ease. IBM WebSphere Liberty Profile is a highly composable, ultra-fast, ultra-light profile of IBM WebSphere Application Server designed for the cloud.

* [SDK for Node.js](https://console.bluemix.net/catalog/starters/sdk-for-nodejs):Develop, deploy, and scale server-side JavaScript® apps with ease. The IBM SDK for Node.js™ provides enhanced performance, security, and serviceability.

# Related IBM Developer content

* [Setup Hyperledger Fabric network on IBM Blockchain Platform](https://developer.ibm.com/tutorials/quick-start-guide-for-ibm-blockchain-platform/)
* [Create and deploy a blockchain network using Hyperledger Fabric SDK for Java](https://github.com/IBM/blockchain-application-using-fabric-java-sdk)
* [How-to use Fabric Java SDK with a TLS-enabled Hyperledger Fabric Network](https://developer.ibm.com/tutorials/hyperledger-fabric-java-sdk-for-tls-enabled-fabric-network/)

# Related links

- [Learn about Crowdfunding](https://en.wikipedia.org/wiki/Crowdfunding)

# Announcement

## Facilitate collaboration between organizations using Hyperledger Fabric
Collaboration enables organizations to work together to achieve a common business purpose. In some scenarios of collaboration, there is no regulation or agreements to enforce the collaboration but there is still a need to ensure transparency and trust among the organizations. An example for this scenario are Non-Governmental Organizations. During times of need, all NGOs are interested in ensuring the goods, materials or services reach the people in need.


In this blog post we will:
* Describe what the new code pattern does.
* Provide a brief overview of Hyperledger Fabric.

This code pattern demonstrates the use of Blockchain to facilitate the collaboration between NGOs. A Hyperledger Fabric network is used to transparently share the details of demand(need) and supply(pledged) of goods and materials between NGOs. A client application built on Fabric Java SDK is used to invoke and query chaincode on Hyperledger network. The operations are exposed as ReST apis to enable integration with different User Interface clients. A web UI application is built on Node.JS for the end user.

Hyperledger is an open source collaborative effort created to advance cross-industry blockchain technologies for business use. This global collaboration is hosted by The Linux Foundation. Please refer to the article [Blockchain basics: Hyperledger Fabric](https://developer.ibm.com/articles/cl-blockchain-hyperledger-fabric-hyperledger-composer-compared/) for additional details.

This code pattern is not just applicable to collaboration between NGOs but also for collaboration between organizations with similar requirements. Try the code pattern out by going directly to our [GitHub repo](https://github.com/IBM/ngo-collaboration-using-blockchain).



