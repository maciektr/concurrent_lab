const folderToWalk = 'test_folder'

const walk = require('walkdir');
const fs = require('fs');
const emitter = walk(folderToWalk);
const printer = require('./print.js')

var wholeCount = 0;

main();

function main(){
    /*
    var filePaths = [];
    emitter.on('file', function(filename, stat) {
        filePaths.push(filename);
    }).on('end', function() {
        fork(filePaths, function() {
            printer.printSetCount(wholeCount);;
        });
    });
    */

    fork(walk.sync(folderToWalk), () => {printer.printSetCount(wholeCount);;});
}

function processFile(filename, callback) {
    if(!filename.includes('.'))
        return;
    var fileCount = 0;
    fs.createReadStream(filename).on('data', function(chunk) {
                fileCount += chunk.toString('utf8')
                .split(/\r\n|[\n\r\u0085\u2028\u2029]/g)
                .length-1;
            }).on('end', function() {
                printer.printFileCount(filename, fileCount);
                wholeCount += fileCount;
                callback();
            }).on('error', function(err) {
                console.error(err);
            });
};


function fork (params, shared_callback) {
    var counter = params.length;
    var callback = function () {
        counter --;
        if (counter == 0)
            shared_callback();
    }

    for (var i=0;i<params.length;i++)
        processFile(params[i], callback);
}
