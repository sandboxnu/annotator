const express = require('express');
const app = express();
const sls = require('serverless-http');
const fs = require('fs');
const multer = require('multer');
const csv = require('fast-csv');
const Router = express.Router;
const router = new Router();
const AWS = require('aws-sdk');

// TODO: get something i think
app.get('/csv', async (req, res, next) => { 
    res.status(200).send('This should be an endpoint to request CSV data according to some parameters.');
})

// TODO: decrypt csv, then write csv to s3 bucket
router.post('/csv', upload.single('file'), function (req, res) {
    
    
    // TODO:
    // - decrypt the file here
    // - if the file can be descypted and is valid:
    // - send it to the s3 bucket!
  // open uploaded file
  csv.fromPath(req.file.path)
    .on("data", function (data) {
        putObjectToS3('bucket-here','key-goes-here', data); 
    })
    .on("end", function () {
        fs.unlinkSync(req.file.path);   
        // remove temp file
    })
});

app.use('/csv', router);

// this function should decrypt the data and log it to the supplied s3 bucket
function putObjectToS3(bucket, key, data) {
    let s3 = new AWS.S3();
    let params = {
        Bucket: bucket,
        Key: key,
        Body: data
    }
    s3.putObject(params, function(err, data) {
        if(err) {
            console.log(err, err.stack);
        } else {
            console.log(data);
        }
    })
}


module.exports.server = sls(app)
