// heavily inspired by https://www.linkedin.com/pulse/large-file-processing-csv-using-aws-lambda-step-functions-nacho-coll/

let AWS = require('aws-sdk'),
    s3 = new AWS.S3(),
    es= require('event-stream'),
    chunkSize = 500 // arbitrary

let result = event.hasOwnProperty('results') ? event.results: {processedRows: 0, importedRows: 0, errors: []};

// track indices of chunks of the csv file
let index = 0,
    processed = 0,
    end = true

// gets the csv file
let pipeline = s3.getObject({
    Bucket: '<MY-BUCKET>',
    Key: '<MY-CSV-FILE-KEY>'
})
// reads in the csv
    .createReadStream()
// pipes into regular expression to remove newlines
    .pipe(es.split(/\r|\r?\n/))
// calls some functionto process the line
    .pipe(es.mapSync(function (line) {
        index++;
        if(result.processedRows + 1 < index) {
            if(processed < chunkSize) {
                processed++; result.processedRows++;
                pipeline.pause();
                process(line);
            } else {
                end = false; // chunk processed but file has not ended
                pipeline.end();
            }
        }
    }))
    // when finished, lets us know
    .on('end', function() {
        result.finished = end; // output if the end of file is reached
        console.log('pipeline end');
    });


