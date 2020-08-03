export default new Vue({
    el: '#content',
    data: {
        isAuth: false,
        site: '',
        login: '',
        password: '',
        img: '',
        name: '',
        surname: '',
        id: 0,
        data: {},
        coins: 0
    },
    methods: {
        form: function(method, formId, fields){
            let form = document.getElementById(formId);
            let obj = {};
            for(let item of fields) {
                if(item === 'login') this.login = form[item].value;
                if(item === 'password') this.passwords = form[item].value;
                obj[item] = form[item].value
            };
            (function(){
                fetch(method, {method: 'POST', body: JSON.stringify(obj)})
                .then(data => data.json())
                .then(data => window.data = data);
            })();
        },
        auth: function(){
            let method = 'auth';
            let formId = 'loginForm';
            let fields = ['login', 'password'];
            let form = document.getElementById(formId);
            let obj = {};
            for(let item of fields) {
                if(item === 'login') this.login = form[item].value;
                if(item === 'password') this.passwords = form[item].value;
                obj[item] = form[item].value
            };

            fetch(method, {method: 'POST', body: JSON.stringify(obj)})
            .then(data => data.json())
            .then(data => {
                if(data.auth === true){
                    console.log('auth!');
                    console.log(data);
                    this.id = data.id;
                    this.login = data.login;
                    this.password = data.password;
                    this.img = data.img;
                    this.name = data.name;
                    this.surname = data.surname;
                    this.isAuth = true;
                    document.cookie = "login="+(data.login);
                    document.cookie = "password="+(data.password);
                }
            })
        },
        buy: (id, cost) => {
            fetch("buy", {method: 'POST', body: JSON.stringify({
                login: content.login,
                password: content.password,
                coins: content.coins,
                id: content.id,
                cost: cost,
                buy: id
                })
            })
            .then(data => data.json())
            .then(data => console.log(data));
        },
        confirm: (id) => {
            fetch("confirm", {method: 'POST', body: JSON.stringify({
                login: content.login,
                password: content.password,
                id: content.id,
                confirm: id
                })
            })
            .then(setTimeout(window.side.basket(), 1000));
        },
        decline: (id) => {
            fetch("decline", {method: 'POST', body: JSON.stringify({
                login: content.login,
                password: content.password,
                id: content.id,
                decline: id
                })
            })
            .then(setTimeout(window.side.basket(), 1000));
        }

    }
})