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
  const payload = { email, password };
  return jwtToken.sign(payload, config.token.secret);
}

function extractToken(header) {
  return header.split(' ')[1];
}

// here comes the real hardcode
const HARDCODED_EMAIL = 'email@adress';
const HARDCODED_PASSWORD = 'pass';
// const HARDCODED_USER = {
//   id: 4,
//   email: 'email@adress',
//   password: 'pass'
// };

app.post('/api/v1/auth/token', (req, res) => {
  const { email, password } = req.body;
  console.log(email, password)
  if (email === HARDCODED_EMAIL && password === HARDCODED_PASSWORD) {
    const token = generateToken(email, password);
    const user = HARDCODED_USER;
    res.send({ token });
  } else {
    res.sendStatus(401);
  }
});

app.get('/api/v1/courses', (req, res) => {
  try {
    const token = extractToken(req.headers.authorization);
    const decode = jwtToken.decode(token);
    const { email } = decode;
    console.log(email)
    fs.readFile(jsonPath, {
      encoding: 'utf-8'
    }, (error, db) => {
      const users  = (JSON.parse(db)).users;
      const user = _.find(users, (user) => user.email === email);
      res.send(user);
    });
  } catch (error) {
    res.sendStatus(401);
  }
});

app.use(jsonServer.router(jsonPath));

app.listen(7777);
