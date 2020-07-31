export default new Vue({
    el: '#content',
    data: {
        isAuth: false,
        site: '',
        login: '',
        password: '',
        img: '',
        name: '',
        surname: ''
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

        }
    }
})