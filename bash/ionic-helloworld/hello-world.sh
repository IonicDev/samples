#! /usr/bin/env bash
# comment: use command ./profile.sh

# Confirm that the profile persistor password is set as an environment variable
if [[ -z "${IONIC_PERSISTOR_PASSWORD}" ]]; then
  echo "[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD"
  exit 1
fi

# Confirm that the profile persistor file exists
PERSISTOR_PATH="${HOME}/.ionicsecurity/profiles.pw"
if [[ ! -f "$PERSISTOR_PATH" ]]; then
    echo "[!] '$PERSISTOR_PATH' does not exist"
    exit 1
fi

# Configure this script to exit when any command fails
set -e

# Set the current applications name and version
ClientMetadata="ionic-application-name:Ciphers CLI Tutorial,ionic-application-version:1.0.0"

# Sample message to encrypt
MESSAGE='this is a secret message!'
echo "ORIGINAL TEXT      : ${MESSAGE}"

# Encrypt a string (The key is automatically created)
ENCRYPTED_MESSAGE=$(machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    chunk encrypt --instr "${MESSAGE}" --metas "${ClientMetadata}")

echo "CIPHER TEXT        : ${ENCRYPTED_MESSAGE}"

# Decrypt a string (The correlating key is automatically fetched)
MESSAGE=$(machina \
  --devicetype password \
  --devicefile ${PERSISTOR_PATH} \
  --devicepw ${IONIC_PERSISTOR_PASSWORD} \
    chunk decrypt --instr "${ENCRYPTED_MESSAGE}" --metas "${ClientMetadata}")

echo "PLAIN TEXT         : ${MESSAGE}"
