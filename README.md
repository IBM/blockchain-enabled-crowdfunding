## ** Work in progress **
# Blockchain enabled Crowdfunding
**Demonstrate the use of Hyperledger Fabric for building a collaboration platform for Crowd Funding.**

Crowdfunding is practice of funding that operates independently of any government and is a way for people, businesses and charities to raise money. Its a way of raising finance by asking a large number of people each for a small amount of money.
People invest simply because they believe in the cause.

Green areas also have a huge role in protection of ecosystems. As forest lands and natural ecosystems reduce just because of the urbanization and creation of the cities, it is necessary to make sure biodiversity is not reducing or at least not becoming extinct. Green areas make cities and neighborhoods more attractive places for people to live and work. Although people like cities, because they are more convenient places for living, they need some rural influence as well, beside lack of plants and trees can even make living impossible.

Here is a blockchain enabled solution for Crowdfunding supporting "Go Green Movement", in which people with a diverse/same portfolios can come forward and be a part of this moment by funding for the cause. This network will give the holistic view of the requirement and its current status. This way a need is catered to in an efficient manner. The problems of over collection or under collection for a need is reduced. The platform brings in trust, accountability and transparency of operations.

When the reader has completed this Code Pattern, they will understand how to:

- Setup blockchain Network using IBP.
- Interact with blockchain network using Fabric Java SDK
- Build a client application which will interact with blockchain network with the help of SDK

# Flow

![](images/architecture.png)

1. Setup Blockchain Network using IBM Blockchain Platform on IBM Cloud.
2. Deploy the client application using Fabric Java SDK. It works as middle layer and exposes REST API.
3. Deploy Web UI application built using NodeJs.
4. User can perform following task using the web interface which internally interacts with Blockchain Network with the help of middle layer.
   * Current status of Funds raised
   * Donate for the cause
   * View all Supporters who have donated


# Pre-requisites

* [IBM Cloud Account](https://cloud.ibm.com)
* [Git Client](https://git-scm.com/downloads) - needed for clone commands.
* [Maven](https://maven.apache.org/install.html)
* [Nodejs](https://nodejs.org/en/download/)

# Steps

Follow these steps to setup and run this code pattern. The steps are described in detail below.

1. [Get the code](#1-get-the-code)
2. [Create IBM Cloud Services](#2-create-ibm-cloud-services)
3. [Setup Hyperledger Fabric Network using Kubernetes on IBM Cloud](#3-setup-hyperledger-fabric-network-using-kubernetes-on-ibm-cloud)
3. [Build the client application using Fabric Java SDK](#3-build-the-client-application-using-fabric-java-sdk)
4. [Build and deploy webapp](#4-build-and-deploy-webapp)
5. [Analyze the Results](#5-analyze-the-results)

## 1. Get the code

- Clone the repo using the below command.
   ```
   git clone https://github.com/IBM/blockchain-enabled-crowdfunding
   ```

 - In this repository,
    * [Chaincode ](https://github.com/IBM/blockchain-enabled-crowdfunding/tree/master/chaincode):
    Go lang based smart contract with application logic that has to be installed on the network.
    * [Client code using Fabric Java SDK](https://github.com/IBM/blockchain-enabled-crowdfunding/tree/master/fabric-java-sdk-app): application code built using Fabric Java SDK to invoke and query chaincode on the hyperledger fabric network. The operations are exposed as ReST APIs when deployed enabling other applications to consume.
    * [Web application code](https://github.com/IBM/blockchain-enabled-crowdfunding/tree/master/webapp): NodeJS based application code to render UI and integrates with the REST APIs exposed by the client application built on Fabric Java SDK.

## 2. Create IBM Cloud Services

**Create IBM Kubernetes Service**

Create a Kubernetes cluster with [Kubernetes Service](https://cloud.ibm.com/containers-kubernetes/catalog/cluster) using IBM Cloud Dashboard. This pattern uses the _free cluster_.

  ![](images/create_kubernetes_service.png)

  > Note: It can take up to 15-20 minutes for the cluster to be set up and provisioned.  

**Create IBM Blockchain Platform Service**

Create IBM Blockchain Platform service instance using IBM Cloud Dashboard.

 ![](images/create_IBP_service.png)
 
## 3. Setup Hyperledger Fabric Network on IBM Blockchain Platform on IBM Cloud

In this step, we will setup the Hyperledger Fabric network using IBM Blockchain Platform as explained below. 

### Create Hyperledger Fabric Network on IBM Blockchain Platform

The blockchain network should consist of two organizations with single peer each and an orderer service for carrying out all the transactions. For detailed steps to create fabric network, please refer to the [quick start guide for IBM Blockchain Platform](https://developer.ibm.com/tutorials/quick-start-guide-for-ibm-blockchain-platform/).

### Deploy Smart Contract on IBM Blockchain Platform

Smart contract(chaincode) is available [here](https://github.com/IBM/blockchain-enabled-crowdfunding/tree/master/chaincode).

**Package the smart contract**
To package follow the instructions provided [here](https://developer.ibm.com/tutorials/quick-start-guide-for-ibm-blockchain-platform/) as step 12. For your convenience, packaged smart contract(.cds) is also provided in repo under `chaincode` directory. Use this file to install smart contract.

**Install and Instantiate smart contract**

- Install smart contract using the `.cds` file generated/downloaded as explained in previous step.
- Instantiate the smart contract. Function name to be provided during instantiation is `init` and no parameters OR we can leave it blank as we are using the default function name (init) only.

**Download connection profile**

Instantiation of smart contract has to be followed by integrating the blockchain network with Fabric Java SDK. Follow the below steps to download `Connection Profile`.

* Under `Instantiated smart contracts` section, click on the three vertical dots for your smart contract as shown. Click on `Connect with SDK` option.
  ![](images/connect_with_sdk.png)
  
* Provide the `MSP name` and `Certificate Authority`. Scroll down and click on `Download Connection Profile`.

  ![](images/download_connection_profile.png)

* Rename the downloaded json file as `connection_profile.json`.
* Place this file in `fabric-java-sdk-app/src/main/resources/`. It gets loaded through `ConnectionProfileLoader.java`

## 3. Build the client application using Fabric Java SDK

Here, we use the [Fabric Java SDK App](https://github.com/IBM/blockchain-enabled-crowdfunding/tree/master/fabric-java-sdk-app) to build a client to invoke and query chaincode on the hyperledger fabric network.

Open the `manifest.yml` file under `fabric-java-sdk-app` directory. Under `env` section, provide the valid credentials for an user who can execute transactions in the network. Also update Organization Affiliation and Chaincode name.

```
applications:
- name: blockchain-enabled-crowdfunding-java
  random-route: true
  memory: 1024M
  path: target/crowdfunding-java.war
  buildpack: java_buildpack
  env:
    admin: <admin-username>
    adminpw: <admin-password>
    OrgAffiliation: <affiliation>
    ChainCodeName: <Chaincode-Name>
```
> Note: In this pattern, we are using admin credentails directly for executing transactions and not registering a new user.

Next, on the command terminal go to `blockchain-enabled-crowdfunding` directory, and execute the below commands:
```
cd fabric-java-sdk-app
mvn clean install
ibmcloud cf push
```
Login to `IBM Cloud`. On the `Dashboard`, verify that an app `blockchain-enabled-crowdfunding-java` is running fine.

Make a note of this Fabric Java SDK client application's url. On IBM Cloud dashboard, click on the application. When application page opens, click on `Visit App URL`. Now make a note of the url, as shown on the browser, even if the page shows some error. If the url ends with a `/` then remove the trailing forward slash. This url should be provided in web application that interacts with this Fabric Java SDK client application.

## 4. Build and deploy webapp

A web application is provided to perform various operations like `Donate Money`, `View Supporters` etc. Web appication code can be found under `webapp` directory.

This web application invokes rest interfaces implemented in Fabric Java SDK client application as explained above. Hence the web application needs Fabric Java SDK client application url for rest invocations.

Perform the following steps:

- Run the below command.
  ```
  cd webapp
  ```
  
- Update the Java application url (as noted in section 3) in `config.js` file of `webapp` directory. 

- Deploy the application to IBM Cloud using the command:
  ```
  ibmcloud cf push
  ```
  Deployment might take a few minutes to complete. Ensure that there are no errors while deploying the application.

## 5. Analyze the Results

Login to `IBM Cloud`. On the `Dashboard`, verify that an app `blockchain-enabled-crowdfunding-webui` is running fine. Click on the web application entry. When application page opens, click on `Visit App URL`. Web application page opens.

![](images/eventDetails.png)

It shows the crowdfunding project details, current status of funds raised and the tasks that you can perform.

There are no donations done by default. To donate, click on `Donate`. 

![](images/donationForm.png)

Provide the required details and click on `Donate Now` to donate. A pop up message appears with the result of the donation request . Click `OK`. Once done click on `Back` button to return to tasks list.

Click on `View Supporters` to view the list of supporters who have donated, as shown in below image.

![](images/viewDonations.png)

# Troubleshooting
See [Debugging.md](./Debugging.md)


# Learn More

- [Track donations with Blockchain](https://developer.ibm.com/patterns/track-donations-blockchain/)
- 

<!-- keep this -->
## License

This code pattern is licensed under the Apache Software License, Version 2. Separate third-party code objects invoked within this code pattern are licensed by their respective providers pursuant to their own separate licenses. Contributions are subject to the [Developer Certificate of Origin, Version 1.1 (DCO)](https://developercertificate.org/) and the [Apache Software License, Version 2](https://www.apache.org/licenses/LICENSE-2.0.txt).

[Apache Software License (ASL) FAQ](https://www.apache.org/foundation/license-faq.html#WhatDoesItMEAN)
