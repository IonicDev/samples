/*
 * (c) 2019-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

// Maps console.log() to add to output_area and console.log().
// The calling HTML script needs to use output-area to display information.

var oldLog = console.log;
console.log = function (message) {
  var output_area = document.getElementById("output-area");
  output_area.innerHTML += '<br>' + message;
  oldLog.apply(console, arguments);
};
