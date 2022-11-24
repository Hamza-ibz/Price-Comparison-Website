let chai = require("chai");
let chaiHttp = require("chai-http");
let server = require("../server");
const should = chai.should();
const { assert } = require('chai')
chai.use(chaiHttp);

chai.should()

chai.use(chaiHttp);

  //Mocha/Chai test showing price is a number
  describe("GET /phone_comparison/search_phone/iphone", () => {
    it("Random phone comparison should have key: price that is a number", (done) => {
      chai
        .request(server)
        .get("/phone_comparison/search_phone/iphone")
        .end((err, res) => {
          let phones = JSON.parse(res.text);
          let randomPhone = Math.floor(Math.random() * phones.data.length);
          phones.data[randomPhone].should.have.property("price");

          assert.isNumber(phones.data[randomPhone].price)
          done();
        });
    });
  });

    //Mocha/Chai test showing iPhone 8 should have key: name that is expected to be a string
    describe("GET /phone_comparison", () => {
        it("Random iPhone 8 should have key: name that is expected to be a string", (done) => {
          chai
            .request(server)
            .get("/phone_comparison/search/iphone8")
            .end((err, res) => {
              let phones = JSON.parse(res.text);
              let randomPhone = Math.floor(Math.random() * phones.data.length);
              phones.data[randomPhone].should.have.property("name").to.be.a('string');
              done();
            });
        });
      });

          //Mocha/Chai test showing number of iphones
    describe("GET /phone_comparison", () => {
        it("should show count (number) of type of iphone", (done) => {
          chai
            .request(server)
            .get("/phone_comparison")
            .end((err, res) => {
              let phones = JSON.parse(res.text);
              phones.should.have.property("count");
              assert.isNumber(phones.count)
              done();
            });
        });
      });

describe("Server", function () {
      //Mocha/Chai test of RESTful Web Service status
      describe("Connection to DB", () => {
        it("should GET all the Phones", (done) => {
          chai
            .request(server)
            .get("/phone_comparison")
            .end((err, res) => {
              res.should.have.status(200);
    
              done();
            });
        });
      });
});

// Mocha/Chai test showing the phones are stored in an array
  describe("GET phone_comparison", () => {
    it("should GET all phone Array", (done) => {
      chai
        .request(server)
        .get("/phone_comparison/search/")
        .end((err, res) => {
          JSON.parse(res.text).data.should.be.a("array");
          done();
        });
    });
  });

    // Mocha/Chai test showing phone comparison is stored in an object
  describe("GET /comparison", () => {
    it("should GET object that contains all phone comparison objects within an Array", (done) => {
      chai
        .request(server)
        .get("/phone_comparison/search_phone/")
        .end((err, res) => {
          JSON.parse(res.text).should.be.a("Object");
          done();
        });
    });
  });

