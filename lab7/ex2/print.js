const fullCurrentPath = require('path').dirname(require.main.filename) + '/';

const printFileCount = function(filename, count){
    var prettyFileName = filename.replace(fullCurrentPath, '')
    console.log(prettyFileName, count);
}

const printSetCount = function(count){
    console.log('Document set lines count: '+count);
}

module.exports = {
    printSetCount, printFileCount
}
