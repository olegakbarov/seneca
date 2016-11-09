/* eslint-env node */
import express from 'express';
import bodyParser from 'body-parser';
import jwt from 'express-jwt';
import jsonServer from 'json-server';
import config from './config';
import jwtToken from 'jsonwebtoken';
import path from 'path';
import fs from 'fs';
import _ from 'lodash';

const jsonPath = path.join(__dirname, 'db.json');
const app = express();

// app.use(jsonServer.defaults);
app.use(bodyParser.json());
app.use(jwt({
  secret: config.token.secret
}).unless(req => {
  const url = req.originalUrl;
  return url === '/api/v1/auth/token';
}));

function generateToken(email, password) {
  const id = "user123"
  const payload = { email, password, id };
  return jwtToken.sign(payload, config.token.secret);
}

function extractToken(header) {
  return header.split(' ')[1];
}

// here comes the real hardcode
const HARDCODED_EMAIL = 'email@adress';
const HARDCODED_PASSWORD = 'pass';

app.post('/api/v1/auth/token', (req, res) => {
  const { email, password } = req.body;
  console.log(email, password)
  if (email === HARDCODED_EMAIL && password === HARDCODED_PASSWORD) {
    const token = generateToken(email, password);
    res.send({ token });
  } else {
    res.sendStatus(401);
  }
});

app.get('/api/v1/courses', (req, res) => {
  try {
    const token = extractToken(req.headers.authorization);
    const decode = jwtToken.decode(token);
    console.log(decode)
    const { email, id } = decode;

    fs.readFile(jsonPath, {
      encoding: 'utf-8'
    }, (error, db) => {
      const courses  = (JSON.parse(db)).courses;
      const result = _.find(courses, (course => course.author === id));
      res.send(result);
    });
  } catch (error) {
    res.sendStatus(401);
  }
});

app.use(jsonServer.router(jsonPath));

app.listen(7777);
