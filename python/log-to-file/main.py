# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import json
import binascii
import time
import inspect
import ionicsdk

# Helpers for logging.
line_number = lambda: inspect.currentframe().f_back.f_lineno
file_name = lambda: inspect.getframeinfo(inspect.currentframe()).filename

sLogChannel = "ionic-python-sample"

# Create a log file name so that the name includes the creation time.
logFilePath = "sample_" + time.strftime("%Y-%m-%d_%H.%M.%S") + ".log"

# Log severity Debug and lower to a file.
config = {
  "sinks": [
    {
      "channels": [sLogChannel],
      "filter": {"type": "Severity", "level": "Debug"},
      "writers": [{"type": "File", "filePattern": logFilePath}]
    }
  ]
}

# Initialize logger
config_json = json.dumps(config)
ionicsdk.log.setup_from_config_json(config_json)
print("Logging to: {0}". format(logFilePath))

# Log to file.
ionicsdk.log.log(ionicsdk.log.SEV_DEBUG, sLogChannel, line_number(), file_name(), "LogToFile Sample")
ionicsdk.log.log(ionicsdk.log.SEV_DEBUG, sLogChannel, line_number(), file_name(), "Sample log entry")
