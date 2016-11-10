### Endtpoints

Req:
```shell
curlpost '{"email":"email@adress","password":"pass"}' localhost:7777/api/v1/auth/token
```

Res:
```
{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImVtYWlsQGFkcmVzcyIsInBhc3N3b3JkIjoicGFzcyIsImlkIjoidXNlcjEyMyIsImlhdCI6MTQ3ODcyNDQxNX0.hs-Xy-c92XftGvbhke6KE7ODuhWpJH9BKDlrFSlH6tM"}
```

---

Req:

```shell
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImVtYWlsQGFkcmVzcyIsInBhc3N3b3JkIjoicGFzcyIsImlkIjoidXNlcjEyMyIsImlhdCI6MTQ3ODY4NjU5NH0.aZT5Ue2NUKCl9W3Ni4h0QAcv8EevQI5SHRr1OS8ej_Q" localhost:7777/api/v1/course
```

Res:
```
{"id":123,"author":"user123","name":"Cute animals"}
```
