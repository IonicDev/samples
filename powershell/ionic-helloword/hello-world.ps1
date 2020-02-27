Import-Module Microsoft.PowerShell.Utility

<# Confirm that the profile persistor password is set as an environment variable #>
if (-not (Get-Variable IONIC_PERSISTOR_PASSWORD -ErrorAction 'Ignore')) {
  echo "[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD"
  exit 1
}

<# Confirm that the profile persistor file exists #>
$PERSISTOR_PATH="$HOME/.ionicsecurity/profiles.pw"
if (-not (Test-Path -path $PERSISTOR_PATH)) {
  echo "[!] '$PERSISTOR_PATH' does not exist"
  exit 1
}

<# Set the current applications name and version #>
$ClientMetadata="ionic-application-name:Keys CLI Tutorial,ionic-application-version:1.0.0"

<# Sample message to encrypt #>
$MESSAGE="'this is a secret message!'"
echo "ORIGINAL TEXT      : ${MESSAGE}"

<# Encrypt a string (The key is automatically created) #>
$ENCRYPTED_MESSAGE=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} `
    chunk encrypt -s "${MESSAGE}" --metas "${ClientMetadata}")

echo "CIPHER TEXT        : ${ENCRYPTED_MESSAGE}"

<# Decrypt a string (The correlating key is automatically fetched) #>
$MESSAGE=$(ionicsdk --devicetype password --devicefile ${PERSISTOR_PATH} --devicepw ${IONIC_PERSISTOR_PASSWORD} `
    chunk decrypt -s "${ENCRYPTED_MESSAGE}" --metas "${ClientMetadata}")

echo "PLAIN TEXT         : ${MESSAGE}"
