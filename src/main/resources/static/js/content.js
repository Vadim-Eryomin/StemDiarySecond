//import all data
import('./components/simple-card.js')
.then(import('./connection.js'))
.then(import('./components/app.js'))
.then(import('./components/side.js'))
.then(import('./libs/cookie.js'))
.then(module => console.log(module));
