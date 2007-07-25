module TServer(run_basic_server,run_threaded_server) where

import Network
import Thrift
import Control.Exception
import TBinaryProtocol
import TChannelTransport
import Control.Concurrent

proc_loop hand proc ps = do v <-proc hand ps
                            if v then proc_loop hand proc ps
                                else return ()

accept_loop hand sock proc transgen iprotgen oprotgen = 
    do (h,hn,_) <- accept sock
       let t = transgen h 
       let ip = iprotgen t
       let op = oprotgen t
       forkIO (handle (\e -> return ()) (proc_loop hand proc (ip,op)))
       accept_loop hand sock proc transgen iprotgen oprotgen
       
run_threaded_server hand proc port transgen iprotgen oprotgen = 
    do sock <- listenOn (PortNumber port)
       accept_loop hand sock proc transgen iprotgen oprotgen
       return ()
       

-- A basic threaded binary protocol socket server.
run_basic_server hand proc port = run_threaded_server hand proc port TChannelTrans TBinaryProtocol TBinaryProtocol
