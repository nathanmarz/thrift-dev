#include "server/TThreadPoolServer.h"
#include "transport/TTransportException.h"
#include "concurrency/Thread.h"
#include "concurrency/ThreadManager.h"
#include <string>
#include <iostream>

namespace facebook { namespace thrift { namespace server { 

using namespace std;
using namespace facebook::thrift::concurrency;
using namespace facebook::thrift::transport;

class TThreadPoolServer::Task: public Runnable {
       
public:
    
  Task(shared_ptr<TProcessor> processor,
       shared_ptr<TTransport> input,
       shared_ptr<TTransport> output) :
    processor_(processor),
    input_(input),
    output_(output) {
  }

  ~Task() {}
    
  void run() {     
    while(true) {
      try {
	processor_->process(input_, output_);
      } catch (TTransportException& ttx) {
        break;
      } catch(...) {
        break;
      }
    }
    input_->close();
    output_->close();
  }

 private:
  shared_ptr<TProcessor> processor_;
  shared_ptr<TTransport> input_;
  shared_ptr<TTransport> output_;

};
  
TThreadPoolServer::TThreadPoolServer(shared_ptr<TProcessor> processor,
                                     shared_ptr<TServerTransport> serverTransport,
                                     shared_ptr<TTransportFactory> transportFactory,
                                     shared_ptr<ThreadManager> threadManager,
                                     shared_ptr<TServerOptions> options) :
  TServer(processor, serverTransport, transportFactory, options), 
  threadManager_(threadManager) {
}

TThreadPoolServer::~TThreadPoolServer() {}

void TThreadPoolServer::serve() {

  shared_ptr<TTransport> client;
  pair<shared_ptr<TTransport>,shared_ptr<TTransport> > io;

  try {
    // Start the server listening
    serverTransport_->listen();
  } catch (TTransportException& ttx) {
    cerr << "TThreadPoolServer::run() listen(): " << ttx.getMessage() << endl;
    return;
  }
  
  while (true) {   
    try {
      // Fetch client from server
      client = serverTransport_->accept();
      // Make IO transports
      io = transportFactory_->getIOTransports(client);
      // Add to threadmanager pool
      threadManager_->add(shared_ptr<TThreadPoolServer::Task>(new TThreadPoolServer::Task(processor_, io.first, io.second)));
    } catch (TTransportException& ttx) {
      break;
    }
  }
}

}}} // facebook::thrift::server
