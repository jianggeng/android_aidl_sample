// IAIDLServer.aidl
package com.jgeng.aidlserver;
import com.jgeng.aidlserver.Data;
// Declare any non-default types here with import statements

interface IAIDLServer {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    Data getData();
}
