# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import json
import binascii
import datetime
import inspect
import ionicsdk

message = "Hello World!"

# Helpers for logging.
line_number = lambda: inspect.currentframe().f_back.f_lineno
file_name = lambda: inspect.getframeinfo(inspect.currentframe()).filename

sLogChannel = "ionic-python-sample"

# Log severity Debug and lower to a file.
config = {
  "sinks": [
    {
      "channels": [sLogChannel],
      "filter": {"type": "Severity", "level": "Debug"},
      "writers": [{"type": "Console"}]
    }
  ]
}

# Initialize logger
config_json = json.dumps(config)
ionicsdk.log.setup_from_config_json(config_json)

# Log to console.
ionicsdk.log.log(ionicsdk.log.SEV_DEBUG, sLogChannel, line_number(), file_name(), "Sample log entry")
