//import all data
import card from './components/card.js'
import input from './components/input.js';
import image from './components/image.js';
import label from './components/label.js'
import button from './components/button.js';
import getCookie from './libs/cookie.js';
import side from './components/apps/side.js';
import content from './components/apps/app.js';

//load autostart scripts
import {} from './libs/connection.js';

(function(){
    let login = getCookie('login');
    let password = getCookie('password')
    window.content = content;
    if(login) {
        fetch('auth', {
            method: 'POST',
            body: JSON.stringify({
                login: login,
                password: password
            })
        })
        .then(data => data.json())
        .then(data => {
            if(data.auth === true){
                console.log(data);
                content.isAuth = true;
                content.login = data.login;
                content.password = data.password;
                content.img = data.img;
                content.name = data.name;
                content.surname = data.surname;
                content.site = 'profile';
                content.id = parseInt(data.id);
            }
            else{
                console.log('try again');
            }
        });
    }
})();

