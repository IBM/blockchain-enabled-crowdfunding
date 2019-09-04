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

This code pattern demonstrates the blockchain enabled crowdfunding which uses the Hyperledger Fabric to build a network to support crowdfunding. Crowdfunding is the quickest, easiest way to gather a large amount of money through donations from supporters. It has its own pros and cons. There is a probability for a fraud in a crowdfunding event. Hence blockchain has been used in this pattern to bring trust and transparency among the participants to manage it efficiently.

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


1. Setup Blockchain Network using IBM Blockchain Platform on IBM Cloud.
2. Deploy the client application using Fabric Java SDK. It works as middle layer and exposes REST API.
3. Deploy Web UI application built using NodeJs.
4. User can perform following task using the web interface which internally interacts with Blockchain Network with the help of middle layer.
   * Current status of Funds raised
   * Donate for the cause
   * View all Supporters who have donated
   
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

* [IBM Blockchain Platform](https://cloud.ibm.com/catalog/services/blockchain-platform): IBM Blockchain Platform is an enterprise-ready blockchain application development platform powered by Hyperledger Fabric.

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

Crowdfunding is the practice of funding a project or venture by raising small amounts of money from a large number of people, typically via the Internet. Crowdfunding is a form of crowdsourcing and alternative finance. 

Some of the advantages of crowdfunding include raising the money faster, public participation, wider coverage of investors. In addition to these advantages, there is a big opportunity for fraud because of lack of transparency and trust.

Blockchain platform is the apt technical solution for implementing crowdfunding scenario which addresses trust and transparency issues. Our [code pattern](https://github.com/IBM/blockchain-enabled-crowdfunding) showcases a methodology to implement such a crowdfunding scenario. In this code pattern, a platform is built for raising funds from public entities. The platform provides the latest state of funds collection, investors, amount. This code pattern uses [IBM Blockchain Platform](https://cloud.ibm.com/docs/services/blockchain?topic=blockchain-ibp-console-overview#ibp-console-overview), Fabric Java SDK and Node.js to build crowdfunding scenario for raising funds to increase green cover.
