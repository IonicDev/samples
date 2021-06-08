/*
 * (c) 2018-2021 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISLog.h"
#include <stdio.h>
#include <cstdlib>
#include <iostream>

int main(int argc, char* argv[]) {

    std::string sLogChannel = "ionic-cpp-sample";
    std::string sLogPath = "./sample-log.txt";

    // initialize logger
    ISLogFilterSeverity * pFilter = new ISLogFilterSeverity(SEV_TRACE); 
    ISLogWriterFile * pFileWriter = new ISLogWriterFile(sLogPath.c_str());
    pFileWriter->setFilter(pFilter);
    ISLogSink * pSink = new ISLogSink();
    pSink->registerChannelName(sLogChannel);
    pSink->registerWriter(pFileWriter);
    ISLogImpl * pLogger = new ISLogImpl(true);
    pLogger->registerSink(pSink);
    ISLog::setSingleton(pLogger);

    // write log
    ISLOG_INFO(sLogChannel.c_str(), "Sample log entry");
}
