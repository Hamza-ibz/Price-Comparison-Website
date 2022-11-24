//Import the express and url modules
let express = require('express');
let url = require("url");

//Status codes defined in external file
require('./http_status.js');

//The express module is a function. When it is executed it returns an app object
let app = express();

//Import the mysql module
let mysql = require('mysql');

//Create a connection object with the user details
let connectionPool = mysql.createPool({
    connectionLimit: 1,
    host: "localhost",
    user: "root",
    password: "root",
    database: "price_comparison",
    debug: false
});

//Set up the application to handle GET requests sent to the user path
app.get('/phone_comparison/*', handleProductGet);//Subfolders
app.get('/phone_comparison', handleProductGet);

app.use(express.static('/Users/hamza/Documents/UniThirdYear/adv_web/real_project/website/server/public'));

//Start the app listening on port 8080
app.listen(8080);


/* Handles GET request sent to products path
   Processes path and query string and calls appropriate functions to
   return the data. */
async function handleProductGet(request, response){
    //Parse the URL
    let urlObj = url.parse(request.url, true);

    //Extract object containing queries from URL object.
    let queries = urlObj.query;

    //Get the pagination properties if they have been set. Will be  undefined if not set.
    let numItems = queries['num_items'];
    let offset = queries['offset'];

    //Split the path of the request into its components
    let pathArray = urlObj.pathname.split("/");

    //Get the last part of the path
    let pathEnd = pathArray[pathArray.length - 1];

    let pathEndArray = pathEnd.split("?");
    

    //If path ends with 'products' we return all products
    try {
        if (pathEnd === 'phone_comparison') {
            
            //Get the total number of products for pagination
            let prodCount = await getPhoneCount();

            //Get the products
            let products = await getPhones(numItems, offset);

            //Combine into a single object and send back to client
            let returnObj = {
                count: prodCount,
                data: products
            }
            
            response.json(returnObj);
        }else if(pathArray[2] === 'search'){
            console.log("search for "+pathEnd)
            console.log(pathEndArray[0])
            let search_result = await searchPhone(pathEnd, numItems, offset);
            let searchCount = await getSearchPhoneCount(pathEndArray);

            let returnObj = {
                count: searchCount,
                data: search_result
            }
            response.json(returnObj);
        }else if(pathArray[2] === 'search_phone'){
            console.log("search for "+pathEnd)
            console.log(pathEndArray[0])
            let search_result = await search(pathEnd, numItems, offset);
            let searchCount = await getSearchCount(pathEndArray);


            let returnObj = {
                count: searchCount,
                data: search_result
            }
            response.json(returnObj);
        }

        //If the last part of the path is a valid product id, return data about that product
        let regEx = new RegExp('^[0-9]+$');//RegEx returns true if string is all digits.
        if( (!pathArray[2] === 'search') && regEx.test(pathEnd)){
            let product = await getProduct(pathEnd);
            response.json(product);
        }
    }
    catch(ex){
        response.status(HTTP_STATUS.NOT_FOUND);
        response.send("{error: '" + JSON.stringify(ex) + "', url: " + request.url + "}");
    }
}


/** Returns a promise to get the total number of products */
async function getProductCount(){
    //Build SQL query
    let sql = "SELECT COUNT(*) FROM phone_comparison INNER JOIN phone ON phone_comparison.phone_id=phone.id";

    //Execute promise to run query
    let result = await executeQuery(sql);

    //Extract the data we need from the result
    return result[0]["COUNT(*)"];
}

async function getPhoneCount(){
    //Build SQL query
    let sql = "SELECT COUNT(*) FROM phone";

    //Execute promise to run query
    let result = await executeQuery(sql);

    //Extract the data we need from the result
    return result[0]["COUNT(*)"];
}

async function getSearchCount(pathEndArray){
    
    //Build SQL query
    let sql = "SELECT COUNT(*) FROM phone_comparison INNER JOIN phone ON phone_comparison.phone_id=phone.id "+"WHERE description LIKE '%"+ pathEndArray[0] +"%' OR " + "site_name LIKE '%"+ pathEndArray[0] +"%'";
    
    //Execute promise to run query
    let result = await executeQuery(sql);

    //Extract the data we need from the result
    return result[0]["COUNT(*)"];
}

async function getSearchPhoneCount(pathEndArray){
    
    //Build SQL query
    let sql = "SELECT COUNT(*) FROM phone "+"WHERE description LIKE '%"+ pathEndArray[0] +"%'";
    
    //Execute promise to run query
    let result = await executeQuery(sql);

    //Extract the data we need from the result
    return result[0]["COUNT(*)"];
}


/** Returns all the products with optional pagination */
async function getProducts(numItems, offset){
    let sql = "SELECT phone_comparison.id, phone_comparison.price, phone_comparison.site_name, phone_comparison.url, phone.name, phone.model, phone.colour, phone.description, phone.storage, phone.image_url "+
    "FROM phone_comparison INNER JOIN phone ON phone_comparison.phone_id=phone.id ";

    //Limit the number of results returned, if this has been specified in the query string
    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY phone_comparison.id LIMIT " + numItems + " OFFSET " + offset;
    }

    //Return promise to run query
    return executeQuery(sql);
}

async function getPhones(numItems, offset){
    let sql = "SELECT phone.name, phone.model, phone.colour, phone.description, phone.storage, phone.image_url "+
    "FROM phone";

    //Limit the number of results returned, if this has been specified in the query string
    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY phone_comparison.id LIMIT " + numItems + " OFFSET " + offset;
    }

    //Return promise to run query
    return executeQuery(sql);
}


/** Returns a promise to get details about a single product */
async function getProduct(id){
    let sql = "SELECT phone_comparison.id, phone_comparison.price, phone_comparison.site_name, phone_comparison.url, phone.name, phone.model, phone.colour, phone.description, phone.storage, phone.image_url "+
    "FROM phone_comparison INNER JOIN phone ON phone_comparison.phone_id=phone.id "+"WHERE phone_comparison.id="+id;
    return executeQuery(sql);
}

async function getPhone(id){
    let sql = "SELECT phone.name, phone.model, phone.colour, phone.description, phone.storage, phone.image_url "+
    "FROM phone "+"WHERE phone.id="+id;
    return executeQuery(sql);
}

async function search(pathEnd, numItems, offset){
    let sql = "SELECT phone_comparison.id, phone_comparison.price, phone_comparison.site_name, phone_comparison.url, phone.name, phone.model, phone.colour, phone.description, phone.storage, phone.image_url "+
    "FROM phone_comparison INNER JOIN phone ON phone_comparison.phone_id=phone.id " +"WHERE description LIKE '%"+ pathEnd +"%'"

    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY phone_comparison.id LIMIT " + numItems + " OFFSET " + offset;
    }

    return executeQuery(sql);
}

async function searchPhone(pathEnd, numItems, offset){
    let sql = "SELECT phone.id, phone.name, phone.model, phone.colour, phone.description, phone.storage, phone.image_url "+
    "FROM phone " +"WHERE description LIKE '%"+ pathEnd +"%'";

    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY phone_comparison.id LIMIT " + numItems + " OFFSET " + offset;
    }

    return executeQuery(sql);
}


/** Wraps connection pool query in a promise and returns the promise */
async function executeQuery(sql){
    //Wrap query around promise
    let queryPromise = new Promise( (resolve, reject)=> {
        //Execute the query
        connectionPool.query(sql, function (err, result) {
            //Check for errors
            if (err) {
                //Reject promise if there are errors
                reject(err);
            }

            //Resole promise with data from database.
            resolve(result);
        });
    });

    //Return promise
    return queryPromise;
}
module.exports = app
