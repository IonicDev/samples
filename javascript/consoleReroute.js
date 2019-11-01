/*
 * (c) 2019-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

// Maps console.log() to add to output_area and console.log().
// The calling HTML script needs to use output-area to display information.

var oldLog = console.log;
console.log = function (message) {

  // Initialize output-area.  Light gainsburo background, monospace font, and 100% size.
  if (message === '') {
    output_area.innerHTML = ''
    output_area.style.backgroundColor = '#EDEDED';
    output_area.style.fontFamily = 'courier';
    output_area.style.fontSize = '100%';
  }
  else {
    output_area.innerHTML += message + '<br>';
    oldLog.apply(console, arguments);
  }
};
