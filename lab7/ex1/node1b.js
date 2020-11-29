function fillArray(value, len) {
  var arr = [];
  for (var i = 0; i < len; i++) {
    arr.push(value);
  }
  return arr;
}

async function printAsync(s) {
   var delay = Math.floor((Math.random()*1000)+500);
   return new Promise((resolve) => {
       setTimeout(function() {
           console.log(s);
	       resolve();
       }, delay);
   })
}

async function printGroup(){
    await printAsync("1");
    await printAsync("2");
    await printAsync("3");
}

var async = require("async");

function loop(m){
    async.waterfall(
	    fillArray(printGroup, m),
	    function (err, result) {}
    );
}

loop(4);
