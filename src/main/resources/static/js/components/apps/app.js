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
        coins: 0,
        pupils: {},
        statuses: ['didnt read', 'in work', 'get this!', 'issued'],
        teachers: {},
        datestring: "",
        timestrimg: ""
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
                console.log(data)
                if(data.auth === 't'){
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
            .then(setTimeout(window.side.basket, 100));
        },
        decline: (id) => {
            fetch("decline", {method: 'POST', body: JSON.stringify({
                login: content.login,
                password: content.password,
                id: content.id,
                decline: id
                })
            })
            .then(setTimeout(window.side.basket, 100));
        },
        lesson: function(course){
            fetch("lesson", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password, 'id': window.content.id, 'course': course})
            })
            .then(data => data.json())
            .then(data => this.data = data)
            .then(data => this.site = 'lesson');
        },
        mark: function(course, lesson){
            fetch("mark", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password, 'id': window.content.id, 'course': course, 'lesson': lesson})
            })
            .then(data => data.json())
            .then(data => this.data = data)
            .then(data => this.site = 'mark')
            .then(data => console.log(this));
        },
        setmark: (id, lesson, pupil) => {
            let form = document.getElementById(id);
            fetch("setmark", {
                method: 'POST',
                body: JSON.stringify(
                    {'login': window.content.login,
                     'password': window.content.password,
                     'id': window.content.id,
                     'lesson': lesson,
                     'a': form['a'].value,
                     'b': form['b'].value,
                     'c': form['c'].value,
                     'pupil': pupil
               })
            })
            .then(window.side.timetable());
        },
        homework: function(course, lesson){
            console.log(course + '  ' + lesson);
            fetch("homework", {
                method: 'POST',
                body: JSON.stringify({'login': window.content.login, 'password': window.content.password, 'id': window.content.id, 'lesson': lesson, 'course': course})
            })
            .then(data => data.json())
            .then(data => this.data = data)
            .then(data => this.site = 'homework')
            .then(data => console.log(this));
        },
        sethomework: (id, lesson, course) => {
            let form = document.getElementById(id);
            fetch("sethomework", {
                method: 'POST',
                body: JSON.stringify(
                    {
                    'login': window.content.login,
                    'password': window.content.password,
                    'id': window.content.id,
                    'lesson': lesson,
                    'course': course,
                    'homework': form['homework'].value
               })
            })
            .then(window.side.timetable());
        },

        editprofile: function(id){
            this.data = this.data.find((item) => item.id === id);
            this.site = 'adminprofileedit';
        },
        saveprofile: function(id, formid){
            let form = document.getElementById(formid);
            fetch("saveprofile", {
                method: 'POST',
                body: JSON.stringify({
                'login': window.content.login,
                'password': window.content.password,
                'id': window.content.id,
                'saveid': id,
                'savelogin': form['login'].value,
                'savepassword': form['password'].value,
                'saveimg': form['img'].value,
                'savename': form['name'].value,
                'savesurname': form['surname'].value,
                'savecoins': form['coins'].value,
                'saveadmin': form['admin'].checked,
                'saveteacher': form['teacher'].checked,
                })
            })
            .then(data => window.side.adminprofile());
        },
        adminprofile: () => { window.side.adminprofile() },
        createprofile: function(){ this.editprofile(-1) },
        editbasket: function(id){
            this.data = this.data.find((item) => item.id === id);
            this.site = 'adminbasketedit';
        },
        savebasket: (id, formid) => {
            let form = document.getElementById(formid);
            fetch("savebasket", {
                method: 'POST',
                body: JSON.stringify({
                'login': window.content.login,
                'password': window.content.password,
                'id': window.content.id,
                'saveid': id,
                'savestatus': form['status'].value,
                })
            })
            .then(data => window.side.adminbasket());
        },
        editshop: function(id){
            this.data = this.data.find((item) => item.id === id);
            this.site = 'adminshopedit';
        },
        saveshop: (id, formid) => {
            let form = document.getElementById(formid);
            fetch("saveshop", {
                method: 'POST',
                body: JSON.stringify({
                'login': window.content.login,
                'password': window.content.password,
                'id': window.content.id,
                'saveid': id,
                'savetitle': form['title'].value,
                'saveimg': form['img'].value,
                'saveabout': form['about'].value,
                'savecost': form['cost'].value,
                'savequantity': form['quantity'].value,
                })
            })
            .then(data => window.side.adminshop());
        },
        createshop: function(){ this.editshop(-1) },
        edittimetable: function(id){
            this.data = this.data.find((item) => item.id === id);
            fetch("teachers", {
                method: 'POST',
                body: JSON.stringify({
                'login': window.content.login,
                'password': window.content.password,
                })
            })
            .then(data => data.json())
            .then(data => this.teachers = data);
            this.site = 'admintimetableedit';

            let date = new Date(parseInt(this.data.coursefirstdate) - new Date().getTimezoneOffset() * 60);
            this.datestring = date.getFullYear() + "-" + (date.getMonth() < 10 ? "0" : "") + date.getMonth() + "-" + (date.getDate() < 10 ? "0" : "") + date.getDate();
            this.timestring = (date.getHours() < 10 ? "0" : "") + date.getHours() + ":" + (date.getMinutes() < 10 ? "0" : "") + date.getMinutes();
        },
        savetimetable: (id, formid) => {
            let form = document.getElementById(formid);
            fetch("savetimetable", {
                method: 'POST',
                body: JSON.stringify({
                'login': window.content.login,
                'password': window.content.password,
                'saveid': id,
                'savename': form['coursename'].value,
                'saveimg': form['courseimg'].value,
                'saveteacher': form['teacher'].value,
                'savedate': (form['date'].valueAsNumber + form['time'].valueAsNumber) - (new Date().getTimezoneOffset() * 60),
                })
            })
            .then(data => window.side.admintimetable());
        },
        adminshop: () => {window.side.adminshop()}
    }
})